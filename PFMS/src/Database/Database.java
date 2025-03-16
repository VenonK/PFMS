package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import Types.Account;
import Types.Budget;
import Types.Transaction;

public class Database { 
	// Connection object
    private static Connection connection;
    // Statement object
    private static Statement statement;
    
    // Database connection credentials
    final static String url = "jdbc:postgresql://localhost:5432/pfms";
    final static String user = "postgres";
    final static String password = "Notaxmar444";
    
    private static void connect() {
        try {
            // Connect to PostgreSQL
            connection = DriverManager.getConnection(url, user, password);
            
        } catch (SQLException e) {
            System.err.println("Connection failed!");
            e.printStackTrace();
        }
    }    
    private static void disconnect() {
    	try {
			connection.close();
		} catch (SQLException e) {
			System.err.println("Closing connection fail");
			e.printStackTrace();
		}
    }
    
    public static void databaseSetup() { 
    	connect();   	
    	try {
    		Statement userTbl = statement;
    		userTbl = connection.createStatement();
    		userTbl.execute("CREATE TABLE IF NOT EXISTS client("
    				+ "userID serial PRIMARY KEY,"
    				+ "username TEXT,"
    				+ "email TEXT,"
    				+ "password TEXT"
    				+ ")");
    		
    	} catch (SQLException e) {
			System.err.println("Something went wrong with user table setup");
			e.printStackTrace();
		}
    		
    	try {
    		Statement accountTbl = statement;
			accountTbl = connection.createStatement();
			accountTbl.execute("CREATE TYPE accountType as ENUM ('Current','Saving','Student')"); 
			accountTbl.execute("CREATE TABLE IF NOT EXISTS account("
					+ "accID serial PRIMARY KEY,"
					+ "accType accountType,"
					+ "accBalance DECIMAL(18,2),"
					+ "transactionLimit DECIMAL(18,2)," 
					+ "liabilities DECIMAL(18,2),"
					+ "userid INT REFERENCES client(userID)"
					+ ")");	
    	} catch (SQLException e) {
			System.err.println("Something went wrong with account table set up");
			e.printStackTrace();
		}
			
    	try {
    		Statement transactionTbl = statement;
			transactionTbl = connection.createStatement();
			transactionTbl.execute("CREATE TABLE IF NOT EXISTS transaction("
					+ "transactionType TEXT,"
					+ "transactionAmount DECIMAL(18,2),"
					+ "date DATE,"
					+ "category TEXT," 
					+ "accid INT REFERENCES account(accID)"
					+ ")");
			
    	} catch (SQLException e) {
			System.err.println("Something went wrong with transaction table set up");
			e.printStackTrace();
		}
    	
    	try {
    		Statement budgetTbl = statement;
			budgetTbl = connection.createStatement(); 
			budgetTbl.execute("CREATE TABLE IF NOT EXISTS budget("
					+ "category TEXT,"
					+ "targetAmount DECIMAL(18,2),"
					+ "currentAmount DECIMAL(18,2),"
					+ "startDate DATE,"
					+ "duration INT,"
					+ "accid INT REFERENCES account(accID)"
					+ ")"); 
			
		} catch (SQLException e) {
			System.err.println("Something went wrong with budget table set up");
			e.printStackTrace();
		}
    	disconnect();
    }
    
    public static void setBudget(String category,String targetAmount,String currentAmount,String start,String duration,String accid) { 
    	connect();
    	Statement stmt = statement;
    	String query = "INSERT INTO budget(category,targetamount,currentamount,startdate,duration,accid)"
    			+ "VALUES("+"'"+category+"'"+","+targetAmount+","+currentAmount+","+"'"+start+"'"+","+duration+","+accid+")";
    	try {	
			stmt = connection.createStatement();
			stmt.execute(query);
		} catch (SQLException e) {
			System.err.println("Failed to set budget");
			e.printStackTrace();
		}
    	disconnect();
    }
    
    public static ArrayList<Budget> getBudgets(String accid){
    	ArrayList<Budget> arr = new ArrayList<Budget>();
    	connect();
    	String query = "SELECT * FROM budget WHERE accid = "+accid;
    	Statement stmt = statement;
    	try {
    		stmt = connection.createStatement();
    		ResultSet rs  = stmt.executeQuery(query);
    		while(rs.next()) {
    			String category = rs.getString("category");
    			double target = rs.getDouble("targetamount");
    			double current = rs.getDouble("currentamount");
    			String start = rs.getDate("startdate").toString();
    			int duration = rs.getInt("duration");
    			arr.add(new Budget(category,current,target,start,duration));
    		}
    	}catch(SQLException sqle) {
    		System.err.println("Issue getting budgets");
    	}
    	
    	return arr;
    }
    
    public static void setExpense(Transaction tr,String accid) {
    	connect();
    	String query = "INSERT INTO transaction(transactiontype,transactionamount,date,category,accid)"
    			+ "VALUES('Expense',"+tr.getAmount()+","+"'"+tr.getDate()+"'"+","+"'"+tr.getDescription()+"'"+","+accid+")";
    	Statement stmt = statement;
    	try {
    		stmt = connection.createStatement();
    		stmt.execute(query);
    	}catch(SQLException sqle) {
    		System.err.println("error in setting expense");
    	}
    	
    	disconnect();
    }
    
    public static void setIncome(Transaction tr,String accid) {
    	connect();
    	String query = "INSERT INTO transaction(transactiontype,transactionamount,date,category,accid)"
    			+ "VALUES('Income',"+tr.getAmount()+","+"'"+tr.getDate()+"'"+","+"'"+tr.getDescription()+"'"+","+accid+")";
    	Statement stmt = statement;
    	try {
    		stmt = connection.createStatement();
    		stmt.execute(query);
    	}catch(SQLException sqle) {
    		System.err.println("error in setting income transaction");
    	}
    	
    	disconnect();
    }
    
    public static ArrayList<Transaction> getTransactions(String accid) {
    	connect();
    	ArrayList<Transaction> arr = new ArrayList<Transaction>();
    	String query = "SELECT * FROM transaction WHERE accid="+accid;
    	Statement stmt = statement;
    	try {
    		stmt = connection.createStatement();
    		ResultSet rs = stmt.executeQuery(query);
    		while (rs.next()) {
    			double amount = rs.getDouble("transactionamount");
    			String date = rs.getDate("date").toString(); 
    			String type = rs.getString("transactiontype");
    			arr.add(new Transaction(date, type, amount)); 
    		}
    	}catch(SQLException sql) {
    		System.err.println("Too little transactions");
    	}
    	disconnect();
    	return arr;
    }
    
    public static ArrayList<Transaction> getExpenses(String accid){
    	connect();
    	ArrayList<Transaction> arr = new ArrayList<Transaction>();
    	String query = "SELECT transactionamount,date,category FROM transaction WHERE accid = "+accid+ " AND transactiontype = 'Expense'";
    	Statement stmt = statement;
    	try {
    		stmt = connection.createStatement();
    		ResultSet rs = stmt.executeQuery(query);
    		while (rs.next()) {
    			double amount = rs.getDouble("transactionamount");
    			String date = rs.getString("date");
    			String desc = rs.getString("category");
    			arr.add(new Transaction(date,desc,amount));
    		}
    	}catch(SQLException sqle) {
    		System.err.println("Having a problem getting expenses");
    	}
    	disconnect();
		return arr;
    }
    
    public static ArrayList<Transaction> getIncome(String accid){
    	connect();
    	ArrayList<Transaction> arr = new ArrayList<Transaction>();
    	String query = "SELECT transactionamount,date,category FROM transaction WHERE accid = "+accid+ " AND transactiontype = 'Income'";
    	Statement stmt = statement;
    	try {
    		stmt = connection.createStatement();
    		ResultSet rs = stmt.executeQuery(query);
    		while (rs.next()) {
    			double amount = rs.getDouble("transactionamount");
    			String date = rs.getString("date");
    			String desc = rs.getString("category");
    			arr.add(new Transaction(date,desc,amount));
    		}
    	}catch(SQLException sqle) {
    		System.err.println("Having a problem getting income");
    	}
    	disconnect();
		return arr;
    }
    
    public static double getExpenseTotalByCategory(String category,String accid){
    	double result = 0;
    	connect();
    	String query = "SELECT transactionamount FROM transaction WHERE category = "+"'"+category+"'"+" AND transactiontype = 'Expense'"+" AND accid="+accid;
    	Statement stmt = statement;
    	try {
    		stmt = connection.createStatement();
    		ResultSet rs = stmt.executeQuery(query);
    		while (rs.next()) {
    			double amount = rs.getDouble("transactionamount");
    			result += amount;
    		}
    	}catch(SQLException sqle) {
    		System.err.println("Error getting expenses by category");
    	}
    	disconnect();
    	return result;
    }
    
    public static double getIncomeTotalByCategory(String category,String accid){
    	double result = 0;
    	connect();
    	String query = "SELECT transactionamount FROM transaction WHERE category = "+"'"+category+"'"+" AND transactiontype = 'Income'"+" AND accid="+accid;
    	Statement stmt = statement;
    	try {
    		stmt = connection.createStatement();
    		ResultSet rs = stmt.executeQuery(query);
    		while (rs.next()) {
    			double amount = rs.getDouble("transactionamount");
    			result += amount;
    		}
    	}catch(SQLException sqle) {
    		System.err.println("Error getting income by category");
    	}
    	disconnect();
    	return result;
    }
    
    public static boolean getUserCredentials(String username, String password) {
    	connect();
    	String query = "SELECT * FROM client WHERE userid = 1";
    	Statement stmt = statement;
    	try {
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				String name = rs.getString("username");
				String pass = rs.getString("password");
				if (username.equals(name) && password.equals(pass)){
					disconnect();
					return true;
				}
			}
		} catch (SQLException sqle) {
			System.err.println("Problem querying user information");
		}
    	disconnect();
		return false;    	
    }
   
    public static double getTotalIncome(String accountid) {
    	connect();
    	double result = 0;
    	String query = "SELECT transactionamount FROM transaction WHERE accid = "+accountid+" AND transactiontype = 'Income'";
    	Statement stmt = statement;
    	try {
    		stmt = connection.createStatement();
    		ResultSet rs = stmt.executeQuery(query);
    		while (rs.next()) {
    			double amount = rs.getDouble("transactionamount");
    			result = result + amount;
    		}
    	}catch(SQLException sqle) {
    		System.err.println("Error in getting the total income");
    	}
    	
    	disconnect();
    	return result;
    }
    
    public static double getTotalExpense(String accountid) {
    	connect();
    	double result = 0;
    	String query = "SELECT transactionamount FROM transaction WHERE accid = "+accountid+" AND transactiontype = 'Expense'";
    	Statement stmt = statement;
    	try {
    		stmt = connection.createStatement();
    		ResultSet rs = stmt.executeQuery(query);
    		while (rs.next()) {
    			double amount = rs.getDouble("transactionamount");
    			result = result + amount;
    		}
    	}catch(SQLException sqle) {
    		System.err.println("Error in getting the total expenses ");
    	}
    	
    	disconnect();
    	return result;
    }
   
    public static String getUsername() {
    	connect();
    	String query = "SELECT username FROM client WHERE userid = 1";
    	Statement stmt = statement;
    	try {
    		stmt = connection.createStatement();
    		ResultSet rs = stmt.executeQuery(query);
    		while (rs.next()) {
    			String name = rs.getString("username");
    			disconnect();
    			return name;
    		}
    	}catch(SQLException sqle){
    		System.out.println("Error getting username");
    	}
    	disconnect();
		return null;
    }
    
    public static boolean setUserCredentials(String username, String password, String email) {
    	connect();
    	Statement stmt = statement;
    	String query = "INSERT INTO client(username,email,password)"
    			+ "VALUES("+"'"+username+"'"+","+"'"+email+"'"+","+"'"+password+"'"+")";
    	try {
    		stmt = connection.createStatement();
    		stmt.execute(query);
    		disconnect();
    		return true;
    	}catch(SQLException sqle){
    		System.err.println("Problem setting user creds");
    		disconnect();
    		return false;
    	} 	
    }
    
    public static double getBalance(String accountid) {
    	connect();
		Statement stmt = statement;
    	String query = "SELECT accbalance FROM account WHERE accid = "+accountid;
    	double balance = 0;
    	try {
    		stmt = connection.createStatement();
    		
    		ResultSet rs = stmt.executeQuery(query);
    		while (rs.next()) {
    			balance = rs.getDouble("accbalance");
    		}
    	}catch(SQLException sqle) {
    		System.err.println("Problem getting balance");
    	}
    	disconnect();
		return balance;
    }
    
    public static double getLiabilities(String accid) {
    	connect();
		Statement stmt = statement;
    	String query = "SELECT liabilities FROM account WHERE accid = "+accid;
    	double liabilities = 0;
    	try {
    		stmt = connection.createStatement();
    		ResultSet rs = stmt.executeQuery(query);
    		while (rs.next()) {
    			liabilities = rs.getDouble("liabilities");
    		}
    	}catch(SQLException sqle) {
    		System.err.println("Problem getting liabilities");
    	}
    	disconnect();
		return liabilities;
    }
    
    
    public static String getAccountID(String type) {
    	connect();
    	String accid = "";
    	String query = "SELECT accid FROM account WHERE acctype="+"'"+type+"'"+";";
    	Statement stmt = statement;
    	try {
    		stmt = connection.createStatement();
    		ResultSet rs = stmt.executeQuery(query);
    		rs.next();
    		Integer id = rs.getInt("accid");
    		accid = id.toString();
    	}catch(SQLException sqle) {
    		System.err.println("Error in getting accountID");
    	}
    	disconnect();
    	return accid;
    }
    
    public static void createAccount(String type,String balance,String transaclimit,String liab,String userid) {
    	connect();
    	String query = "INSERT INTO account(acctype,accbalance,transactionlimit,liabilities,userid)"
    			+ "VALUES("+"'"+type+"'"+","+balance+","+transaclimit+","+liab+","+userid+")";
    	Statement stmt = statement;
    	try {
    		stmt = connection.createStatement();
    		stmt.execute(query);
    	}catch(SQLException sqle) {
    		System.err.println("Problem creating account");
    	}
    	disconnect();
    }
    
    public static void deleteAccount(String accid) {
    	connect();
    	String query = "DELETE FROM account WHERE accid="+accid;
    	Statement stmt = statement;
    	try {
    		stmt = connection.createStatement();
    		stmt.execute(query);
    	}catch(SQLException sqle) {
    		System.err.println("Problem deleting account");
    	}
    	disconnect();
    }
    
    public static ArrayList<Account> getAccounts(){
    	connect();
    	ArrayList<Account> arr = new ArrayList<Account>();
    	String query ="SELECT * FROM account WHERE userid = 1";
    	Statement stmt = statement;
    	try {
    		stmt = connection.createStatement();
    		ResultSet rs = stmt.executeQuery(query);
    		while(rs.next()) {
    			double limit = rs.getDouble("transactionlimit");
    			String type = rs.getString("acctype");
    			double balance = rs.getDouble("accbalance");
    			arr.add(new Account(type, balance, limit));
    		}
    	}catch(SQLException sqle) {
    		System.err.println("Issuing in getting accounts");
    	}
    	disconnect();
    	return arr;
    }
    
    public static int numOfAccounts() {
    	connect();
    	int result = 0;
    	String query = "SELECT acctype FROM account WHERE userid = 1";
    	Statement stmt = statement;
    	try {
    		stmt = connection.createStatement();
    		ResultSet rs = stmt.executeQuery(query);
    		while(rs.next()) {
    			result = result +1;
    		}
    	}catch(SQLException sqle) {
    		
    	}
    	disconnect();
    	return result;
    }
    
    public static void updateBudget(Budget budget,String accid) {
    	connect();
    	Double newAmount = budget.getCurrent();
    	String category = budget.getCategory();
    	String query= "UPDATE budget SET currentamount = "+newAmount.toString()+" WHERE category = "+"'"+category+"'"+"AND accid = "+accid;
    	Statement stmt = statement;
    	try {
    		stmt = connection.createStatement();
    		stmt.execute(query);
    	}catch(SQLException sqle) {
    		System.err.println("Trouble updating budget");
    	}
    	disconnect();
    }
    
    public static double getTotalBalance() {
    	connect();
    	double result = 0;
    	String query = "SELECT accbalance FROM account WHERE userid = 1";
    	Statement stmt = statement;
    	try {
    		stmt = connection.createStatement();
    		ResultSet rs= stmt.executeQuery(query);
    		while (rs.next()) {
    			double balance = rs.getDouble("accbalance");
    			result += balance;
    		}
    	}catch(SQLException sqle) {
    		System.err.println("Problem getting total balance");
    	}
    	disconnect();
    	return result;
    }
    
    public static int getNumberofBudgets(String accid) {
    	connect();
    	int result = 0;
    	String query = "SELECT * FROM budget WHERE accid="+accid;
    	Statement stmt = statement;
    	try {
    		stmt = connection.createStatement();
    		ResultSet rs = stmt.executeQuery(query);
    		while(rs.next()) {
    			result++;
    		}
    	}catch(SQLException sqle) {
    		System.err.println("Error getting total budgets");
    	}
    	
    	disconnect();
    	return result;
    }
    
    public static double getIncomePerMonth(String accid,int month) {
    	connect();
    	double result = 0;
    	String query = "";
    	if (month == 12){
    		query = "SELECT transactionamount FROM transaction WHERE accid = "+accid+ " AND transactiontype='Income' AND date >= '2025-"+month+"-01' AND date <= '2025-"+month+"-31'";
    	}else {
    		int nextMonth = month +1;
    		query = "SELECT transactionamount FROM transaction WHERE accid = "+accid+ " AND transactiontype='Income' AND date >= '2025-"+month+"-01' AND date <= '2025-"+nextMonth+"-1'";
    	}
    	Statement stmt = statement;
    	try {
    		stmt = connection.createStatement();
    		ResultSet rs = stmt.executeQuery(query);
    		while(rs.next()) {
    			double amount = rs.getDouble("transactionamount");
    			result += amount;
    		}
    	}catch(SQLException sqle) {
    		System.err.println("Error getting result "+month);
    	}
    	disconnect();
    	return result;
    }
}