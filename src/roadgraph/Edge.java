package roadgraph;

import java.math.BigDecimal;

import geography.GeographicPoint;

/**
 * Edges
 * @author xiaofandou
 *
 */
public class Edge{
	private GeographicPoint from; 
	private GeographicPoint to;
	private String roadName;
	private String roadType;
	private double length;
	private MapGraph m;
	
	Edge(GeographicPoint from, GeographicPoint to, String roadName,
			String roadType, double length, MapGraph m){
		this.from = from;
		this.to = to;
		this.roadName = roadName;
		this.roadType = roadType;
		this.length = length;
		this.m = m;
	}
	
	public void setMapGraph(MapGraph m){
		this.m = m;
	}
	
	public MapGraph mapGraph(){
		return m;
	}

	public GeographicPoint from(){
		return new GeographicPoint(from.getX(), from.getY());
	}
	
	public GeographicPoint to(){
		return new GeographicPoint(to.getX(), to.getY());
	}
	
	public String roadName(){
		return new String(roadName);
	}
	
	public String roadType(){
		return new String(roadType);
	}
	
	public double length(){
		return this.length;
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof Edge)){ return false; }
		Edge e = (Edge)o;
		
		BigDecimal l = new BigDecimal(this.length);
		BigDecimal el = new BigDecimal(e.length);
		return (from.equals(e.from()) &&
				to.equals(e.to()) &&
				this.roadName.equals(e.roadName()) &&
				this.roadType.equals(e.roadType()) &&
				l.compareTo(el) == 0);
	}
	
	public String toString(){
		return "[" + from + "--" + length + "-->" + to + "]";
	}

}
