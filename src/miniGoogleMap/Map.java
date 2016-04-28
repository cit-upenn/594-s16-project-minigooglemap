	package miniGoogleMap;

import geography.GeographicPoint;
import gmapsfx.GoogleMapView;
import gmapsfx.MapComponentInitializedListener;
import gmapsfx.javascript.object.GoogleMap;
import gmapsfx.javascript.object.LatLong;
import gmapsfx.javascript.object.MVCArray;
import gmapsfx.javascript.object.MapOptions;
import gmapsfx.javascript.object.MapTypeIdEnum;
import gmapsfx.javascript.object.Marker;
import gmapsfx.javascript.object.MarkerOptions;
import gmapsfx.shapes.Polyline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Main class for mini-google map 
 * @author xiaofandou
 *
 */
public class Map extends Application
					 implements MapComponentInitializedListener {
	//zxczzcxcxz
	
	protected GoogleMapView mapComponent;
	protected GoogleMap map;
	protected DataSet ds;
		
	/* GUI components */
	protected Stage primaryStage;
	//=========level 1===========
	ChoiceBox<String> choiceBox;
	protected Button loadButton;
	//=========level 2===========
	Label mapLabel;
	
	//=========level 3===========
	Label startLabel;
	
	
	//=========level 4===========
	Label destLabel;
	
	
	//=========level 5===========
	static Label currPosLabel;
	

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		this.primaryStage = primaryStage;
		mapComponent = new GoogleMapView();
		mapComponent.addMapInitializedListener(this);

		
		/* Right Panel */
	    VBox right = new VBox();
	    HBox bottom = new HBox();
	    
	    /* box of map file dropdown and load button */
	    HBox mapFiles = new HBox();
	    mapFiles.setAlignment(Pos.CENTER);
	    choiceBox = new ChoiceBox<>();
	    choiceBox.getItems().add("Upenn.map");
	    choiceBox.setPrefWidth(200);
	    
	    loadButton = new Button("Load Map");
	    
	    mapFiles.getChildren().addAll(choiceBox, loadButton);
	    
	    
	    /* box showing current selected map */
	    HBox currMap = new HBox();
	    currMap.setPadding(new Insets(20, 0, 0, 0));
	    mapLabel = new Label("Current Map: No Map Selected");
	    mapLabel.setGraphic(new ImageView("http://www.googlemapsmarkers.com/v1/009900/"));
	    mapLabel.setTextFill(Color.BLUE);
	    currMap.getChildren().addAll(mapLabel);
	    
	    /* box for start */
	    HBox startBox = new HBox();
	    startBox.setPadding(new Insets(20, 0, 0, 0));
	    startLabel = new Label("Starting Point: No point selected");
	    startLabel.setTextFill(Color.DARKMAGENTA);
	    startBox.getChildren().addAll(startLabel);
	    
	    /* box for dest. */
	    HBox destBox = new HBox();
	    destBox.setPadding(new Insets(20, 0, 0, 0));
	    destLabel = new Label("Destination: No point selected");
	    destLabel.setTextFill(Color.DARKMAGENTA);
	    destBox.getChildren().addAll(destLabel);
	    
	    
	    /* box for currPos */
	    HBox currPosBox = new HBox();
	    currPosBox.setPadding(new Insets(20, 0, 0, 0));
	    currPosLabel = new Label("Current Positon: No point selected");
	    currPosLabel.setTextFill(Color.GREEN);
	    
	    currPosBox.getChildren().addAll(currPosLabel);
	    
	    /*  datafetching */
	    TextField fetch = new TextField();
	    fetch.setPromptText("type \"example.map\"");
	    Button fetchBUtton = new Button("Fetch Data");
	    
	    
	    /* Add all the layouts to right box, in the order of levels */
	    Label chooseLabel = new Label("Choose a Map:");
	    chooseLabel.setTextFill(Color.DARKRED);
		right.getChildren().addAll(chooseLabel);
		right.getChildren().addAll(mapFiles, currMap);
		right.getChildren().addAll(startBox);
		right.getChildren().addAll(destBox);
		right.getChildren().addAll(currPosBox);
		
		/* set size for the right box */
		right.setPadding(new Insets(20, 0, 0, 0));
		right.setPrefWidth(300);
		
		/* the bottom pane*/
		bottom.getChildren().addAll(fetch, fetchBUtton);
		
		/* Attach button listeners for all buttons */
		attachButtonListeners();
		
		/* set overall layout */
		BorderPane bp = new BorderPane();
		Scene scene = new Scene(bp);
        bp.setCenter(mapComponent);
        bp.setRight(right);
        bp.setBottom(bottom);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Mini-Google Map");
        primaryStage.show();
	}

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

    }
    public static void main(String[] args) {
    	launch(args);
    }
    
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
    
    private void attachButtonListeners() {
    	loadButton.setOnAction(e -> {
    		
	    });
    }
    
    public static void setCurrPosLabel(GeographicPoint pos) {
    	LabelManager.setLabelText(currPosLabel, "Current Position: (" + pos.getX() + ", " + pos.getY() + ")");
    };

}
