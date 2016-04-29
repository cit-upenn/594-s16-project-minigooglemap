package miniGoogleMap;


import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;


public class MapMaker {
	
	private float[] boundArr;
	private HashMap<Integer, Loc> nodes = new HashMap<Integer, Loc>();
	private String query;
    private String[] highwaysArr = {"motorway", "trunk", "primary", "secondary", 
    		"tertiary", "unclassified", "residential", "motorway_link", "trunk_link", 
    		"primary_link", "secondary_link", "tertiary_link", "living_street"};
    
	class Loc{
	    private double lat, lon;

	    public Loc(double lat, double lon) {
	        this.lat = lat;
	        this.lon = lon;
	    }

	    public String toString() {
	    	String s = "" + lat + " " + lon + " ";
	        return s;
	    }
	    /**
	     * @param boundArr[0]->boundArr[3]: south lat, west lon, north lat, east lon
	     */
	    public boolean outOfBounds(float[] bounds) {
	        return (lat < bounds[0] || lon < bounds[1] || lat > bounds[2] || lon > bounds[3]);
	    }
	}
	
    public MapMaker(float[] bounds) {
        this.boundArr = bounds;
        this.query = getQuery(boundArr);
    }
	
    public boolean parseData(String filename) {
        JsonObject data = getData();

        JsonArray elements = data.getJsonArray("elements");

        for (JsonObject elem : elements.getValuesAs(JsonObject.class)) {
            if (elem.getString("type").equals("node")) {
                nodes.put(elem.getInt("id"), new Loc(elem.getJsonNumber("lat").doubleValue(), elem.getJsonNumber("lon").doubleValue()));
            }
        }

        PrintWriter outfile;
        try {
            outfile = new PrintWriter(filename);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        for (JsonObject elem : elements.getValuesAs(JsonObject.class)) {
            if (elem.getString("type").equals("way")) {
                String street = elem.getJsonObject("tags").getString("name", "");
                String type = elem.getJsonObject("tags").getString("highway", "");
                String oneway = elem.getJsonObject("tags").getString("oneway", "no");
                List<JsonNumber> nodelist = elem.getJsonArray("nodes").getValuesAs(JsonNumber.class);
                for (int i = 0; i < nodelist.size() - 1; i++) {
                    Loc start = nodes.get(nodelist.get(i).intValue());
                    Loc end = nodes.get(nodelist.get(i + 1).intValue());
                    if (start.outOfBounds(boundArr) || end.outOfBounds(boundArr)) {
                        continue;
                    }

                    outfile.println("" + start + end + "\"" + street + "\" " + type);
                    if (oneway.equals("no")) {
                        outfile.println("" + end + start + "\"" + street + "\" " + type);
                    }
                }
            }
        }
        outfile.close();
        return true;
    }
    
    public JsonObject getData() {
        HttpURLConnection conn = null;
        try {
            URL url = new URL("http://overpass-api.de/api/interpreter");
            conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Charset", "utf-8;q=0.7,*;q=0.7");
            
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(this.query);
            wr.close();

            InputStream is = conn.getInputStream();
            JsonReader rdr = Json.createReader(is);
        
            return rdr.readObject();
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public String getQuery(float[] boundsArray) {
    	
        String query = "[out:json];(";
        String bounds = "(";
        for (int i = 0; i < 4; i++) {
            bounds += boundsArray[i];
            if (i < 3) {
                bounds += ",";
            } else {
                bounds += ")";
            }
        }

        for (String s : highwaysArr) {
            query += "way[\"highway\"=\"" + s + "\"]" + bounds + ";";
        }
        query += "); (._;>;); out;";
        return query;
    }
    
}
