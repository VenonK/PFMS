package Home;

import javafx.scene.Scene;
import Database.Database;
import Types.Budget;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
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
import java.util.Calendar;
import java.util.NoSuchElementException;

public class Budgets {
	public static String accid;
	public static BorderPane root;
	private static HBox createHeader() {
	    HBox header = new HBox(10);
	    header.setStyle("-fx-padding: 10; -fx-background-color: #f4f4f4;");

	    // Page Title
	    Text title = new Text("Budgets");
	    title.setFont(new Font(20));

	    // Add Budget Button
	    Button addBudgetButton = new Button("Add Budget");
	    addBudgetButton.setOnAction(event -> {
	        // Open the Add Budget form
	        showAddBudgetForm();
	    });
	    
	    Button returnButton = new Button("Home");
	    returnButton.setOnAction(event->{returnToHome();});
	    
	    header.getChildren().addAll(title, addBudgetButton,returnButton);
	    return header;
	}
	
	private static VBox createOverviewSection() {
	    VBox overview = new VBox(10);
	    overview.setStyle("-fx-padding: 20;");

	    // Total Budgets
	    Integer budgets = Database.getNumberofBudgets(accid);
	    Text totalBudgets = new Text("Total Budgets: "+budgets.toString());
	    totalBudgets.setFont(new Font(18));
	    
		// Budget Progress Bar
	    ProgressBar budgetProgress = new ProgressBar(); 
	    // Total Spent vs. Budgeted
	    Text totalSpentVsBudgeted = new Text("Latest Budget Progress");
	    ArrayList<Budget> budgets1 = Database.getBudgets(accid);
	    try {
	    Budget latestBudget = budgets1.getLast();
	    budgetProgress = new ProgressBar(latestBudget.getCurrent() / latestBudget.getTarget());
	    budgetProgress.setPrefWidth(200);
	    }catch(NoSuchElementException e) {
	    	System.err.println("No budgets");
	    }

	    

	    overview.getChildren().addAll(totalBudgets, totalSpentVsBudgeted, budgetProgress);
	    return overview;
	}
	
	@SuppressWarnings("unchecked")
	private static TableView<Budget> createBudgetsList() {
	    TableView<Budget> table = new TableView<>();

	    // Columns
	    TableColumn<Budget, String> categoryCol = new TableColumn<>("Category");
	    categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

	    TableColumn<Budget, Double> budgetedCol = new TableColumn<>("Budgeted");
	    budgetedCol.setCellValueFactory(new PropertyValueFactory<>("current"));

	    TableColumn<Budget, Double> remainingCol = new TableColumn<>("Target");
	    remainingCol.setCellValueFactory(new PropertyValueFactory<>("target"));

	    table.getColumns().addAll(categoryCol, budgetedCol,remainingCol);
	    
	    ArrayList<Budget> arr = Database.getBudgets(accid);
	    for (Budget i : arr) {
	    	table.getItems().add(i);
	    }
	    
	    // Row Click Event
	    table.setRowFactory(tv -> {
	        TableRow<Budget> row = new TableRow<>();
	        row.setOnMouseClicked(event -> {
	            if (event.getClickCount() == 2 && !row.isEmpty()) {
	                Budget selectedBudget = row.getItem();
	                // Show budget details
	                System.out.println("Selected Budget: "+selectedBudget.getCategory());
	            }
	        });
	        return row;
	    });

	    return table;
	}
	
	private static void showAddBudgetForm() {
		Stage window = new Stage();
		window.setResizable(false);
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("New Budget");
		window.getIcons().add(new Image("/Images/menuIcon.png"));
	    VBox formLayout = new VBox(10);
	    formLayout.setStyle("-fx-padding: 20;");

	    // Form Fields
	    TextField categoryField = new TextField();
	    TextField targetAmountField = new TextField();
	    TextField duration = new TextField();

	    // Save Button
	    Button saveButton = new Button("Save");
	    saveButton.setOnAction(event -> {
	        // Save the budget and close the form
	    	Calendar calendar = Calendar.getInstance();
	    	String date = ""+calendar.get(Calendar.YEAR)+"-"+calendar.get(Calendar.MONTH)+"-"+calendar.get(Calendar.DATE);
	        Database.setBudget(categoryField.getText(), targetAmountField.getText(), "0", date, duration.getText(), accid);
	        window.close();
	    });

	    formLayout.getChildren().addAll(
	        new Label("Category:"), categoryField,
	        new Label("Target Budget Amount:"), targetAmountField,
	        new Label("Duration(in months):"), duration,
	        saveButton
	    );
	    
	    Scene scene = new Scene(formLayout);
	    window.setOnCloseRequest(event -> window.close());
	    window.setScene(scene);
	    window.showAndWait();
	}
	
	public static BorderPane setBudgetRoot() {
		root = new BorderPane();
		root.setTop(createHeader());
		root.setCenter(createOverviewSection());
		root.setLeft(createBudgetsList());
	
		return root;
	}
	
	private static void returnToHome() {
		HomePage.accountID = accid;
		HomePage.setMainComponents();
		HomePage.setStage();
	}
}
