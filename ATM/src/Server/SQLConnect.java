package Server;

// Imports
import java.io.*;
import java.util.*;
import java.sql.*;

public interface SQLConnect {
    // Specify configurations file
    public static final String FILEPATH = "/Users/peter/Desktop/Codebase/Assignments/CSC1109-ATM/ATM";
    // public static final String FILEPATH_ADRIANO = "C:/Users/kerop/Documents/1109 JAVA-OOP/CSC1109-ATM/ATM/settings.config";
    // public static final String FILEPATH_DINIE = "ATM";
    // public static final String CONFIGURATION_FILE = String.format("%s/settings.config", FILEPATH);
    public static final String CONFIGURATION_FILE = String.format("%s/settings_prod.config", FILEPATH);
    // public static final String CONFIGURATION_FILE = String.format("%s/settings.config", FILEPATH_ADRIANO);
    // public static final String CONFIGURATION_FILE = String.format("%s/settings.config", FILEPATH_DINIE);
    
    // Connect to databse
    public default Connection getDBConnection() {
        // Initialise return variable
        Connection dbConnection = null;

        // Create a FileInputSteam object to read configuration settings file
        FileInputStream settingsFile = null;
        
        // Create a Properties object to load the configuration settings
        Properties propSettings = new Properties();

        if(dbConnection == null){
            try{
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


            }catch(Exception e){
                e.printStackTrace();
            }
        }
        // Return Connection object to calling function
        return dbConnection;
    }

    // Close Connection
    public default void disconnectDB(Connection db){
        if (db != null) {
            try {
                db.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
