package Home;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import Database.Database;
import Types.Budget;
import Types.Transaction;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.cell.PropertyValueFactory;


public class HomePage {
	public static Stage HomeStage = new Stage();
	public static BorderPane root;
	public static Scene HomeScene;
	public static String accountID;
	
	static {
		HomeStage.setTitle("PFMS");
		HomeStage.getIcons().add(new Image("/Images/menuIcon.png"));
		HomeStage.setResizable(false);
		accountID = Database.getAccountID("Current");
		HomeStage.setOnCloseRequest(event -> {
        	event.consume();
        	boolean exitReq = ExitScreen.ExitCheck();
        	if (exitReq) {
        		HomeStage.close();
        	}
        	});
		System.out.println("Entering the Home screen");
	}

	public static void setStage() {
		HomeScene = new Scene(root);
		HomeStage.setScene(HomeScene);
		HomeStage.show();
	}

	public static void setMainComponents(){
		root = null;
		root = new BorderPane();
        root.setTop(createHeader());
        root.setCenter(createCenterSection());
        root.setLeft(createBudgetsSection());
        root.setRight(createChartsSection());
        root.setBottom(createNavigationMenu());
        root.setPadding(new Insets(25, 25, 25, 25));
        
	}
	
	private static HBox createHeader() {
	    HBox header = new HBox(10);
	    header.setStyle("-fx-padding: 10; -fx-background-color: #f4f4f4;");

	    // User Profile
	    String userName = Database.getUsername();
	    Text profile = new Text("Hello "+userName+" Welcome to PFMS");
	    
	    header.getChildren().addAll(profile);
	    return header;
	}
	
	private static VBox createCenterSection() {
	    VBox center = new VBox(10);
	    center.setStyle("-fx-padding: 20;");

	    // Current Balance
	    Double balance = Database.getTotalIncome(accountID);
	    Text bal = new Text("Balance: "+balance.toString());	    
	    bal.setFont(new Font(24));
	    
	    Double liabilities = Database.getTotalExpense(accountID);
	    Text liab = new Text("Liabilities: "+liabilities.toString());	    
	    liab.setFont(new Font(24));
	    

	    // Income vs. Expenses (Pie Chart)
	    double[] monero = fillPieChart();
	    PieChart.Data incomeData = new PieChart.Data("Income",monero[0]);
	    PieChart.Data expensesData = new PieChart.Data("Expenses", monero[1]);
	    PieChart pieChart = new PieChart();
	    pieChart.getData().addAll(incomeData, expensesData);

	    // Net Worth
	    Double networth = Database.getTotalIncome(accountID) - Database.getTotalExpense(accountID);
	    Text netWorth = new Text("Net Worth: "+networth.toString());
	    
	    // Recent Transaction Table 
	    TableView<Transaction> recentTransactions = createRecentTransactions();
	    recentTransactions.setRowFactory(tv -> {
	        TableRow<Transaction> row = new TableRow<>();
	        row.setOnMouseClicked(event -> {
	            if (event.getClickCount() == 2 && !row.isEmpty()) {
	                Transaction selectedTransaction = row.getItem();
	                // Handle double-click on a row
	                System.out.println("Selected Transaction: " + selectedTransaction.getDescription());
	            }
	        });
	        return row;
	    });

	    center.getChildren().addAll(bal, liab,pieChart, netWorth,recentTransactions);
	    return center;
	}
	
	private static double[] fillPieChart() {
		double[] arr = null;
		arr = new double[2];
		arr[0] = Database.getTotalIncome(accountID);
		arr[1] = Database.getTotalExpense(accountID);
		return arr;
	}
	
	@SuppressWarnings("unchecked")
	private static TableView<Transaction> createRecentTransactions() {
	    TableView<Transaction> table = new TableView<>();

	    // Columns
	    TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
	    dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

	    TableColumn<Transaction, String> descriptionCol = new TableColumn<>("Description");
	    descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

	    TableColumn<Transaction, Double> amountCol = new TableColumn<>("Amount");
	    amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

	    table.getColumns().addAll(dateCol, descriptionCol, amountCol);
	    
	    ArrayList<Transaction> transactions = Database.getTransactions(accountID);
	    if(transactions.size() < 10) {
	    	for (int i = 0;i<transactions.size();i++) {
		    	table.getItems().add(transactions.get(i));
		    }
	    }else {
	    	for (int i = 0;i<10;i++) {
		    	table.getItems().add(transactions.get(i));
		    }
	    }
	    
	    return table;
	}
	
	private static VBox createBudgetsSection() {
	    VBox left = new VBox(10);
	    left.setStyle("-fx-padding: 20;");

	    // Budget Progress
	    ArrayList<Budget> budgets = Database.getBudgets(accountID);
	    try {
	    Budget latestBudget = budgets.getLast();
	    ProgressBar budgetProgress = new ProgressBar(latestBudget.getCurrent() / latestBudget.getTarget());
	    budgetProgress.setPrefWidth(200);

	    // Savings Goals
	    Text savingsGoal = new Text(latestBudget.getCategory());
	    Button editBudgetButton = new Button("Edit Budget");
        editBudgetButton.setOnAction(event -> {
            setBudgetsPage();
        });

        left.getChildren().addAll(new Text("Budgets"), budgetProgress, new Text("Latest Budget"), savingsGoal, editBudgetButton);
        }catch(NoSuchElementException e) {
	    	System.err.println("No budgets");
	    }
	    
        return left;
    }
	

	private static BarChart<String, Number> createChartsSection() {
	    CategoryAxis xAxis = new CategoryAxis();
	    NumberAxis yAxis = new NumberAxis();
	    BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
	
	    XYChart.Series<String, Number> series = new XYChart.Series<>();
	    series.getData().add(new XYChart.Data<>("Jan",Database.getIncomePerMonth(accountID, 1)));
	    series.getData().add(new XYChart.Data<>("Feb",Database.getIncomePerMonth(accountID, 2)));
	    series.getData().add(new XYChart.Data<>("Mar",Database.getIncomePerMonth(accountID, 3)));
	    series.getData().add(new XYChart.Data<>("Apr",Database.getIncomePerMonth(accountID, 4)));
	    series.getData().add(new XYChart.Data<>("May",Database.getIncomePerMonth(accountID, 5)));
	    series.getData().add(new XYChart.Data<>("Jun",Database.getIncomePerMonth(accountID, 6)));
	    series.getData().add(new XYChart.Data<>("Jul",Database.getIncomePerMonth(accountID, 7)));
	    series.getData().add(new XYChart.Data<>("Aug",Database.getIncomePerMonth(accountID, 8)));
	    series.getData().add(new XYChart.Data<>("Sep",Database.getIncomePerMonth(accountID, 9)));
	    series.getData().add(new XYChart.Data<>("Oct",Database.getIncomePerMonth(accountID, 10)));
	    series.getData().add(new XYChart.Data<>("Nov",Database.getIncomePerMonth(accountID, 11)));
	    series.getData().add(new XYChart.Data<>("Dec",Database.getIncomePerMonth(accountID, 12)));
	
	    barChart.getData().add(series);
	    return barChart;
	}
	
	private static HBox createNavigationMenu() {
	    HBox bottom = new HBox(10);
	    bottom.setStyle("-fx-padding: 10; -fx-background-color: #f4f4f4;");
	    
	    Button accounts = new Button("Accounts");
	    accounts.setOnMouseClicked(event -> {
	        setAccountPage();
	    });
	    
	    Button expenses = new Button("Expenses");
	    expenses.setOnMouseClicked(event -> {
            setExpensePage();
        });
	    
	    Button budgets = new Button("Budgets");
	    budgets.setOnMouseClicked(event -> {
            setBudgetsPage();
        });
	    
	    Button income = new Button("Income");
	    income.setOnMouseClicked(event -> {
	    	setIncomePage();
	    });


	    bottom.getChildren().addAll(accounts,expenses,income,budgets);
	    return bottom;
	} 
	
	private static void setExpensePage() {
		Expenses.accid = accountID;
		HomeScene.setRoot(Expenses.setExpenseRoot());
		HomeStage.show();
	}
	
	private static void setAccountPage() {
		Accounts.accountid = accountID;
		HomeScene.setRoot(Accounts.setAccountRoot());
		HomeStage.show();
	}
	
	private static void setBudgetsPage() {
		Budgets.accid = accountID;
		HomeScene.setRoot(Budgets.setBudgetRoot());
		HomeStage.show();
	}
	
	private static void setIncomePage() {
		Income.accid = accountID;
		HomeScene.setRoot(Income.setIncomeRoot());
		HomeStage.show();
	}
}
