package miniGoogleMap;

import java.io.*;

import gmapsfx.GoogleMapView;
import gmapsfx.MapComponentInitializedListener;
import gmapsfx.javascript.JavascriptArray;
import gmapsfx.javascript.object.GoogleMap;
import gmapsfx.javascript.object.LatLong;
import gmapsfx.javascript.object.MapOptions;
import gmapsfx.javascript.object.MapTypeIdEnum;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * the main app class, works as viewer.
 * 
 * @author xiaofandou, ziyu chen
 *
 */
public class Map extends Application
					 implements MapComponentInitializedListener {
	
	
	private GoogleMapView mapComponent;
	private GoogleMap map;
	private DataSet ds;
	
	
	/* GUI components */
	private Stage primaryStage;
	//=========level 1===========
	private ChoiceBox<String> choiceBox;
	private Button loadButton;
	//=========level 2===========
	private Label mapLabel;
	
	//=========level 3===========
	private Label startLabel;
	
	
	//=========level 4===========
	private Label destLabel;
	
	
	//=========level 5===========
	private static Label currPosLabel;
	
	//=========level 6===========
	private Button startButton;
	private Button destButton;
	
	//=========level 7===========
	private ChoiceBox<String> alg;
	
	//=========level 8===========
	private Button path;
	private Button visualize;
	private Button reset;
	
	
	//=========level 9============
	private TextField mapFile;
	private Button fetch;
	
	private JavascriptArray jsArray;
	

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		this.primaryStage = primaryStage;
		mapComponent = new GoogleMapView();
		mapComponent.addMapInitializedListener(this);

		
		/* Right Panel */
	    VBox right = new VBox();
	    
	    /* box of map file dropdown and load button */
	    HBox mapFiles = new HBox(6);
	    mapFiles.setAlignment(Pos.CENTER);
	    choiceBox = new ChoiceBox<>();
	    
	    /* read map files */
	    BufferedReader br = new BufferedReader(new FileReader("data/maps/mapfiles.list"));
	    try {
	    	String line = "";
	    	while((line = br.readLine()) != null) {
	    		choiceBox.getItems().add(line);
	    	}
	    } catch(Exception e) {
	    	
	    }
	    
	    choiceBox.setPrefWidth(200);
	    
	    loadButton = new Button("Load Map");
	    
	    mapFiles.getChildren().addAll(choiceBox, loadButton);
	    
	    
	    /* box showing current selected map */
	    HBox currMap = new HBox();
	    currMap.setPadding(new Insets(20, 0, 0, 0));
	    mapLabel = new Label("Current Map: No Map Selected");
	    mapLabel.setGraphic(new ImageView("http://rolold.f2fmedia.tv/wp-content/uploads/2014/05/google-maps-logo-icon-small.png"));
	    mapLabel.setTextFill(Color.BLUE);
	    currMap.getChildren().addAll(mapLabel);
	    
	    /* box for start */
	    HBox startBox = new HBox();
	    startBox.setPadding(new Insets(20, 0, 0, 0));
	    startLabel = new Label("Starting Point: No point selected");
	    startLabel.setTextFill(Color.DARKMAGENTA);
	    startLabel.setGraphic(new ImageView(DataSet.startURL));
	    startBox.getChildren().addAll(startLabel);
	    
	    /* box for dest. */
	    HBox destBox = new HBox();
	    destBox.setPadding(new Insets(20, 0, 0, 0));
	    destLabel = new Label("Destination: No point selected");
	    destLabel.setTextFill(Color.DARKMAGENTA);
	    destLabel.setGraphic(new ImageView(DataSet.destinationURL));
	    destBox.getChildren().addAll(destLabel);
	    
	    
	    /* box for currPos */
	    HBox currPosBox = new HBox();
	    currPosBox.setPadding(new Insets(20, 0, 0, 0));
	    currPosLabel = new Label("Current: No point selected");
	    currPosLabel.setTextFill(Color.GREEN);
	    currPosLabel.setGraphic(new ImageView(DataSet.SELECTED_URL));
	    currPosBox.getChildren().addAll(currPosLabel);
	    
	    
	    /* box for start and dest buttons */
	    HBox pointButtonBox = new HBox(30);
	    pointButtonBox.setAlignment(Pos.CENTER);
	    pointButtonBox.setPadding(new Insets(20, 0, 0, 0));
	    startButton = new Button("Choose as Start");
	    startButton.setDisable(true);	//disable the button at first
	    destButton = new Button("Choose as Dest");
	    destButton.setDisable(true);	//disable the button at first
	    pointButtonBox.getChildren().addAll(startButton, destButton);
	    
	    /* box for drop down menu */
	    HBox algBox = new HBox(10);
	    algBox.setPadding(new Insets(20, 0, 0, 0));
	    alg = new ChoiceBox<>();
	    alg.getItems().add("BFS");
	    alg.getItems().add("Dijkstra");
	    alg.getItems().add("A Star");
	    alg.setValue("Dijkstra");
	    algBox.getChildren().addAll(new Label("Choose search algorithm: "), alg);
	    
	    /* box for show path and visualization and reset */
	    HBox routeBox = new HBox(20);
	    path = new Button("Show Path");
	    path.setDisable(true);
	    visualize = new Button("Visualization");
	    
	    visualize.setDisable(true);
	    reset = new Button("Reset");
	    
	    routeBox.setPadding(new Insets(20, 0, 0, 0));
	    routeBox.setAlignment(Pos.CENTER);
	    routeBox.getChildren().addAll(path, visualize, reset);
	    
	    
	    /* box for fetch */
	    HBox fetchBox = new HBox(20);
	    fetchBox.setPadding(new Insets(30, 0, 0, 0));
	    fetchBox.setAlignment(Pos.CENTER);
	    
	    mapFile = new TextField();
	    fetch = new Button("Fetch Data");
	    fetchBox.getChildren().addAll(mapFile, fetch);
	    
	    
	    /* Add all the layouts to right box, in the order of levels */
	    Label chooseLabel = new Label("Choose a Map:");
	    chooseLabel.setTextFill(Color.DARKRED);
		right.getChildren().addAll(chooseLabel);
		right.getChildren().addAll(mapFiles, currMap);
		right.getChildren().addAll(startBox);
		right.getChildren().addAll(destBox);
		right.getChildren().addAll(currPosBox);
		right.getChildren().addAll(pointButtonBox);
		right.getChildren().addAll(algBox);
		right.getChildren().addAll(routeBox);
		right.getChildren().addAll(fetchBox);
		
		/* set size for the right box */
		right.setPadding(new Insets(20, 0, 0, 0));
		right.setPrefWidth(300);
		
		
		/* set overall layout */
		BorderPane bp = new BorderPane();
		Scene scene = new Scene(bp);
        bp.setCenter(mapComponent);
        bp.setRight(right);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Mini-Google Map");
        primaryStage.show();
	}
	
	/**
	 * initialize the map
	 */
    public void mapInitialized() {
    	LatLong center = new LatLong(39.9474533, -75.1876342);

    	MapOptions options = new MapOptions();
		options.center(center)
		       .mapMarker(false)
		       .mapType(MapTypeIdEnum.ROADMAP)
		       //maybe set false
		       .mapTypeControl(true)
		       .overviewMapControl(false)
		       .panControl(true)
		       .rotateControl(false)
		       .scaleControl(false)
		       .streetViewControl(false)
		       .zoom(18)
		       .zoomControl(true);

        map = mapComponent.createMap(options);
        
        setupJSAlerts(mapComponent.getWebView());
        
        /* Create a controller and attach button listeners for all buttons */

        new Controller(
				map, ds,
				choiceBox, alg,
				loadButton, startButton, destButton,
				path, visualize, reset, 
				mapLabel, startLabel, destLabel, currPosLabel,
				jsArray);
		
		
		
		
        /* create a fetcher for data fetch */
		new FetchController(map, mapFile, fetch, choiceBox);

    }
    
    public static void main(String[] args) {
    	launch(args);
    }
    
    // set up the alert window
    private void setupJSAlerts(WebView webView) {
        webView.getEngine().setOnAlert( e -> {
            Stage popup = new Stage();
            popup.initOwner(primaryStage);
            popup.initStyle(StageStyle.UTILITY);
            popup.initModality(Modality.WINDOW_MODAL);

            StackPane content = new StackPane();
            content.getChildren().setAll(
              new Label(e.getData())
            );
            content.setPrefSize(200, 100);

            popup.setScene(new Scene(content));
            popup.showAndWait();
        });
    }
    
    /**
     * set the content of label
     * @param label label to set
     * @param message the message to be set.
     */
    public static void setLabel(String label, String message) {
    	if("curr".equals(label)){
    		LabelManager.setLabelText(currPosLabel, message);
    	}
    }
}
