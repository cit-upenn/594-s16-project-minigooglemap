package roadgraph;

import geography.GeographicPoint;

import java.util.HashSet;
import java.util.Set;

public class MapNode {
	private Set<MapEdge> edges;
	private GeographicPoint location;
	private double distance;
	private double actualDistance;
	
	/**
	 * Constructor of the class
	 * @param location the location represented by the GeographicPoint
	 */
	MapNode(GeographicPoint location) {
		this.location = location;
		edges = new HashSet<MapEdge>();
		distance = 0.0;
		actualDistance = 0.0;
	}
	
	/**
	 * Getter of the location
	 * @return location of the node
	 */
	public GeographicPoint getLocation() {
		return this.location;
	}
	
	/**
	 * Getter of the edges
	 * @return set view of all edges of the node
	 */
	public Set<MapEdge> getEdges() {
		return this.edges;
	}
	
	/**
	 * Getter of predicted distance
	 * @return the distance
	 */
	public double getDistance() {
		return this.distance;
	}
	
	/**
	 * Setter of the predicted distance
	 * @param distance the predicted distance
	 */
	public void setDistance(double distance) {
	    this.distance = distance;
	}

	/**
	 * Getter of the actual distance
	 * @return the actual distance
	 */
	public double getActualDistance() {
		return this.actualDistance;
	}
	
	/**
	 * Setter of the actual distance
	 * @param actualDistance the actual distance
	 */
	public void setActualDistance(double actualDistance) {
	    this.actualDistance = actualDistance;
	}
	
	/**
	 * Find all neighbors of current node
	 * @return set view of current node's neighbors
	 */
	public Set<MapNode> getNeighbors() {
		return null;
	}
	
	/**
	 * Add edge to current node
	 * @param edge
	 */
	public void addEdge(MapEdge edge) {
		
	}
	
	
	/**
	 * Override the equals method to compare the location of two nodes only
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MapNode) || o == null) {
			return false;
		}
		
		MapNode thatNode = (MapNode) o;
		return this.location.equals(thatNode.location);
	}
	

	/**
	 * Override the hashCode method for comparison
	 * @return the hashCode of the node
	 */
	@Override
	public int hashCode() {
		return this.location.hashCode();
	}
	
	/**
	 * Override the toString method for debugging
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("node location: ").append(this.location.toString()).append("\n");
		sb.append("at the crossing: ");
		for (MapEdge e : edges) {
			sb.append(e.getRoadName()).append(", ");
		}
		return sb.toString();
	}
	
}
