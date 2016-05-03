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
		
		// first map all the points to their outgoing and incoming edges
        HashMap<GeographicPoint,List<LinkedList<Road>>> map = buildMap(roadDataFile);
        // second find all the intersections
		Collection<GeographicPoint> intersections = getIntersections(map);
		
        // write the intersections into the file
		try {
			
			// create file 
			File file = new File(intersectionsFile);
			file.createNewFile();
			PrintWriter writer = new PrintWriter(file);
			
			// add edges to nodes
			for (GeographicPoint pt : intersections) {
				// Track the node to its next node, building up the points 
				List<LinkedList<Road>> all = map.get(pt);
				LinkedList<Road> outgoing = all.get(0);
				for (Road r : outgoing) {
					HashSet<GeographicPoint> used = new HashSet<GeographicPoint>();
					used.add(pt);
				
					List<GeographicPoint> pointsOnEdge = findPointsOnEdge(map, r, intersections);
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
	
	
	/**
	 * maps points to lists of lists of lines for ougoing and 
	 * incoming roads respectively
	 * @param filename name of the .map file
	 * @return a map of points
	 */
	private HashMap<GeographicPoint, List<LinkedList<Road>>> buildMap(String filename) {
		
        HashMap<GeographicPoint,List<LinkedList<Road>>> map = 
        		new HashMap<GeographicPoint,List<LinkedList<Road>>>();
        
		try { 
			FileReader fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            // Read the lines out of the file and put them in a HashMap by points
            String nextLine = bufferedReader.readLine();
            while (nextLine != null) {
            	
            	// parse the line
            	Road road = parse(nextLine);
            	
            	//add info to the map
        		List<LinkedList<Road>> pt1 = map.get(road.point1);
        		// not in map
        		if (pt1 == null) {
        			pt1 = new ArrayList<LinkedList<Road>>();
        			// this list will store the outgoing edges
        			pt1.add(new LinkedList<Road>());
        			// this second list will store incoming edges
        			pt1.add(new LinkedList<Road>());
        			map.put(road.point1, pt1);
        		}
        		// see point1 as the point with this road as the outgoing edge
        		List<Road> out = pt1.get(0);
        		out.add(road);
        		
        		List<LinkedList<Road>> pt2 = map.get(road.point2);
        		// not yet in map
        		if (pt2 == null) {
        			pt2 = new ArrayList<LinkedList<Road>>();
        			pt2.add(new LinkedList<Road>());
        			pt2.add(new LinkedList<Road>());
        			map.put(road.point2, pt2);
        		}
        		// see point2 as the point with this road as the incoming edge
        		List<Road> in = pt2.get(1);
        		in.add(road);
        		
            	nextLine = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		return map;
	}
	
	
	
	/**
	 * find intersections
	 * @param map
	 * @return list of intersections
	 */
	private HashSet<GeographicPoint> getIntersections (HashMap<GeographicPoint, List<LinkedList<Road>>> map) {

		List<GeographicPoint> intersections = new LinkedList<GeographicPoint>();
		HashSet<GeographicPoint> intersectionsHash = new HashSet<GeographicPoint>();
		
		// iterate through each point
		for (GeographicPoint pt : map.keySet()) {
			
			boolean isInersection = true;
			List<LinkedList<Road>> allRoads = map.get(pt);
			LinkedList<Road> out = allRoads.get(0);
			LinkedList<Road> in = allRoads.get(1);
			
			// only one edge out and one edge in
			if (in.size() == 1 && out.size() == 1) {
				// if the edge out and the edge in are the same edge 
				// this is the a dead end
				if (in.get(0).sameName(out.get(0))){
					if (!(in.get(0).point1.equals(out.get(0).point2) 
							&& in.get(0).point2.equals(out.get(0).point1))){
						isInersection = false;
					}
				}
			}
			
			if (in.size() == 2 && out.size() == 2) {
				// If all the road segments have the same name, 
				// And there are two pairs of reversed nodes, then 
				// this is not an intersection because the roads pass
				// through.
				Road in1 = in.get(0);
				Road in2 = in.get(1);
				Road out1 = out.get(0);
				Road out2 = out.get(1);
				String name = in.get(0).roadName;
				boolean sameName = true;
				
				for (Road info : in) {
					if (!info.roadName.equals(name)) {
						sameName = false;
					}
				}
				for (Road info : out) {
					if (!info.roadName.equals(name)) {
						sameName = false;
					}
				}
				
				boolean passThrough = false;
				if ((in1.isReverse(out1) && in2.isReverse(out2)) ||
						(in1.isReverse(out2) && in2.isReverse(out1))) {
					
					passThrough = true;
				} 
				
				if (sameName && passThrough) {
					isInersection = false;
				} 
			}
			
			if (isInersection == true) {
				intersections.add(pt);
			}
		}
		
		// get rid of duplicate
		
		for (GeographicPoint point : intersections) {
			intersectionsHash.add(point);
		}
		
		return intersectionsHash;
	}

	
	@Override
	public void loadRoadMap(String filename, roadgraph.MapGraph map, 
			HashMap<GeographicPoint,HashSet<RoadSegment>> segments, 
			Set<GeographicPoint> intersectionsToLoad) {

        HashMap<GeographicPoint,List<LinkedList<Road>>> pointMap = 
        		buildMap(filename);
		
        // Add the nodes to the graph
        HashSet<GeographicPoint> intersections = getIntersections(pointMap);
		for (GeographicPoint pt : intersections) {
			map.addVertex(pt);
			if (intersectionsToLoad != null) {
				intersectionsToLoad.add(pt);
			}
			intersections.add(pt);
		}

		addEdgesAndSegments(intersections, pointMap, map, segments);
	}

	
	// Once you have built the pointMap and added the Nodes, 
	// add the edges and build the road segments if the segments
	// map is not null.
	private static void addEdgesAndSegments(Collection<GeographicPoint> nodes, 
			HashMap<GeographicPoint,List<LinkedList<Road>>> pointMap, MapGraph map, 
			HashMap<GeographicPoint,HashSet<RoadSegment>> segments) {
	
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
	
	
	private static List<GeographicPoint> findPointsOnEdge(HashMap<GeographicPoint,List<LinkedList<Road>>> pointMap,
		Road info, Collection<GeographicPoint> nodes)  {
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
}	