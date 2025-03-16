package Home;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("PFMS"); // sets the name of the main window
        primaryStage.getIcons().add(new Image("/Images/menuIcon.png"));
        primaryStage.setResizable(false); // makes it so the stage is not resizable
        primaryStage.setOnCloseRequest(event -> {
        	event.consume();
        	Boolean exitReq = ExitScreen.ExitCheck();
        	if (exitReq) {
        		primaryStage.close();
        	}
        	});
        
        LogIn.setComponents();
        LogIn.setStage();
        
	}
	

	public void stop() throws Exception{
		super.stop();
	}
}
