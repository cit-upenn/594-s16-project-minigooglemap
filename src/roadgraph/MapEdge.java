package roadgraph;

/**
 * The class represent the edge in the roadgraph
 * @author Qiannan
 *
 */
public class MapEdge {
		
	private String roadName;
	private String roadType; // from data source
	private MapNode head;
	private MapNode tail;
	private double length;
	
	/**
	 * Constructor
	 * @param roadName name of the road
	 * @param roadType type of the road provided by API
	 * @param head head of the directed edge
	 * @param tail tail of the directed edge
	 * @param length length of the edge
	 */
	public MapEdge(MapNode head, MapNode tail, String roadName, String roadType, double length) {
		this.roadName = roadName;
		this.roadType = roadType;
		this.head = head;
		this.tail = tail;
		this.length = length;
	}
	
	/**
	 * Getter of the head node
	 * @return the head node
	 */
	public MapNode getHead() {
		return this.head;
	}
	
	/**
	 * Getter of the tail node
	 * @return the tail node
	 */
	public MapNode getTail() {
		return this.tail;
	}
	
	/**
	 * Getter of the length of the edge
	 * @return the length of the edge
	 */
	public double getLength() {
		return this.length;
	}
	
	/**
	 * Getter of the roadName
	 * @return the road name of the edge
	 */
	public String getRoadName() {
		return this.roadName;
	}
	
	/**
	 * Getter of the roadtype
	 * @return the road type of the edge
	 */
	public String getRoadType() {
		return this.roadType;
	}
}
