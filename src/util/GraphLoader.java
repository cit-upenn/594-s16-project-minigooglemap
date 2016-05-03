package util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import geography.GeographicPoint;
import geography.RoadSegment;

/**
 * This interface provide two main methods that deals with .map file
 * including a method that loads a road map into a directed graph
 * and a method that get all the intersections of a road map
 * @author ZiyuChen
 */
public interface GraphLoader {
	
	/**	  
	 * Read in a .map file containing road data
	 * each line is a segment of a one-way road with format as follow:
	 * lat1 lon1 lat2 lon2 roadName roadType
	 *
	 * This method will collapse the points so that only intersections 
	 * are represented as nodes in the graph.
	 * 
	 * @param filename The file containing the road data
	 * @param map The directed graph to load the map into.
	 */
	public void loadRoadMap(String filename, roadgraph.MapGraph map,  
			HashMap<GeographicPoint,HashSet<RoadSegment>> segments, 
			Set<GeographicPoint> intersectionsToLoad);
	
	/** 
	 * Read in a .map file containing road data
	 * each line is a segment of a one-way road with format as follow:
	 * lat1 lon1 lat2 lon2 roadName roadType
	 * 
	 * This method will process all the points such that only intersections 
	 * will represented as nodes in the graph.
	 * 
	 * @param roadDataFile The file containing the road data
	 * @param intersectionsFile The output file containing the intersections.
	 */
	public void createIntersectionsFile(String roadDataFile, String intersectionsFile);
	
}
