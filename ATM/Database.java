// Package declaration
package ATM;

// Imports
import java.io.*;
import java.util.Properties;
import java.sql.*;

// Main class for Database connections
public class Database {
   // Configuration settings file
   // public static final String CONFIGURATION_FILE = "settings.config";
   public static final String CONFIGURATION_FILE = "settings_prod.config";


   public static void main(String[] args) {

      // Create a FileInputSteam object to read configuration settings file
      FileInputStream settingsFile = null;
      
      // Create a Properties object to load the configuration settings
      Properties propSettings = new Properties();

      // Load program's configuration settings
      try {
         // Read configuration settings file
         settingsFile = new FileInputStream(CONFIGURATION_FILE);
         // Load configurations
         propSettings.load(settingsFile);
         // Close FileInputStream object
         if(settingsFile != null) {
            settingsFile.close(); 
         }
      }
      catch (FileNotFoundException exception) {
         // Terminate program 
         exception.printStackTrace();
         System.exit(-1);
      }
      catch (IOException exception) {
         // Terminate program 
         exception.printStackTrace();
         System.exit(-1);
      }

      // Create string constants from settings
      final String CONNECTION_URL = String.format("jdbc:mysql://%s:%s/%s", propSettings.getProperty("DB_HOST"), propSettings.getProperty("DB_PORT"), propSettings.getProperty("DB_NAME"));
      final String CONNECTION_USER = propSettings.getProperty("DB_USER");
      final String CONNECTION_PASSWORD = propSettings.getProperty("DB_PASSWORD");

      try {

            // Step 1: Construct a database 'Connection' object called 'conn'
            Connection conn = DriverManager.getConnection(CONNECTION_URL, CONNECTION_USER, CONNECTION_PASSWORD);   // For MySQL only

            // Step 2: Construct a 'Statement' object called 'stmt' inside the Connection created (SHOULD USE PREPARED STATEMENT)
            Statement stmt = conn.createStatement();

            // Step 3: Write a SQL query string. Execute the SQL query via the 'Statement'.
            //  The query result is returned in a 'ResultSet' object called 'rset'.
            String strSelect = "SELECT * FROM User;";
            System.out.println("The SQL statement is: " + strSelect + "\n"); // Echo For debugging

            ResultSet rset = stmt.executeQuery(strSelect);

            // Step 4: Process the 'ResultSet' by scrolling the cursor forward via next().
            //  For each row, retrieve the contents of the cells with getXxx(columnName).
            System.out.println("The records selected are:");
            int rowCount = 0;
            // Row-cursor initially positioned before the first row of the 'ResultSet'.
            // rset.next() inside the whole-loop repeatedly moves the cursor to the next row.
            // It returns false if no more rows.
            while(rset.next()) {   // Repeatedly process each row
               String title = rset.getString("username");  // retrieve a 'String'-cell in the row
               String price = rset.getString("email");  // retrieve a 'double'-cell in the row
               String    qty   = rset.getString("passwordhash");       // retrieve a 'int'-cell in the row
               System.out.println(title + ", " + price + ", " + qty);
               ++rowCount;
         }

         System.out.println("Total number of records = " + rowCount);

      } 
      
      catch(SQLException ex) {

         ex.printStackTrace();

      }  // Step 5: Close conn and stmt - Done automatically by try-with-resources (JDK 7)
      catch(Exception e) {

         System.out.println(e);
      
      }

   }

}