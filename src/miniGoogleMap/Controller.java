package miniGoogleMap;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

import java.util.regex.Pattern;

import gmapsfx.GoogleMapView;
import gmapsfx.javascript.object.GoogleMap;
import gmapsfx.javascript.object.LatLong;
import gmapsfx.javascript.object.LatLongBounds;


public class Controller {
	
	private GoogleMap map;
	
    private Button fetchButton;
    private TextField fetchText;
    
    private static String PATTERN = "[\\w_]+.map";
    private static String DIR = "data/maps/";
    
    public Controller(Button fetchButton){
    	this.fetchButton = fetchButton;
    }
	
    /**
     * Registers event to Fetch Data
     */
    private void setFetchButton() {
    	fetchButton.setOnAction(e -> {
    		String fName = fetchText.getText();		
    		// check for valid file name: example.map
    		if((checkFileName(fName)) != null) {
    			if (!checkBoundsSize(.1)) {
    				Alert alert = new Alert(AlertType.ERROR);
        			alert.setTitle("Size Error");
        			alert.setHeaderText("Map Size Error");
        			alert.setContentText("Map boundaries are too large.");
        			alert.showAndWait();
    			} else if (!checkBoundsSize(0.02)) {
                	Alert warning = new Alert(AlertType.CONFIRMATION);
                	warning.setTitle("Size Warning");
                	warning.setHeaderText("Map Size Warning");
                	warning.setContentText("Your map file may take a long time to download,\nand your computer may crash when you try to\nload the intersections. Continue?");
                	warning.showAndWait().ifPresent(response -> {
                		if (response == ButtonType.OK) {
                			fetch();
                		}
                	});
                } else {
                	fetch();
                }

    		}
    		else {
    		    Alert alert = new Alert(AlertType.ERROR);
    			alert.setTitle("Filename Error");
    			alert.setHeaderText("Input Error");
    			alert.setContentText("Check filename input. \n\n\n"
    								 + "Filename must match format : [filename].map."
    								 + "\n\nUse only uppercase and lowercase letters,\nnumbers, and underscores in [filename].");

    			alert.showAndWait();
    		}
    	});
    }
    
    
    public String checkFileName(String filename) {
    	if(Pattern.matches(PATTERN, filename)) {
            return DIR + filename;
    	}
    	return null;
    }
    
    // gets current bounds of map view
    public float[] getBoundsArray() {
    	
        LatLong sw, ne;
    	LatLongBounds bounds = map.getBounds();
    	
    	sw = bounds.getSouthWest();
    	ne = bounds.getNorthEast();
    	
    	float[] boundsArr = {(float) sw.getLatitude(),(float) sw.getLongitude(),
    			(float) ne.getLatitude(),(float) ne.getLongitude()}; // south, west, north, east
    	
    	return boundsArr;
    }
    
    public float boundsSize() {
    	float[] boundsArr = getBoundsArray();
    	return (boundsArr[2] - boundsArr[0]) * (boundsArr[3] - boundsArr[1]);
    }
    
    public boolean checkBoundsSize(double limit) {
    	if (boundsSize() > limit) {
    		return false;
    	}
    	return true;
    }
    
    
    public void fetch() {
    	// TO-DO
    }
    
    
}
