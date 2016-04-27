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
	
	/**
	 * Add edge to this graph
	 * @param head the head of the directed edge
	 * @param tail the tail of the directed edge
	 * @param roadName road name of the edge
	 * @param roadType road type of the edge
	 * @param length length of the edge
	 */
	public void addEdge(GeographicPoint head, GeographicPoint tail, String roadName, String roadType, double length) {
		MapNode headNode = vertexMap.get(head);
		MapNode tailNode = vertexMap.get(tail);
		if (headNode == null) {
			throw new NullPointerException("headnode is not in the graph");
		}
		if (tailNode == null) {
			throw new NullPointerException("tailnode is not in the graph");
		}
		MapEdge newEdge = new MapEdge(headNode, tailNode, roadName, roadType, length);
		edges.add(newEdge);
		headNode.addEdge(newEdge);
	}
	
	// Note: Every time a vertex is explored, append it to nodeSearched.
	public List<GeographicPoint> bfs(GeographicPoint start, GeographicPoint end, List<GeographicPoint> nodeSearched) {
		//TODO: implementation
		return null;
	}
	
	public List<GeographicPoint> dijkstra(GeographicPoint start, GeographicPoint end, List<GeographicPoint> nodeSearched) {
		//TODO: implementation
		return null;
	}
	
	public List<GeographicPoint> aStarSearch(GeographicPoint start, GeographicPoint end, List<GeographicPoint> nodeSearched) {
		//TODO: implementation
		return null;
	}
}