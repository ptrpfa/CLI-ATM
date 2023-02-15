// Package declaration
package ATM;

// Imports
import java.io.*;
import java.util.*;
import java.sql.*;

// Main class for Database connections
public class Database {
   // Configuration settings file
   // public static final String CONFIGURATION_FILE = "settings.config";
   public static final String CONFIGURATION_FILE = "settings_prod.config";

   // Program entrypoint
   public static void main(String[] args) {

      /* Load configuration settings */
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


      /* Create database connection */
      try {

         // Create Connection object to database
         Connection dbConnection = DriverManager.getConnection(CONNECTION_URL, CONNECTION_USER, CONNECTION_PASSWORD); 

         // SQL Statements for testing
         String query = "SELECT * FROM User;";
         String insert = "INSERT INTO User VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
         String update = "UPDATE User SET Phone=\"+65 12345678\" WHERE Username LIKE ?"; // Add %?% during setting

         // Declare a PreparedStatement object
         PreparedStatement prepStatement;

         /* QUERY */
         prepStatement = dbConnection.prepareStatement(query);
         ResultSet results = prepStatement.executeQuery();
         // Loop through results of query
         while(results.next()) {   
            // Get column values
            int userid = results.getInt("UserID");
            String username = results.getString("Username");
            String salt = results.getString("PasswordSalt");
            String hash = results.getString("PasswordHash");
            String email = results.getString("Email");
            String phone = results.getString("Phone");
            String addressOne = results.getString("AddressOne");
            String addressTwo = results.getString("AddressTwo");
            String addressThree = results.getString("AddressThree");
            String postal = results.getString("PostalCode");
            Timestamp timestamp = results.getTimestamp("RegistrationDate");
            java.util.Date datetime = new java.util.Date(timestamp.getTime());
            boolean active = results.getInt("Active") > 0 ? true : false;

            // Print values
            System.out.printf("\n%s %s %s %s %s %s %s %s %s %s %s %s\n", userid, username, salt, hash, email, phone, addressOne ,addressTwo, addressThree, postal, datetime, active);
         }

         /* INSERT */
         // Hash Map of column values
         LinkedHashMap<String, Object> linkedhashmap = new LinkedHashMap<String, Object>();
         linkedhashmap.put("UserID", 11);
         linkedhashmap.put("Username", "HELLO HELLO");
         linkedhashmap.put("PasswordSalt", "pepper");
         linkedhashmap.put("PasswordHash", "hash");
         linkedhashmap.put("Email", "email@ello.com");
         linkedhashmap.put("Phone", "8593883");
         linkedhashmap.put("AddressOne", "a1");
         linkedhashmap.put("Address2", "a1");
         linkedhashmap.put("Address3", "a1");
         linkedhashmap.put("PostalCode", "testaddition");
         linkedhashmap.put("RegistrationDate", new java.util.Date());
         linkedhashmap.put("Active", 0);

         prepStatement = dbConnection.prepareStatement(insert);

         // Automatic setting of variable datatype argument list
         int index = 1;
         // Loop through each item in the hashmap
         for(Map.Entry<String, Object> entry : linkedhashmap.entrySet()) {

            String key = entry.getKey();
            Object value = entry.getValue();

            // check datatypes
            if(entry.getValue() instanceof String) {
               prepStatement.setString(index, value.toString());
               index++;
            }
            if(entry.getValue() instanceof Integer) {
               prepStatement.setInt(index, (int)value);
               index++;
            }
            else if(entry.getValue() instanceof Double) {
               prepStatement.setDouble(index, (Double)value);
               index++;
            }
            else if(entry.getValue() instanceof java.util.Date) {
               java.util.Date dateValue = (java.util.Date) value;
               Timestamp timestampValue = new Timestamp(dateValue.getTime());
               prepStatement.setTimestamp(index, timestampValue);
               index++;
            }
            else if(entry.getValue() instanceof Boolean) {
               int intValue = (Boolean)value ? 1 : 0;
               prepStatement.setInt(index, intValue);
               index++;
            }

         }

         System.out.println(prepStatement.executeUpdate() + " record effected!");

         /* UPDATE */
         prepStatement = dbConnection.prepareStatement(update);
         prepStatement.setString(1, "%" + "ggy" + "%");
         System.out.println(prepStatement.executeUpdate() + " record effected!");

         // Close connection
         if(dbConnection != null) {
            dbConnection.close();
         }

      } 
      catch(SQLException exception) {
         // Terminate program 
         exception.printStackTrace();
         System.exit(-1);
      } 
      catch(Exception exception) { // Catch miscellaneous exceptions
         // Terminate program
         exception.printStackTrace();
         System.exit(-1);
      }

   }

   // Interface implementations

   // Read (SELECT)
   // 

   // Create
   // public abstract boolean createDB()

   // Update

   // Delete

}