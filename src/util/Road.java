package util;

import geography.GeographicPoint;

//A class to store information about the lines in the road files.
class Road {
	GeographicPoint point1, point2;
	String roadName, roadType;
	
	/** constructor for RoadLineInfo
	 * read from the file
	 * @param p1 One point
	 * @param p2 The other point
	 * @param roadName name of the road
	 * @param roadType type of the road
	 */
	public Road(GeographicPoint p1, GeographicPoint p2, String roadName, String roadType) 
	{
		this.point1 = p1;
		this.point2 = p2;
		this.roadName = roadName;
		this.roadType = roadType;
	}

	/**
	 * This method determine whether two RoadLineInfo obj are the same
	 */
	public boolean equals(Object o)
	{
		if (o == null) {
			return false;
		}
		if (!(o instanceof Road)) {
			return false;
		}
		Road info = (Road)o;
		return info.point1.equals(this.point1) && info.point2.equals(this.point2)  &&
				info.roadType.equals(this.roadType) && info.roadName.equals(this.roadName);
	}
	
	/** Get the other point */
	public GeographicPoint getOtherPoint(GeographicPoint pt) {
		if (pt == null || (!pt.equals(point1) && !pt.equals(point2))){
			throw new IllegalArgumentException();
		}
		return pt.equals(point1)? point2 : point1;
	}
	
	/** Return a reversed road */
	public Road getReverseCopy()
	{	
		Road reverse = new Road(this.point2, this.point1, this.roadName, this.roadType);
		return reverse;
	}
	
	
	/** Calculate the hashCode based on the hashCodes of the two GeographicPoint
	 * @return The hashcode for this object.
	 */
	public int hashCode()
	{	
		return point1.hashCode() + point2.hashCode();
	}
	
	/** Check if these segments are part of the same road 
	 * @param roadInfo The RoadLineInfo to be checked.
	 * @return true/false based on whether they are in the same road or not.
	 */
	public boolean sameRoad(Road roadInfo){
		return roadInfo.roadName.equals(this.roadName) && roadInfo.roadType.equals(this.roadType);
	}
	
	public boolean sameName(Road roadInfo){
		return roadInfo.roadName.equals(this.roadName);
	}
	

	public boolean isReverse(Road other) {
		return this.point1.equals(other.point2) && this.point2.equals(other.point1) &&
				this.roadName.equals(other.roadName) && this.roadType.equals(other.roadType);
	}
	
	/* print stuff */
	public String print() {
		return this.point1 + " " + this.point2 + " " + this.roadName + " " + this.roadType;
		
	}
	
}