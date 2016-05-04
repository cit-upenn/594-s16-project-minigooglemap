package miniGoogleMap;

import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

import gmapsfx.javascript.object.GoogleMap;
import gmapsfx.javascript.object.LatLong;
import gmapsfx.javascript.object.LatLongBounds;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import mapMaker.MapMaker;

/**
 * Controller specially for fetching data.
 * @author xiaofandou
 *
 */
public class FetchController {
	
	private GoogleMap map;

	private TextField mapFile;
	private Button fetch;
	private ChoiceBox<String> choiceBox;
	
	private static final String DATA_FILE_PATTERN = "[\\w_]+.map";
    private static final String DATA_FILE_DIR_STR = "data/maps/";
	
	public FetchController(GoogleMap map, 
			TextField mapFile, Button fetch, ChoiceBox<String> choiceBox) {
		this.map = map;
		this.mapFile = mapFile;
		this.fetch = fetch;
		this.choiceBox = choiceBox;
		
		setUpComponents();
	}
	
	private void setUpComponents() {
		fetch.setOnAction(e -> {
			System.out.println("fetch button pressed!");
			String fName = mapFile.getText();
			
			// check for valid file name ___.map or mapfiles/___.map
    		if((checkDataFileName(fName)) != null) {
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
                			runFetchTask(checkDataFileName(fName), fetch);
                		}
                	});
                } else {
                	runFetchTask(checkDataFileName(fName), fetch);
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
	
	/**
	 * helper function for data fetching
	 * @param fName the file to write
	 * @param button 
	 */
	private void runFetchTask(String fName, Button button) {
        float[] arr = getBoundsArray();

    	Task<String> task = new Task<String>() {
            @Override
        	public String call() {
        		if(writeDataToFile(fName, arr)) {
                    return fName;
        		}

        		return "z" + fName;

            }
        };

        //Alert fetchingAlert = MapApp.getInfoAlert("Loading : ", "Fetching data for current map area...");
        AlertBox.display("Loading : ", "Fetching data for current map area...");
        task.setOnSucceeded( e -> {
        	
        	try {  
                FileWriter writer = new FileWriter("data/maps/mapfiles.list", true);  
                writer.write("\n" + fName.substring(10));  
                writer.close();  
            } catch (IOException excep) {  
                excep.printStackTrace();  
            }  
        	
        	
        	AlertBox.display("Fetch completed : ", "Data set : \"" + fName + "\" written to file!");

        	choiceBox.getItems().add(fName.substring(10));
            button.setDisable(false);

        });


        task.setOnFailed( e -> {
        	AlertBox.display("Fetch failed : ", "Failed to load the map");
        });

        task.setOnRunning(e -> {
            button.setDisable(true);
        });


        Thread fetchThread = new Thread(task);
        fetchThread.start();
    }
	
	/**
	 * writes geographic data flat file parameters arr contains the coordinates of the bounds for the map region
	 * @param filename
	 * @param arr
	 * @return boolean indicating if the file needs to re-parse this time (if new file, return true)
	 */
    public boolean writeDataToFile(String filename, float[] arr) {
     	MapMaker mm = new MapMaker(arr);

    	// parse data and write to filename
    	if(mm.parseData(filename)) {
            return true;
    	}

        return false;
    }

	
	/**
     * Check if file name matches pattern [filename].map
     *
     * @param str - path to check
     * @return string to use as path
     */
    public String checkDataFileName(String str) {
    	if(Pattern.matches(DATA_FILE_PATTERN, str)) {
            return DATA_FILE_DIR_STR + str;
    	}
    	return null;
    }
    
    /**
     * check boundary size
     * @param limit
     * @return
     */
    private boolean checkBoundsSize(double limit) {
    	if (boundsSize() > limit) {
    		return false;
    	}
    	return true;
    }
    
    /**
     * get boundary size of current map
     * @return
     */
    private float boundsSize() {
    	float[] bounds = getBoundsArray();
    	System.out.println((bounds[2] - bounds[0]) * (bounds[3] - bounds[1]));
    	return (bounds[2] - bounds[0]) * (bounds[3] - bounds[1]);
    }
    
    /**
     * gets current bounds of map view
     * @return
     */
    private float[] getBoundsArray() {
        LatLong sw, ne;
    	LatLongBounds bounds = map.getBounds();

    	sw = bounds.getSouthWest();
    	ne = bounds.getNorthEast();
    	
    	// [S, W, N, E]
    	return new float[] {(float) sw.getLatitude(), (float) sw.getLongitude(),
    			            (float) ne.getLatitude(), (float) ne.getLongitude()};
    }

}
