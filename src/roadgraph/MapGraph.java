package roadgraph;


import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
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

	/**
	 * Extracted the sanity check for the shortest path search methods
	 * @param start the start point
	 * @param end the end point
	 * @return the checked and valid MapNode of start and end point
	 */
	private MapNode[] sanityCheck(GeographicPoint start, GeographicPoint end) {
		if (start == null) {
			throw new NullPointerException("invalid start point");
		}
		if (end == null) {
			throw new NullPointerException("invalid end point");
		}
		MapNode startNode = vertexMap.get(start);
		MapNode endNode = vertexMap.get(end);
		if (startNode == null) {
			throw new NullPointerException("start point beyond map scope");
		}
		if (endNode == null) {
			throw new NullPointerException("end point beyond map scope");
		}
		return new MapNode[]{startNode, endNode};
	}

	/**
	 * Helper function to generate path
	 * @param current the current MapNode
	 * @param startNode the start MapNode
	 * @param endNode the end MapNode
	 * @param prev the prev mapping
	 * @return the path in list
	 */
	private List<GeographicPoint> generatePath(MapNode current, MapNode startNode, MapNode endNode, Map<MapNode, MapNode> prev) {
		// initialize path
		LinkedList<GeographicPoint> path = new LinkedList<>();
		
		// if current map nodes are not connected, may have no path exist
		if (!current.equals(endNode)) {
			// may need some action from view side, here simply print to console and return null
			System.out.println("No available path found in the map from " + startNode.getLocation() + " to " + endNode.getLocation());
			return null;
		}

		// construct the path from end to start
		path.add(current.getLocation());
		while (!current.equals(startNode)) {
			MapNode prevNode = prev.get(current);
			path.addFirst(prevNode.getLocation());
			current = prevNode;
		}

		return path;
	}
	
	/**
	 * Search for shortest route using BFS
	 * @param start start location
	 * @param end end location
	 * @param nodeSearched list of node searched for visualization, add by visit time
	 * @return the list of node represent the shortest route
	 */
	public List<GeographicPoint> bfs(GeographicPoint start, GeographicPoint end, List<GeographicPoint> nodeSearched) {
		// sanity check
		MapNode[] checked = sanityCheck(start, end);
		MapNode startNode = checked[0];
		MapNode endNode = checked[1];

		// initialize variables
		Map<MapNode, MapNode> prev = new HashMap<>(); // also acts as visited
		Queue<MapNode> queue = new ArrayDeque<>();	
		queue.offer(startNode);
		MapNode current = null;

		while (!queue.isEmpty()) {
			current = queue.poll();
			nodeSearched.add(current.getLocation()); // for view side to realize visualization

			// exit condition
			if (current.equals(endNode)) {
				break;
			}

			Set<MapNode> neighbors = current.getNeighbors();
			for (MapNode neighbor : neighbors) {
				if (!prev.containsKey(neighbor)) {
					prev.put(neighbor, current);
					queue.offer(neighbor);
				}
			}
		}

		return generatePath(current, startNode, endNode, prev);
	}

	/**
	 * Search for shortest route using Dijkstra
	 * @param start start location
	 * @param end end location
	 * @param nodeSearched list of node searched for visualization, add by visit time
	 * @return the list of node represent the shortest route
	 */
	public List<GeographicPoint> dijkstra(GeographicPoint start, GeographicPoint end, List<GeographicPoint> nodeSearched) {
		// sanity check
		MapNode[] checked = sanityCheck(start, end);
		MapNode startNode = checked[0];
		MapNode endNode = checked[1];

		// initialize variables
		Map<MapNode, MapNode> prev = new HashMap<>();
		PriorityQueue<MapNode> queue = new PriorityQueue<>();	
		queue.offer(startNode);
		startNode.setShortest(0);
		MapNode current = null;

		while (!queue.isEmpty()) {
			current = queue.poll(); 				  // minimum in terms of shortest in the current queue
			double curShortest = current.getShortest();
			nodeSearched.add(current.getLocation());  // for view side to realize visualization

			// exit condition
			if (current.equals(endNode)) {
				break;
			}

			Set<MapEdge> edges = current.getEdges();
			for (MapEdge e : edges) {
				// add to priority queue & prev if first visit the node
				MapNode neighbor = e.getTail();
				if (!prev.containsKey(neighbor)) {
					prev.put(neighbor, current);
					queue.offer(neighbor);
				}

				// relax procedure
				double weight = e.getLength();
				double neighborShortest = neighbor.getShortest();
				if (neighborShortest > weight + curShortest) {
					neighbor.setShortest(weight + curShortest); // update shortest
					prev.put(neighbor, current); 				// update prev
				}
			}
		}

		return generatePath(current, startNode, endNode, prev);
	}

	/**
	 * Search for shortest route using A-Star search
	 * @param start start location
	 * @param end end location
	 * @param nodeSearched list of node searched for visualization, add by visit time
	 * @return the list of node represent the shortest route
	 */
	public List<GeographicPoint> aStarSearch(GeographicPoint start, GeographicPoint end, List<GeographicPoint> nodeSearched) {
		// sanity check
		MapNode[] checked = sanityCheck(start, end);
		MapNode startNode = checked[0];
		MapNode endNode = checked[1];

		// initialize variables
		Map<MapNode, MapNode> prev = new HashMap<>();
		PriorityQueue<MapNode> queue = new PriorityQueue<>();	
		queue.offer(startNode);
		startNode.setShortest(0);
		startNode.updateShortest(end);
		MapNode current = null;
		
		while (!queue.isEmpty()) {
			current = queue.poll(); 				  // minimum in terms of shortest in the current queue
			double curShortest = current.getShortest();
			double curHeuristic = current.getHeuristic();
			nodeSearched.add(current.getLocation());  // for view side to realize visualization

			// exit condition
			if (current.equals(endNode)) {
				break;
			}

			Set<MapEdge> edges = current.getEdges();
			for (MapEdge e : edges) {
				// add to priority queue & prev if first visit the node
				MapNode neighbor = e.getTail();
				if (!prev.containsKey(neighbor)) {
					prev.put(neighbor, current);
					queue.offer(neighbor);
				}

				// relax procedure
				double weight = e.getLength();
				double neighborShortest = neighbor.getShortest();
				if (neighborShortest > weight + curShortest - curHeuristic) {
					neighbor.setShortest(weight + curShortest); // update shortest
					neighbor.updateShortest(end);
					prev.put(neighbor, current); 				// update prev
				}
			}
		}

		return generatePath(current, startNode, endNode, prev);
	}
}