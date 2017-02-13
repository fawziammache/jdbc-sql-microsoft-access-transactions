package A12Pack;

public class Tuple {
	
	private int transId;
	private String zipCode;
	private String schoolName;
	private int dependentStudents;
	private int independentStudents;
	
	
	public Tuple(int transId, String zipCode, String schoolName, int dependentStudents, int independentStudents){
		
		this.transId = transId;
		this.zipCode = zipCode;
		this.schoolName = schoolName;
		this.dependentStudents = dependentStudents;
		this.independentStudents = independentStudents;
	}
	
	public int getTransId() { return this.transId; }
	public String getZipCode() { return this.zipCode ; }
	public String getSchoolName() { return this.schoolName; }
	public int getDependentStudents() { return this.dependentStudents; }
	public int getIndependentStudents() { return this.independentStudents; }
	
	

}
