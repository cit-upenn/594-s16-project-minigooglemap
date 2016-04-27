package roadgraph;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import geography.GeographicPoint;

/**
 * The class that represent the graph of the study area.
 * In the graph, the nodes are the intersections of the roads, and
 * the edges are the roads
 * @author Qiannan
 *
 */
public class MapGraph {
	
	private Map<GeographicPoint, MapNode> vertexMap;
	private Set<MapEdge> edges;
	
	public MapGraph() {
		vertexMap = new HashMap<>();
		edges = new HashSet<>();
	}
}
