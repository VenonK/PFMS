package Types;

public class Budget {
	private double current;
	private double target;
	private String start;
	private int duration;
	private String category;
	
	public Budget(String category,double currentAmount,double targetAmount,String startDate,int duration) {
		this.current = currentAmount;
		this.target = targetAmount;
		this.start = startDate;
		this.duration = duration;
		this.category = category;
	}
	
	public double getCurrent() {return current;}
	public double getTarget() {return target;}
	public String getStartDate() {return start;}
	public int getDuration() {return duration;}
	public String getCategory() {return category;}
}
