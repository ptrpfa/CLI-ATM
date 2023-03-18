package Server;

// Imports
import java.io.*;
import java.util.*;

// Centralised class for reading and returning configuration settings
public class Settings {

    // Specify configurations file (production)
    public static final String FILEPATH = "ATM";
    public static final String CONFIGURATION_FILE = String.format("%s/settingsLocal.config", FILEPATH);

    // Local settings
    // public static final String CONFIGURATION_FILE = String.format("%s/settingsLocal.config", FILEPATH);
    // public static final String CONFIGURATION_FILE = String.format("%s/local_settings.config", FILEPATH);

    // Function to read database settings from the configuration file
    public static String[] getDBConfig() {

        // Initialise array
        String dbSettings[] = new String[5];

        // Create a FileInputSteam object to read configuration settings file
        FileInputStream settingsFile = null;
        
        // Create a Properties object to load the configuration settings
        Properties propSettings = new Properties();

        try{
            // Read configuration settings file
            settingsFile = new FileInputStream(CONFIGURATION_FILE);
            // Load configurations
            propSettings.load(settingsFile);
            // Close FileInputStream object
            if(settingsFile != null) {
                settingsFile.close(); 
            }
            // Get database settings 
            dbSettings[0] = propSettings.getProperty("DB_HOST");
            dbSettings[1] = propSettings.getProperty("DB_PORT");
            dbSettings[2] = propSettings.getProperty("DB_NAME");
            dbSettings[3] = propSettings.getProperty("DB_USER");
            dbSettings[4] = propSettings.getProperty("DB_PASSWORD");
        } 
        catch(Exception e){
            e.printStackTrace();
        }

        // Return array to caller
        return dbSettings;

    }

    // Function to read Twilio API keys from the configuration file
    public static String[] getTwilioAPIKeys() {

        // Initialise array
        String keys[] = new String[4];

        // Create a FileInputSteam object to read configuration settings file
        FileInputStream settingsFile = null;
        
        // Create a Properties object to load the configuration settings
        Properties propSettings = new Properties();

        try{
            // Read configuration settings file
            settingsFile = new FileInputStream(CONFIGURATION_FILE);
            // Load configurations
            propSettings.load(settingsFile);
            // Close FileInputStream object
            if(settingsFile != null) {
                settingsFile.close(); 
            }
            // Create API keys from settings
            keys[0] = propSettings.getProperty("ACCOUNT_SID");
            keys[1] = propSettings.getProperty("AUTH_TOKEN");
            keys[2] = propSettings.getProperty("PHONE_NO");
            keys[3] = propSettings.getProperty("OUTGOING_NO_1");
        } 
        catch(Exception e){
            e.printStackTrace();
        }

        // Return array of API keys to caller
        return keys;

    }

}