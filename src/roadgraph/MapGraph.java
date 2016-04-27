package roadgraph;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
	
	/**
	 * Constructor of the class, initialize the fields
	 */
	public MapGraph() {
		vertexMap = new HashMap<>();
		edges = new HashSet<>();
	}
	
	/**
	 * Get the current size in terms of the node counts
	 * @return the node counts in the graph
	 */
	public int getNumVertices() {
		return vertexMap.size();
	}
	
	/**
	 * Get the current size of the graph in terms of the edges counts
	 * @return the edges count in the graph
	 */
	public int getNumEdges() {
		return edges.size();
	}
	
	/**
	 * Get all the vertices in the map
	 * @return the set view of all vertices in the graph
	 */
	public Set<GeographicPoint> getVertices() {
		return vertexMap.keySet();
	}
	
	/**
	 * Add vertex to this graph
	 * @param point the point to add
	 */
	public void addVertex(GeographicPoint point) {
		// error check
		if (vertexMap.containsKey(point)) {
			System.out.println("The point at " + point + " already exists!");
		} else {
			MapNode node = new MapNode(point);
			vertexMap.put(point, node);
		}
	}
	
	public void addEdge() {
		//TODO: how many args??
	}
	
	public List<GeographicPoint> bfs(GeographicPoint start, GeographicPoint end) {
		//TODO: implementation
		return null;
	}
	
	public List<GeographicPoint> dijkstra(GeographicPoint start, GeographicPoint end) {
		//TODO: implementation
		return null;
	}
	
	public List<GeographicPoint> aStarSearch(GeographicPoint start, GeographicPoint end) {
		//TODO: implementation
		return null;
	}
}
