package Server;

import java.sql.*;
import java.util.Scanner;
import java.util.Date;

import User.BusinessUser;
import User.NormalUser;
import User.User;

public class ServerUser {

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

    // Method to reset user password
    public static void resetUserPassword(User user) {
        int passwordTries = 2;
        
        do {
            Connection db = SQLConnect.getDBConnection();
            // Try to connect to DB
            try {
                String sql1 = String.format("SELECT * FROM user WHERE Username = '%s'", user.getUsername());
                PreparedStatement statement1 = db.prepareStatement(sql1);
                ResultSet myRs1 = statement1.executeQuery();

                myRs1.next();

                System.out.print("Request to reset password\n");
                Scanner input = new Scanner(System.in);
                System.out.print("Enter old password: ");
                String oldPassword = input.nextLine();

                String DBPassword = AES256.decrypt(myRs1.getString("PasswordHash"));

                PassChecker.checkPassword(oldPassword, DBPassword, passwordTries);

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

                        System.out.println("\nPassword resetted successfully!\n");
                        passwordTries = -1;
                        break;
                    }
                    System.out.print("Password did not match! Please try again " + (3-i) + " tries left.\n");
                }
            }
            catch (SQLException e) {
                // Check for any SQL connection errors
                e.printStackTrace();
            }
            catch (WrongPasswordException e){
                System.out.println(e.getMessage());
            }
            finally {
                // Close DB connection
                SQLConnect.disconnectDB(db);
            }
            passwordTries--;
        } while (passwordTries >= 0);
    }

    // Method to deactivate user from DB
    public static void deactivateUser(User user) throws InterruptedException {
        int passwordTries = 2;
        int deactivationOption1 = 0;
        int deactivationOption2 = 0;
        
        do {
            Connection db = SQLConnect.getDBConnection();
            // Try to connect to DB
            try {
                String sql1 = String.format("SELECT * FROM user WHERE Username = '%s'", user.getUsername());
                PreparedStatement statement1 = db.prepareStatement(sql1);
                ResultSet myRs1 = statement1.executeQuery();

                myRs1.next();

                System.out.print("Deactivation of user account\n");
                Scanner input = new Scanner(System.in);
                System.out.print("Enter password: ");
                String oldPassword = input.nextLine();

                String DBPassword = AES256.decrypt(myRs1.getString("PasswordHash"));

                PassChecker.checkPassword(oldPassword, DBPassword, passwordTries);
            
                String sql2 = String.format("UPDATE User SET Active = ? WHERE UserID = %s", user.getUserID());
                PreparedStatement statement = db.prepareStatement(sql2);

                System.out.print("\n");
                // Display menu and check for valid input of 1 - 2
                while(deactivationOption1 != 1 && deactivationOption1 != 2) {
                    System.out.print("Activation of user account can only be done on administrator side.");
                    System.out.print("\nAre you sure you want to deactivate your user account? ");
                    System.out.print("\n1- Yes");
                    System.out.print("\n2- No");
                    System.out.print("\n> ");
                    deactivationOption1 = input.nextInt();

                    if(deactivationOption1 != 1 && deactivationOption1 != 2) {
                        System.out.println("Wrong input. Try again..\n");
                    }
                }
                
                // If user wants to deactivate user account
                if(deactivationOption1 == 1) {
                    while(deactivationOption2 != 1 && deactivationOption2 != 2) {
                        System.out.print("\nConfirm deactivation? ");
                        System.out.print("\n1- Yes");
                        System.out.print("\n2- No");
                        System.out.print("\n> ");
                        deactivationOption2 = input.nextInt();

                        if(deactivationOption2 != 1 && deactivationOption2 != 2) {
                            System.out.println("Wrong input. Try again..\n");
                        }
                    }

                    // If user confirms deactivation of user account
                    if(deactivationOption1 == deactivationOption2) {
                        statement.setInt(1, 0);

                        // Perform database updates and exit session
                        statement.executeUpdate();
                        System.out.print("\nUser account has been deactivated.");
                        System.out.print("\nNow logging out...");
                        Thread.sleep(1000);
                        System.exit(1);
                    }
                    
                    // User confirms not to deactivate user
                    deactivationOption1 = 2;
                }
                if(deactivationOption1 == 2){
                    System.out.println("\nUser account deactivation request aborted");
                    passwordTries = -1;
                }
            }
            catch (WrongPasswordException e) {
                // Check for any input errors
                System.out.println(e.getMessage());
            }
            catch (SQLException e) {
                // Check for any SQL connection errors
                e.printStackTrace();
            }
            finally {
                // Close DB connection
                SQLConnect.disconnectDB(db);
            }
            passwordTries--;
        } while (passwordTries >= 0);
    }
}

class WrongPasswordException extends Exception {
    public WrongPasswordException(String errorMessage) {
        super(errorMessage);
    }
}

class PassChecker {
    public static void checkPassword(String oldPass, String newPass, int tries) throws WrongPasswordException {
        if (!oldPass.equals(newPass)) {
            throw new WrongPasswordException("Incorrect password! " + tries + " tries left.\n");
        }
    }
    public static void checkOption(int number, int max) throws WrongPasswordException {
        if (number > max) {
            throw new WrongPasswordException("Wrong Input. Try again..\n");
        }
    }
}
