package Server;

import java.sql.*;
import java.util.Scanner;

import javax.swing.Action;

import java.util.Date;

import User.BusinessUser;
import User.NormalUser;
import User.User;

public class ServerUser {

    // Method to check username and password, return User object which is an instance of NormalUser or BusinessUser
    public User checkUser(String username, String password) {
        // Initialise connection to DB
        Connection db = SQLConnect.getDBConnection();
        
        // Declare user object that will be returned for session
        User user = null;
        
        // Try to connect to DB
        try {
            // Template to select "User" DB with inputted username
            String sql = String.format("SELECT * FROM user WHERE Username = '%s'", username);
            PreparedStatement statement1 = db.prepareStatement(sql);
            ResultSet myRs1 = statement1.executeQuery();
            
            // Start reading after title row onwards
            if(myRs1.next()){
                // Check whether user is deactivated, disallow login if disabled
                if(myRs1.getInt("Active") == 0) {
                    System.out.println("\nYour account is inactive. Please contact the bank administrator.");
                    System.exit(1);
                }
                    
                String tempPassword = AES256.encrypt(password);

                // Process to check if password is correct, start pulling user data from DB
                if(tempPassword.equals(myRs1.getString("PasswordHash"))) {
                    // ID and type reserved to differentiate and create Normal or Business user
                    int userID = myRs1.getInt("UserID");
                    int userType = myRs1.getInt("UserType");

                    //Create User object with default User attributes 
                    user = new User(userID,
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
                                    myRs1.getInt("UserType"),
                                    myRs1.getBoolean("Active"));
                    
                    // If user is NormalUser, get extra attributes that NormalUser has
                    if (userType == 1) {
                        // Template to select "NormalUser" DB for fetching data
                        String sql2 = String.format("SELECT * FROM normaluser WHERE UserID = %s", userID);
                        PreparedStatement statement2 = db.prepareStatement(sql2);

                        // Prepare to read database of where inputted UserID is located
                        ResultSet myRs2 = statement2.executeQuery();

                        // Start reading after the title row onwards
                        myRs2.next();

                        // Read from NormalUser DB and load user with saved particulars
                        user = new NormalUser(user, 
                                            myRs2.getString("NRIC"),
                                            myRs2.getString("FirstName"),
                                            myRs2.getString("MiddleName"),
                                            myRs2.getString("LastName"),
                                            myRs2.getString("Gender"),
                                            myRs2.getDate("Birthday"));
                    }

                    // If user is BusinessUser, get extra attributes that BusinessUser has
                    else if (userType == 2) {
                        // Template to select "BusinessUser" DB for fetching data
                        String sql2 = String.format("SELECT * FROM businessuser WHERE UserID = %s", userID);
                        PreparedStatement statement2 = db.prepareStatement(sql2);

                        // Prepare to read database of where inputted UserID is located
                        ResultSet myRs2 = statement2.executeQuery();

                        // Start reading after the title row onwards
                        myRs2.next();

                        // Read from "BusinessUser" DB and load user with saved particulars
                        user = new BusinessUser(user,
                                                myRs2.getString("UEN"),
                                                myRs2.getString("BusinessName"));
                    }
                }
            }
        }
        catch (SQLException e) {
            // Check for any SQL connection errors
            e.printStackTrace();
        }
        finally {
            // Close DB connection
            SQLConnect.disconnectDB(db);
        }
        return user;
    }
    
    // Method to update DBs with latest information
    public void updateUser(User user) {
        Connection db = SQLConnect.getDBConnection();
        
        // Try to connect to DB
        try {
            // Template to select "User" DB for updating data
            String sql1 = String.format("UPDATE User SET Username = ?, Email = ?, Phone = ?, AddressOne = ?, AddressTwo = ?, AddressThree = ?, PostalCode = ? WHERE UserID = %s", user.getUserID());
            PreparedStatement statement1 = db.prepareStatement(sql1);
            
            // Fill up update statements with latest particulars for "User" DB
            statement1.setString(1, user.getUsername());
            statement1.setString(2, user.getEmail());
            statement1.setString(3, user.getPhone());
            statement1.setString(4, user.getAddresses(1));
            statement1.setString(5, user.getAddresses(2));
            statement1.setString(6, user.getAddresses(3));
            statement1.setString(7, user.getPostalCode());

            // If user is NormalUser, update specific DB
            if (user instanceof NormalUser) {
                NormalUser tempUser = (NormalUser) user;
                
                // Template to select "NormalUser" DB for updating data
                String sql2 = String.format("UPDATE NormalUser SET NRIC = ?, FirstName = ?, MiddleName = ?, LastName = ?, Gender = ? WHERE UserID = %s", tempUser.getUserID());
                PreparedStatement statement2 = db.prepareStatement(sql2);
                
                // Fill up update statements with latest particulars for "NormalUser" DB
                statement2.setString(1, tempUser.getNRIC());
                statement2.setString(2, tempUser.getFirstName());
                statement2.setString(3, tempUser.getMiddleName());
                statement2.setString(4, tempUser.getLastName());
                statement2.setString(5, tempUser.getGender());

                // Perform database updates
                statement1.executeUpdate();
                statement2.executeUpdate();
            }

            // If user is BusinessUser, update specific DB
            else if (user instanceof BusinessUser) {
                BusinessUser tempUser = (BusinessUser) user;

                // Template to select "BusinessUser" DB for updating data
                String sql2 = String.format("UPDATE businessuser SET UEN = ?, BusinessName = ? WHERE UserID = %s", tempUser.getUserID());
                PreparedStatement statement2 = db.prepareStatement(sql2);

                // Fill up update statements with latest particulars for "BusinessUser" DB
                statement2.setString(1, tempUser.getUEN());
                statement2.setString(2, tempUser.getBusinessName());
                
                // Perform database updates
                statement1.executeUpdate();
                statement2.executeUpdate();
            }
        }
        catch (SQLException e) {
            // Check for any SQL connection errors
            e.printStackTrace();
        }
        finally {
            // Close DB connection
            SQLConnect.disconnectDB(db);
        }
    }

    // Method to create user (METHOD WIP)
    public void registerUser() {
        Scanner input = new Scanner(System.in);

        System.out.print("1. Business user\n2. Normal user\nEnter user type: ");
        int userType = input.nextInt();

        System.out.print("Enter username: ");
        String username = input.nextLine();

        System.out.print("Enter password: ");
        String password = input.nextLine();

        System.out.print("Enter email: ");
        String email = input.nextLine();

        System.out.print("Enter phone: ");
        int phone = input.nextInt();

        System.out.print("Enter address (Address 1, Address 2, Address 3): ");
        String address1 = input.nextLine();
        String address2 = input.nextLine();
        String address3 = input.nextLine();

        System.out.println("Enter postal code: ");
        int postalCode = input.nextInt();

        Date registerDate = new Date();

        if (userType == 1){
            System.out.print("Enter NRIC: ");
            String NRIC = input.nextLine();

            System.out.print("Enter first name: ");
            String firstName = input.nextLine();

            System.out.print("Enter last name: ");
            String lastName = input.nextLine();
            
            System.out.print("Enter middle name (If applicable): ");
            String middleName = input.nextLine();

            System.out.print("1. Male\n2. Female\nSelect gender: ");
            int genderInt = input.nextInt();
            
            if(genderInt == 1) {
                String gender = "Male";
            }

            else if(genderInt == 2) {
                String gender = "Female";
            }

            System.out.print("Enter DOB:");

        }

        else if (userType == 2) {
            System.out.print("Enter business name");
            String businessName = input.nextLine();

            System.out.print("Enter UEN: ");
            String UEN = input.nextLine();
        }
    }

    // Method to reset user password
    public static void resetUserPassword(User user) {
        Connection db = SQLConnect.getDBConnection();
        
        // Try to connect to DB
        try {
            String sql1 = String.format("SELECT * FROM user WHERE Username = '%s'", user.getUsername());
            PreparedStatement statement1 = db.prepareStatement(sql1);
            ResultSet myRs1 = statement1.executeQuery();

            myRs1.next();

            Scanner input = new Scanner(System.in);
            System.out.print("Enter old password: ");
            String oldPassword = input.nextLine();

            String DBPassword = AES256.decrypt(myRs1.getString("PasswordHash"));

            if(oldPassword.equals(DBPassword)) {
                System.out.print("Enter new password: ");
                String newPassword1 = input.nextLine();

                for(int i = 1; i < 4; i++) {
                    // Template to select "User" DB for updating data
                    String sql2 = String.format("UPDATE User SET PasswordHash = ? WHERE UserID = %s", user.getUserID());
                    PreparedStatement statement = db.prepareStatement(sql2);

                    System.out.print("Confirm new password: ");
                    String newPassword2 = input.nextLine();

                    if(newPassword1.equals(newPassword2)){
                        newPassword1 = AES256.encrypt(newPassword2);

                        user.setPasswordHash(newPassword1);

                        statement.setString(1, newPassword1);

                        // Perform database updates
                        statement.executeUpdate();

                        System.out.println("Password resetted successfully!\n");
                        break;
                    }
                    System.out.print("Password did not match! Please try again " + (3-i) + " tries left.\n");
                }
            }
            else {
                System.out.println("Incorrect password.");
            }
            input.close();
        }
        catch (SQLException e) {
            // Check for any SQL connection errors
            e.printStackTrace();
        }
        finally {
            // Close DB connection
            SQLConnect.disconnectDB(db);
        }
    }

    public static void main(String[] args) {
        ServerUser serverUser = new ServerUser();
        
        // User testUser = serverUser.checkUser("Na0m1_N30", "0nlyf4ns!"); // NormalUser
        User testUser = serverUser.checkUser("Liu Kai Ping", "testest"); // BusinessUser
        
        if (testUser instanceof NormalUser) {
            // NormalUser user = (NormalUser) testUser;
            
            // user.setAddress(user.getAddresses(1), user.getAddresses(2), "TEST_ADDRESS_NORMAL", user.getPostalCode());
            // user.setAllNames(user.getFirstName(), "梁文珊", user.getLastName());
            // serverUser.updateUser(user);
            
            // System.out.println("\nGood day Mr/Ms " + user.getLastName() + ", " + user.getFirstName() + " " + user.getMiddleName() + ". " + user.getNRIC());
            // System.out.println("You have been a member since " + user.getRegistrationDate());
            // System.out.println("Your birthday is coming soon! At: " + user.getBirthday());
        }
        else if (testUser instanceof BusinessUser) {
            BusinessUser user = (BusinessUser) testUser;
            
            serverUser.resetUserPassword(user);
            // user.setAddress(user.getAddresses(1), user.getAddresses(2), "TEST_ADDRESS_BUSINESS", user.getPostalCode());
            // user.setBusinessName("Onlyfans.com");
            // serverUser.updateUser(user);
            
            // System.out.println("\nWelcome " + user.getBusinessName() + ". Your username is: " + user.getUsername());
            // System.out.println("Your company UEN is " + user.getUEN());
            // System.out.println("Your user activity is: " + user.getActive());
        }
    }
}

class WrongNumberException extends Exception {
    public WrongNumberException(String errorMessage) {
        super(errorMessage);
    }
}

class NumberChecker {
    public static void checkNegative(double number) throws WrongNumberException {
        if (number < 0) {
            throw new WrongNumberException("Amount cannot be negative!\n");
        }
    }

    public static void checkOption(int number, int max) throws WrongNumberException {
        if (number > max) {
            throw new WrongNumberException("Wrong Choice lah bang\n");
        }
    }
}
