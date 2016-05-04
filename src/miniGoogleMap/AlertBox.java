package miniGoogleMap;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * A class to manage alert box.
 * @author xiaofandou
 *
 */
public class AlertBox {
	
	/**
	 * display an alert box with given tile and message.
	 * @param title
	 * @param message
	 */
	public static void display(String title, String message) {
		
		Stage window = new Stage();
		// block other windows.
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setMinWidth(250);
		
		Label label = new Label();
		label.setText(message);
		
		Button closeButton = new Button("OK");
		closeButton.setOnAction(e -> {
			window.close();
		});
		
		VBox layout = new VBox(10);
		layout.getChildren().addAll(label, closeButton);
		layout.setAlignment(Pos.CENTER);
		
		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.showAndWait();			// blocks any user interaction until the alert box is close.
	}
}
