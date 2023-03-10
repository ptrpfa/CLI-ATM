package Server;

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import User.BusinessUser;
import User.NormalUser;
import User.User;

public class ServerUser {

    // Method to create user
    public static void registerUser() throws ParseException{
        int userID;
        String username;
        String passwordSalt;
        String passwordHash;
        String email;
        String phone;
        String addressOne;
        String addressTwo;
        String addressThree;
        String postalCode;
        Date registrationDate;
        int userType = 0;
        boolean active = true;

        Scanner input = new Scanner(System.in);
        
        do{
            System.out.print("Enter user type\n");
            System.out.print("1- Normal user\n");
            System.out.print("2- Business user\nEnter user type: ");
            userType = input.nextInt();
        } while (userType != 1 & userType != 2);

        input.nextLine();
        
        System.out.print("\nEnter username: ");
        username = input.nextLine();

        System.out.print("\nEnter password: ");
        String passwordTemp = input.nextLine();

        String salt = AES256.generateSalt();
        passwordSalt = salt;

        passwordHash = AES256.encrypt(passwordTemp, salt);

        System.out.print("\nEnter email: ");
        email = input.nextLine();

        System.out.print("\nEnter phone: ");
        phone = input.nextLine();

        System.out.print("\nEnter address one: ");
        addressOne = input.nextLine();

        System.out.print("\nEnter address two: ");
        addressTwo = input.nextLine();

        System.out.print("\nEnter address three: ");
        addressThree = input.nextLine();

        System.out.print("\nEnter postal code: ");
        postalCode = input.nextLine();

        registrationDate = new Date();

        if (userType == 1){
            String NRIC;
            String firstName;
            String middleName;
            String lastName;
            String gender = "";
            Date birthday;

            System.out.print("\nEnter NRIC: ");
            NRIC = input.nextLine();
            
            System.out.print("\nEnter first name: ");
            firstName = input.nextLine();
            
            System.out.print("\nEnter last name: ");
            lastName = input.nextLine();
            
            System.out.print("\nEnter middle name (If applicable): ");
            middleName = input.nextLine();
            
            int genderInt = 0;
            do {
                System.out.println("Are you a:");
                System.out.print("1- Male");
                System.out.println("2- Female");
                System.out.println("> ");
                genderInt = input.nextInt();
            } while (genderInt != 1 & genderInt != 2);
            
            if(genderInt == 1) {
                gender = "Male";
            }
            
            else if(genderInt == 2) {
                gender = "Female";
            }
            
            System.out.print("\nEnter DOB (dd-MM-yyyy): ");
            String birthdayTemp = input.nextLine();
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            birthday = dateFormat.parse(birthdayTemp);

            NormalUser newUser = new NormalUser(0, username, passwordSalt, passwordHash, email, phone, addressOne, 
                                                addressTwo, addressThree, postalCode, registrationDate, userType, active, 
                                                NRIC, firstName, middleName, lastName, gender, birthday);

            createUser(newUser);
        }

        else if (userType == 2) {
            String UEN;
            String businessName;

            System.out.print("\nEnter UEN: ");
            UEN = input.nextLine();

            System.out.print("\nEnter business name: ");
            businessName = input.nextLine();

            BusinessUser newUser = new BusinessUser(0, username, passwordSalt, passwordHash, email, phone, addressOne, 
                                                    addressTwo, addressThree, postalCode, registrationDate, userType, active, 
                                                    UEN, businessName);
            createUser(newUser);
        }
        System.out.println("Successfully created new user!");
    }

    // Method to add new user into DB
    public static void createUser(User user) {
        // Initialise connection to DB
        Connection db = SQLConnect.getDBConnection();

        // Try to connect to DB
        try {
            // Fill up update statements with latest particulars for "User" DB
            String sql1 = "INSERT INTO User (Username, PasswordSalt, PasswordHash, Email, Phone, AddressOne," +
                         "AddressTwo, AddressThree, PostalCode, RegistrationDate, UserType, Active)" + 
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement1 = db.prepareStatement(sql1);

            // Fill up update statements with latest particulars for "User" DB
            statement1.setString(1, user.getUsername());
            statement1.setString(2, user.getPasswordSalt());
            statement1.setString(3, user.getPasswordHash());
            statement1.setString(4, user.getEmail());
            statement1.setString(5, user.getPhone());
            statement1.setString(6, user.getAddresses(1));
            statement1.setString(7, user.getAddresses(2));
            statement1.setString(8, user.getAddresses(3));
            statement1.setString(9, user.getPostalCode());
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            statement1.setTimestamp(10, timestamp);
            statement1.setInt(11, user.getUserType());
            statement1.setInt(12, user.getActive());

            int rowsInserted1 = statement1.executeUpdate();

            if (rowsInserted1 > 0) {
               System.out.println("\nA new row in User database was inserted successfully!");
            }

            String sqlTemp = String.format("SELECT * FROM User WHERE Username = '%s'", user.getUsername());
            PreparedStatement statementTemp = db.prepareStatement(sqlTemp);
            ResultSet myRsTemp = statementTemp.executeQuery();
            
            if(myRsTemp.next()) {
                user.setUserID(myRsTemp.getInt("UserID"));
            }

            // Check if normal user, insert into NormalUser DB
            if (user instanceof NormalUser) {
                NormalUser newUser = (NormalUser) user;

                String sql2 = "INSERT INTO NormalUser (UserID, NRIC, FirstName, MiddleName, LastName, Gender, Birthday) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement statement2 = db.prepareStatement(sql2);

                // Fill up update statements with latest particulars for "NormalUser" DB
                statement2.setInt(1, newUser.getUserID());
                statement2.setString(2, newUser.getNRIC());
                statement2.setString(3, newUser.getFirstName());
                statement2.setString(4, newUser.getMiddleName());
                statement2.setString(5, newUser.getLastName());
                statement2.setString(6, newUser.getGender());

                // Format birthday date as a string in the MySQL date format
                SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String tempDate = newUser.getBirthday();    
                String mySQLDate = newDateFormat.format(tempDate);

                statement2.setString(6, mySQLDate);

                int rowsInserted2 = statement2.executeUpdate();

                if (rowsInserted2 > 0) {
                    System.out.println("A new row in NormalUser database was inserted successfully!");
                }
            }

            // Check if business user, insert into BusinessUser DB
            if (user instanceof BusinessUser) {
                BusinessUser newUser = (BusinessUser) user;

                String sql = "INSERT INTO BusinessUser (UserID, UEN, BusinessName) VALUES (?, ?, ?)";
                PreparedStatement statement2 = db.prepareStatement(sql);
    
                // Fill up update statements with latest particulars for "BusinessUser" DB
                statement2.setInt(1, newUser.getUserID());
                statement2.setString(2, newUser.getUEN());
                statement2.setString(3, newUser.getBusinessName());

                int rowsInserted2 = statement2.executeUpdate();

                if (rowsInserted2 > 0) {
                    System.out.println("A new row in BusinessUser database was inserted successfully!");
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
            String sql = "SELECT * FROM User WHERE username = ?";
            PreparedStatement statement1 = db.prepareStatement(sql);
            statement1.setString(1, username);
            ResultSet myRs1 = statement1.executeQuery();

            // Start reading after title row onwards
            if(myRs1.next()){
                // Check whether user is deactivated, disallow login if disabled
                if(myRs1.getInt("Active") == 0) {
                    System.out.println("\nYour account is inactive. Please contact the bank administrator.");
                    System.exit(1);
                }
                String tempPassword = AES256.encrypt(password, myRs1.getString("PasswordSalt"));
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
                        String sql2 = String.format("SELECT * FROM NormalUser WHERE UserID = %s", userID);
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
                        String sql2 = String.format("SELECT * FROM BusinessUser WHERE UserID = %s", userID);
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
    public static void updateUser(User user) {
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
                String sql2 = String.format("UPDATE BusinessUser SET UEN = ?, BusinessName = ? WHERE UserID = %s", tempUser.getUserID());
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

    // Method to display update options and get new information
    public static void getNewUpdates(User user) {
        Scanner input = new Scanner(System.in);
        int updateChoice = 0;
        int max = 7;
        String newUpdate;

        while(updateChoice < 1 || updateChoice > max) {
            System.out.println("\nWhich user detail would you like to update..\n");
            System.out.println("1- Username");
            System.out.println("2- Email");
            System.out.println("3- Phone");
            System.out.println("4- Address One");
            System.out.println("5- Address Two");
            System.out.println("6- Address Three");
            System.out.println("7- Postal Code");

            if (user instanceof NormalUser) {
                max = 13;
                System.out.println("8- NRIC");
                System.out.println("9- First Name");
                System.out.println("10- Middle Name");
                System.out.println("11- Last Name");
                System.out.println("12- Gender");
                System.out.println("13- Return to menu");
            }

            if (user instanceof BusinessUser) {
                max = 10;
                System.out.println("8- UEN");
                System.out.println("9- Business Name");
                System.out.println("10- Return to menu");
            }
            
            System.out.println("\nWhat do you want to do?");
            System.out.print("> ");
            updateChoice = input.nextInt();
            input.nextLine();
        }

        switch(updateChoice) {
            case 1:
                System.out.println("Current username: " + user.getUsername());
                System.out.print("Enter new username: ");
                newUpdate = input.nextLine();
                user.setUsername(newUpdate);
                
                break;
            case 2:
                System.out.println("Current email: " + user.getEmail());
                System.out.print("Enter new email: ");
                newUpdate = input.nextLine();
                user.setEmail(newUpdate);
                
                break;
            case 3:
                System.out.println("Current phone: " + user.getPhone());
                System.out.print("Enter new phone: ");
                newUpdate = input.nextLine();
                user.setPhone(newUpdate);
                
                break;
            case 4:
                System.out.println("Current address one: " + user.getAddresses(1));
                System.out.print("Enter new address one: ");
                newUpdate = input.nextLine();
                user.setAddress(newUpdate, user.getAddresses(2), user.getAddresses(3), user.getPostalCode());
                
                break;
            case 5:
                System.out.println("Current address two: " + user.getAddresses(2));
                System.out.print("Enter new address two: ");
                newUpdate = input.next();
                user.setAddress(user.getAddresses(1), newUpdate, user.getAddresses(3), user.getPostalCode());
                
                break;
            case 6:
                System.out.println("Current address three: " + user.getAddresses(3));
                System.out.print("Enter new address three: ");
                newUpdate = input.next();
                user.setAddress(user.getAddresses(1), user.getAddresses(2), newUpdate, user.getPostalCode());
                
                break;
            case 7:
                System.out.println("Current postal code: " + user.getPostalCode());
                System.out.print("Enter new postal code: ");
                newUpdate = input.next();
                user.setAddress(user.getAddresses(1), user.getAddresses(2), user.getAddresses(3), newUpdate);
                
                break;
            case 8:
                if (user instanceof NormalUser) {
                    NormalUser tempUser = (NormalUser) user;

                    System.out.println("Current NRIC: " + tempUser.getNRIC());
                    System.out.print("Enter new NRIC: ");
                    newUpdate = input.next();
                    tempUser.setNRIC(newUpdate);
                }
                if (user instanceof BusinessUser) {
                    BusinessUser tempUser = (BusinessUser) user;
                    System.out.println("Current UEN: " + tempUser.getUEN());
                    System.out.print("Enter new UEN: ");
                    newUpdate = input.next();
                    tempUser.setUEN(newUpdate);
                }

                break;
            case 9:
                if (user instanceof NormalUser) {
                    NormalUser tempUser = (NormalUser) user;

                    System.out.println("Current first name: " + tempUser.getFirstName());
                    System.out.print("Enter new first name: ");
                    newUpdate = input.next();
                    tempUser.setAllNames(newUpdate, tempUser.getMiddleName(), tempUser.getLastName());
                }
                if (user instanceof BusinessUser) {
                    BusinessUser tempUser = (BusinessUser) user;
                    System.out.println("Current business name: " + tempUser.getBusinessName());
                    System.out.print("Enter new business name: ");
                }

                break;
            case 10:
                if (user instanceof NormalUser) {
                    NormalUser tempUser = (NormalUser) user;

                    System.out.println("Current middle name: " + tempUser.getFirstName());
                    System.out.print("Enter new middle name: ");
                    newUpdate = input.next();
                    tempUser.setAllNames(newUpdate, newUpdate, tempUser.getLastName());
                }
                break;
            case 11:
                NormalUser tempUser2 = (NormalUser) user;

                System.out.println("Current last name: " + tempUser2.getLastName());
                System.out.print("Enter new last name: ");
                newUpdate = input.next();
                tempUser2.setAllNames(tempUser2.getFirstName(), tempUser2.getMiddleName(), newUpdate);

                break;
            case 12:
                NormalUser tempUser3 = (NormalUser) user;

                System.out.println("Current gender: " + tempUser3.getGender());
                System.out.print("Enter new gender: ");
                newUpdate = input.next();
                tempUser3.setGender(newUpdate);

                break;
            case 13:
                break;
        }
    }

    // Method to reset user password
    public static void resetUserPassword(User user) {
        // Loop counter where user only allowed 3 tries
        int passwordTries = 2;
        
        do {
            Connection db = SQLConnect.getDBConnection();
            // Try to connect to DB
            try {
                // Template to select "User" DB for retrieving data
                String sql1 = String.format("SELECT * FROM User WHERE Username = '%s'", user.getUsername());
                PreparedStatement statement1 = db.prepareStatement(sql1);
                ResultSet myRs1 = statement1.executeQuery();

                myRs1.next();

                // Get old password input for security feature
                System.out.print("\nRequest to reset password\n");
                Scanner input = new Scanner(System.in);
                System.out.print("Enter old password: ");
                String oldPassword = input.nextLine();

                // Get old password from DB
                String DBPassword = AES256.decrypt(myRs1.getString("PasswordHash"), myRs1.getString("PasswordSalt"));

                // Check old password with DB password. If match continue, else throw exception
                PassChecker.checkPassword(oldPassword, DBPassword, passwordTries);

                // Get new password input
                System.out.print("\nEnter new password: ");
                String newPassword1 = input.nextLine();

                // Secondary loop counter where user have 3 tries to confirm new password
                for(int i = 1; i < 4; i++) {
                    // Template to select "User" DB for updating data
                    String sql2 = String.format("UPDATE User SET PasswordHash = ? WHERE UserID = %s", user.getUserID());
                    PreparedStatement statement = db.prepareStatement(sql2);

                    // Get confirmed new password input
                    System.out.print("\nConfirm new password: ");
                    String newPassword2 = input.nextLine();

                    // Check new password matches twice. If match continue, else reduce tries by 1
                    if(newPassword1.equals(newPassword2)){
                        // Encrpyts new password and set it for updating to DB
                        newPassword1 = AES256.encrypt(newPassword2, myRs1.getString("PasswordSalt"));
                        user.setPasswordHash(newPassword1);
                        statement.setString(1, newPassword1);

                        // Perform database updates
                        statement.executeUpdate();

                        // Prints successful message and breaks out of loop and method
                        System.out.println("\nPassword resetted successfully!\n");
                        passwordTries = -1;
                        break;
                    }
                    
                    System.out.print("Passwords did not match! Please try again. " + (3-i) + " tries left.\n");
                 
                    if(i == 3){
                        passwordTries = 0;
                    }
                }
            }
            catch (SQLException e) {
                // Check for any SQL connection errors
                e.printStackTrace();
            }
            catch (WrongPasswordException e){
                // Check for password mismatch
                System.out.println(e.getMessage());
            }
            finally {
                // Close DB connection
                SQLConnect.disconnectDB(db);
            }
            // If confirm new password fails 3 times, print message and break from loop
            if(passwordTries == 0) {
                passwordTries = -1;
                System.out.print("\nPassword reset request aborted.\n\n");
            }
            passwordTries--;
        } while (passwordTries >= 0);
    }

    // Method to deactivate user from DB
    public static void deactivateUser(User user){
        // Loop counter where user only allowed 3 tries
        int passwordTries = 2;
        int deactivationOption1 = 0;
        int deactivationOption2 = 0;
        
        do {
            Connection db = SQLConnect.getDBConnection();
            // Try to connect to DB
            try {
                // Template to select "User" DB for retrieving data
                String sql1 = String.format("SELECT * FROM User WHERE Username = '%s'", user.getUsername());
                PreparedStatement statement1 = db.prepareStatement(sql1);
                ResultSet myRs1 = statement1.executeQuery();

                myRs1.next();

                // Get old password input for security feature
                System.out.print("\nDeactivation of user account\n");
                Scanner input = new Scanner(System.in);
                System.out.print("Enter password: ");
                String oldPassword = input.nextLine();

                // Get old password from DB
                String DBPassword = AES256.decrypt(myRs1.getString("PasswordHash"), myRs1.getString("PasswordSalt"));

                // Check old password with DB password. If match continue, else throw exception
                PassChecker.checkPassword(oldPassword, DBPassword, passwordTries);
            
                // Template to select "User" DB for updating data
                String sql2 = String.format("UPDATE User SET Active = ? WHERE UserID = %s", user.getUserID());
                PreparedStatement statement = db.prepareStatement(sql2);

                // Display menu and checks for valid input of 1 - 2
                System.out.print("\n");
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
                
                // If user wants to deactivate user account and checks for valid input of 1 - 2
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
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            System.out.println(e.getMessage());
                        }finally{
                            System.exit(1);
                        }
                    }
                    
                    // Else, user confirms not to deactivate user account and prints abort message
                    deactivationOption1 = 2;
                }
                // If, user decides not to deactivate user account
                if(deactivationOption1 == 2) {
                    passwordTries = 0;
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
            // If password tries fails 3 times or user decides not to deactivate user account
            if(passwordTries == 0){
                System.out.println("\nUser account deactivation request aborted.\n");
                passwordTries = -1;
            }
            passwordTries--;
        } while (passwordTries >= 0);
    }

    public static void main(String[] args) throws ParseException {
        ServerUser.registerUser();
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
            throw new WrongPasswordException("Incorrect password! " + tries + " tries left.");
        }
    }
    public static void checkOption(int number, int max) throws WrongPasswordException {
        if (number > max) {
            throw new WrongPasswordException("Wrong Input. Try again..\n");
        }
    }
}
