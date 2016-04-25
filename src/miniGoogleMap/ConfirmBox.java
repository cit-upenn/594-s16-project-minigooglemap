package miniGoogleMap;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmBox {
	
	static boolean answer;
	public static boolean display(String title, String message) {
		
		Stage window = new Stage();
		// block other windows.
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setMinWidth(250);
		
		Label label = new Label();
		label.setText(message);
		
		Button yesButton = new Button("Yes");
		Button noButton = new Button("No");
		yesButton.setOnAction(e -> {
			answer = true;
			System.out.println();
			window.close();
		});
		
		
		noButton.setOnAction(e -> {
			answer = false;
			System.out.println();
			window.close();
		});
		
		VBox layout = new VBox(10);
		layout.getChildren().addAll(label, yesButton, noButton);
		layout.setAlignment(Pos.CENTER);
		
		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.showAndWait();			// blocks any user interaction until the alert box is close.
		
		return answer;
	}
}










































