package geography;

import java.awt.geom.Point2D.Double;

/**
 * This class is to wrap up the vertex to have information
 * like longitude and latitude, and the calculation for 
 * distance using the longitude and latitude
 * @author xiaofandou, Qiannan
 *
 */
@SuppressWarnings("serial")
public class GeographicPoint extends Double {
	
	/**
	 * Constructor
	 * @param latitude latitude of the point in double
	 * @param longitude longitude of the point in double
	 */
	public GeographicPoint(double latitude, double longitude) {
		super(latitude, longitude);
	}
	
	/**
	 * Calculates the geographic distance in km between this point and 
	 * the other point. 
	 * @param other
	 * @return The distance between this lat, lon point and the other point
	 */
	public double distance(GeographicPoint other) {
		return getDist(this.getX(), this.getY(), other.getX(), other.getY());     
	}
	
    /**
     * Helper function for calculating the distance between two points using the
     * lat and lon of each points
     * @param lat1 latitude of point 1
     * @param lon1 longitude of point 1
     * @param lat2 latitude of point 2
     * @param lon2 longitude of point 2
     * @return the distance
     */
    private double getDist(double lat1, double lon1, double lat2, double lon2) {
    	int R = 6373; // radius of the earth in kilometres
    	double lat1rad = Math.toRadians(lat1);
    	double lat2rad = Math.toRadians(lat2);
    	double deltaLat = Math.toRadians(lat2-lat1);
    	double deltaLon = Math.toRadians(lon2-lon1);

    	double a = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) +
    	        Math.cos(lat1rad) * Math.cos(lat2rad) *
    	        Math.sin(deltaLon/2) * Math.sin(deltaLon/2);
    	double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

    	double d = R * c;
    	return d;
    }
    
    /**
     * override tostring() method for debugging
     */
    @Override
    public String toString() {
    	return "(" + getX() + ", " + getY() + ")";
    }
	
}
