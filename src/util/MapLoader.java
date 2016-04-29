package util;

import java.io.BufferedReader;
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
 * A utility class that reads various kinds of files into different 
 * graph structures.
 */
public class MapLoader implements GraphLoader
{
	
	@Override
	public void createIntersectionsFile(String roadDataFile, String intersectionsFile)
	{
		Collection<GeographicPoint> nodes = new HashSet<GeographicPoint>();
        HashMap<GeographicPoint,List<LinkedList<RoadLineInfo>>> pointMap = 
        		buildPointMapOneWay(roadDataFile);
		
        // Print the intersections to the file
		List<GeographicPoint> intersections = findIntersections(pointMap);
		for (GeographicPoint pt : intersections) {
			nodes.add(pt);
		}

		try {
			PrintWriter writer = new PrintWriter(intersectionsFile, "UTF-8");

			// Now we need to add the edges
			// This is the tricky part
			for (GeographicPoint pt : nodes) {
				// Trace the node to its next node, building up the points 
				// on the edge as you go.
				List<LinkedList<RoadLineInfo>> inAndOut = pointMap.get(pt);
				LinkedList<RoadLineInfo> outgoing = inAndOut.get(0);
				for (RoadLineInfo info : outgoing) {
					HashSet<GeographicPoint> used = new HashSet<GeographicPoint>();
					used.add(pt);
				
					List<GeographicPoint> pointsOnEdge = 
							findPointsOnEdge(pointMap, info, nodes);
					GeographicPoint end = pointsOnEdge.remove(pointsOnEdge.size()-1);
					writer.println(pt + " " + end);
				}
				
			}
			writer.flush();
			writer.close();
		}
		catch (Exception e) {
			System.out.println("Exception opening intersections file " + e);
		}
	
	}

	
	@Override
	public void loadRoadMap(String filename, roadgraph.MapGraph map,  
			HashMap<GeographicPoint,HashSet<RoadSegment>> segments, 
			Set<GeographicPoint> intersectionsToLoad)
	{
		Collection<GeographicPoint> nodes = new HashSet<GeographicPoint>();
        HashMap<GeographicPoint,List<LinkedList<RoadLineInfo>>> pointMap = 
        		buildPointMapOneWay(filename);
		
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
			HashMap<GeographicPoint,List<LinkedList<RoadLineInfo>>> pointMap,
			MapGraph map, 
			HashMap<GeographicPoint,HashSet<RoadSegment>> segments)
	{
	
		// Now we need to add the edges
		// This is the tricky part
		for (GeographicPoint pt : nodes) {
			// Trace the node to its next node, building up the points 
			// on the edge as you go.
			List<LinkedList<RoadLineInfo>> inAndOut = pointMap.get(pt);
			LinkedList<RoadLineInfo> outgoing = inAndOut.get(0);
			for (RoadLineInfo info : outgoing) {
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
	findPointsOnEdge(HashMap<GeographicPoint,List<LinkedList<RoadLineInfo>>> pointMap,
		RoadLineInfo info, Collection<GeographicPoint> nodes) 
	{
		List<GeographicPoint> toReturn = new LinkedList<GeographicPoint>();
		GeographicPoint pt = info.point1;
		GeographicPoint end = info.point2;
		List<LinkedList<RoadLineInfo>> nextInAndOut = pointMap.get(end);
		LinkedList<RoadLineInfo> nextLines = nextInAndOut.get(0);
		while (!nodes.contains(end)) {
			toReturn.add(end);
			RoadLineInfo nextInfo = nextLines.get(0);
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


	
	// Find all the intersections.  Intersections are either dead ends 
	// (1 road in and 1 road out, which are the reverse of each other)
	// or intersections between two different roads, or where three
	// or more segments of the same road meet.
	private List<GeographicPoint> 
	findIntersections(HashMap<GeographicPoint,List<LinkedList<RoadLineInfo>>> pointMap) {
		// Now find the intersections.  These are roads that do not have
		// Exactly 1 or 2 roads coming in and out, where the roads in
		// match the roads out.
		List<GeographicPoint> intersections = new LinkedList<GeographicPoint>();
		for (GeographicPoint pt : pointMap.keySet()) {
			List<LinkedList<RoadLineInfo>> roadsInAndOut = pointMap.get(pt);
			LinkedList<RoadLineInfo> roadsOut = roadsInAndOut.get(0);
			LinkedList<RoadLineInfo> roadsIn = roadsInAndOut.get(1);
			
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
				for (RoadLineInfo info : roadsIn) {
					if (!info.roadName.equals(name)) {
						sameName = false;
					}
				}
				for (RoadLineInfo info : roadsOut) {
					if (!info.roadName.equals(name)) {
						sameName = false;
					}
				}
				
				RoadLineInfo in1 = roadsIn.get(0);
				RoadLineInfo in2 = roadsIn.get(1);
				RoadLineInfo out1 = roadsOut.get(0);
				RoadLineInfo out2 = roadsOut.get(1);
		
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
		
	// Build the map from points to lists of lists of lines.
	// The map returned is indexed by a GeographicPoint.  The values
	// are lists of length two where each entry in the list is a list.
	// The first list stores the outgoing roads while the second 
	// stores the outgoing roads.
	private HashMap<GeographicPoint, List<LinkedList<RoadLineInfo>>>
	buildPointMapOneWay(String filename)
	{
		BufferedReader reader = null;
        HashMap<GeographicPoint,List<LinkedList<RoadLineInfo>>> pointMap = 
        		new HashMap<GeographicPoint,List<LinkedList<RoadLineInfo>>>();
		try {
            String nextLine;
            reader = new BufferedReader(new FileReader(filename));
            // Read the lines out of the file and put them in a HashMap by points
            while ((nextLine = reader.readLine()) != null) {
            	RoadLineInfo line = splitInputString(nextLine);
            	addToPointsMapOneWay(line, pointMap);
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Problem loading dictionary file: " + filename);
            e.printStackTrace();
        }
		
		return pointMap;
	}


	// Add the next line read from the file to the points map.
	private static void 
	addToPointsMapOneWay(RoadLineInfo line,
						HashMap<GeographicPoint,List<LinkedList<RoadLineInfo>>> map)
	{
		List<LinkedList<RoadLineInfo>> pt1Infos = map.get(line.point1);
		if (pt1Infos == null) {
			pt1Infos = new ArrayList<LinkedList<RoadLineInfo>>();
			pt1Infos.add(new LinkedList<RoadLineInfo>());
			pt1Infos.add(new LinkedList<RoadLineInfo>());
			map.put(line.point1, pt1Infos);
		}
		List<RoadLineInfo> outgoing = pt1Infos.get(0);
		outgoing.add(line);
		
		List<LinkedList<RoadLineInfo>> pt2Infos = map.get(line.point2);
		if (pt2Infos == null) {
			pt2Infos = new ArrayList<LinkedList<RoadLineInfo>>();
			pt2Infos.add(new LinkedList<RoadLineInfo>());
			pt2Infos.add(new LinkedList<RoadLineInfo>());
			map.put(line.point2, pt2Infos);
		}
		List<RoadLineInfo> incoming = pt2Infos.get(1);
		incoming.add(line);
		
	}
	
	// Split the input string into the line information
	private RoadLineInfo splitInputString(String input)
	{	
		
		ArrayList<String> tokens = new ArrayList<String>();
		Pattern tokSplitter = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"");
		Matcher m = tokSplitter.matcher(input);
		
		while (m.find()) {
			if (m.group(1) != null) {
				tokens.add(m.group(1));	
			}
			else {
				tokens.add(m.group());
			}
		}

    	double lat1 = Double.parseDouble(tokens.get(0));
        double lon1 = Double.parseDouble(tokens.get(1));
        double lat2 = Double.parseDouble(tokens.get(2));
        double lon2 = Double.parseDouble(tokens.get(3));
        GeographicPoint p1 = new GeographicPoint(lat1, lon1);
        GeographicPoint p2 = new GeographicPoint(lat2, lon2);

        return new RoadLineInfo(p1, p2, tokens.get(4), tokens.get(5));
		
	}
}	
	


