package Home;

import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertBox {
	public AlertBox(Alert.AlertType alertType, Stage owner, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
	}
}
