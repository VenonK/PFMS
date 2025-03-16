package Home;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.ArrayList;
import Database.Database;
import Types.Account;

public class Accounts {
	private static BorderPane root;
	public static String accountid;
	
	public static BorderPane setAccountRoot() {
		root = new BorderPane();
		root.setTop(createHeader());
		root.setCenter(createOverviewSection());
		root.setLeft(createAccountsList());
		
		return root;
	}
	
	private static HBox createHeader() {
	    HBox header = new HBox(10);
	    header.setStyle("-fx-padding: 10; -fx-background-color: #f4f4f4;");

	    // Page Title
	    Text title = new Text("Accounts");
	    title.setFont(new Font(20));

	    // Add Account Button
	    Button addAccountButton = new Button("Add Account");
	    addAccountButton.setOnAction(event -> {
	        // Open the Add Account form
	        showAddAccountForm();
	    });

	    Button switchAccount = new Button("Switch Account");
	    switchAccount.setOnAction(event->{switchAccount();});
	    
	    
	    Button returnHome = new Button("Home");
	    returnHome.setOnAction(event ->{returnToHome();});

	    header.getChildren().addAll(title, addAccountButton,returnHome,switchAccount);
	    return header;
	}
	
	private static void switchAccount() {
		Stage window = new Stage();
		window.setResizable(false);
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("New Account");
		window.getIcons().add(new Image("/Images/menuIcon.png"));
	    VBox formLayout = new VBox(10);
	    formLayout.setStyle("-fx-padding: 20;");

	    // Form Fields
	    ComboBox<String> typeComboBox = new ComboBox<>();
	    typeComboBox.getItems().addAll("Current", "Saving", "Student");
	    Button switchAcc = new Button("Switch");
	    switchAcc.setOnAction(event->{
	    	accountid = Database.getAccountID(typeComboBox.getValue());
	    });
	    formLayout.getChildren().addAll(
	    		new Label("Pick Account:"),typeComboBox,
	    		switchAcc
	    	);
	    
	    
	    Scene scene = new Scene(formLayout);
	    window.setOnCloseRequest(event -> window.close());
	    window.setScene(scene);
	    window.showAndWait();
	}
	
	private static VBox createOverviewSection() {
	    VBox overview = new VBox(10);
	    overview.setStyle("-fx-padding: 20;");

	    // Total Balance
	    Double balance = Database.getTotalIncome(accountid);
	    Text totalBalance = new Text("Total Balance: "+balance.toString());
	    totalBalance.setFont(new Font(18));

	    // Net Worth
	    Double networth = Database.getTotalIncome(accountid) - Database.getTotalExpense(accountid);
	    Text netWorth = new Text("Net Worth: "+networth.toString());

	    // Account Summary
	    Integer numAcc = Database.numOfAccounts();
	    Text accountSummary = new Text(numAcc.toString() + ": This is the total number of accounts");

	    overview.getChildren().addAll(totalBalance, netWorth, accountSummary);
	    return overview;
	}
	
	@SuppressWarnings("unchecked")
	private static TableView<Account> createAccountsList() {
	    TableView<Account> table = new TableView<>();

	    // Columns
	    TableColumn<Account, String> typeCol = new TableColumn<>("Type");
	    typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

	    TableColumn<Account, Double> balanceCol = new TableColumn<>("Balance");
	    balanceCol.setCellValueFactory(new PropertyValueFactory<>("balance"));
	    
	    TableColumn<Account, Double> nameCol = new TableColumn<>("Limit");
	    nameCol.setCellValueFactory(new PropertyValueFactory<>("limit"));

	    table.getColumns().addAll(nameCol, typeCol, balanceCol);

	    // Sample Data
	    ArrayList<Account> accounts = Database.getAccounts();
	    for (Account i:accounts) {
	    	i.setBalance(720);
	    	table.getItems().add(i);
	    }

	    // Row Click Event
	    table.setRowFactory(tv -> {
	        TableRow<Account> row = new TableRow<>();
	        row.setOnMouseClicked(event -> {
	            if (event.getClickCount() == 2 && !row.isEmpty()) {
	                Account selectedAccount = row.getItem();
	                // Show account details
	                System.out.println("Selected Account: " + selectedAccount.getType());
	            }
	        });
	        return row;
	    });

	    return table;
	}
	
	
	
	private static void showAddAccountForm() {
	    // Create a new window or dialog for the form
		Stage window = new Stage();
		window.setResizable(false);
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("New Account");
		window.getIcons().add(new Image("/Images/menuIcon.png"));
	    VBox formLayout = new VBox(10);
	    formLayout.setStyle("-fx-padding: 20;");

	    // Form Fields
	    ComboBox<String> typeComboBox = new ComboBox<>();
	    typeComboBox.getItems().addAll("Current", "Saving", "Student");
	    TextField balanceField = new TextField();
	    TextField transactionLimitField = new TextField();
	    TextField liabilities = new TextField();

	    // Save Button
	    Button saveButton = new Button("Save");
	    saveButton.setOnMouseClicked(event ->{
	    	Database.createAccount(typeComboBox.getValue(), balanceField.getText(), transactionLimitField.getText(), liabilities.getText(), "1");
	    	window.close();
	    }
	    );
	    
	    formLayout.getChildren().addAll(
	        new Label("Account Type:"), typeComboBox,
	        new Label("Initial Balance:"), balanceField,
	        new Label("Transaction Limit:"), transactionLimitField,
	        new Label("Initial Liabilities:"),liabilities,
	        saveButton
	    );
	    
	    Scene scene = new Scene(formLayout);
	    window.setOnCloseRequest(event -> window.close());
	    window.setScene(scene);
	    window.showAndWait();
	}
	
	private static void returnToHome() {
		HomePage.accountID = accountid;
		HomePage.setMainComponents();
		HomePage.setStage();
	}
	
}
