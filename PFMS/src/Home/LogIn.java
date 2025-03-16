package Home;

import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.layout.GridPane;
import Database.Database;



public class LogIn {
	private static Stage logInStage = new Stage();
	private static GridPane root;
	private static Scene logInScene;
	
	static {
		logInStage.setTitle("PFMS");
		logInStage.getIcons().add(new Image("/Images/menuIcon.png"));
		logInStage.setResizable(false);
		logInStage.setOnCloseRequest(event -> {
        	event.consume();
        	Boolean exitReq = ExitScreen.ExitCheck();
        	if (exitReq) {
        		logInStage.close();
        	}
        	});
		System.out.println("Entering the login screen");
	}
	
	public static void setStage() {
		logInScene = new Scene(root);
		logInStage.setScene(logInScene);
		logInStage.show();
	}
	
	public static void setComponents() {
		root = new GridPane();
		root.setAlignment(Pos.CENTER); // changes default alignment from top-left to the centre
        root.setHgap(10);// changes vertical and horizontal gaps between rows and columns
        root.setVgap(10); 
        root.setPadding(new Insets(25, 25, 25, 25)); // adds padding to the edges top,right,bottom,left
        
        Text scenetitle = new Text("Log In"); // creates a text object that cannot be edited or interacted with
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20)); 
        root.add(scenetitle, 0, 0, 2, 1); // (column, row, column span, row span)
        
        Label userName = new Label("User Name:");
        root.add(userName, 0, 1);
        
        TextField userField = new TextField(); // creates a user enterable text field
        root.add(userField, 1, 1); // places onto GridPane object
        
        Label pw = new Label("Password:"); 
        root.add(pw, 0, 2);
        
        PasswordField pwField = new PasswordField(); // creates a user enterable password field(obfuscates all information entered)
        root.add(pwField, 1, 2); // places onto GridPane object
        
        Button registerBtn = new Button("Register instead");
        Button signInBtn = new Button("Sign in");
        
        signInBtn.setOnAction(event -> validateUserCreds(userField.getText(),pwField.getText()));
        registerBtn.setOnAction(event -> {
        	openRegister(); 
        });
        
        root.add(registerBtn, 0, 4); // places onto GridPane object
        root.add(signInBtn, 2, 4);
        Database.databaseSetup();
	}
	
	private static void validateUserCreds(String username, String password) {
		boolean userVerify = Database.getUserCredentials(username, password);
		System.out.println(userVerify);
		if (userVerify) {
			openHome();
		} else {
			new AlertBox(Alert.AlertType.ERROR,logInStage,"User Identification Error", "Username or password are incorrect, please try again or consider registering again");
		}
	}
	
	private static void openHome() {
		logInStage.close();
		root= null;
		HomePage.setMainComponents();
		HomePage.setStage();
		new AlertBox(Alert.AlertType.CONFIRMATION,logInStage,"Successful Log-In", "You have successefully entered ");
	}
	
	private static void openRegister() {
		logInStage.close();
    	root = null;
    	Register.setComponents();
    	Register.setStage();
	}
	
}