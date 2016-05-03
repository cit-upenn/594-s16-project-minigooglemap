package miniGoogleMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sun.media.jfxmedia.events.MarkerEvent;

import geography.GeographicPoint;
import geography.RoadSegment;
import gmapsfx.javascript.event.UIEventType;
import gmapsfx.javascript.object.GoogleMap;
import gmapsfx.javascript.object.InfoWindow;
import gmapsfx.javascript.object.InfoWindowOptions;
import gmapsfx.javascript.object.LatLong;
import gmapsfx.javascript.object.LatLongBounds;
import gmapsfx.javascript.object.MVCArray;
import gmapsfx.javascript.object.Marker;
import gmapsfx.javascript.object.MarkerOptions;
import gmapsfx.shapes.Polyline;
import netscape.javascript.JSObject;
import roadgraph.MapGraph;
import util.MapLoader;

/**
 * Class to wrap the graph, .map file map, and other information about
 * the map data sets used
 *
 * @author Xiaofan
 *
 */
public class DataSet {
	
	private String filePath;
	
	private String fileName;
	
	private roadgraph.MapGraph graph;
	private MapLoader mapLoader;
	private Set<GeographicPoint> intersections;
    private HashMap<geography.GeographicPoint,HashSet<geography.RoadSegment>>  roads;
	//private boolean currentlyDisplayed;
	
	private HashMap<geography.GeographicPoint, Marker> markers;
	
	private GoogleMap map;

	
	private GeographicPoint currPos;
	private GeographicPoint start, dest;
	
	private InfoWindow posInfoWindow;

	private Polyline path;
	private List<LatLong> exploredNodes;
	private HashMap<String, LatLongBounds> bounds;
	protected static String startURL = "http://maps.google.com/mapfiles/kml/pal3/icon40.png";
    protected static String destinationURL = "http://maps.google.com/mapfiles/kml/pal2/icon5.png";
    protected static String SELECTED_URL = "https://maps.gstatic.com/mapfiles/ms2/micons/red-dot.png";
    protected static String markerURL = "http://maps.google.com/mapfiles/kml/paddle/blu-diamond-lv.png";
	protected static String visURL = "http://maps.google.com/mapfiles/kml/paddle/red-diamond-lv.png";
    
	
	/**
	 * Constructor.
	 * @param fileName the map file to load.
	 * @param map map object
	 */
	public DataSet (String fileName, GoogleMap map) {
        this.filePath = "data/maps/";
        this.fileName = fileName;
        graph = null;
        roads = null;
        //currentlyDisplayed = false;
        
        markers = new HashMap<>();
        
        this.map = map;
        
        mapLoader = new MapLoader();
        
        exploredNodes = new ArrayList<>();
        
        path = new Polyline();
        
        bounds = new HashMap<>();
	}

	/**
	 * set the graph model behind the scene.
	 * @param graph
	 */
    public void setGraph(roadgraph.MapGraph graph) {
    	this.graph = graph;
    }
    
    /**
     * set the road
     * @param roads roads
     */
    public void setRoads(HashMap<geography.GeographicPoint,HashSet<geography.RoadSegment>>  roads) { this.roads = roads; }
    
    
    //public roadgraph.MapGraph getGraph(){ return graph; }
    
    /** Return the intersections in this graph.
     * In order to keep it consistent, if getVertices in the graph returns something 
     * other than null (i.e. it's been implemented) we get the vertices from 
     * the graph itself.  But if the graph hasn't been implemented, we return 
     * the set of intersections we separately maintain specifically for this purpose.
     * @return The set of road intersections (vertices in the graph)
     */
    public Set<GeographicPoint> getIntersections() {
    	Set<GeographicPoint> intersectionsFromGraph = graph.getVertices();
    	if (intersectionsFromGraph == null) {
    		return intersections;
    	}
    	else {
    		return intersectionsFromGraph;
    	}
    }
    
    /**
     * road getter.
     * @return roads information
     */
    public HashMap<geography.GeographicPoint,HashSet<geography.RoadSegment>>  getRoads() { return this.roads; }

    /**
     * load the map
     */
    public void initializeGraph() {
        graph = new roadgraph.MapGraph();
        roads = new HashMap<geography.GeographicPoint, HashSet<geography.RoadSegment>>();
        intersections = new HashSet<GeographicPoint>();

    	mapLoader.loadRoadMap(filePath + fileName, graph, roads, intersections);
    	
    	LatLongBounds mapBound = new LatLongBounds();
    	for(GeographicPoint pos: graph.getVertices()) {
    		mapBound.extend(new LatLong(pos.getX(), pos.getY()));
    	}
    	bounds.put("mapBound", mapBound);
    	
    	System.out.println(roads == null);
    	LatLong center = null;
    	
    	/* put the markers into dataset */
    	for(GeographicPoint pos: intersections) {
    		
    		if(center == null) {
    			center = new LatLong(pos.getX(), pos.getY());
    		}
    		
    		Marker marker = new Marker(new MarkerOptions()
    				.animation(null)
   				 	.icon(markerURL)
   				 	.position(new LatLong(pos.getX(), pos.getY()))
                    .title(null)
                    .visible(true));
    		
    		markers.put(pos, marker);
    		
    		map.addMarker(marker);
    		
    		map.addUIEventHandler(marker, UIEventType.click, (JSObject o) -> {
                /* set information window */
                if(posInfoWindow != null) posInfoWindow.close();
                
                posInfoWindow = new InfoWindow(new InfoWindowOptions()
                		.content("<strong>Current Position:</strong> ("
                				+ pos.getX() + ", " + pos.getY() + ")"));
                
                posInfoWindow.open(map, marker);
                
                /* set Icon */
                resetMarker();

                marker.setIcon(SELECTED_URL);
                
                /* set current position */
                currPos = pos;
                Map.setLabel("curr", "Current: " + currPos);

            });
    	}

    	map.fitBounds(bounds.get("mapBound"));
    }
    
    /**
     * file name getter
     * @return the file name of map
     */
	public String getFileName() {
		return this.fileName;
	}

	/**
	 * get all the points of a road
	 * @return all the points of roads.
	 */
    public Object[] getPoints() {
    	Set<geography.GeographicPoint> pointSet = roads.keySet();
    	return pointSet.toArray();
    }
//    
//    
//    public boolean isDisplayed() {
//    	return this.currentlyDisplayed;
//    }
//
//    public void setDisplayed(boolean value) {
//    	this.currentlyDisplayed = value;
//    }
    
    /**
     * resets the markers
     */
    public void resetMarker() {
    	for(GeographicPoint pos: markers.keySet()) {
    		
    		markers.get(pos).setVisible(true);
    		if(pos.equals(start) || pos.equals(dest)) {
    			continue;
    		}
    		
    		markers.get(pos).setIcon(markerURL);
    	}
    }
    
    /**
     * current point getter
     * @return current geographic point
     */
    public GeographicPoint currPos() {
    	return currPos;
    }
    
    /**
     * starting point getter
     * @return starting point
     */
    public GeographicPoint startPoint() {
    	return start;
    }
    
    /**
     * set starting point to be current point selected.
     */
    public void setStart() {
    	
    	start = currPos;
    	
    	/* set chosen point to be start marker */
    	resetMarker();
    	markers.get(start).setIcon(startURL);
    	
    	/* modifies the window */
    	if(posInfoWindow != null) posInfoWindow.close();
        
        posInfoWindow = new InfoWindow(new InfoWindowOptions()
        		.content("<strong>Starting Point:</strong>" + start));
        
        posInfoWindow.open(map, markers.get(start));
        
    	System.out.println("Starting point is set to be: " + start);
    }
    
    /**
     * destination getter
     * @return current destination
     */
    public GeographicPoint dest() {
    	return dest;
    }
    
    /**
     * set destination to be current selected point.
     */
    public void setDest() {

    	dest = currPos;
    	
    	/* set chosen point to be start marker */
    	resetMarker();
    	markers.get(dest).setIcon(destinationURL);
    	
    	/* modifies the window */
    	if(posInfoWindow != null) posInfoWindow.close();
        
        posInfoWindow = new InfoWindow(new InfoWindowOptions()
        		.content("<strong>Destination:</strong>" + dest));
        
        posInfoWindow.open(map, markers.get(dest));
        
    	
    	System.out.println("Destination set to be: " + dest);
    }
    
    /**
     * reset the variables.
     */
    public void reset() {
    	/* close info window */
    	if(posInfoWindow != null) posInfoWindow.close();
    	
    	/* reset current position */
    	currPos = null;
    	Map.setLabel("curr", "Current: No point selected");
    	
    	/* reset starting point and destination */
    	start = null;
    	dest = null;
    	
    	for(GeographicPoint pos: markers.keySet()) {
    		markers.get(pos).setIcon(markerURL);
    		markers.get(pos).setVisible(true);
    	}
    	
    	
    	for(GeographicPoint pos: markers.keySet()) {
    		markers.get(pos).setVisible(false);
    		map.removeMarker(markers.get(pos));
    	}
    
    		
    	if(path != null) {
    		map.removeMapShape(path);
    	}
    }
    
    /**
     * graph getter
     * @return graph model
     */
    public MapGraph map() {
    	return graph;
    }
    
    /**
     * close information window
     */
    public void closeInfoWindow() {
    	if(posInfoWindow != null) posInfoWindow.close();
    }
    
    /**
     * set the marker
     * @param pos marker position
     * @param url marker icon
     */
    public void setMarker(GeographicPoint pos, String url) {
    	markers.get(pos).setIcon(url);
    }
    
    /**
     * set path from starting point to destination
     * @param partialPath
     */
    public void setPath(List<GeographicPoint> partialPath) {
    	List<LatLong> completePath = constructMapPath(partialPath);
    	MVCArray pathArray = new MVCArray();
		
    	LatLongBounds pathBound = new LatLongBounds();
		for(int i = 0; i < completePath.size(); i++) {
			pathBound.extend(completePath.get(i));
			pathArray.push(completePath.get(i));
		}
		bounds.put("pathBound", pathBound);
		path.setPath(pathArray);
    }
    
    /**
     * the path to be displayed
     * @return the path
     */
    public Polyline path() {
    	return path;
    }
    
    /**
     * get the boundary of a set of points
     * @param bound the type of boundary
     * @return the boundary
     */
    public LatLongBounds getBound(String bound) {
    	return bounds.get(bound);
    }
    
    /**
     * set the node set used for 
     * @param nodes
     */
    public void setExploredNodes(List<GeographicPoint> nodes) {
    	LatLongBounds visualBound = new LatLongBounds();
    	for(int i = 0; i < nodes.size(); i++) {
    		exploredNodes.add(new LatLong(nodes.get(i).getX(), nodes.get(i).getY()));
    		visualBound.extend(new LatLong(nodes.get(i).getX(), nodes.get(i).getY()));
    	}
    	bounds.put("visualBound", visualBound);
    }
    
    /**
     * explored Nodes getter
     * @return a set of explored nodes.
     */
    public List<LatLong> exploredNodes() {
    	return exploredNodes; 
    }
    
    /**
     * set visibility of unselected markers
     * @param visible visibility
     */
    public void setUnselectedMarkers(boolean visible) {
    	for(GeographicPoint pos: markers.keySet()) {
    		if(pos.equals(start) || pos.equals(dest)) {
    			continue;
    		}
    		
    		markers.get(pos).setVisible(visible);
    	}
    }
    
    
    
    /**
     * Construct path including road regments
     * @param path - path with only intersections
     * @return list of LatLongs corresponding the path of route
     */
    private List<LatLong> constructMapPath(List<geography.GeographicPoint> path) {
    	List<LatLong> retVal = new ArrayList<LatLong>();
        List<geography.GeographicPoint> segmentList = null;
    	geography.GeographicPoint curr;
    	geography.GeographicPoint next;

    	geography.RoadSegment chosenSegment = null;;

        for(int i = 0; i < path.size() - 1; i++) {
            double minLength = Double.MAX_VALUE;
        	curr = path.get(i);
        	next = path.get(i+1);

        	if(getRoads().containsKey(curr)) {
        		HashSet<geography.RoadSegment> segments = getRoads().get(curr);
        		Iterator<geography.RoadSegment> it = segments.iterator();

        		// get segments which are
            	geography.RoadSegment currSegment;
                while(it.hasNext()) {
                	currSegment = it.next();
                	if(currSegment.getOtherPoint(curr).equals(next)) {
                		if(currSegment.getLength() < minLength) {
                			chosenSegment = currSegment;
                		}
                	}
                }

                if(chosenSegment != null) {
                    segmentList = chosenSegment.getPoints(curr, next);
                    for(geography.GeographicPoint point : segmentList) {
                        retVal.add(new LatLong(point.getX(), point.getY()));
                    }
                }
                else {
                	System.err.println("ERROR in constructMapPath : chosenSegment was null");
                }
        	}
        }
    	return retVal;
    }


}