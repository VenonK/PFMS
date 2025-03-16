package Home;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.text.Text;

public class ExitScreen {
	public static boolean exiting;

	public static boolean ExitCheck() {
		Stage window = new Stage();
		window.getIcons().add(new Image("/Images/menuIcon.png"));
		window.setResizable(false);
		window.initModality(Modality.APPLICATION_MODAL); // forces the user to interact with it
		window.setTitle("Confirm Exit");
		window.setMinWidth(400);
		window.setMinHeight(50);
		
		HBox hbox = new HBox(10);
		Button exitBtn = new Button("Yes");
		Button remainBtn= new Button("No");
		hbox.getChildren().addAll(exitBtn,remainBtn);
		
		exitBtn.setOnAction(event ->{
			exiting = true;
			window.close();
		});
		
		remainBtn.setOnAction(event ->{
			exiting = false;
			window.close();
		});
		
		Text sceneText = new Text("Are you sure you want to exit the application");
		
		GridPane root = new GridPane();
		root.setAlignment(Pos.CENTER);
		root.add(sceneText, 0, 0);
		root.add(hbox,0,1);
		
		Scene scene = new Scene(root);
		window.setOnCloseRequest(event -> {event.consume();});
		window.setScene(scene);
		window.showAndWait();
		
		return exiting;
	}
}
