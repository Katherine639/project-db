/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.sql.Timestamp;


/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class DBproject{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public DBproject(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + DBproject.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		DBproject esql = null;
		
		try{
			System.out.println("(1)");
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new DBproject (dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add Plane");
				System.out.println("2. Add Pilot");
				System.out.println("3. Add Flight");
				System.out.println("4. Add Technician");
				System.out.println("5. Book Flight");
				System.out.println("6. List number of available seats for a given flight.");
				System.out.println("7. List total number of repairs per plane in descending order");
				System.out.println("8. List total number of repairs per year in ascending order");
				System.out.println("9. Find total number of passengers with a given status");
				System.out.println("10. < EXIT");
				System.out.println("Enter QUIT at any time to exit current option."); 
	
				switch (readChoice()){
					case 1: AddPlane(esql); break;
					case 2: AddPilot(esql); break;
					case 3: AddFlight(esql); break;
					case 4: AddTechnician(esql); break;
					case 5: BookFlight(esql); break;
					case 6: ListNumberOfAvailableSeats(esql); break;
					case 7: ListsTotalNumberOfRepairsPerPlane(esql); break;
					case 8: ListTotalNumberOfRepairsPerYear(esql); break;
					case 9: FindPassengersCountWithStatus(esql); break;
					case 10: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice

	public static void AddPlane(DBproject esql) {//1
		int plane_id = 0;
		String make = "";
		String model = "";
		int seats = 0;
		int age = 0;
		String QUIT = "QUIT"; 
		String input = "";

		System.out.println("\nYou have chosen to add a plane.");
		try{
			//generate new plane_id by incrementing current largest value
			List<List<String>> result = esql.executeQueryAndReturnResult("SELECT MAX(id) FROM Plane;");
			plane_id = Integer.parseInt(result.get(0).get(0))+1;
			//~ System.out.println("largest current plane id: " + plane_id);
			
			// plane make and model assuming the only available ones are what's listed already
			result = esql.executeQueryAndReturnResult("SELECT DISTINCT make FROM Plane;");
			List<String> makeList = new ArrayList<String>();
			for(int i = 0; i < result.size(); i++){
				makeList.add(result.get(i).get(0).replaceAll("\\s+",""));
				//~ System.out.println(makes.get(i));
			}
			
			boolean validMake = false;
			System.out.print("Enter make: ");
			do{
				input = in.readLine(); 
                                if (input.equals(QUIT) ) {
                                        System.out.print("Returning to main menu.\n\n");
                                        return;
                                }
				//make = in.readLine();
			
				if(makeList.contains(input)) {
					validMake = true;
				} else {
					System.out.print("Not a valid make. Please enter a valid make: ");
				}
			} while (validMake == false);	
			
			// assume the model inputted is correct if it starts with a letter
			// since there is no set standard for model names except for that
			boolean validModel = false;
			System.out.print("Enter model: ");
			do{
				input = in.readLine();
                                if (input.equals(QUIT) ) {
                                        System.out.print("Returning to main menu.\n\n");
                                        return;
                                }
	
				if(Character.isLetter(input.charAt(0))){
					validModel = true;
				} else {
					System.out.print("Not a valid model. Please enter a valid model: ");
				}
				
			} while(validModel == false);
			
			boolean validSeats = false;
			System.out.print("Enter seats (1-250): ");
			do{
				input = in.readLine(); 
                                if (input.equals(QUIT) ) {
                                        System.out.print("Returning to main menu.\n\n");
                                        return;
                                }
				seats = Integer.parseInt(input);
				if(seats <= 0 || seats > 250){
					System.out.print("Not a valid seat number. Please enter a number 1-250: ");
				} else {
					validSeats = true;
				}
			} while (validSeats == false);
			
			
			boolean validAge = false;
			System.out.print("Enter age: ");
			do{
				input = in.readLine(); 
                                if (input.equals(QUIT) ) {
                                        System.out.print("Returning to main menu.\n\n");
                                        return;
                                }
				age = Integer.parseInt(input);
				if(age > 0) validAge=true;
				else System.out.print("Not a valid age. Please enter a number higher than 0: ");
			} while (validAge == false);
			
			String query = "INSERT INTO Plane (id,make,model,age,seats) VALUES(";
			query += plane_id + ", '" + make +"', '" + model +"', " + age +", " + seats + ");"; 
			
				
			//System.out.println(query);
			esql.executeUpdate(query);
			System.out.print("Plane has been added. \n \n "); 
	}
	catch(Exception e){
		System.err.println("ERROR " + e.getMessage() + ". Please enter valid inputs.") ;
	}
}
	public static void AddPilot(DBproject esql) {//2
		int pilot_id = 0;
		String fullname = "";
		String nationality = "";
		String input = "";
		String QUIT = "QUIT";

		try {
			//generate new pilot_id by incrementing current largest value
			List<List<String>> result = esql.executeQueryAndReturnResult("SELECT MAX(id) FROM Pilot;");
			pilot_id = Integer.parseInt(result.get(0).get(0))+1;

			// assume the inputted is correct if it starts with a letter
			// since there is no set standard for names except that
			boolean validFullname = false;
			System.out.print("Enter full name: ");
			do{
				input = in.readLine();
                                if (input.equals(QUIT) ) {
                                        System.out.print("Returning to main menu.\n\n");
                                        return;
                                }
			
				if(Character.isLetter(input.charAt(0))){
					validFullname = true;
				} else {
					System.out.print("Not a valid full name. Please enter a name: ");
				}
				
			} while(validFullname == false);
			
			// assume the inputted is correct if it starts with a letter
			// since there is no set standard for names except that
			boolean validNationality = false;
			System.out.print("Enter nationality: ");
			do{
				input = in.readLine(); 
                                if (input.equals(QUIT) ) {
                                        System.out.print("Returning to main menu.\n\n");
                                        return;
                                }
	
				if(Character.isLetter(input.charAt(0))){
					validNationality = true;
				} else {
					System.out.print("Not a valid nationality. Please enter a valid nationality: ");
				}
				
			} while(validNationality == false);
			
			String query = "INSERT INTO Pilot (id,fullname,nationality) VALUES(";
			query += pilot_id + ", '" + fullname +"', '" + nationality +"');"; 
			
			esql.executeUpdate(query);
			System.out.print("Pilot has been added. \n \n"); 	
		}catch(Exception e){
			System.err.println("ERROR " + e.getMessage()+ ". Please enter valid inputs.");
		}
	}

	public static void AddFlight(DBproject esql) {//3
		// Given a pilot, plane and flight, adds a flight in the DB
		
		//int pilot_id = 0;
		String input = "";
		int pilot_id = 0;
		int plane_id = 0;
		int fnum = 0; // int flight_id = 0;
	    	int cost = 0;
	    	int num_sold = 0;
	    	int num_stops = 0;
		String actual_departure_date = "";
		String actual_arrival_date = "";
		String arrival_airport = "";
		String departure_airport = "";
		String QUIT = "QUIT"; 
		try{
			System.out.print("Enter Pilot Id: ");
			boolean validPilot = false;
			String existsChecker = "SELECT * FROM Pilot WHERE id = ";
			do{
				input = in.readLine(); //Integer.parseInt(in.readLine());
				if (input.equals(QUIT) ) {
					System.out.print("Returning to main menu.\n\n"); 
					return;
				} 

				pilot_id = Integer.parseInt(input);
                                existsChecker += pilot_id + " LIMIT 1;";				
				if (esql.executeQuery(existsChecker) == 0) {
						System.out.print("This pilot does not exist. Enter a valid pilot id: ");
				} else {
					validPilot = true;
				}
			}while(validPilot == false);
			
				
			System.out.print("Enter Plane Id: ");
			boolean validPlane = false;
			existsChecker = "SELECT * FROM Plane WHERE id = ";
			do{
				input = in.readLine(); //Integer.parseInt(in.readLine());
                                if (input.equals(QUIT) ) { 
                                        System.out.print("Returning to main menu.\n\n");
					return;
                                }
				plane_id = Integer.parseInt(input);
								
				existsChecker += plane_id + " LIMIT 1;";
				if (esql.executeQuery(existsChecker) == 0) {
						System.out.print("This plane does not exist. Enter a valid plane id: ");
				} else{
					validPlane = true;
				}
			} while (validPlane == false);
				
			boolean validArrCode = false;
			System.out.print("Enter arrival airport code: ");
			do{
				input = in.readLine(); 
                                if (input.equals(QUIT) ) {
                                        System.out.print("Returning to main menu.\n\n");
                                        return;
                                }

				if(Character.isLetter(input.charAt(0))){
					validArrCode = true;
				} else {
					System.out.print("Not a valid arrival airport code. Please re-enter: ");
				}
			} while (validArrCode == false);
			
			boolean validDeptCode = false; 
			System.out.print("Enter departure airport code: ");
			do{
				input = in.readLine(); 
                                if (input.equals(QUIT) ) {
                                        System.out.print("Returning to main menu.\n\n");
                                        return;
                                }

				if(Character.isLetter(input.charAt(0))){
					validDeptCode = true;
				} else {
					System.out.print("Not a valid departure airport code. Please re-enter: ");
				}
			} while (validDeptCode == false);
			
			
			System.out.print("Enter departure date (YYYY-MM-DD HH:MM:SS): ");
				actual_departure_date = in.readLine();
					
			System.out.print("Enter arrival date (YYYY-MM-DD HH:MM:SS): ");
				actual_arrival_date = in.readLine();
			
			System.out.print("Enter number of stops: ");
				num_stops = Integer.parseInt(in.readLine());
				
			System.out.print("Enter cost: ");
				cost = Integer.parseInt(in.readLine());
				
			System.out.print("Enter number sold: ");
				num_sold = Integer.parseInt(in.readLine());

			
			//generate new pilot_id by incrementing current largest value
			List<List<String>> result = esql.executeQueryAndReturnResult("SELECT MAX(fnum) FROM Flight;");
			fnum = Integer.parseInt(result.get(0).get(0))+1;
			String query = "INSERT INTO Flight (fnum, cost, num_sold, num_stops, actual_departure_date, actual_arrival_date, arrival_airport, departure_airport) VALUES ("; 
			query += fnum + " ," + cost + " ," + num_sold + " ," + num_stops + " ,'" + actual_departure_date + "' ,'" + actual_arrival_date + "' ,'" + arrival_airport + "' ,'" + departure_airport + "');";  
			//~ System.out.println(query);
			
			
			int fiid = 0; 
			result = esql.executeQueryAndReturnResult("SELECT MAX(fiid) FROM FlightInfo;");
			fiid = Integer.parseInt(result.get(0).get(0))+1;
			String FlightInfoQuery = "INSERT INTO FlightInfo (fiid, flight_id, pilot_id, plane_id) VALUES ("; 
			FlightInfoQuery += fiid + ", " + fnum + ", " + pilot_id + ", " + plane_id + ");";  
			//~ System.out.println(FlightInfoQuery);
			
			int sched_id = 0;
			result = esql.executeQueryAndReturnResult("SELECT MAX(id) FROM Schedule;");
			sched_id = Integer.parseInt(result.get(0).get(0))+1;
			String departure_time = "1111-11-11 11:11";
			String arrival_time = "1111-11-11 11:11";
			String ScheduleQuery = "INSERT INTO Schedule (id, flightNum, departure_time, arrival_time) VALUES (";
			ScheduleQuery += sched_id + ", " + fnum + " ,'" + departure_time + "' ,'" + arrival_time + "');";
			//~ System.out.println(ScheduleQuery);

			
			esql.executeUpdate(query);
			esql.executeUpdate(FlightInfoQuery);
			esql.executeUpdate(ScheduleQuery);
			System.out.print("Flight has been added.\n \n"); 
				
		} catch(Exception e) {
			System.err.println (e.getMessage()+ ". Please enter valid inputs.");
		}
		

	}

	public static void AddTechnician(DBproject esql) {//4
		int id = 0;
		String fullname = "";
		String input = "";
		String QUIT = "QUIT";

		try {
			//generate new pilot_id by incrementing current largest value
			List<List<String>> result = esql.executeQueryAndReturnResult("SELECT MAX(id) FROM Technician;");
			id = Integer.parseInt(result.get(0).get(0))+1;
			
			
			// assume the inputted is correct if it starts with a letter
			// since there is no set standard for names except that
			boolean validFullname = false;
			System.out.print("Enter full name: ");
			do{
				input = in.readLine(); 
                                if (input.equals(QUIT) ) {
                                        System.out.print("Returning to main menu.\n\n");
                                        return;
                                }
	
				if(Character.isLetter(input.charAt(0))){
					validFullname = true;
				} else {
					System.out.print("Not a valid full name. Please enter a name: ");
				}
				
			} while(validFullname == false);
			
			String query = "INSERT INTO Technician (id,full_name) VALUES(";
			query += id + ", '" + fullname + "');"; 
			
				
			//System.out.println(query);
			esql.executeUpdate(query);
			System.out.print("Technician has been added.\n \n");
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage()+ ". Please enter valid inputs.");
		}
		
	}
	

	//extra function to create customer profile
	public static int CreateProfile(DBproject esql) {
		int id = 0;
		String fname = "";
		String lname = "";
		String gender = "";
		String dob = "";
		String address = ""; 
		String contact_num = "";
		String ID = ""; 
		String zip = ""; 
		String M = "M";
		String F = "F";
		String input = "";
		String QUIT = "QUIT";

		System.out.print("Create new customer profile by entering following information. \n");  
		try {
			List<List<String>> result = esql.executeQueryAndReturnResult("SELECT MAX(id) FROM Customer");
			id = Integer.parseInt(result.get(0).get(0)) +1; 

			boolean validfname = false;
                        System.out.print("Enter first name: ");
                        do{
				input = in.readLine();
                                if (input.equals(QUIT) ) {
                                        System.out.print("Profile creation terminated.\n");
                                        return -1;
                                }
                                if(Character.isLetter(input.charAt(0))) validfname = true;
                                else System.out.print("Please enter a valid first name: ");
                        } while(validfname == false);	
	
			boolean validlname = false;
                        System.out.print("Enter last name: ");
                        do{
                                input = in.readLine();
                                if (input.equals(QUIT) ) {
                                        System.out.print("Profile creation terminated.\n");
                                        return -1;
                                }
                                if(Character.isLetter(input.charAt(0))) validlname = true;
                                else System.out.print("Please enter a valid last name: ");
                        } while(validlname == false);
			
			boolean validgender = false;
                        System.out.print("Enter gender (F/M): ");
                        do{
                                input = in.readLine();
                                if (input.equals(QUIT) ) {
                                        System.out.print("Profile creation terminated.\n");
                                        return -1;
                                }
                                if(M.equals(input) || F.equals(input)) validgender = true;
                                else System.out.print("Please enter M or F: ");
                        } while(validgender == false);

			boolean validdob = false;
                        System.out.print("Enter DOB(MM-DD-YYYY): ");
                        do{
                                input = in.readLine();
                                if (input.equals(QUIT) ) {
                                        System.out.print("Profile creation terminated.\n");
                                        return -1;
                                }
                                if(input.length() == 10) validdob = true;
                                else System.out.print("Please enter a valid DOB: ");
                        } while(validdob == false);

			boolean validaddress = false;
                        System.out.print("Enter address: ");
                        do{
				input = in.readLine();
                                if (input.equals(QUIT) ) {
                                        System.out.print("Profile creation terminated.\n");
                                        return -1;
                                }
                                if(Character.isLetter(input.charAt(0))) validaddress = true;
                                else System.out.print("Please enter a valid address: ");
                        } while(validaddress == false);

			boolean validcontact_num = false;
                        System.out.print("Enter a valid 10 digit conctact number: ");
                        do{
				input = in.readLine();
                                if (input.equals(QUIT) ) {
                                        System.out.print("Profile creation terminated.\n");
                                        return -1;
                                }
                                if(input.length() == 10) validcontact_num = true;
                                else System.out.print("Please enter a valid contact number: ");
                        } while(validcontact_num == false);

			boolean validzip = false;
                        System.out.print("Enter 5 digit zip code: ");
                        do{
				input = in.readLine();
                                if (input.equals(QUIT) ) {
                                        System.out.print("Profile creation terminated.\n");
                                        return -1;
                                }
                                if(input.length()==5) validzip = true;
                                else System.out.print("Please enter a valid zip code: ");
                        } while(validzip == false);
	
			String query = "INSERT INTO customer (id,fname,lname,gtype,dob,address,phone,zipcode) VALUES(";
                        query += id + ", '" + fname +"', '" + lname +"', '" + gender +"', '" + dob +"', '"+ address  +"', '" + contact_num +"', '"+ zip + "');";

                        esql.executeUpdate(query);
                        System.out.print("Customer profile has been created. \n");
			System.out.print("Your Customer ID is: " + id + ". \n\n"); 
			return 0;
		} catch (Exception e) {
                        System.out.println("ERROR: "+ e.getMessage()+". Please enter valid input.");
                }

		return 0;	
	}

	public static void BookFlight(DBproject esql) {//5
		// Given a customer and a flight that he/she wants to book, add a reservation to the DB
		// Get customer 
		try{
			int customer_id = 0;
			String flight_num = "";
			String depart_code = "";
			String arriv_code = "";
			String month = "";
			String year = "";
			String N = "N";
			String Y = "Y";
			List<List<String>> result = new ArrayList<List<String>>();
			String createProfile = "";
			boolean validCustomer = false;
			String input = "";
			String QUIT = "QUIT";

			do {
				System.out.print("Do you have a customer profile? (Y/N): "); 
				String response = in.readLine();
                                if (response.equals(QUIT) ) {
                                        System.out.print("Returning to main menu.\n\n");
                                        return;
                                }
				if(Y.equals(response)==true) {
					System.out.print("Enter customer ID: ");
					String existsChecker = "SELECT * FROM Customer WHERE id = ";
					input = in.readLine();
                                	if (input.equals(QUIT) ) {
                                        	System.out.print("Returning to main menu.\n\n");
                                        	return;
                                	}
					customer_id = Integer.parseInt(input);
					existsChecker += customer_id + " LIMIT 1;";
				
					if (esql.executeQuery(existsChecker) == 0) {
						System.out.print("Your profile does not exist. Please create one \n");
						//CreateProfile(DBproject esql); 
					}else {
						validCustomer = true;
					}
				}
				else if(N.equals(response)==true) {
					int q = CreateProfile(esql);
					if (q==-1) {
						System.out.print("Returning to main menu. \n\n"); 
						return; 
					} 
					System.out.print("Continue booking your flight.\n"); 
					validCustomer=true; 
				}
				else{
					System.out.print("in else"); 
					validCustomer=false; 
				}
			} while (validCustomer == false);
			
			boolean flightExists = false;
			do {	
				System.out.print("Enter departure airport code:");
				depart_code = in.readLine();
					
				System.out.print("Enter arrival airport code:");
				arriv_code = in.readLine();
						
				System.out.print("Enter departure month (MM):");
				month = in.readLine();
							
				System.out.print("Enter departure year (YYYY):");
				year = in.readLine();
							
				String query = "SELECT * FROM Flight WHERE EXTRACT(MONTH FROM actual_departure_date) = '" + month + "' AND EXTRACT(YEAR FROM actual_departure_date) = '" + year + "' AND arrival_airport = '" + arriv_code + "' AND departure_airport = '" + depart_code + "' ORDER BY cost;";	
				if(esql.executeQueryAndPrintResult(query) == 0) 
					System.out.print("No such flight exists. Please re-enter: "); 
				else flightExists = true;
					
			} while (flightExists == false);
			
			System.out.print("Enter the flight number of the flight you would like to reserve: ");
                        flight_num = in.readLine();

			int plane_id = 0;
			int num_seats = 0;
			int num_sold = 0;
				
			String flightInfoQuery = "SELECT plane_id FROM FlightInfo WHERE flight_id = " + flight_num;
			result = esql.executeQueryAndReturnResult(flightInfoQuery);
			plane_id = Integer.parseInt(result.get(0).get(0));
			//~ System.out.println("Plane #: " + plane_id);
				
			String flightQuery = "SELECT num_sold FROM Flight WHERE fnum = " + flight_num;
			result = esql.executeQueryAndReturnResult(flightQuery);
			num_sold = Integer.parseInt(result.get(0).get(0));
			//~ System.out.println("Seats sold: " + num_sold);
				
			String planeQuery = "SELECT seats FROM Plane WHERE id = " + plane_id;
			result = esql.executeQueryAndReturnResult(planeQuery);
			num_seats = Integer.parseInt(result.get(0).get(0));
			//~ System.out.println("Seats: " + num_seats);
							
			
			// Add reservation
			List<List<String>> rnumResult = esql.executeQueryAndReturnResult("SELECT MAX(rnum) FROM Reservation;");
			int rnum = Integer.parseInt(rnumResult.get(0).get(0))+1;
			
			String status = "";
			if (num_sold < num_seats) {
				esql.executeUpdate("UPDATE Flight SET num_sold = num_sold + 1 WHERE fnum = " + flight_num + ";");
				System.out.println("You have reserved a spot on flight: " + flight_num + "!");
				status = "R";
			}
			else if (num_sold >= num_seats) {
				System.out.println("You have been waitlisted");
				status = "W";
			}
			else {
				System.out.println("status could not be determined");
			}
			String reservationQuery = "INSERT INTO Reservation (rnum,cid,fid, status) VALUES(";
			reservationQuery += rnum + ", " + customer_id + ", " + flight_num + " ,'"+ status+ "');"; 

			esql.executeUpdate(reservationQuery);
			System.out.print("Reservation saved. \n\n"); 		
		
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		
	}

	public static void ListNumberOfAvailableSeats(DBproject esql) {//6
		// For flight number and date, find the number of availalbe seats (i.e. total plane capacity minus booked seats )
		
		int flight_num = 0;
		//String date = "0000-00-00";
		String input = "";
		String QUIT = "QUIT"; 	
		System.out.print("Enter flight_num: ");
		try {
			input = in.readLine();
                                if (input.equals(QUIT) ) {
                                        System.out.print("Returning to main menu.\n\n");
                                        return;
                        }
			flight_num = Integer.parseInt(input);
		}catch (Exception e) {
			System.out.print("Your input is invalid");
		}
		
		/*System.out.print("Enter departure date: ");
		try {
			date = in.readLine();
		}catch (Exception e) {
			System.out.print("Your input is invalid");	
		}*/
		
		//check if flight is full
		//if num_sold < num_seats -> reserve
		//else if num_sold >= num_seats -> waitlist
		
		List<List<String>> result;
	    int plane_id = 0;
		int num_seats = 0;
		int num_sold = 0;
		
		
		try{	
			String flightInfoQuery = "SELECT plane_id FROM FlightInfo WHERE flight_id = " + flight_num;
			result = esql.executeQueryAndReturnResult(flightInfoQuery);
			plane_id = Integer.parseInt(result.get(0).get(0));
			//~ System.out.println(plane_id);
			
			String flightQuery = "SELECT num_sold FROM Flight WHERE fnum = " + flight_num;
			result = esql.executeQueryAndReturnResult(flightQuery);
			num_sold = Integer.parseInt(result.get(0).get(0));
			//~ System.out.println(num_sold);
			
			String planeQuery = "SELECT seats FROM Plane WHERE id = " + plane_id;
			result = esql.executeQueryAndReturnResult(planeQuery);
			num_seats = Integer.parseInt(result.get(0).get(0));
			//~ System.out.println(num_seats);
			
		}catch (Exception e) { System.out.println(e.getMessage()); }
		
		System.out.println("Number of seats available: " + (num_seats - num_sold) + "\n\n");
		
	}

	public static void ListsTotalNumberOfRepairsPerPlane(DBproject esql) {//7
		// Count number of repairs per planes and list them in descending order
		List<Integer> planesList = new ArrayList<Integer>();
		List<Integer> repairsList = new ArrayList<Integer>();
		try{
			String query = "SELECT plane_id, COUNT(plane_id) FROM Repairs GROUP BY plane_id ORDER BY COUNT(plane_id) DESC;";
			List<List<String>> result = esql.executeQueryAndReturnResult(query);
			for(int i = 0; i < result.size(); i++){
				System.out.println("Plane " + Integer.parseInt(result.get(i).get(0)) + " has had " + Integer.parseInt(result.get(i).get(1)) + " repairs.");
			}
			
			
		} catch(Exception e) { System.out.println(e.getMessage()); }
	}

	public static void ListTotalNumberOfRepairsPerYear(DBproject esql) {//8
		// Count repairs per year and list them in ascending order
		String query = "SELECT COUNT(rid) FROM Repairs WHERE EXTRACT(YEAR FROM repair_date) =";
		try{
			// GET DISTINCT YEARS (result) in ascending order of COUNT(repair id's)
			List<List<String>> result = esql.executeQueryAndReturnResult("SELECT DISTINCT EXTRACT(YEAR FROM repair_date), COUNT(rid) FROM Repairs GROUP BY EXTRACT(YEAR FROM repair_date) ORDER BY COUNT(rid) ASC;");

			List<String> yearList = new ArrayList<String>();
			for(int i = 0; i < result.size(); i++){
				yearList.add(result.get(i).get(0).replaceAll("\\s+",""));
				// GET NUMBER OF REPAIRS FOR EACH DISTINCT YEAR (resultRepairs) in ascending order of COUNT(repair id's)
				List<List<String>> resultRepairs = esql.executeQueryAndReturnResult(query + yearList.get(i) + "ORDER BY COUNT(rid)");
				int num_repairs = Integer.parseInt(resultRepairs.get(0).get(0));
				
				System.out.println(num_repairs + " repairs made in " + result.get(i).get(0).replaceAll("\\s+",""));
			}
			
			
		
		
			//~ esql.executeQueryAndPrintResult(query);
		} catch(Exception e) { System.out.println(e.getMessage()); }
	}
	
	public static void FindPassengersCountWithStatus(DBproject esql) {//9
		// Find how many passengers there are with a status (i.e. W,C,R) and list that number.
		int flight_num = 0;
		String status = "";
		
		boolean validFlight = false;
		do {
			System.out.print("Enter flight_num: ");
		    String existsChecker = "SELECT * FROM Flight WHERE fnum = ";
			try {
				flight_num = Integer.parseInt(in.readLine());
				existsChecker += flight_num + " LIMIT 1;";
				if (esql.executeQuery(existsChecker) == 0) {
						System.out.println("This flight does not exist.");
				} else {
					validFlight = true;
				}
				
			}catch (Exception e) {
				System.out.println("Your input is invalid");
			}
		} while (validFlight == false);
		
		boolean validStatus = false;
		
		do {
			System.out.print("Enter status: ");
			try {
				status = in.readLine();
				if (status.equals("W") || status.equals("R") || status.equals("C")) {
					validStatus = true;
				} else {
					System.out.println("Not a valid status. Valid statuses include: R,W,C");
				}
				
			}catch (Exception e) {
				System.out.println("Your input is invalid");
				
			}
		} while (validStatus == false);
		
		String query = "SELECT status, COUNT(status) FROM Reservation WHERE fid = " + flight_num + " AND status = '" +
						status.trim() + "' GROUP BY status;";
		
		try{	
			esql.executeQueryAndPrintResult(query);
			
			
		}catch (Exception e) { System.out.println(e.getMessage()); }
		
	}
}
