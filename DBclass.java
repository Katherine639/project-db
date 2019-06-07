import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;























public class DBproject
{
  private Connection _connection = null;
  static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
  
  public DBproject(String paramString1, String paramString2, String paramString3, String paramString4) throws SQLException {
    System.out.print("Connecting to database...");
    try
    {
      String str = "jdbc:postgresql://localhost:" + paramString2 + "/" + paramString1;
      System.out.println("Connection URL: " + str + "\n");
      

      _connection = DriverManager.getConnection(str, paramString3, paramString4);
      System.out.println("Done");
    } catch (Exception localException) {
      System.err.println("Error - Unable to Connect to Database: " + localException.getMessage());
      System.out.println("Make sure you started postgres on this machine");
      System.exit(-1);
    }
  }
  






  public void executeUpdate(String paramString)
    throws SQLException
  {
    Statement localStatement = _connection.createStatement();
    

    localStatement.executeUpdate(paramString);
    

    localStatement.close();
  }
  








  public int executeQueryAndPrintResult(String paramString)
    throws SQLException
  {
    Statement localStatement = _connection.createStatement();
    

    ResultSet localResultSet = localStatement.executeQuery(paramString);
    




    ResultSetMetaData localResultSetMetaData = localResultSet.getMetaData();
    int i = localResultSetMetaData.getColumnCount();
    int j = 0;
    

    int k = 1;
    while (localResultSet.next()) {
      if (k != 0) {
        for (m = 1; m <= i; m++) {
          System.out.print(localResultSetMetaData.getColumnName(m) + "\t");
        }
        System.out.println();
        k = 0;
      }
      for (int m = 1; m <= i; m++)
        System.out.print(localResultSet.getString(m) + "\t");
      System.out.println();
      j++;
    }
    localStatement.close();
    return j;
  }
  








  public List<List<String>> executeQueryAndReturnResult(String paramString)
    throws SQLException
  {
    Statement localStatement = _connection.createStatement();
    

    ResultSet localResultSet = localStatement.executeQuery(paramString);
    




    ResultSetMetaData localResultSetMetaData = localResultSet.getMetaData();
    int i = localResultSetMetaData.getColumnCount();
    int j = 0;
    

    int k = 0;
    ArrayList localArrayList1 = new ArrayList();
    while (localResultSet.next()) {
      ArrayList localArrayList2 = new ArrayList();
      for (int m = 1; m <= i; m++)
        localArrayList2.add(localResultSet.getString(m));
      localArrayList1.add(localArrayList2);
    }
    localStatement.close();
    return localArrayList1;
  }
  







  public int executeQuery(String paramString)
    throws SQLException
  {
    Statement localStatement = _connection.createStatement();
    

    ResultSet localResultSet = localStatement.executeQuery(paramString);
    
    int i = 0;
    

    if (localResultSet.next()) {
      i++;
    }
    localStatement.close();
    return i;
  }
  








  public int getCurrSeqVal(String paramString)
    throws SQLException
  {
    Statement localStatement = _connection.createStatement();
    
    ResultSet localResultSet = localStatement.executeQuery(String.format("Select currval('%s')", new Object[] { paramString }));
    if (localResultSet.next()) return localResultSet.getInt(1);
    return -1;
  }
  

  public void cleanup()
  {
    try
    {
      if (_connection != null) {
        _connection.close();
      }
    }
    catch (SQLException localSQLException) {}
  }
  





  public static void main(String[] paramArrayOfString)
  {
    if (paramArrayOfString.length != 3) {
      System.err.println("Usage: java [-classpath <classpath>] " + DBproject.class
        .getName() + " <dbname> <port> <user>");
      
      return;
    }
    
    DBproject localDBproject = null;
    try
    {
      System.out.println("(1)");
      try
      {
        Class.forName("org.postgresql.Driver");
      }
      catch (Exception localException1) {
        System.out.println("Where is your PostgreSQL JDBC Driver? Include in your library path!");
        localException1.printStackTrace();
        return;
      }
      
      System.out.println("(2)");
      String str1 = paramArrayOfString[0];
      String str2 = paramArrayOfString[1];
      String str3 = paramArrayOfString[2];
      
      localDBproject = new DBproject(str1, str2, str3, "");
      
      int i = 1;
      while (i != 0) {
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
        
        switch (readChoice()) {
        case 1:  AddPlane(localDBproject); break;
        case 2:  AddPilot(localDBproject); break;
        case 3:  AddFlight(localDBproject); break;
        case 4:  AddTechnician(localDBproject); break;
        case 5:  BookFlight(localDBproject); break;
        case 6:  ListNumberOfAvailableSeats(localDBproject); break;
        case 7:  ListsTotalNumberOfRepairsPerPlane(localDBproject); break;
        case 8:  ListTotalNumberOfRepairsPerYear(localDBproject); break;
        case 9:  FindPassengersCountWithStatus(localDBproject); break;
        case 10:  i = 0; }
      }
      return;
    } catch (Exception localException3) {
      System.err.println(localException3.getMessage());
    } finally {
      try {
        if (localDBproject != null) {
          System.out.print("Disconnecting from database...");
          localDBproject.cleanup();
          System.out.println("Done\n\nBye !");
        }
      }
      catch (Exception localException6) {}
    }
  }
  
  public static int readChoice()
  {
    int i;
    for (;;)
    {
      System.out.print("Please make your choice: ");
      try {
        i = Integer.parseInt(in.readLine());
      }
      catch (Exception localException) {
        System.out.println("Your input is invalid!");
      }
    }
    
    return i;
  }
  
  public static void AddPlane(DBproject paramDBproject) {
    int i = 0;
    String str1 = "";
    String str2 = "";
    int j = 0;
    int k = 0;
    String str3 = "QUIT";
    String str4 = "";
    
    System.out.println("\nYou have chosen to add a plane.");
    try
    {
      List localList = paramDBproject.executeQueryAndReturnResult("SELECT MAX(id) FROM Plane;");
      i = Integer.parseInt((String)((List)localList.get(0)).get(0)) + 1;
      


      localList = paramDBproject.executeQueryAndReturnResult("SELECT DISTINCT make FROM Plane;");
      ArrayList localArrayList = new ArrayList();
      for (int m = 0; m < localList.size(); m++) {
        localArrayList.add(((String)((List)localList.get(m)).get(0)).replaceAll("\\s+", ""));
      }
      

      m = 0;
      System.out.print("Enter make: ");
      do {
        str4 = in.readLine();
        if (str4.equals(str3)) {
          System.out.print("Returning to main menu.\n\n");
          return;
        }
        

        if (localArrayList.contains(str4)) {
          m = 1;
        } else {
          System.out.print("Not a valid make. Please enter a valid make: ");
        }
      } while (m == 0);
      


      int n = 0;
      System.out.print("Enter model: ");
      do {
        str4 = in.readLine();
        if (str4.equals(str3)) {
          System.out.print("Returning to main menu.\n\n");
          return;
        }
        
        if (Character.isLetter(str4.charAt(0))) {
          n = 1;
        } else {
          System.out.print("Not a valid model. Please enter a valid model: ");
        }
        
      } while (n == 0);
      
      int i1 = 0;
      System.out.print("Enter seats (1-250): ");
      do {
        str4 = in.readLine();
        if (str4.equals(str3)) {
          System.out.print("Returning to main menu.\n\n");
          return;
        }
        j = Integer.parseInt(str4);
        if ((j <= 0) || (j > 250)) {
          System.out.print("Not a valid seat number. Please enter a number 1-250: ");
        } else {
          i1 = 1;
        }
      } while (i1 == 0);
      

      int i2 = 0;
      System.out.print("Enter age: ");
      do {
        str4 = in.readLine();
        if (str4.equals(str3)) {
          System.out.print("Returning to main menu.\n\n");
          return;
        }
        k = Integer.parseInt(str4);
        if (k > 0) i2 = 1; else
          System.out.print("Not a valid age. Please enter a number higher than 0: ");
      } while (i2 == 0);
      
      String str5 = "INSERT INTO Plane (id,make,model,age,seats) VALUES(";
      str5 = str5 + i + ", '" + str1 + "', '" + str2 + "', " + k + ", " + j + ");";
      


      paramDBproject.executeUpdate(str5);
      System.out.print("Plane has been added. \n \n ");
    }
    catch (Exception localException) {
      System.err.println("ERROR " + localException.getMessage() + ". Please enter valid inputs.");
    }
  }
  
  public static void AddPilot(DBproject paramDBproject) { int i = 0;
    String str1 = "";
    String str2 = "";
    String str3 = "";
    String str4 = "QUIT";
    
    try
    {
      List localList = paramDBproject.executeQueryAndReturnResult("SELECT MAX(id) FROM Pilot;");
      i = Integer.parseInt((String)((List)localList.get(0)).get(0)) + 1;
      


      int j = 0;
      System.out.print("Enter full name: ");
      do {
        str3 = in.readLine();
        if (str3.equals(str4)) {
          System.out.print("Returning to main menu.\n\n");
          return;
        }
        
        if (Character.isLetter(str3.charAt(0))) {
          j = 1;
        } else {
          System.out.print("Not a valid full name. Please enter a name: ");
        }
        
      } while (j == 0);
      


      int k = 0;
      System.out.print("Enter nationality: ");
      do {
        str3 = in.readLine();
        if (str3.equals(str4)) {
          System.out.print("Returning to main menu.\n\n");
          return;
        }
        
        if (Character.isLetter(str3.charAt(0))) {
          k = 1;
        } else {
          System.out.print("Not a valid nationality. Please enter a valid nationality: ");
        }
        
      } while (k == 0);
      
      String str5 = "INSERT INTO Pilot (id,fullname,nationality) VALUES(";
      str5 = str5 + i + ", '" + str1 + "', '" + str2 + "');";
      
      paramDBproject.executeUpdate(str5);
      System.out.print("Pilot has been added. \n \n");
    } catch (Exception localException) {
      System.err.println("ERROR " + localException.getMessage() + ". Please enter valid inputs.");
    }
  }
  


  public static void AddFlight(DBproject paramDBproject)
  {
    String str1 = "";
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    String str2 = "";
    String str3 = "";
    String str4 = "";
    String str5 = "";
    String str6 = "QUIT";
    try {
      System.out.print("Enter Pilot Id: ");
      int i2 = 0;
      String str7 = "SELECT * FROM Pilot WHERE id = ";
      do {
        str1 = in.readLine();
        if (str1.equals(str6)) {
          System.out.print("Returning to main menu.\n\n");
          return;
        }
        
        i = Integer.parseInt(str1);
        str7 = str7 + i + " LIMIT 1;";
        if (paramDBproject.executeQuery(str7) == 0) {
          System.out.print("This pilot does not exist. Enter a valid pilot id: ");
        } else {
          i2 = 1;
        }
      } while (i2 == 0);
      

      System.out.print("Enter Plane Id: ");
      int i3 = 0;
      str7 = "SELECT * FROM Plane WHERE id = ";
      do {
        str1 = in.readLine();
        if (str1.equals(str6)) {
          System.out.print("Returning to main menu.\n\n");
          return;
        }
        j = Integer.parseInt(str1);
        
        str7 = str7 + j + " LIMIT 1;";
        if (paramDBproject.executeQuery(str7) == 0) {
          System.out.print("This plane does not exist. Enter a valid plane id: ");
        } else {
          i3 = 1;
        }
      } while (i3 == 0);
      
      int i4 = 0;
      System.out.print("Enter arrival airport code: ");
      do {
        str1 = in.readLine();
        if (str1.equals(str6)) {
          System.out.print("Returning to main menu.\n\n");
          return;
        }
        
        if (Character.isLetter(str1.charAt(0))) {
          i4 = 1;
        } else {
          System.out.print("Not a valid arrival airport code. Please re-enter: ");
        }
      } while (i4 == 0);
      
      int i5 = 0;
      System.out.print("Enter departure airport code: ");
      do {
        str1 = in.readLine();
        if (str1.equals(str6)) {
          System.out.print("Returning to main menu.\n\n");
          return;
        }
        
        if (Character.isLetter(str1.charAt(0))) {
          i5 = 1;
        } else {
          System.out.print("Not a valid departure airport code. Please re-enter: ");
        }
      } while (i5 == 0);
      

      System.out.print("Enter departure date (YYYY-MM-DD HH:MM:SS): ");
      str2 = in.readLine();
      
      System.out.print("Enter arrival date (YYYY-MM-DD HH:MM:SS): ");
      str3 = in.readLine();
      
      System.out.print("Enter number of stops: ");
      i1 = Integer.parseInt(in.readLine());
      
      System.out.print("Enter cost: ");
      m = Integer.parseInt(in.readLine());
      
      System.out.print("Enter number sold: ");
      n = Integer.parseInt(in.readLine());
      


      List localList = paramDBproject.executeQueryAndReturnResult("SELECT MAX(fnum) FROM Flight;");
      k = Integer.parseInt((String)((List)localList.get(0)).get(0)) + 1;
      String str8 = "INSERT INTO Flight (fnum, cost, num_sold, num_stops, actual_departure_date, actual_arrival_date, arrival_airport, departure_airport) VALUES (";
      str8 = str8 + k + " ," + m + " ," + n + " ," + i1 + " ,'" + str2 + "' ,'" + str3 + "' ,'" + str4 + "' ,'" + str5 + "');";
      


      int i6 = 0;
      localList = paramDBproject.executeQueryAndReturnResult("SELECT MAX(fiid) FROM FlightInfo;");
      i6 = Integer.parseInt((String)((List)localList.get(0)).get(0)) + 1;
      String str9 = "INSERT INTO FlightInfo (fiid, flight_id, pilot_id, plane_id) VALUES (";
      str9 = str9 + i6 + ", " + k + ", " + i + ", " + j + ");";
      

      int i7 = 0;
      localList = paramDBproject.executeQueryAndReturnResult("SELECT MAX(id) FROM Schedule;");
      i7 = Integer.parseInt((String)((List)localList.get(0)).get(0)) + 1;
      String str10 = "1111-11-11 11:11";
      String str11 = "1111-11-11 11:11";
      String str12 = "INSERT INTO Schedule (id, flightNum, departure_time, arrival_time) VALUES (";
      str12 = str12 + i7 + ", " + k + " ,'" + str10 + "' ,'" + str11 + "');";
      


      paramDBproject.executeUpdate(str8);
      paramDBproject.executeUpdate(str9);
      paramDBproject.executeUpdate(str12);
      System.out.print("Flight has been added.\n \n");
    }
    catch (Exception localException) {
      System.err.println(localException.getMessage() + ". Please enter valid inputs.");
    }
  }
  

  public static void AddTechnician(DBproject paramDBproject)
  {
    int i = 0;
    String str1 = "";
    String str2 = "";
    String str3 = "QUIT";
    
    try
    {
      List localList = paramDBproject.executeQueryAndReturnResult("SELECT MAX(id) FROM Technician;");
      i = Integer.parseInt((String)((List)localList.get(0)).get(0)) + 1;
      



      int j = 0;
      System.out.print("Enter full name: ");
      do {
        str2 = in.readLine();
        if (str2.equals(str3)) {
          System.out.print("Returning to main menu.\n\n");
          return;
        }
        
        if (Character.isLetter(str2.charAt(0))) {
          j = 1;
        } else {
          System.out.print("Not a valid full name. Please enter a name: ");
        }
        
      } while (j == 0);
      
      String str4 = "INSERT INTO Technician (id,full_name) VALUES(";
      str4 = str4 + i + ", '" + str1 + "');";
      


      paramDBproject.executeUpdate(str4);
      System.out.print("Technician has been added.\n \n");
    } catch (Exception localException) {
      System.out.println("ERROR: " + localException.getMessage() + ". Please enter valid inputs.");
    }
  }
  


  public static int CreateProfile(DBproject paramDBproject)
  {
    int i = 0;
    String str1 = "";
    String str2 = "";
    String str3 = "";
    String str4 = "";
    String str5 = "";
    String str6 = "";
    String str7 = "";
    String str8 = "";
    String str9 = "M";
    String str10 = "F";
    String str11 = "";
    String str12 = "QUIT";
    
    System.out.print("Create new customer profile by entering following information. \n");
    try {
      List localList = paramDBproject.executeQueryAndReturnResult("SELECT MAX(id) FROM Customer");
      i = Integer.parseInt((String)((List)localList.get(0)).get(0)) + 1;
      
      int j = 0;
      System.out.print("Enter first name: ");
      do {
        str11 = in.readLine();
        if (str11.equals(str12)) {
          System.out.print("Profile creation terminated.\n");
          return -1;
        }
        if (Character.isLetter(str11.charAt(0))) j = 1; else
          System.out.print("Please enter a valid first name: ");
      } while (j == 0);
      
      int k = 0;
      System.out.print("Enter last name: ");
      do {
        str11 = in.readLine();
        if (str11.equals(str12)) {
          System.out.print("Profile creation terminated.\n");
          return -1;
        }
        if (Character.isLetter(str11.charAt(0))) k = 1; else
          System.out.print("Please enter a valid last name: ");
      } while (k == 0);
      
      int m = 0;
      System.out.print("Enter gender (F/M): ");
      do {
        str11 = in.readLine();
        if (str11.equals(str12)) {
          System.out.print("Profile creation terminated.\n");
          return -1;
        }
        if ((str9.equals(str11)) || (str10.equals(str11))) m = 1; else
          System.out.print("Please enter M or F: ");
      } while (m == 0);
      
      int n = 0;
      System.out.print("Enter DOB(MM-DD-YYYY): ");
      do {
        str11 = in.readLine();
        if (str11.equals(str12)) {
          System.out.print("Profile creation terminated.\n");
          return -1;
        }
        if (str11.length() == 10) n = 1; else
          System.out.print("Please enter a valid DOB: ");
      } while (n == 0);
      
      int i1 = 0;
      System.out.print("Enter address: ");
      do {
        str11 = in.readLine();
        if (str11.equals(str12)) {
          System.out.print("Profile creation terminated.\n");
          return -1;
        }
        if (Character.isLetter(str11.charAt(0))) i1 = 1; else
          System.out.print("Please enter a valid address: ");
      } while (i1 == 0);
      
      int i2 = 0;
      System.out.print("Enter a valid 10 digit conctact number: ");
      do {
        str11 = in.readLine();
        if (str11.equals(str12)) {
          System.out.print("Profile creation terminated.\n");
          return -1;
        }
        if (str11.length() == 10) i2 = 1; else
          System.out.print("Please enter a valid contact number: ");
      } while (i2 == 0);
      
      int i3 = 0;
      System.out.print("Enter 5 digit zip code: ");
      do {
        str11 = in.readLine();
        if (str11.equals(str12)) {
          System.out.print("Profile creation terminated.\n");
          return -1;
        }
        if (str11.length() == 5) i3 = 1; else
          System.out.print("Please enter a valid zip code: ");
      } while (i3 == 0);
      
      String str13 = "INSERT INTO customer (id,fname,lname,gtype,dob,address,phone,zipcode) VALUES(";
      str13 = str13 + i + ", '" + str1 + "', '" + str2 + "', '" + str3 + "', '" + str4 + "', '" + str5 + "', '" + str6 + "', '" + str8 + "');";
      
      paramDBproject.executeUpdate(str13);
      System.out.print("Customer profile has been created. \n");
      System.out.print("Your Customer ID is: " + i + ". \n\n");
      return 0;
    } catch (Exception localException) {
      System.out.println("ERROR: " + localException.getMessage() + ". Please enter valid input.");
    }
    
    return 0;
  }
  
  public static void BookFlight(DBproject paramDBproject)
  {
    try
    {
      int i = 0;
      String str1 = "";
      String str2 = "";
      String str3 = "";
      String str4 = "";
      String str5 = "";
      String str6 = "N";
      String str7 = "Y";
      Object localObject = new ArrayList();
      String str8 = "";
      int j = 0;
      String str9 = "";
      String str10 = "QUIT";
      do
      {
        System.out.print("Do you have a customer profile? (Y/N): ");
        String str11 = in.readLine();
        if (str11.equals(str10)) {
          System.out.print("Returning to main menu.\n\n");
          return;
        }
        if (str7.equals(str11) == true) {
          System.out.print("Enter customer ID: ");
          String str12 = "SELECT * FROM Customer WHERE id = ";
          str9 = in.readLine();
          if (str9.equals(str10)) {
            System.out.print("Returning to main menu.\n\n");
            return;
          }
          i = Integer.parseInt(str9);
          str12 = str12 + i + " LIMIT 1;";
          
          if (paramDBproject.executeQuery(str12) == 0) {
            System.out.print("Your profile does not exist. Please create one \n");
          }
          else {
            j = 1;
          }
        }
        else if (str6.equals(str11) == true) {
          int m = CreateProfile(paramDBproject);
          if (m == -1) {
            System.out.print("Returning to main menu. \n\n");
            return;
          }
          System.out.print("Continue booking your flight.\n");
          j = 1;
        }
        else {
          System.out.print("in else");
          j = 0;
        }
      } while (j == 0);
      
      int k = 0;
      do {
        System.out.print("Enter departure airport code:");
        str2 = in.readLine();
        
        System.out.print("Enter arrival airport code:");
        str3 = in.readLine();
        
        System.out.print("Enter departure month (MM):");
        str4 = in.readLine();
        
        System.out.print("Enter departure year (YYYY):");
        str5 = in.readLine();
        
        String str13 = "SELECT * FROM Flight WHERE EXTRACT(MONTH FROM actual_departure_date) = '" + str4 + "' AND EXTRACT(YEAR FROM actual_departure_date) = '" + str5 + "' AND arrival_airport = '" + str3 + "' AND departure_airport = '" + str2 + "' ORDER BY cost;";
        if (paramDBproject.executeQueryAndPrintResult(str13) == 0)
          System.out.print("No such flight exists. Please re-enter: "); else {
          k = 1;
        }
      } while (k == 0);
      
      System.out.print("Enter the flight number of the flight you would like to reserve: ");
      str1 = in.readLine();
      
      int n = 0;
      int i1 = 0;
      int i2 = 0;
      
      String str14 = "SELECT plane_id FROM FlightInfo WHERE flight_id = " + str1;
      localObject = paramDBproject.executeQueryAndReturnResult(str14);
      n = Integer.parseInt((String)((List)((List)localObject).get(0)).get(0));
      

      String str15 = "SELECT num_sold FROM Flight WHERE fnum = " + str1;
      localObject = paramDBproject.executeQueryAndReturnResult(str15);
      i2 = Integer.parseInt((String)((List)((List)localObject).get(0)).get(0));
      

      String str16 = "SELECT seats FROM Plane WHERE id = " + n;
      localObject = paramDBproject.executeQueryAndReturnResult(str16);
      i1 = Integer.parseInt((String)((List)((List)localObject).get(0)).get(0));
      



      List localList = paramDBproject.executeQueryAndReturnResult("SELECT MAX(rnum) FROM Reservation;");
      int i3 = Integer.parseInt((String)((List)localList.get(0)).get(0)) + 1;
      
      String str17 = "";
      if (i2 < i1) {
        paramDBproject.executeUpdate("UPDATE Flight SET num_sold = num_sold + 1 WHERE fnum = " + str1 + ";");
        System.out.println("You have reserved a spot on flight: " + str1 + "!");
        str17 = "R";
      }
      else if (i2 >= i1) {
        System.out.println("You have been waitlisted");
        str17 = "W";
      }
      else {
        System.out.println("status could not be determined");
      }
      String str18 = "INSERT INTO Reservation (rnum,cid,fid, status) VALUES(";
      str18 = str18 + i3 + ", " + i + ", " + str1 + " ,'" + str17 + "');";
      
      paramDBproject.executeUpdate(str18);
      System.out.print("Reservation saved. \n\n");
    }
    catch (Exception localException) {
      System.out.println(localException.getMessage());
    }
  }
  



  public static void ListNumberOfAvailableSeats(DBproject paramDBproject)
  {
    int i = 0;
    
    String str1 = "";
    String str2 = "QUIT";
    System.out.print("Enter flight_num: ");
    try {
      str1 = in.readLine();
      if (str1.equals(str2)) {
        System.out.print("Returning to main menu.\n\n");
        return;
      }
      i = Integer.parseInt(str1);
    } catch (Exception localException1) {
      System.out.print("Your input is invalid");
    }
    












    int j = 0;
    int k = 0;
    int m = 0;
    
    try
    {
      String str3 = "SELECT plane_id FROM FlightInfo WHERE flight_id = " + i;
      List localList = paramDBproject.executeQueryAndReturnResult(str3);
      j = Integer.parseInt((String)((List)localList.get(0)).get(0));
      

      String str4 = "SELECT num_sold FROM Flight WHERE fnum = " + i;
      localList = paramDBproject.executeQueryAndReturnResult(str4);
      m = Integer.parseInt((String)((List)localList.get(0)).get(0));
      

      String str5 = "SELECT seats FROM Plane WHERE id = " + j;
      localList = paramDBproject.executeQueryAndReturnResult(str5);
      k = Integer.parseInt((String)((List)localList.get(0)).get(0));
    }
    catch (Exception localException2) {
      System.out.println(localException2.getMessage());
    }
    System.out.println("Number of seats available: " + (k - m) + "\n\n");
  }
  

  public static void ListsTotalNumberOfRepairsPerPlane(DBproject paramDBproject)
  {
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    try {
      String str = "SELECT plane_id, COUNT(plane_id) FROM Repairs GROUP BY plane_id ORDER BY COUNT(plane_id) DESC;";
      List localList = paramDBproject.executeQueryAndReturnResult(str);
      for (int i = 0; i < localList.size(); i++) {
        System.out.println("Plane " + Integer.parseInt((String)((List)localList.get(i)).get(0)) + " has had " + Integer.parseInt((String)((List)localList.get(i)).get(1)) + " repairs.");
      }
    }
    catch (Exception localException) {
      System.out.println(localException.getMessage());
    }
  }
  
  public static void ListTotalNumberOfRepairsPerYear(DBproject paramDBproject) {
    String str = "SELECT COUNT(rid) FROM Repairs WHERE EXTRACT(YEAR FROM repair_date) =";
    try
    {
      List localList1 = paramDBproject.executeQueryAndReturnResult("SELECT DISTINCT EXTRACT(YEAR FROM repair_date), COUNT(rid) FROM Repairs GROUP BY EXTRACT(YEAR FROM repair_date) ORDER BY COUNT(rid) ASC;");
      
      ArrayList localArrayList = new ArrayList();
      for (int i = 0; i < localList1.size(); i++) {
        localArrayList.add(((String)((List)localList1.get(i)).get(0)).replaceAll("\\s+", ""));
        
        List localList2 = paramDBproject.executeQueryAndReturnResult(str + (String)localArrayList.get(i) + "ORDER BY COUNT(rid)");
        int j = Integer.parseInt((String)((List)localList2.get(0)).get(0));
        
        System.out.println(j + " repairs made in " + ((String)((List)localList1.get(i)).get(0)).replaceAll("\\s+", ""));
      }
      

    }
    catch (Exception localException)
    {
      System.out.println(localException.getMessage());
    }
  }
  
  public static void FindPassengersCountWithStatus(DBproject paramDBproject) {
    int i = 0;
    String str1 = "";
    
    int j = 0;
    do {
      System.out.print("Enter flight_num: ");
      String str2 = "SELECT * FROM Flight WHERE fnum = ";
      try {
        i = Integer.parseInt(in.readLine());
        str2 = str2 + i + " LIMIT 1;";
        if (paramDBproject.executeQuery(str2) == 0) {
          System.out.println("This flight does not exist.");
        } else {
          j = 1;
        }
      }
      catch (Exception localException1) {
        System.out.println("Your input is invalid");
      }
    } while (j == 0);
    
    int k = 0;
    do
    {
      System.out.print("Enter status: ");
      try {
        str1 = in.readLine();
        if ((str1.equals("W")) || (str1.equals("R")) || (str1.equals("C"))) {
          k = 1;
        } else {
          System.out.println("Not a valid status. Valid statuses include: R,W,C");
        }
      }
      catch (Exception localException2) {
        System.out.println("Your input is invalid");
      }
      
    } while (k == 0);
    

    String str3 = "SELECT status, COUNT(status) FROM Reservation WHERE fid = " + i + " AND status = '" + str1.trim() + "' GROUP BY status;";
    try
    {
      paramDBproject.executeQueryAndPrintResult(str3);
    }
    catch (Exception localException3) {
      System.out.println(localException3.getMessage());
    }
  }
}
