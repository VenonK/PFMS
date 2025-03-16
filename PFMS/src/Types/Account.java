package Types;

public class Account {
	private double limit;
	private String type;
	private double balance;

	
	public Account(String type,double balance,double limit) {
		this.limit = limit;
		this.type = type;
		this.balance = balance;
	}
	
	
	public String getType() {return type;}
	public double getLimit() {return limit;}
	public double getBalance() {return balance;}
	public void setBalance(double h) {this.balance = h;}
	
}
