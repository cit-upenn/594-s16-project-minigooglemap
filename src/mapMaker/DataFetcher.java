package mapMaker;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.json.*;


/**
 * 
 * This class is for fetching the geolocation and edges via overpass api.
 * @author xiaofandou
 *
 */
public class DataFetcher {
    private final String[] HIGHWAYS = {"motorway", "trunk", "primary", "secondary", "tertiary", "unclassified", "residential", "motorway_link", "trunk_link", "primary_link", "secondary_link", "tertiary_link", "living_street"};
    private String query;
    
    /**
     * constructor using bounds
     * @param bounds
     */
    public DataFetcher(float[] bounds) {
        this.query = this.constructQuery(bounds);
    }

    /**
     * setup connection to get data
     * @return the JsonObject representation of the data
     */
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

    /**
     * To generate the query
     * @param boundsArray the bounds Array
     * @return the generated query
     */
    public String constructQuery(float[] boundsArray) {
        String q = "[out:json];(";
        String bounds = "(";
        for (int i = 0; i < 4; i++) {
            bounds += boundsArray[i];
            if (i < 3) {
                bounds += ",";
            } else {
                bounds += ")";
            }
        }
        

        for (String s : HIGHWAYS) {
            q += "way[\"highway\"=\"" + s + "\"]" + bounds + ";";
        }

        q += "); (._;>;); out;";

        return q;
    }
    
}
