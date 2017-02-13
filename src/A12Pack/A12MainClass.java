package A12Pack;

import java.sql.*;
import java.util.ArrayList;


public class A12MainClass {

	public static String dbName = "sun.jdbc.odbc.JdbcOdbcDriver";
	public static String dbConnection = "jdbc:odbc:DRIVER={"
			+ "Microsoft Access Driver (*.mdb)};"
			+ "DBQ=.//Ammache-Fawzi-database.mdb";
	
	// Main method
	public static void main(String[] args) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
	
		try {
			System.out.println("Establishing connection to database...");
			Class.forName(dbName); 
			conn = DriverManager.getConnection(dbConnection); // dbConnection in dbInfo class
			System.out.println("Connected to database.");
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM INPUTRel");
			
			
			while (rs.next()){
		
				// All attribute values in the current tuple in an Object called Tuple
				Tuple tuple = new Tuple(rs.getInt("TransId"), rs.getString("ZipCode"), 
						rs.getString("SchoolName"), rs.getInt("DependentStudents"), 
						rs.getInt("IndependentStudents"));
								
				 // Insert tuple t1 into Schools
				 // If tuple does not violate any constraints, it will be added to the relation
				 // Otherwise, the SQLException is handled by the method according to the SQLState
					if (satisfyConstraints(tuple.getDependentStudents(), tuple.getIndependentStudents()) 
							&& insertInApplications(conn, tuple)){
						
						//Update tuple t2 if all constraints are satisfied
						updateSchools(conn, tuple.getSchoolName(), tuple.getZipCode());
						
						// OUTPUTRel --> Success message
						outputSuccessMessage(conn, tuple);
					}
					else if (!satisfyConstraints(tuple.getDependentStudents(), tuple.getIndependentStudents())) { 
						// OUTPUTRel --> Failure message because of dynamic constraint violation
						String failureReason = "Dynamic";
						outputFailureMessage(conn, tuple, failureReason);
					}
					else 
						continue;
			}
			
			System.out.println("All transactions processed. \nValid tuples were added to Applications relation, "
					+ "and necessary updates were made to Schools relation. \nSee OUTPUTRel for error messages. ");
			
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {rs.close();
			stmt.close();
			conn.close();
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
	}

	// Function to insert insert tuple in Applications relation
	public static boolean insertInApplications(Connection conn, Tuple t) {
		PreparedStatement pstmt = null;
		try {
			String query = "INSERT INTO Applications " + "(ZipCode, SchoolName, DependentStudents, IndependentStudents) "
					+ "values (?, ?, ?, ?)";
			pstmt = conn.prepareStatement(query);
			
			pstmt.setString(1, t.getZipCode());
			pstmt.setString(2, t.getSchoolName());
			pstmt.setInt(3, t.getDependentStudents());
			pstmt.setInt(4, t.getIndependentStudents());
			pstmt.executeUpdate();
			return true;
			
		} catch (SQLException se) {
			String SQLState = se.getSQLState();
			String failureReason = "";
			
			if (SQLState.equals("S1000")){
				failureReason = "PK";
				outputFailureMessage(conn, t, failureReason);
			}
			
			else if (SQLState.equals("23000")){
				failureReason = "FK";
				outputFailureMessage(conn, t, failureReason);
			}
			else {
				failureReason = "N/A";
				outputFailureMessage(conn, t, failureReason);
				//throw se;
			}
			return false;
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// Function to update table Schools, which is related to Applications via FK
	public static void updateSchools(Connection conn, String schoolName, String zipCode) {
		Statement stmt = null;
		try {
		
			stmt = conn.createStatement();
			String query = "UPDATE Schools SET ZipCode = '" + zipCode + "' WHERE SchoolName = '" + schoolName +"'";
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// Function to satisfy Dynamic Constraint
	public static boolean satisfyConstraints (int dependentStudents, int independentStudents){
		// The number of dependent students cannot be zero if the number of independent
		// students is non-zero. They either both have to be zero, or both have to be
		// greater than zero
		
		if (dependentStudents == 0 && independentStudents > 0 )
			return false;
		else if (independentStudents == 0 && dependentStudents > 0)
			return false;
		else
			return true;	
	}
	
	// Function to write a success message in OUTPUTRel
	public static void outputSuccessMessage (Connection conn, Tuple t){
		// params (in order): TransId, ZipCode, SchoolName, DependentStudents, IndependentStudents 
	
		
		PreparedStatement pstmt = null;
		try {
			String query = "INSERT INTO OUTPUTRel " + "(TransId, RunMessage) "
					+ "values (?, ?)";
			pstmt = conn.prepareStatement(query);
			String RunMessage = "Transaction " + t.getTransId() + " succeeded for the following values of the input; "
					+ t.getZipCode() + ", " + t.getSchoolName() + ", " + t.getDependentStudents() + ", " + t.getIndependentStudents();
			
			pstmt.setInt(1, t.getTransId());
			pstmt.setString(2, RunMessage);
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	// Function to write a failure message in OUTPUTRel
	public static void outputFailureMessage (Connection conn, Tuple t, String failureReason){
		// params (in order): TransId, ZipCode, SchoolName, DependentStudents, IndependentStudents 		
		
		PreparedStatement pstmt = null;

		try {
			String query = "INSERT INTO OUTPUTRel " + "(TransId, RunMessage) "
					+ "values (?, ?)";
			pstmt = conn.prepareStatement(query);
			String RunMessage = "Transaction " + t.getTransId() + " failed due to a " + failureReason 
					+ " constraint, for the following values of the input; " 
					+ t.getZipCode() + ", " + t.getSchoolName() + ", " + t.getDependentStudents() + ", " + t.getIndependentStudents();
			
			pstmt.setInt(1, t.getTransId());
			pstmt.setString(2, RunMessage);
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
}
	
