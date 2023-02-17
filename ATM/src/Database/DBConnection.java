// Package declaration
package Database;

// Imports
import java.io.*;
import java.util.*;
import java.sql.*;

// Interface for database connections
public abstract interface DBConnection {
    
    // Specify configurations file
    public static final String FILEPATH = ""; // Insert your file path here
    // public static final String CONFIGURATION_FILE = String.format("%s/settings.config", FILEPATH);
    public static final String CONFIGURATION_FILE = String.format("%s/settings_prod.config", FILEPATH);

    // Method to establish a database connection
    public default Connection getDBConnection() {

        // Initialise return variable
        Connection dbConnection = null;

        // Create a FileInputSteam object to read configuration settings file
        FileInputStream settingsFile = null;
        
        // Create a Properties object to load the configuration settings
        Properties propSettings = new Properties();

        try {

            // Read configuration settings file
            settingsFile = new FileInputStream(CONFIGURATION_FILE);

            // Load configurations
            propSettings.load(settingsFile);

            // Close FileInputStream object
            if(settingsFile != null) {
                settingsFile.close(); 
            }

            // Create string constants from settings
            final String CONNECTION_URL = String.format("jdbc:mysql://%s:%s/%s", propSettings.getProperty("DB_HOST"), propSettings.getProperty("DB_PORT"), propSettings.getProperty("DB_NAME"));
            final String CONNECTION_USER = propSettings.getProperty("DB_USER");
            final String CONNECTION_PASSWORD = propSettings.getProperty("DB_PASSWORD");
            
            // Initialise Connection object to database
            dbConnection = DriverManager.getConnection(CONNECTION_URL, CONNECTION_USER, CONNECTION_PASSWORD); 

        }
        catch(Exception exception) { // Catch miscellaneous exceptions
            exception.printStackTrace();
        }
        finally {
            // Return Connection object to calling function
            return dbConnection;
        }
    }

    // Method to close a database connection (for redundancy)
    public default boolean closeDBConnection(Connection dbConnection) {
        
        try {
            // Close connection
            if(dbConnection != null) {
                dbConnection.close();
            }
            // Return true
            return true;
        }
        catch(SQLException exception) {
            // Return false
            exception.printStackTrace();
            return false;
        } 
        catch(Exception exception) { // Catch miscellaneous exceptions
            // Return false
            exception.printStackTrace();
            return false;
        }

    }

    // Method to get the values of an object from the database (SELECT * FROM <table> WHERE id = <identifier>)
    public default HashMap<String, Object> getValues(String classType, String identifier) {
        
        // Arguments:
        // classType: Specify class type (ie User, Transaction, etc)
        // identifier: Specify identifier (ie Username, TransactionNo, etc)

        // User (NormalUser or BusinessUser): Get details of a user using their username
        // Account: Get details of an account using its accountNo
        // UserAccount: Get 

        // Get a database connection
        Connection dbConnection = getDBConnection();
        // Return to caller immediately if no connection was established
        if(dbConnection == null){
            return null;
        }

        // Initialise return variable
        HashMap<String, Object> values = new HashMap<String, Object>();

        if(classType.toLowerCase().equals("user")) {
            // Create SQL query
            String query = "SELECT * FROM User WHERE Username = ?;";
            try {
                PreparedStatement prepStatement = dbConnection.prepareStatement(query);
                prepStatement.setString(1, identifier);
                ResultSet results = prepStatement.executeQuery();
                results.next(); // Assume query will always yield a result!
                
                // Parse results
                int userid = results.getInt("UserID");
                String username = results.getString("Username");
                String passwordSalt = results.getString("PasswordSalt");
                String passwordHash = results.getString("PasswordHash");
                String email = results.getString("Email");
                String phone = results.getString("Phone");
                String addressOne = results.getString("AddressOne");
                String addressTwo = results.getString("AddressTwo");
                String addressThree = results.getString("AddressThree");
                String postalCode = results.getString("PostalCode");
                Timestamp timestamp = results.getTimestamp("RegistrationDate");
                java.util.Date datetime = new java.util.Date(timestamp.getTime());
                boolean active = results.getInt("Active") > 0 ? true : false;       // 1: Active, 0: Inactive
                
                // Add results into hashmap
                values.put("UserID", userid);
                values.put("Username", username);
                values.put("PasswordSalt", passwordSalt);
                values.put("PasswordHash", passwordHash);
                values.put("Email", email);
                values.put("Phone", phone);
                values.put("AddressOne", addressOne);
                values.put("AddressTwo", addressTwo);
                values.put("AddressThree", addressThree);
                values.put("PostalCode", postalCode);
                values.put("RegistrationDate", datetime);
                values.put("Active", active);
            }
            catch(SQLException exception) {
                exception.printStackTrace();
            }
            catch(Exception exception) {
                exception.printStackTrace();
            }
            finally {
                // Close database connection
                closeDBConnection(dbConnection);
            }
        }
        else if(classType.toLowerCase().equals("normaluser")) {
            // Create SQL query
            String query = "SELECT User.*, NormalUser.* FROM User LEFT JOIN NormalUser ON User.UserID = NormalUser.UserID WHERE User.UserID = (SELECT UserID FROM User WHERE Username = ?);";
            try {
                PreparedStatement prepStatement = dbConnection.prepareStatement(query);
                prepStatement.setString(1, identifier);
                ResultSet results = prepStatement.executeQuery();
                results.next(); // Assume query will always yield a result!
                
                // Parse results
                int userid = results.getInt("UserID");
                String username = results.getString("Username");
                String passwordSalt = results.getString("PasswordSalt");
                String passwordHash = results.getString("PasswordHash");
                String email = results.getString("Email");
                String phone = results.getString("Phone");
                String addressOne = results.getString("AddressOne");
                String addressTwo = results.getString("AddressTwo");
                String addressThree = results.getString("AddressThree");
                String postalCode = results.getString("PostalCode");
                Timestamp timestamp = results.getTimestamp("RegistrationDate");
                java.util.Date datetime = new java.util.Date(timestamp.getTime());
                boolean active = results.getInt("Active") > 0 ? true : false;       // 1: Active, 0: Inactive
                String NRIC = results.getString("NRIC");
                String firstName = results.getString("FirstName");
                String middleName = results.getString("MiddleName");
                String lastName = results.getString("LastName");
                String gender = results.getString("Gender");
                timestamp = results.getTimestamp("Birthday");
                java.util.Date birthday = new java.util.Date(timestamp.getTime());
                
                // Add results into hashmap
                values.put("UserID", userid);
                values.put("Username", username);
                values.put("PasswordSalt", passwordSalt);
                values.put("PasswordHash", passwordHash);
                values.put("Email", email);
                values.put("Phone", phone);
                values.put("AddressOne", addressOne);
                values.put("AddressTwo", addressTwo);
                values.put("AddressThree", addressThree);
                values.put("PostalCode", postalCode);
                values.put("RegistrationDate", datetime);
                values.put("Active", active);
                values.put("NRIC", NRIC);
                values.put("FirstName", firstName);
                values.put("MiddleName", middleName);
                values.put("LastName", lastName);
                values.put("Gender", gender);
                values.put("Birthday", birthday);
            }
            catch(SQLException exception) {
                exception.printStackTrace();
            }
            catch(Exception exception) {
                exception.printStackTrace();
            }
            finally {
                // Close database connection
                closeDBConnection(dbConnection);
            }
        }
        else if(classType.toLowerCase().equals("businessuser")) {
            // Create SQL query
            String query = "SELECT User.*, BusinessUser.* FROM User LEFT JOIN BusinessUser ON User.UserID = BusinessUser.UserID WHERE User.UserID = (SELECT UserID FROM User WHERE Username = ?);";
            try {
                PreparedStatement prepStatement = dbConnection.prepareStatement(query);
                prepStatement.setString(1, identifier);
                ResultSet results = prepStatement.executeQuery();
                results.next(); // Assume query will always yield a result!
                
                // Parse results
                int userid = results.getInt("UserID");
                String username = results.getString("Username");
                String passwordSalt = results.getString("PasswordSalt");
                String passwordHash = results.getString("PasswordHash");
                String email = results.getString("Email");
                String phone = results.getString("Phone");
                String addressOne = results.getString("AddressOne");
                String addressTwo = results.getString("AddressTwo");
                String addressThree = results.getString("AddressThree");
                String postalCode = results.getString("PostalCode");
                Timestamp timestamp = results.getTimestamp("RegistrationDate");
                java.util.Date datetime = new java.util.Date(timestamp.getTime());
                boolean active = results.getInt("Active") > 0 ? true : false;       // 1: Active, 0: Inactive
                String UEN = results.getString("UEN");
                String businessName = results.getString("BusinessName");
                
                // Add results into hashmap
                values.put("UserID", userid);
                values.put("Username", username);
                values.put("PasswordSalt", passwordSalt);
                values.put("PasswordHash", passwordHash);
                values.put("Email", email);
                values.put("Phone", phone);
                values.put("AddressOne", addressOne);
                values.put("AddressTwo", addressTwo);
                values.put("AddressThree", addressThree);
                values.put("PostalCode", postalCode);
                values.put("RegistrationDate", datetime);
                values.put("Active", active);
                values.put("UEN", UEN);
                values.put("BusinessName", businessName);
            }
            catch(SQLException exception) {
                exception.printStackTrace();
            }
            catch(Exception exception) {
                exception.printStackTrace();
            }
            finally {
                // Close database connection
                closeDBConnection(dbConnection);
            }
        }
        else if(classType.toLowerCase().equals("account")){
            // Create SQL query
            String query = "SELECT * FROM User WHERE Username = ?;";
            try {
                PreparedStatement prepStatement = dbConnection.prepareStatement(query);
                prepStatement.setString(1, identifier);
                ResultSet results = prepStatement.executeQuery();
                results.next(); // Assume query will always yield a result!
                
                // Parse results
                int userid = results.getInt("UserID");
                String username = results.getString("Username");
                String passwordSalt = results.getString("PasswordSalt");
                String passwordHash = results.getString("PasswordHash");
                String email = results.getString("Email");
                String phone = results.getString("Phone");
                String addressOne = results.getString("AddressOne");
                String addressTwo = results.getString("AddressTwo");
                String addressThree = results.getString("AddressThree");
                String postalCode = results.getString("PostalCode");
                Timestamp timestamp = results.getTimestamp("RegistrationDate");
                java.util.Date datetime = new java.util.Date(timestamp.getTime());
                boolean active = results.getInt("Active") > 0 ? true : false;       // 1: Active, 0: Inactive
                
                // Add results into hashmap
                values.put("UserID", userid);
                values.put("Username", username);
                values.put("PasswordSalt", passwordSalt);
                values.put("PasswordHash", passwordHash);
                values.put("Email", email);
                values.put("Phone", phone);
                values.put("AddressOne", addressOne);
                values.put("AddressTwo", addressTwo);
                values.put("AddressThree", addressThree);
                values.put("PostalCode", postalCode);
                values.put("RegistrationDate", datetime);
                values.put("Active", active);
            }
            catch(SQLException exception) {
                exception.printStackTrace();
            }
            catch(Exception exception) {
                exception.printStackTrace();
            }
            finally {
                // Close database connection
                closeDBConnection(dbConnection);
            }
        }
        else if(classType.toLowerCase().equals("transaction")){
            ;
        }
        else if(classType.toLowerCase().equals("cheque")){
            ;
        }
        else if(classType.toLowerCase().equals("chequeaccount")){
            // to check account type (receiving or issuing account)
            ;
        }
        else if(classType.toLowerCase().equals("chequetransaction")){
            // to check transaction type (issuing or receiving transaction)
            ;
        }
    
        // Return value mappings to calling function
        return values;
    }

}