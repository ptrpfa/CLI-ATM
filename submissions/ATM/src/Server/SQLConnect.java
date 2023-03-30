package Server;

// Imports
import java.sql.*;

// Interface for database connections
public interface SQLConnect {

    // Connect to databse
    public static Connection getDBConnection() {

        // Initialise connection object
        Connection dbConnection = null;

        try {
            // Read database settings from configuration file
            String dbSettings[] = Settings.getDBConfig();
            String connectionURL = String.format("jdbc:mysql://%s:%s/%s", dbSettings[0], dbSettings[1], dbSettings[2]);
            
            // Establish a connection to the database
            dbConnection = DriverManager.getConnection(connectionURL, dbSettings[3], dbSettings[4]); 
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        // Return Connection object to calling function
        return dbConnection;

    }

    // Close Connection
    public static void disconnectDB(Connection db){
        if (db != null) {
            try {
                db.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
