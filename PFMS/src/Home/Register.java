package Home;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import Database.Database;

public class Register {
	private static Stage registerStage = new Stage();
	private static GridPane root;
	private static Scene registerScene;
	
	static {
		registerStage.setTitle("PFMS");
		registerStage.getIcons().add(new Image("/Images/menuIcon.png"));
		registerStage.setResizable(false);
		registerStage.setOnCloseRequest(event -> {
        	event.consume();
        	boolean exitReq = ExitScreen.ExitCheck();
        	if (exitReq) {
        		registerStage.close();
        	}
        	});
	}
	
	public static void setStage() {
		registerScene = new Scene(root);
		registerStage.setScene(registerScene);
		registerStage.show();
	}
	
	public static void setComponents() {
		root = new GridPane();
        root.setAlignment(Pos.CENTER);
        root.setHgap(10);
        root.setVgap(10); 
        root.setPadding(new Insets(25, 25, 25, 25));

        Text sceneText = new Text("Register");
        sceneText.setFont(Font.font("Verdena", FontWeight.NORMAL, 20));
        root.add(sceneText, 0, 0,2,1);
        
        Label userName = new Label("User Name:");
        root.add(userName, 0, 1);
        
        TextField userTextField = new TextField(); 
        root.add(userTextField, 1, 1); 
        
        
        Label emailAdd = new Label("Email Address:");
        root.add(emailAdd, 0, 2);
        
        TextField emailTextField = new TextField(); 
        root.add(emailTextField, 1, 2);
        
        Label pw = new Label("Password:"); 
        root.add(pw, 0, 3);

        PasswordField pwBox = new PasswordField(); // creates a user enterable password field(obfuscates all information entered)
        pwBox.setPromptText("password");
        root.add(pwBox, 1, 3); // places onto GridPane object
        
        Button login = new Button("Login instead");
        login.setOnAction(event -> {
        	openLogin();
        });
        HBox hbBtn1 = new HBox(10);
        hbBtn1.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn1.getChildren().add(login);
        root.add(hbBtn1, 1, 5);
        
        Button register = new Button("Register");
        register.setOnAction(event -> {
        	boolean verify = setCreds(userTextField.getText(),pwBox.getText(),emailTextField.getText());
        	openHome(verify);
        });
        HBox hbtn2 = new HBox(10);
        hbBtn1.setAlignment(Pos.BOTTOM_LEFT);
        hbBtn1.getChildren().add(register);
        root.add(hbtn2, 0, 0);
        
	}
	
	private static boolean isValidEmailAddress(String email) {
		   boolean result = true;
		   try {
		      InternetAddress emailAddr = new InternetAddress(email);
		      emailAddr.validate();
		   } catch (AddressException ex) {
		      result = false;
		   }
		   return result;
	}
	
	private static boolean setCreds(String user, String pw, String email){
		boolean emailVerif = isValidEmailAddress(email);
		if ((user =="" || pw == "" || email == "" ) && (!emailVerif)) {
			return false;
		}
		return Database.setUserCredentials(user,pw,email);
	}
	
	private static void openHome(boolean x) {
		if (x) {
			new AlertBox(Alert.AlertType.CONFIRMATION,registerStage,"Successful Log-In","You have successfully entered the application");
			registerStage.close();
			root = null;
			Database.createAccount("Current", "0", "0", "0","1");
			HomePage.setMainComponents();
			HomePage.setStage();
		}else {
			new AlertBox(Alert.AlertType.ERROR,registerStage,"Unsuccessful Log-In", "Please enter correct information into the required fields");
		}
	}

	private static void openLogin() {
		registerStage.close();
    	root = null;
    	LogIn.setComponents();
    	LogIn.setStage();
	}
}
