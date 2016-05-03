package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import basicgraph.Graph;
import geography.GeographicPoint;
import geography.RoadSegment;
import roadgraph.MapGraph;

/**
 * This class reads different kinds of files into different graph structures.
 */
public class MapLoader implements GraphLoader  {
	
	@Override
	public void createIntersectionsFile(String roadDataFile, String intersectionsFile) {
		
        HashMap<GeographicPoint,List<LinkedList<Road>>> map = buildMap(roadDataFile);
		List<GeographicPoint> intersections = findIntersections(map);
		Collection<GeographicPoint> nodes = new HashSet<GeographicPoint>();
		for (GeographicPoint point : intersections) {
			nodes.add(point);
		}
		
        // write the intersections into the file
		try {
			
			// create file 
			File file = new File(intersectionsFile);
			file.createNewFile();
			PrintWriter writer = new PrintWriter(file);
			
			// add edges to nodes
			for (GeographicPoint pt : nodes) {
				// Track the node to its next node, building up the points 
				List<LinkedList<Road>> inAndOut = map.get(pt);
				LinkedList<Road> outgoing = inAndOut.get(0);
				for (Road info : outgoing) {
					HashSet<GeographicPoint> used = new HashSet<GeographicPoint>();
					used.add(pt);
				
					List<GeographicPoint> pointsOnEdge = findPointsOnEdge(map, info, nodes);
					GeographicPoint end = pointsOnEdge.remove(pointsOnEdge.size()-1);
					writer.write(pt + " " + end + "\n");
				}
				
			}
			writer.flush();
			writer.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	// Build map that maps points to lists of lists of lines.
	// The map returned is indexed by a GeographicPoint.  The values
	// are lists of length two where each entry in the list is a list.
	// The first list stores the outgoing roads while the second 
	// stores the outgoing roads.
	private HashMap<GeographicPoint, List<LinkedList<Road>>> buildMap(String filename){
		
        HashMap<GeographicPoint,List<LinkedList<Road>>> map = 
        		new HashMap<GeographicPoint,List<LinkedList<Road>>>();
        
		try { 
			FileReader fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            // Read the lines out of the file and put them in a HashMap by points
            String nextLine = bufferedReader.readLine();
            while (nextLine != null) {
            	// parse the line 
            	
            	
            	// parse the string
            	Road line = parse(nextLine);
            	
            	
            	
            	
            	
            	
            	
            	//add info to the map
            	
            	
            	
            	
            	buildMapHelper(line, map);
            	nextLine = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		return map;
	}
	
	
	// Add the next line read from the file to the points map.
	private static void buildMapHelper(Road line,
						HashMap<GeographicPoint,List<LinkedList<Road>>> map) {
		List<LinkedList<Road>> pt1Infos = map.get(line.point1);
		if (pt1Infos == null) {
			pt1Infos = new ArrayList<LinkedList<Road>>();
			pt1Infos.add(new LinkedList<Road>());
			pt1Infos.add(new LinkedList<Road>());
			map.put(line.point1, pt1Infos);
		}
		List<Road> outgoing = pt1Infos.get(0);
		outgoing.add(line);
		
		List<LinkedList<Road>> pt2Infos = map.get(line.point2);
		if (pt2Infos == null) {
			pt2Infos = new ArrayList<LinkedList<Road>>();
			pt2Infos.add(new LinkedList<Road>());
			pt2Infos.add(new LinkedList<Road>());
			map.put(line.point2, pt2Infos);
		}
		List<Road> incoming = pt2Infos.get(1);
		incoming.add(line);
		
	}
	
	
	
	// Find all the intersections. 
	// Including dead ends, intersections between different roads
	private List<GeographicPoint> findIntersections(HashMap<GeographicPoint,
			List<LinkedList<Road>>> pointMap) {
		// Now find the intersections.  These are roads that do not have
		// Exactly 1 or 2 roads coming in and out, where the roads in
		// match the roads out.
		List<GeographicPoint> intersections = new LinkedList<GeographicPoint>();
		for (GeographicPoint pt : pointMap.keySet()) {
			List<LinkedList<Road>> roadsInAndOut = pointMap.get(pt);
			LinkedList<Road> roadsOut = roadsInAndOut.get(0);
			LinkedList<Road> roadsIn = roadsInAndOut.get(1);
			
			boolean isNode = true;
			
			if (roadsIn.size() == 1 && roadsOut.size() == 1) {
				// If these are the reverse of each other, then this is
				// and intersection (dead end)
				if (!(roadsIn.get(0).point1.equals(roadsOut.get(0).point2) &&
						roadsIn.get(0).point2.equals(roadsOut.get(0).point1))
						&& roadsIn.get(0).roadName.equals(roadsOut.get(0).roadName)) {
					isNode = false;
				}
			}
			if (roadsIn.size() == 2 && roadsOut.size() == 2) {
				// If all the road segments have the same name, 
				// And there are two pairs of reversed nodes, then 
				// this is not an intersection because the roads pass
				// through.
			
				String name = roadsIn.get(0).roadName;
				boolean sameName = true;
				for (Road info : roadsIn) {
					if (!info.roadName.equals(name)) {
						sameName = false;
					}
				}
				for (Road info : roadsOut) {
					if (!info.roadName.equals(name)) {
						sameName = false;
					}
				}
				
				Road in1 = roadsIn.get(0);
				Road in2 = roadsIn.get(1);
				Road out1 = roadsOut.get(0);
				Road out2 = roadsOut.get(1);
		
				boolean passThrough = false;
				if ((in1.isReverse(out1) && in2.isReverse(out2)) ||
						(in1.isReverse(out2) && in2.isReverse(out1))) {
					
					passThrough = true;
				} 
				
				if (sameName && passThrough) {
					isNode = false;
				} 

			} 
			if (isNode) {
				intersections.add(pt);
			}
		}
		return intersections;
	}

	
	@Override
	public void loadRoadMap(String filename, roadgraph.MapGraph map,  
			HashMap<GeographicPoint,HashSet<RoadSegment>> segments, 
			Set<GeographicPoint> intersectionsToLoad)
	{
		Collection<GeographicPoint> nodes = new HashSet<GeographicPoint>();
        HashMap<GeographicPoint,List<LinkedList<Road>>> pointMap = 
        		buildMap(filename);
		
        // Add the nodes to the graph
		List<GeographicPoint> intersections = findIntersections(pointMap);
		for (GeographicPoint pt : intersections) {
			map.addVertex(pt);
			if (intersectionsToLoad != null) {
				intersectionsToLoad.add(pt);
			}
			nodes.add(pt);
		}
		
		
		addEdgesAndSegments(nodes, pointMap, map, segments);
	}

	
	// Once you have built the pointMap and added the Nodes, 
	// add the edges and build the road segments if the segments
	// map is not null.
	private static void addEdgesAndSegments(Collection<GeographicPoint> nodes, 
			HashMap<GeographicPoint,List<LinkedList<Road>>> pointMap,
			MapGraph map, 
			HashMap<GeographicPoint,HashSet<RoadSegment>> segments)
	{
	
		// Now we need to add the edges
		// This is the tricky part
		for (GeographicPoint pt : nodes) {
			// Trace the node to its next node, building up the points 
			// on the edge as you go.
			List<LinkedList<Road>> inAndOut = pointMap.get(pt);
			LinkedList<Road> outgoing = inAndOut.get(0);
			for (Road info : outgoing) {
				HashSet<GeographicPoint> used = new HashSet<GeographicPoint>();
				used.add(pt);
				
				List<GeographicPoint> pointsOnEdge = 
						findPointsOnEdge(pointMap, info, nodes);
				GeographicPoint end = pointsOnEdge.remove(pointsOnEdge.size()-1);
				double length = getRoadLength(pt, end, pointsOnEdge);
				map.addEdge(pt, end, info.roadName, info.roadType, length);

				// If the segments variable is not null, then we 
				// save the road geometry
				if (segments != null) {
					// Now create road Segments for each edge
					HashSet<RoadSegment> segs = segments.get(pt);
					if (segs == null) {
						segs = new HashSet<RoadSegment>();
						segments.put(pt,segs);
					}
					RoadSegment seg = new RoadSegment(pt, end, pointsOnEdge, 
							info.roadName, info.roadType, length);
					segs.add(seg);
					segs = segments.get(end);
					if (segs == null) {
						segs = new HashSet<RoadSegment>();
						segments.put(end,segs);
					}
					segs.add(seg);
				}
			}
		}
	}
			
	
	// Calculate the length of this road segment taking into account all of the 
	// intermediate geographic points.
	private static double getRoadLength(GeographicPoint start, GeographicPoint end,
			List<GeographicPoint> path)
	{
		double dist = 0.0;
		GeographicPoint curr = start;
		for (GeographicPoint next : path) {
			dist += curr.distance(next);
			curr = next;
		}
		dist += curr.distance(end);
		return dist;
	}
	
	private static List<GeographicPoint>
	findPointsOnEdge(HashMap<GeographicPoint,List<LinkedList<Road>>> pointMap,
		Road info, Collection<GeographicPoint> nodes) 
	{
		List<GeographicPoint> toReturn = new LinkedList<GeographicPoint>();
		GeographicPoint pt = info.point1;
		GeographicPoint end = info.point2;
		List<LinkedList<Road>> nextInAndOut = pointMap.get(end);
		LinkedList<Road> nextLines = nextInAndOut.get(0);
		while (!nodes.contains(end)) {
			toReturn.add(end);
			Road nextInfo = nextLines.get(0);
			if (nextLines.size() == 2) {
				if (nextInfo.point2.equals(pt)) {
					nextInfo = nextLines.get(1);
				}
			}
			else if (nextLines.size() != 1) {
				System.out.println("Something went wrong building edges");
			}
			pt = end;
			end = nextInfo.point2;
			nextInAndOut = pointMap.get(end);
			nextLines = nextInAndOut.get(0);
		}
		toReturn.add(end);
		
		return toReturn;
	}

	
	// parse the line
	private Road parse(String line) {	
	        
    	String pattern = "([\\S]+)(\\s)([\\S]+)(\\s)([\\S]+)(\\s)([\\S]+)(\\s\")(.*)(\"\\s)(.*)";
		
    	Pattern r = Pattern.compile(pattern);
    	Matcher _m = r.matcher(line);
    	
		double d1 = 0;
		double d2 = 0;
		double d3 = 0;
		double d4 = 0;            		
		String loc = "";
		String typ = "";
    	while (_m.find()){
    		d1 = Double.parseDouble(_m.group(1));
    		d2 = Double.parseDouble(_m.group(3));
    		d3 = Double.parseDouble(_m.group(5));
    		d4 = Double.parseDouble(_m.group(7));
    		loc = _m.group(9);
    		typ = _m.group(11);
    	}
    	
        GeographicPoint p1 = new GeographicPoint(d1, d2);
        GeographicPoint p2 = new GeographicPoint(d3, d4);
    	Road road = new Road(p1, p2, loc, typ);
    	
    	return road;
		
	}
}	