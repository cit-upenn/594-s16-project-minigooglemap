package util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import geography.GeographicPoint;
import geography.RoadSegment;

public interface GraphLoader {
	
	/**	  
	 *  Read in a file specifying a map.
	 *
	 * The file contains data lines as follows:
	 * lat1 lon1 lat2 lon2 roadName roadType
	 * 
	 * where each line is a segment of a road
	 * These road segments are assumed to be ONE WAY.
	 * 
	 * This method will collapse the points so that only intersections 
	 * are represented as nodes in the graph.
	 * 
	 * @param filename The file containing the road data, in the format 
	 *   described.
	 * @param map The graph to load the map into.  The graph is
	 *   assumed to be directed.
	 */
	public void loadRoadMap(String filename, roadgraph.MapGraph map,  
			HashMap<GeographicPoint,HashSet<RoadSegment>> segments, 
			Set<GeographicPoint> intersectionsToLoad);
	
	/** 
	 * 	 * The file contains data lines as follows:
	 * lat1 lon1 lat2 lon2 roadName roadType
	 * 
	 * where each line is a segment of a road
	 * These road segments are assumed to be ONE WAY.
	 * 
	 * This method will collapse the points so that only intersections 
	 * are represented as nodes in the graph.
	 * 
	 * @param roadDataFile The file containing the road data, in the format 
	 *   described.
	 * @param intersectionsFile The output file containing the intersections.
	 */
	public void createIntersectionsFile(String roadDataFile, String intersectionsFile);
}
