package miniGoogleMap;

import javafx.scene.control.Label;

/**
 * A class for manage label
 * @author xiaofandou
 *
 */
public class LabelManager {

	/**
	 * set the given label given content.
	 * @param label the label to be set
	 * @param s the text that the label is set to
	 */
	public static void setLabelText(Label label, String s) {
		label.setText(s);
	}

}
