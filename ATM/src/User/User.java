package User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Server.MySQLConnect;

class User {
    private int userID;
    private String username;
    private String passwordSalt;
    private String passwordHash;
    private String email;
    private String phone;
    private String addressOne;
    private String addressTwo;
    private String addressThree;
    private String postalCode;
    private Date registrationDate;
    private boolean active;

    // Constructor taking 11 data from DB including NULLs
    // public User(int userID, String username, String passwordSalt, String passwordHash, String email, String phone, String addressOne, 
    //             String addressTwo, String addressThree, String postalCode, Date registrationDate, boolean active) { 
        
    //     this.userID = userID;
    //     this.username = username;
    //     this.passwordSalt = passwordSalt;
    //     this.passwordHash = passwordHash;
    //     this.email = email;
    //     this.phone = phone;
    //     this.addressOne = addressOne;
    //     this.addressTwo = addressTwo;
    //     this.addressThree = addressThree;
    //     this.postalCode = postalCode;
    //     this.registrationDate = registrationDate;
    //     this.active = active;
    
    // }

    public User(int userID) {
        boolean exists = false;
        MySQLConnect mysqlConnect = new MySQLConnect();

        // Select "User" database
        String sql = "SELECT * FROM user";

        // Try to connect with inputted UserID
        try {
            PreparedStatement statement = mysqlConnect.connect().prepareStatement(sql);
            ResultSet myRs = statement.executeQuery();  

            // Go to data row onwards
            myRs.next(); 

            // Reiterate the table to look for data where inputted userID exists
            while(myRs.next()){
                if(myRs.getInt("UserID") == userID){
                    this.username = myRs.getString("Username");
                    this.passwordSalt = myRs.getString("PasswordSalt");
                    this.passwordHash = myRs.getString("PasswordHash");
                    this.email = myRs.getString("Email");
                    this.phone = myRs.getString("Phone");
                    this.addressOne = myRs.getString("AddressOne");
                    this.addressTwo = myRs.getString("AddressTwo");
                    this.addressThree = myRs.getString("AddressThree");
                    this.postalCode = myRs.getString("PostalCode");
                    this.registrationDate = myRs.getDate("RegistrationDate");
                    exists = true;
                }
            }

            // Check if user exists after iterating entire database (maybe put in main?)
            if(exists == false){
                System.out.println("User does not exist bodoh!");
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        } 
        finally {
            mysqlConnect.disconnect();
        }
    }

    // Returns user username
    public String getUsername() {
        return this.username;
    }

    // Returns user email 
    public String getEmail() {
        return this.email;
    }

    // Returns user phone number
    public String getPhone() {
        return this.phone;
    }

    // Returns user address depending on requested option
    public String getAddresses(int option) {
        if(option == 1) {
            String address = this.addressOne + ", "+ this.postalCode;
            return address;
        }
        else if(option == 2) {
            String address = this.addressTwo + ", "+ this.postalCode;
            return address;
        }
        else {
            String address = this.addressThree + ", "+ this.postalCode;
            return address;
        }
    }

    // Returns user registration date in dd-MM-yyyy format
    public String getRegistrationDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy"); // MM = numeric month, MMM = month in shortform, MMMM month in longform
        String date = formatter.format(this.registrationDate);
        return date;
    }

    public boolean setUsername(String name) {
        boolean exists = false;
        MySQLConnect mysqlConnect = new MySQLConnect();

        // Select "User" database
        String sql = "SELECT * FROM user";

        // Try to connect with inputted UserID
        try {
            PreparedStatement statement = mysqlConnect.connect().prepareStatement(sql);
            ResultSet myRs = statement.executeQuery();  

            // Go to data row onwards
            myRs.next(); 

            // Reiterate the table to look for data where inputted userID exists
            while(myRs.next()){
                if(myRs.getInt("UserID") == userID){
                    String query = "UPDATE User SET Phone=\"+65 12345678\" WHERE Username LIKE ?"; // Add %?% during setting
                }
            }

            // Check if user exists after iterating entire database (maybe put in main?)
            if(exists == false){
                System.out.println("User does not exist bodoh!");
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        } 
        finally {
            mysqlConnect.disconnect();
        }
        return true;
    }

    public static void main(String[] args) {
        
    }
}
