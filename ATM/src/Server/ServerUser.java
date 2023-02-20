package Server;

import java.sql.*;

import User.BusinessUser;
import User.NormalUser;

class ServerUser implements SQLConnect {

    // Fetching of user information when normal user logs in
    public NormalUser findNormalUser(int userID) {
        Connection db = SQLConnect.super.getDBConnection();

        // Create default NormalUser object to return user afterwards
        NormalUser normalUser = new NormalUser();

        // Template to select "User" and "NormalUser" database for fetching data
        String sql1 = String.format("SELECT * FROM user WHERE UserID = %s", userID);
        String sql2 = String.format("SELECT * FROM normaluser WHERE UserID = %s", userID);

        // Try to connect with inputted UserID
        try {
            PreparedStatement statement1 = db.prepareStatement(sql1);
            PreparedStatement statement2 = db.prepareStatement(sql2);

            // Prepare to read database of where inputted UserID is located
            ResultSet myRs1 = statement1.executeQuery();  
            ResultSet myRs2 = statement2.executeQuery();  

            // Start reading after the title row onwards
            myRs1.next();
            myRs2.next();

            // Read from User and NormalUser database and load user with saved particulars 
            normalUser = new NormalUser(userID, 
                                        myRs1.getString("Username"), 
                                        myRs1.getString("PasswordSalt"), 
                                        myRs1.getString("PasswordHash"), 
                                        myRs1.getString("Email"), 
                                        myRs1.getString("Phone"), 
                                        myRs1.getString("AddressOne"), 
                                        myRs1.getString("AddressTwo"), 
                                        myRs1.getString("AddressThree"), 
                                        myRs1.getString("PostalCode"), 
                                        myRs1.getDate("RegistrationDate"),
                                        myRs1.getBoolean("Active"),

                                        myRs2.getString("NRIC"),
                                        myRs2.getString("FirstName"),
                                        myRs2.getString("MiddleName"),
                                        myRs2.getString("LastName"),
                                        myRs2.getString("Gender"),
                                        myRs2.getDate("Birthday"));            
        } 
        catch (SQLException e) {
            e.printStackTrace();
        } 
        finally {
            SQLConnect.super.disconnectDB(db);
        }
        return normalUser;
    }

    // Fetching of user information when business user logs in
    public BusinessUser findBusinessUser(int userID) {
        Connection db = SQLConnect.super.getDBConnection();

        // Create default BusinessUser object to return user afterwards
        BusinessUser businessUser = new BusinessUser();

        // Template to select "User" and "BusinessUser" database for fetching data
        String sql1 = String.format("SELECT * FROM user WHERE UserID = %s", userID);
        String sql2 = String.format("SELECT * FROM businessuser WHERE UserID = %s", userID);

        // Try to connect with inputted UserID
        try {
            PreparedStatement statement1 = db.prepareStatement(sql1);
            PreparedStatement statement2 = db.prepareStatement(sql2);

            // Prepare to read database of where inputted UserID is located
            ResultSet myRs1 = statement1.executeQuery();  
            ResultSet myRs2 = statement2.executeQuery();  

            // Start reading after the title row onwards
            myRs1.next();
            myRs2.next();

            // Read from User and NormalUser database and load user with saved particulars 
            businessUser = new BusinessUser(userID, 
                                        myRs1.getString("Username"), 
                                        myRs1.getString("PasswordSalt"), 
                                        myRs1.getString("PasswordHash"), 
                                        myRs1.getString("Email"), 
                                        myRs1.getString("Phone"), 
                                        myRs1.getString("AddressOne"), 
                                        myRs1.getString("AddressTwo"), 
                                        myRs1.getString("AddressThree"), 
                                        myRs1.getString("PostalCode"), 
                                        myRs1.getDate("RegistrationDate"),
                                        myRs1.getBoolean("Active"),

                                        myRs2.getString("UEN"),
                                        myRs2.getString("BusinessName"));            
        } 
        catch (SQLException e) {
            e.printStackTrace();
        } 
        finally {
            SQLConnect.super.disconnectDB(db);
        }
        return businessUser;
    }

    // Call this method at end of session to update database with latest information
    public void updateNormalUser(NormalUser normalUser) {
        Connection db = SQLConnect.super.getDBConnection();

        // Template to select "User" and "NormalUser" database for updating data
        String sql1 = String.format("UPDATE User SET Username = ?, Email = ?, Phone = ?, AddressOne = ?, AddressTwo = ?, AddressThree = ?, PostalCode = ? WHERE UserID = %s", normalUser.getUserID());
        String sql2 = String.format("UPDATE NormalUser SET NRIC = ?, FirstName = ?, MiddleName = ?, LastName = ?, Gender = ? WHERE UserID = %s", normalUser.getUserID());

        // Try to connect to "User" and "NormalUser" database
        try {
            PreparedStatement statement1 = db.prepareStatement(sql1);
            PreparedStatement statement2 = db.prepareStatement(sql2);

            // Fill up update statements with latest particulars
            statement1.setString(1, normalUser.getUsername());
            statement1.setString(2, normalUser.getEmail());
            statement1.setString(3, normalUser.getPhone());
            statement1.setString(4, normalUser.getAddresses(1));
            statement1.setString(5, normalUser.getAddresses(2));
            statement1.setString(6, normalUser.getAddresses(3));
            statement1.setString(7, normalUser.getPostalCode());

            statement2.setString(1, normalUser.getNRIC());
            statement2.setString(2, normalUser.getFirstName());
            statement2.setString(3, normalUser.getMiddleName());
            statement2.setString(4, normalUser.getLastName());
            statement2.setString(5, normalUser.getGender());

            // Perform database updates
            statement1.executeUpdate();
            statement2.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        } 
        finally {
            SQLConnect.super.disconnectDB(db);
        }
    }

    // Call this method at end of session to update database with latest information
    public void updateBusinessUser(BusinessUser businessUser) {
        Connection db = SQLConnect.super.getDBConnection();

        // Template to select "User" and "BusinessUser" database for updating data
        String sql1 = String.format("UPDATE user SET Username = ?, Email = ?, Phone = ?, AddressOne = ?, AddressTwo = ?, AddressThree = ?, PostalCode = ? WHERE UserID = %s", businessUser.getUserID());
        String sql2 = String.format("UPDATE businessuser SET UEN = ?, BusinessName = ? WHERE UserID = %s", businessUser.getUserID());

        // Try to connect to "User" and "BusinessUser" database
        try {
            PreparedStatement statement1 = db.prepareStatement(sql1);
            PreparedStatement statement2 = db.prepareStatement(sql2);

            // Fill up update statements with latest particulars
            statement1.setString(1, businessUser.getUsername());
            statement1.setString(2, businessUser.getEmail());
            statement1.setString(3, businessUser.getPhone());
            statement1.setString(4, businessUser.getAddresses(1));
            statement1.setString(5, businessUser.getAddresses(2));
            statement1.setString(6, businessUser.getAddresses(3));
            statement1.setString(7, businessUser.getPostalCode());

            statement2.setString(1, businessUser.getUEN());
            statement2.setString(2, businessUser.getBusinessName());

            // Perform database updates
            statement1.executeUpdate();
            statement2.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        } 
        finally {
            SQLConnect.super.disconnectDB(db);
        }
    }

    public static void main(String[] args) {
        Integer userID = 833022;

        ServerUser testUser = new ServerUser();
        NormalUser testestUser = testUser.findNormalUser(userID);
        
        testestUser.setUsername("Na0m1_N30");
        testestUser.setEmail("naomi@neo.sg");
        testestUser.setPhone("+65 91234567");
        testestUser.setAddress("Singapore", "SIT", "NYP", "626393");

        testestUser.setAllNames("Naomi", "梁文珊", "Neo");
        testestUser.setNRIC("S3333333X");

        testUser.updateNormalUser(testestUser);

        System.out.println("\nGood day Mr/Ms " + testestUser.getLastName() + ", " + testestUser.getFirstName());
        System.out.println("You have been a member since " + testestUser.getRegistrationDate());
        System.out.println("Your birthday is coming soon! At: " + testestUser.getBirthday());
    }
}
