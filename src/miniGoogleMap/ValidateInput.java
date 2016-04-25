package miniGoogleMap;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ValidateInput extends Application {
	
	
	Stage window;
	Scene scene1, scene2;
	Button button;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
		window.setTitle("Window");
		// When the user close the window, do what is passed in the parenthesis.
	
		VBox layout = new VBox();
		
		layout.setPadding(new Insets(20, 20, 20, 20));
		
		TextField nameInput = new TextField();
		button = new Button("Go!");
		button.setOnAction(e -> {
			System.out.println(nameInput.getText());
			nameInput.setText("");
		});
		
		scene1 = new Scene(layout, 300, 300);
		layout.getChildren().addAll(nameInput, button);
		
		
		window.setScene(scene1);
		
		window.show();
		
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}

}




























