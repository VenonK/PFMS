package Home;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
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
import Types.Transaction;

public class Expenses {
	private static BorderPane root;
	public static String accid;
	private static HBox createHeader() {
	    HBox header = new HBox(10);
	    header.setStyle("-fx-padding: 10; -fx-background-color: #f4f4f4;");

	    // Page Title
	    Text title = new Text("Expenses");
	    title.setFont(new Font(20));

	    // Add Expense Button
	    Button addExpenseButton = new Button("Add Expense");
	    addExpenseButton.setOnAction(event -> {
	        // Open the Add Expense form
	        showAddExpenseForm();
	    });

	    Button homeButton = new Button("Home");
	    homeButton.setOnAction(event -> {
	        returnToHome();
	    });

	    header.getChildren().addAll(title, addExpenseButton, homeButton);
	    return header;
	}
	
	
	
	private static VBox createOverviewSection() {
	    VBox overview = new VBox(10);
	    overview.setStyle("-fx-padding: 20;");

	    // Total Expenses
	    Text totalExpenses = new Text("Total Expenses: "+Database.getTotalExpense(accid));
	    totalExpenses.setFont(new Font(18));

	    // Top Spending Categories (Pie Chart)
	    PieChart topCategoriesChart = new PieChart();
	    topCategoriesChart.getData().addAll(
	        new PieChart.Data("Food", Database.getExpenseTotalByCategory("Food", accid)),
	        new PieChart.Data("Transport", Database.getExpenseTotalByCategory("Transport", accid)),
	        new PieChart.Data("Entertainment", Database.getExpenseTotalByCategory("Entertainment", accid))
	    );

	    overview.getChildren().addAll(totalExpenses, topCategoriesChart);
	    return overview;
	}
	
	@SuppressWarnings("unchecked")
	private static TableView<Transaction> createExpenseList() {
	    TableView<Transaction> table = new TableView<>();

	    // Columns
	    TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
	    dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

	    TableColumn<Transaction, String> categoryCol = new TableColumn<>("Category");
	    categoryCol.setCellValueFactory(new PropertyValueFactory<>("description"));

	    TableColumn<Transaction, Double> amountCol = new TableColumn<>("Amount");
	    amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

	    table.getColumns().addAll(dateCol, categoryCol, amountCol);

	    // Sample Data
	    ArrayList<Transaction> arr = Database.getExpenses(accid);
	    for (Transaction i : arr) {
	    	table.getItems().add(i);
	    }

	    return table;
	}
	
	private static void showAddExpenseForm() {
	    // Create a new window or dialog for the form
		Stage window = new Stage();
		window.setResizable(false);
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("New Expense");
		window.getIcons().add(new Image("/Images/menuIcon.png"));
		VBox formLayout = new VBox(10);
	    formLayout.setStyle("-fx-padding: 20;");
	    // Form Fields
	    DatePicker datePicker = new DatePicker();
	    ComboBox<String> categoryComboBox = new ComboBox<>();
	    categoryComboBox.getItems().addAll("Food", "Transport", "Entertainment");
	    TextField amountField = new TextField();
	    

	    // Save Button
	    Button saveButton = new Button("Save");
	    saveButton.setOnAction(event -> {
	        // Save the expense and close the form
	    	String str = amountField.getText();
	        Database.setExpense(new Transaction(datePicker.getValue().toString(),
	        		categoryComboBox.getValue(),
	        		Double.parseDouble(str)),accid);
	        window.close();
	    });

	    formLayout.getChildren().addAll(
	        new Label("Date:"), datePicker,
	        new Label("Category:"), categoryComboBox,
	        new Label("Amount:"), amountField,
	        saveButton
	    );

	    Scene scene = new Scene(formLayout);
	    window.setOnCloseRequest(event -> window.close());
	    window.setScene(scene);
	    window.showAndWait();
	}
	
	public static BorderPane setExpenseRoot() {
		HBox header = createHeader();
		VBox overview = createOverviewSection();
		TableView<Transaction> table = createExpenseList();
	    root = new BorderPane();
	    root.setTop(header);
	    root.setCenter(table);
	    root.setLeft(overview);
		return root;
	}
	
	private static void returnToHome() {
		HomePage.accountID = accid;
		HomePage.setMainComponents();
		HomePage.setStage();
	}
}
