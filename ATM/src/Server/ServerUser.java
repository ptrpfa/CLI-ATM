package Server;

import java.io.Console;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import User.BusinessUser;
import User.NormalUser;
import User.User;
import picocli.CommandLine;

public class ServerUser {

    // Method to create user
    public static void registerUser() throws ParseException {
   
        String username;
        String passwordSalt;
        String passwordHash;
        String email;
        String phone = "";
        String addressOne;
        String addressTwo;
        String addressThree;
        String postalCode;
        Date registrationDate;
        int userType = 0;
        boolean active = true;

        Scanner input = new Scanner(System.in);
        Console console = System.console();
        
        // Set option for user to register as NormalUser or BusinessUser which will set different prompts later
        do{
            System.out.print(CommandLine.Help.Ansi.ON.string("@|51 What user type are you?\n|@"));
            System.out.print(CommandLine.Help.Ansi.ON.string("@|39 1- Normal user\n|@"));
            System.out.print(CommandLine.Help.Ansi.ON.string("@|39 2- Business user\n|@"));
            System.out.println("Enter user type: ");
            userType = input.nextInt();
        } while (userType != 1 & userType != 2);
        input.nextLine();
        
        // Get user's desired username used for log in
        
        System.out.print("\nEnter username: ");
        username = input.nextLine();

        // Get user's desired password used for log in
        char[] passwordChars = console.readPassword("\nEnter password (Hidden for Security): ");
        String passwordTemp = new String(passwordChars);
    

        // Generate unique random salt to be added to password 
        passwordSalt = AES256.generateSalt();

        // Encrypts user password + salt for better security
        passwordHash = AES256.encrypt(passwordTemp, passwordSalt);

        // Get user's email 
        System.out.print("\nEnter email: ");
        email = input.nextLine();

        // Loop controller
        boolean isVerified = false;
        boolean smsSent = false;
        String otp = SMS.generateOTP(6);
        String input_otp = null;

        System.out.print("\nEnter phone (+65): ");
        phone = input.nextLine();
        phone = "+65" + phone; // Add country code by default as verified number uses +65

        // Sends verification message to phone
        smsSent = SMS.sendSMS(phone, "Your Bank OTP is: " + otp);
        if(smsSent) {
            System.out.println(CommandLine.Help.Ansi.ON.string("@|208 A One-Time Passcode has been sent to your phone number.|@"));
        }

        // Sends verification message to user's phone and check for verification status
        while (!isVerified) {

            System.out.printf("Enter your OTP (%s): ", otp);
            input_otp = input.nextLine();

            if(otp.equals(input_otp)) {
                isVerified = true;
                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 Phone has been verified!|@"));
            }
            else {
                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 Wrong OTP entered! Please try again.|@"));
            }

        }

        // Get user's first address
        System.out.print("\nEnter address one: ");
        addressOne = input.nextLine();

        // Get user's second address
        System.out.print("\nEnter address two: ");
        addressTwo = input.nextLine();

        // Get user's third address
        System.out.print("\nEnter address three: ");
        addressThree = input.nextLine();

        // Get user's postal code
        System.out.print("\nEnter postal code: ");
        postalCode = input.nextLine();

        // Generate current date as registration date
        registrationDate = new Date();

        // If user is a NormalUser, prompt these
        if (userType == 1){
            String NRIC;
            String firstName;
            String middleName;
            String lastName;
            String gender = "";
            Date birthday = null;

            // Get user's NRIC
            System.out.print("\nEnter NRIC: ");
            NRIC = input.nextLine();
            
            // Get user's first name
            System.out.print("\nEnter first name: ");
            firstName = input.nextLine();
            
            // Get user's last name
            System.out.print("\nEnter last name: ");
            lastName = input.nextLine();
            
            // Get user's middle name
            System.out.print("\nEnter middle name (If applicable): ");
            middleName = input.nextLine();
            
            // Get user's gender using integer options for standardisation
            int genderInt = 0;
            do {
                System.out.println(CommandLine.Help.Ansi.ON.string("@|51 \nAre you a:|@"));
                System.out.println(CommandLine.Help.Ansi.ON.string("@|39 1- Male|@"));
                System.out.println(CommandLine.Help.Ansi.ON.string("@|39 2- Female|@"));
                System.out.print("> ");
                genderInt = input.nextInt();
            } while (genderInt != 1 & genderInt != 2);
            
            if(genderInt == 1) {
                gender = "Male";
            }
            else if(genderInt == 2) {
                gender = "Female";
            }

            input.nextLine();

            while(birthday == null){
                try {
                    // Get user's birthday and convert to proper necessary format 
                    System.out.print("\nEnter DOB (dd-MM-yyyy): ");
                    String birthdayTemp = input.nextLine();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    birthday = dateFormat.parse(birthdayTemp);
                    input.nextLine();
                } 
                catch (ParseException e) {
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|208 Invalid date format. Please enter the date in the format dd-MM-yyyy.|@"));
                }
            }

            // Create NormalUser object which holds user data and sends for user creation process
            NormalUser newUser = new NormalUser(0, username, email, phone, addressOne, 
                                                addressTwo, addressThree, postalCode, registrationDate, userType, active, 
                                                NRIC, firstName, middleName, lastName, gender, birthday);

            createUser(newUser, passwordSalt, passwordHash);
        }

        // If user is a BusinessUser, prompt these
        else if (userType == 2) {
            String UEN;
            String businessName;

            // Get company's UEN
            System.out.print("\nEnter UEN: ");
            UEN = input.nextLine();

            // Get company's name
            System.out.print("\nEnter business name: ");
            businessName = input.nextLine();

            // Create BusinessUser object which holds user data and sends for user creation process
            BusinessUser newUser = new BusinessUser(0, username, email, phone, addressOne, 
                                                    addressTwo, addressThree, postalCode, registrationDate, userType, active, 
                                                    UEN, businessName);
            createUser(newUser, passwordSalt, passwordHash);
        }

        //Clean up
        input.close();

    }

    // Method to add new user into DB
    public static void createUser(User user, String passwordSalt, String passwordHash) {
        // Initialise connection to DB
        Connection db = SQLConnect.getDBConnection();

        // Try to connect to DB
        try {
            // Fill up update statements with latest particulars for User DB
            String sql1 = "INSERT INTO User (Username, PasswordSalt, PasswordHash, Email, Phone, AddressOne," +
                         "AddressTwo, AddressThree, PostalCode, RegistrationDate, UserType, Active)" + 
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement1 = db.prepareStatement(sql1);

            // Fill up update statements with latest particulars for User DB
            statement1.setString(1, user.getUsername());
            statement1.setString(2, passwordSalt);
            statement1.setString(3, passwordHash);
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

            // Insert into User DB
            int rowsInserted1 = statement1.executeUpdate();

            // Prints successful insertion
            if (rowsInserted1 > 0) {
               System.out.println(CommandLine.Help.Ansi.ON.string("@|51 \nA new row in User database was inserted successfully!|@"));
            }

            // Pull auto incremented userID from User DB to insert into NormalUser or BusinessUser DB
            String sqlTemp = String.format("SELECT * FROM User WHERE Username = '%s'", user.getUsername());
            PreparedStatement statementTemp = db.prepareStatement(sqlTemp);
            ResultSet myRsTemp = statementTemp.executeQuery();
            if(myRsTemp.next()) {
                user.setUserID(myRsTemp.getInt("UserID"));
            }

            // Check if NormalUser, insert into NormalUser DB
            if (user instanceof NormalUser) {
                // Down-size to child object, NormalUser for child method usage
                NormalUser newUser = (NormalUser) user;

                // Template to insert into NormalUser DB
                String sql2 = "INSERT INTO NormalUser (UserID, NRIC, FirstName, MiddleName, LastName, Gender, Birthday) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement statement2 = db.prepareStatement(sql2);

                // Fill up update statements with latest particulars for NormalUser DB
                statement2.setInt(1, newUser.getUserID());
                statement2.setString(2, newUser.getNRIC());
                statement2.setString(3, newUser.getFirstName());
                statement2.setString(4, newUser.getMiddleName());
                statement2.setString(5, newUser.getLastName());
                statement2.setString(6, newUser.getGender());

                try {
                    // Format birthday date as a string in the MySQL date format and fill up update statement
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String tempDate = newUser.getBirthday();    
                    java.util.Date utilDate = dateFormat.parse(tempDate);

                    // Convert java.util.Date object to java.sql.Date object
                    Date sqlDate = new Date(utilDate.getTime());
            
                    // Format java.sql.Date object to MySQL date format
                    String mySQLDate = newDateFormat.format(sqlDate);

                    statement2.setString(7, mySQLDate);

                    // Insert into NormalUser DB
                    int rowsInserted2 = statement2.executeUpdate();

                    // Prints successful insertion
                    if (rowsInserted2 > 0) {
                        System.out.println(CommandLine.Help.Ansi.ON.string("@|51 A new row in NormalUser database was inserted successfully!|@"));
                    }
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            // Check if business user, insert into BusinessUser DB
            if (user instanceof BusinessUser) {
                // Down-size to child object, BusinessUser for child method usage
                BusinessUser newUser = (BusinessUser) user;

                // Template to insert into BusinessUser DB
                String sql = "INSERT INTO BusinessUser (UserID, UEN, BusinessName) VALUES (?, ?, ?)";
                PreparedStatement statement2 = db.prepareStatement(sql);
    
                // Fill up update statements with latest particulars for BusinessUser DB
                statement2.setInt(1, newUser.getUserID());
                statement2.setString(2, newUser.getUEN());
                statement2.setString(3, newUser.getBusinessName());

                // Insert into BusinessUser DB
                int rowsInserted2 = statement2.executeUpdate();

                // Prints successful insertion
                if (rowsInserted2 > 0) {
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|51 A new row in BusinessUser database was inserted successfully!|@"));
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
            String sql = "SELECT * FROM User u " +
            "LEFT JOIN NormalUser nu ON u.userid = nu.userid " +
            "LEFT JOIN BusinessUser bu on u.userid = bu.userid " +
            "WHERE u.Username = ?";

            PreparedStatement statement1 = db.prepareStatement(sql);
            statement1.setString(1, username);
            ResultSet myRs1 = statement1.executeQuery();

            // Start reading after title row onwards
            if(myRs1.next()){
                // Check whether user is deactivated, disallow login if disabled
                if(myRs1.getInt("Active") == 0) {
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|51 \nYour account is inactive. Please contact the bank administrator.|@"));
                    System.exit(1);
                }
                String tempPassword = AES256.encrypt(password, myRs1.getString("PasswordSalt"));
                // Process to check if password is correct, start pulling user data from DB
                if(tempPassword.equals(myRs1.getString("PasswordHash"))) {
                    // ID and type reserved to differentiate and create Normal or Business user
                    int userID = myRs1.getInt("UserID");
                    int userType = myRs1.getInt("UserType");

                    // If user is NormalUser, get extra attributes that NormalUser has
                    if (userType == 1) {

                        // Read from NormalUser DB and load user with saved particulars
                        user = new NormalUser(userID,
                                            myRs1.getString("Username"),
                                            myRs1.getString("Email"),
                                            myRs1.getString("Phone"),
                                            myRs1.getString("AddressOne"),
                                            myRs1.getString("AddressTwo"),
                                            myRs1.getString("AddressThree"),
                                            myRs1.getString("PostalCode"),
                                            myRs1.getDate("RegistrationDate"),
                                            myRs1.getInt("UserType"),
                                            myRs1.getBoolean("Active"),
                                            myRs1.getString("NRIC"),
                                            myRs1.getString("FirstName"),
                                            myRs1.getString("MiddleName"),
                                            myRs1.getString("LastName"),
                                            myRs1.getString("Gender"),
                                            myRs1.getDate("Birthday")
                                            );
                                            user.setCensorPass(myRs1.getString("PasswordSalt"), tempPassword);
                        return user;
                    }
                    // If user is BusinessUser
                    else if (userType == 2) {
                        // Read from "BusinessUser" DB and load user with saved particulars
                        user = new BusinessUser(userID,
                                                myRs1.getString("Username"),
                                                myRs1.getString("Email"),
                                                myRs1.getString("Phone"),
                                                myRs1.getString("AddressOne"),
                                                myRs1.getString("AddressTwo"),
                                                myRs1.getString("AddressThree"),
                                                myRs1.getString("PostalCode"),
                                                myRs1.getDate("RegistrationDate"),
                                                myRs1.getInt("UserType"),
                                                myRs1.getBoolean("Active"),
                                                myRs1.getString("UEN"),
                                                myRs1.getString("BusinessName")
                                                );
                                                user.setCensorPass(myRs1.getString("PasswordSalt"), tempPassword);
                        return user;
                    }
                }
            }
        }
        catch (SQLException e) {
            // Check for any SQL connection errors
            e.printStackTrace();
            System.exit(1);
        }
        finally {
            // Close DB connection
            SQLConnect.disconnectDB(db);
        }
        return user;
    }
    
    // Method to update DBs with latest information
    public static boolean updateUser(User user) {
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
            // e.printStackTrace();
            return false;
        }
        finally {
            // Close DB connection
            SQLConnect.disconnectDB(db);
        }
        return true;
    }

    // Method to display update options and get new information
    public static void getNewUpdates(Scanner input, User user) {
        boolean isContinue = true;
        int updateChoice = -1;
        int max = 7;
        String newUpdate = "";
        String oldValue;

        do{
            do {
                try {
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|51 Which user detail would you like to update..\n|@"));
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|39 1- Username|@"));
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|39 2- Email|@"));
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|39 3- Phone|@"));
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|39 4- Address One|@"));
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|39 5- Address Two|@"));
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|39 6- Address Three|@"));
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|39 7- Postal Code|@"));

                    if (user instanceof NormalUser) {
                        max = 12;
                        System.out.println(CommandLine.Help.Ansi.ON.string("@|39 8- NRIC|@"));
                        System.out.println(CommandLine.Help.Ansi.ON.string("@|39 9- First Name|@"));
                        System.out.println(CommandLine.Help.Ansi.ON.string("@|39 10- Middle Name|@"));
                        System.out.println(CommandLine.Help.Ansi.ON.string("@|39 11- Last Name|@"));
                        System.out.println(CommandLine.Help.Ansi.ON.string("@|39 12- Gender|@"));
                        System.out.println(CommandLine.Help.Ansi.ON.string("@|51 \nPress '0' to go back to the previous menu.|@"));
                    }

                    if (user instanceof BusinessUser) {
                        max = 9;
                        System.out.println(CommandLine.Help.Ansi.ON.string("@|39 8- UEN|@"));
                        System.out.println(CommandLine.Help.Ansi.ON.string("@|39 9- Business Name|@"));
                        System.out.println(CommandLine.Help.Ansi.ON.string("@|51 \nPress '0' to go back to the previous menu.|@"));
                    }
                    
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|51 What do you want to do?|@"));
                    System.out.print("> ");
                    updateChoice = input.nextInt();
                    checkOption(updateChoice, max);
                    input.nextLine();
                } 
                catch (WrongException e){
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|208 " + e.getMessage() + "|@"));
                }
            } while (updateChoice < 0 || updateChoice > max);

            System.out.println("\n");
            boolean isUpdated = false;

            label: do {
                switch(updateChoice) {
                    case 0: 
                        isContinue = false;
                        isUpdated = true;
                        break;
                    case 1:
                        try {
                            System.out.println(CommandLine.Help.Ansi.ON.string("@|51 Current username: " + user.getUsername() + "|@"));
                            System.out.print("Enter new username: ");
                            newUpdate = input.nextLine();
                            oldValue = user.getUsername();
                            
                            checkString(oldValue, newUpdate, "username");
                            user.setUsername(newUpdate);

                            if(!updateUser(user)) {
                                user.setUsername(oldValue);
                                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUsername already taken!\n|@"));
                            }
                            else {
                                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUpdate request successful!\n|@"));
                                isUpdated = true;
                            }
                        }
                        catch (WrongException e) {
                            System.out.println(CommandLine.Help.Ansi.ON.string("@|208 " + e.getMessage() + "|@"));
                        }

                        break;
                    case 2:
                        try {
                            System.out.println(CommandLine.Help.Ansi.ON.string("@|51 Current email: " + user.getEmail() + "|@"));
                            System.out.print("Enter new email: ");
                            newUpdate = input.nextLine();
                            oldValue = user.getEmail();

                            checkString(oldValue, newUpdate, "email");
                            user.setEmail(newUpdate);

                            if(!updateUser(user)) {
                                user.setEmail(oldValue);
                                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nEmail already taken!\n|@"));
                            }
                            else {
                                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUpdate request successful!\n|@"));
                                isUpdated = true;
                            }
                        }
                        catch (WrongException e) {
                            System.out.println(CommandLine.Help.Ansi.ON.string("@|208 " + e.getMessage() + "|@"));
                        }
                        
                        break;
                    case 3:
                        try {
                            System.out.println(CommandLine.Help.Ansi.ON.string("@|51 Current phone: " + user.getPhone() + "|@"));
                            System.out.print("Enter new phone: ");
                            newUpdate = input.nextLine();
                            oldValue = user.getPhone();

                            checkString(oldValue, newUpdate, "phone number");
                            user.setPhone(newUpdate);

                            if(!updateUser(user)) {
                                user.setPhone(oldValue);
                                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nPhone number already taken!\n|@"));
                            }
                            else {
                                // Loop controller
                                boolean isVerified = false;
                                boolean smsSent = false;
                                String otp = SMS.generateOTP(6);
                                String input_otp = null;

                                // Sends verification message to phone
                                smsSent = SMS.sendSMS(newUpdate, "Your Bank OTP is: " + otp);
                                if(smsSent) {
                                    System.out.println(CommandLine.Help.Ansi.ON.string("@|208 A One-Time Passcode has been sent to your phone number for verification.|@"));
                                }

                                // Sends verification message to user's phone and check for verification status
                                while (!isVerified) {

                                    System.out.printf("Enter your OTP (%s): ", otp);
                                    input_otp = input.nextLine();

                                    if(otp.equals(input_otp)) {
                                        isVerified = true;
                                        System.out.println(CommandLine.Help.Ansi.ON.string("@|208 Phone has been verified!|@"));
                                    }
                                    else {
                                        System.out.println(CommandLine.Help.Ansi.ON.string("@|208 Wrong OTP entered! Please try again.|@"));
                                    }

                                }
                                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUpdate request successful!\n|@"));
                                isUpdated = true;
                            }
                        }
                        catch (WrongException e) {
                            System.out.println(CommandLine.Help.Ansi.ON.string("@|208 " + e.getMessage() + "|@"));
                        }

                        break;
                    case 4:
                        try {
                            System.out.println(CommandLine.Help.Ansi.ON.string("@|51 Current address one: " + user.getAddresses(1) + "|@"));
                            System.out.print("Enter new address one: ");
                            newUpdate = input.nextLine();
                            oldValue = user.getAddresses(1);

                            checkString(oldValue, newUpdate, "address one");
                            newUpdate = capitalize(newUpdate);
                            user.setAddress(newUpdate, user.getAddresses(2), user.getAddresses(3), user.getPostalCode());

                            if(!updateUser(user)) {
                                user.setAddress(oldValue, user.getAddresses(2), user.getAddresses(3), user.getPostalCode());
                                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUpdate request failed!\n|@"));
                            }
                            else {
                                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUpdate request successful!\n|@"));
                                isUpdated = true;
                            }
                        }
                        catch (WrongException e) {
                            System.out.println(CommandLine.Help.Ansi.ON.string("@|208 " + e.getMessage() + "|@"));
                        }

                        break;
                    case 5:
                        try {
                            System.out.println(CommandLine.Help.Ansi.ON.string("@|51 Current address two: " + user.getAddresses(2) + "|@"));
                            System.out.print("Enter new address two: ");
                            newUpdate = input.nextLine();
                            oldValue = user.getAddresses(2);

                            checkString(oldValue, newUpdate, "address two");
                            newUpdate = capitalize(newUpdate);
                            user.setAddress(user.getAddresses(1), newUpdate, user.getAddresses(3), user.getPostalCode());

                            if(!updateUser(user)) {
                                user.setAddress(user.getAddresses(1), oldValue, user.getAddresses(3), user.getPostalCode());
                                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUpdate request failed!\n|@"));
                            }
                            else {
                                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUpdate request successful!\n|@"));
                                isUpdated = true;
                            }
                        }
                        catch (WrongException e) {
                            System.out.println(CommandLine.Help.Ansi.ON.string("@|208 " + e.getMessage() + "|@"));
                        }
                        
                        break;
                    case 6:
                        try {
                            System.out.println(CommandLine.Help.Ansi.ON.string("@|51 Current address three: " + user.getAddresses(3) + "|@"));
                            System.out.print("Enter new address three: ");
                            newUpdate = input.nextLine();
                            oldValue = user.getAddresses(3);

                            checkString(oldValue, newUpdate, "address three");
                            newUpdate = capitalize(newUpdate);
                            user.setAddress(user.getAddresses(1), user.getAddresses(2), newUpdate, user.getPostalCode());

                            if(!updateUser(user)) {
                                user.setAddress(user.getAddresses(1), user.getAddresses(2), oldValue, user.getPostalCode());
                                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUpdate request failed!\n|@"));
                            }
                            else {
                                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUpdate request successful!\n|@"));
                                isUpdated = true;
                            }
                        }
                        catch (WrongException e) {
                            System.out.println(CommandLine.Help.Ansi.ON.string("@|208 " + e.getMessage() + "|@"));
                        }
                        
                        break;
                    case 7:
                        try{
                            System.out.println(CommandLine.Help.Ansi.ON.string("@|51 Current postal code: " + user.getPostalCode() + "|@"));
                            System.out.print("Enter new postal code: ");
                            newUpdate = input.next();
                            oldValue = user.getPostalCode();

                            checkString(oldValue, newUpdate, "postal code");
                            user.setAddress(user.getAddresses(1), user.getAddresses(2), user.getAddresses(3), newUpdate);
                            
                            if(!updateUser(user)) {
                                user.setAddress(user.getAddresses(1), user.getAddresses(2), user.getAddresses(3), oldValue);
                                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUpdate request failed!\n|@"));
                            }
                            else {
                                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUpdate request successful!\n|@"));
                                isUpdated = true;
                            }
                        }
                        catch (WrongException e) {
                            System.out.println(CommandLine.Help.Ansi.ON.string("@|208 " + e.getMessage() + "|@"));
                        }

                        break;
                    case 8:
                        try{
                            if (user instanceof NormalUser) {
                                NormalUser tempUser = (NormalUser) user;

                                System.out.println(CommandLine.Help.Ansi.ON.string("@|51 Current NRIC: " + tempUser.getNRIC() + "|@"));
                                System.out.print("Enter new NRIC: ");
                                newUpdate = input.next();
                                oldValue = tempUser.getNRIC();

                                checkString(oldValue, newUpdate, "NRIC");
                                tempUser.setNRIC(newUpdate);

                                if(!updateUser(tempUser)) {
                                    tempUser.setNRIC(oldValue);
                                    System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUpdate request failed!\n|@"));
                                }
                                else {
                                    System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUpdate request successful!\n|@"));
                                    isUpdated = true;
                                }
                            }
                            if (user instanceof BusinessUser) {
                                BusinessUser tempUser = (BusinessUser) user;
                                System.out.println(CommandLine.Help.Ansi.ON.string("@|51 Current UEN: " + tempUser.getUEN() + "|@"));
                                System.out.print("Enter new UEN: ");
                                newUpdate = input.next();
                                oldValue = tempUser.getUEN();

                                checkString(oldValue, newUpdate, "UEN");
                                tempUser.setUEN(newUpdate);

                                if(!updateUser(tempUser)) {
                                    tempUser.setUEN(oldValue);
                                    System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUpdate request failed!\n|@"));
                                }
                                else {
                                    System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUpdate request successful!\n|@"));
                                    isUpdated = true;
                                }
                            }
                        }
                        catch (WrongException e) {
                            System.out.println(CommandLine.Help.Ansi.ON.string("@|208 " + e.getMessage() + "|@"));
                        }

                        break;
                    case 9:
                        try{
                            if (user instanceof NormalUser) {
                                NormalUser tempUser = (NormalUser) user;

                                System.out.println(CommandLine.Help.Ansi.ON.string("@|51 Current first name: " + tempUser.getFirstName() + "|@"));
                                System.out.print("Enter new first name: ");
                                newUpdate = input.next();
                                oldValue = tempUser.getFirstName();

                                checkString(oldValue, newUpdate, "first name");
                                tempUser.setAllNames(newUpdate, tempUser.getMiddleName(), tempUser.getLastName());

                                if(!updateUser(tempUser)) {
                                    tempUser.setAllNames(oldValue, tempUser.getMiddleName(), tempUser.getLastName());
                                    System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUpdate request failed!\n|@"));
                                }
                                else {
                                    System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUpdate request successful!\n|@"));
                                    isUpdated = true;
                                }
                            }
                            if (user instanceof BusinessUser) {
                                BusinessUser tempUser = (BusinessUser) user;
                                System.out.println(CommandLine.Help.Ansi.ON.string("@|51 Current business name: " + tempUser.getBusinessName() + "|@"));
                                System.out.print("Enter new business name: ");
                                newUpdate = input.next();
                                oldValue = tempUser.getBusinessName();

                                checkString(oldValue, newUpdate, "business name");
                                tempUser.setBusinessName(newUpdate);

                                if(!updateUser(tempUser)) {
                                    tempUser.setBusinessName(oldValue);
                                    System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUpdate request failed!\n|@"));
                                }
                                else {
                                    System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUpdate request successful!\n|@"));
                                    isUpdated = true;
                                }
                            }
                        }
                        catch (WrongException e) {
                            System.out.println(CommandLine.Help.Ansi.ON.string("@|208 " + e.getMessage() + "|@"));
                        }

                        break;
                    case 10:
                        try{
                            NormalUser tempUser = (NormalUser) user;

                            System.out.println(CommandLine.Help.Ansi.ON.string("@|51 Current middle name: " + tempUser.getMiddleName() + "|@"));
                            System.out.print("Enter new middle name: ");
                            newUpdate = input.next();
                            oldValue = tempUser.getMiddleName();

                            checkString(oldValue, newUpdate, "middle name");
                            newUpdate = capitalize(newUpdate);
                            tempUser.setAllNames(tempUser.getFirstName(), newUpdate, tempUser.getLastName());

                            if(!updateUser(tempUser)) {
                                tempUser.setAllNames(tempUser.getFirstName(), oldValue, tempUser.getLastName());
                                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUpdate request failed!\n|@"));
                            }
                            else {
                                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUpdate request successful!\n|@"));
                                isUpdated = true;
                            }
                        }
                        catch (WrongException e) {
                            System.out.println(CommandLine.Help.Ansi.ON.string("@|208 " + e.getMessage() + "|@"));
                        }

                        break;
                    case 11:
                        try{
                            NormalUser tempUser2 = (NormalUser) user;

                            System.out.println(CommandLine.Help.Ansi.ON.string("@|51 Current last name: " + tempUser2.getLastName() + "|@"));
                            System.out.print("Enter new last name: ");
                            newUpdate = input.next();
                            oldValue = tempUser2.getLastName();

                            checkString(oldValue, newUpdate, "last name");
                            newUpdate = capitalize(newUpdate);
                            tempUser2.setAllNames(tempUser2.getFirstName(), tempUser2.getMiddleName(), newUpdate);

                            if(!updateUser(tempUser2)) {
                                tempUser2.setAllNames(tempUser2.getFirstName(), tempUser2.getMiddleName(), oldValue);
                                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUpdate request failed!\n|@"));
                            }
                            else {
                                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUpdate request successful!\n|@"));
                                isUpdated = true;
                            }
                        }
                        catch (WrongException e) {
                            System.out.println(CommandLine.Help.Ansi.ON.string("@|208 " + e.getMessage() + "|@"));
                        }

                        break;
                    case 12:
                        try{
                            NormalUser tempUser3 = (NormalUser) user;

                            System.out.println(CommandLine.Help.Ansi.ON.string("@|51 Current gender: " + tempUser3.getGender() + "|@"));
                            System.out.println(CommandLine.Help.Ansi.ON.string("@|51 Select new gender|@"));
                            System.out.println(CommandLine.Help.Ansi.ON.string("@|39 1- Male|@"));
                            System.out.println(CommandLine.Help.Ansi.ON.string("@|39 2- Female|@"));
                            System.out.println(CommandLine.Help.Ansi.ON.string("@|51 \nPress '0' to go back to the previous menu.|@"));
                            System.out.print("> ");
                            int tempUpdate = input.nextInt();
                            oldValue = tempUser3.getGender();

                            checkOption(tempUpdate, 2);

                            if(tempUpdate == 0) {
                                System.out.println("");
                                break label;
                            }
                            else if(tempUpdate == 1) {
                                newUpdate = "Male";
                            }
                            else {
                                newUpdate = "Female";
                            }

                            checkString(oldValue, newUpdate, "gender");
                            tempUser3.setGender(newUpdate);

                            if(!updateUser(tempUser3)) {
                                tempUser3.setAllNames(tempUser3.getFirstName(), tempUser3.getMiddleName(), oldValue);
                                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUpdate request failed!\n|@"));
                            }
                            else {
                                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUpdate request successful!\n|@"));
                                isUpdated = true;
                            }
                        }
                        catch (WrongException e) {
                            System.out.println(CommandLine.Help.Ansi.ON.string("@|208 " + e.getMessage() + "|@"));
                        }
                        
                        break;
                }
            } while (!isUpdated);
        } while (isContinue);

        System.out.print("\n");
    }

    // Method to reset user password
    public static void resetUserPassword(User user) {
        // Loop counter where user only allowed 3 tries
        int passwordTries = 2;
        Console console = System.console();

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
                System.out.println("Request to reset password\n");
                char[] passwordChars = console.readPassword("Enter old password (Hidden for Security): ");
                String oldPassword = new String(passwordChars);

                // Get old password from DB
                String DBPassword = AES256.decrypt(myRs1.getString("PasswordHash"), myRs1.getString("PasswordSalt"));

                // Check old password with DB password. If match continue, else throw exception
                checkPassword(oldPassword, DBPassword, passwordTries);

                // Get new password input
                passwordChars = console.readPassword("Enter new password (Hidden for Security): ");
                String newPassword = new String(passwordChars);

                // Secondary loop counter where user have 3 tries to confirm new password
                for(int i = 1; i < 4; i++) {
                    // Template to select "User" DB for updating data
                    String sql2 = String.format("UPDATE User SET PasswordHash = ? WHERE UserID = %s", user.getUserID());
                    PreparedStatement statement = db.prepareStatement(sql2);

                    // Get confirmed new password input
                    passwordChars = console.readPassword("Confirm new password (Hidden for Security): ");
                    String confirmPassword = new String(passwordChars);

                    // Check new password matches twice. If match continue, else reduce tries by 1
                    if(newPassword.equals(confirmPassword)){
                        // Encrpyts new password and set it for updating to DB
                        newPassword = AES256.encrypt(confirmPassword, myRs1.getString("PasswordSalt"));
                        statement.setString(1, newPassword);

                        // Perform database updates
                        statement.executeUpdate();

                        // Prints successful message and breaks out of loop and method
                        System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nPassword resetted successfully!\n|@"));
                        passwordTries = -1;
                        user.setCensorPass(myRs1.getString("PasswordSalt"), newPassword);
                        break;
                    }
                    
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|208 Passwords did not match! Please try again. " + (3-i) + " tries left.\n|@"));
                 
                    if(i == 3){
                        passwordTries = 0;
                    }
                }
            }
            catch (SQLException e) {
                // Check for any SQL connection errors
                e.printStackTrace();
            }
            catch (WrongException e){
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
                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nPassword reset request aborted.\n\n|@"));
            }
            passwordTries--;
        } while (passwordTries >= 0);
    }

    // Method to deactivate user from DB
    public static void deactivateUser(Scanner input, User user){
        // Loop counter where user only allowed 3 tries
        int passwordTries = 2;
        int deactivationOption1 = 0;
        int deactivationOption2 = 0;
        Console console = System.console();
        
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
                char[] passwordChars = console.readPassword("Enter password (Hidden for Security): ");
                String oldPassword = new String(passwordChars);

                // Get old password from DB
                String DBPassword = AES256.decrypt(myRs1.getString("PasswordHash"), myRs1.getString("PasswordSalt"));

                // Check old password with DB password. If match continue, else throw exception
                checkPassword(oldPassword, DBPassword, passwordTries);
            
                // Template to select "User" DB for updating data
                String sql2 = String.format("UPDATE User SET Active = ? WHERE UserID = %s", user.getUserID());
                PreparedStatement statement = db.prepareStatement(sql2);

                // Display menu and checks for valid input of 1 - 2
                System.out.print("\n");
                while(deactivationOption1 != 1 && deactivationOption1 != 2) {
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|51 Activation of user account can only be done on administrator side.|@"));
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|51 \nAre you sure you want to deactivate your user account?|@"));
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|39 \n1- Yes|@"));
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|39 \n2- No|@"));
                    System.out.print("\n> ");
                    deactivationOption1 = input.nextInt();

                    if(deactivationOption1 != 1 && deactivationOption1 != 2) {
                        System.out.println(CommandLine.Help.Ansi.ON.string("@|208 Wrong input. Try again..\n|@"));
                    }
                }
                
                // If user wants to deactivate user account and checks for valid input of 1 - 2
                if(deactivationOption1 == 1) {
                    while(deactivationOption2 != 1 && deactivationOption2 != 2) {
                        System.out.println(CommandLine.Help.Ansi.ON.string("@|51 \nConfirm deactivation? |@"));
                        System.out.println(CommandLine.Help.Ansi.ON.string("@|39 \n1- Yes|@"));
                        System.out.println(CommandLine.Help.Ansi.ON.string("@|39 \n2- No|@"));
                        System.out.print("\n> ");
                        deactivationOption2 = input.nextInt();

                        if(deactivationOption2 != 1 && deactivationOption2 != 2) {
                            System.out.println(CommandLine.Help.Ansi.ON.string("@|51 Wrong input. Try again..\n|@"));
                        }
                    }

                    // If user confirms deactivation of user account
                    if(deactivationOption1 == deactivationOption2) {
                        statement.setInt(1, 0);

                        // Perform database updates and exit session
                        statement.executeUpdate();
                        System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUser account has been deactivated.|@"));
                        System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nNow logging out...|@"));
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            System.out.println(e.getMessage());
                        }finally{
                            input.close();
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
            catch (WrongException e) {
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
                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nUser account deactivation request aborted.\n|@"));
                passwordTries = -1;
            }
            passwordTries--;
        } while (passwordTries >= 0);
    }

    /* Start of user defined exception */
    private static class WrongException extends Exception {
        public WrongException(String errorMessage) {
            super(errorMessage);
        }
    }

    private static void checkPassword(String oldPass, String newPass, int tries) throws WrongException {
        if(!oldPass.equals(newPass)) {
            throw new WrongException("\nIncorrect password! " + tries + " tries left.\n");
        }
    }

    private static void checkOption(int number, int max) throws WrongException {
        if(number > max || number < 0) {
            throw new WrongException("\nInvalid input. Enter 0 to " + max + ".\n");
        }
    }

    private static void checkString(String oldString, String newString, String variable) throws WrongException {
        if(newString.isEmpty() || newString.trim().isEmpty()) {
            throw new WrongException("\nField cannot be empty!\n");
        }
        if(oldString.equals(newString)) {
            throw new WrongException("\nNew " + variable + " cannot be same as old " + variable + "!\n");
        }
    }
    /* End of exception */

    // Method to capitalized first character of a string
    public static String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input; // Return the input as-is if it's null or empty
        }
        // Uppercase the first character and concatenate the rest of the string
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
