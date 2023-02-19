package User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Server.MySQLConnect;

class NormalUser extends User {
    private String NRIC;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private Date birthday;
    
    // public NormalUser(int userID, String username, String passwordSalt, String passwordHash, String email, String phone, String addressOne, 
    //                   String addressTwo, String addressThree, String postalCode, Date registrationDate, boolean active, 
    //                   String NRIC, String firstName, String middleName, String lastName, String gender, String birthDay) {

    //     super(userID, username, passwordSalt, passwordHash, email, phone, addressOne, addressTwo, addressThree, postalCode, registrationDate, active);
    //     this.NRIC = NRIC;
    //     this.firstName = firstName;
    //     this.middleName = middleName;
    //     this.lastName = lastName;
    //     this.gender = gender;
    //     this.birthDay = birthDay;
    // }

    public NormalUser(int userID) {
        super(userID);

        boolean exists = false;
        MySQLConnect mysqlConnect = new MySQLConnect();

        // Select "NormalUser" database
        String sql = "SELECT * FROM normaluser";

        // Try to connect with inputted UserID
        try {
            PreparedStatement statement = mysqlConnect.connect().prepareStatement(sql);
            ResultSet myRs = statement.executeQuery();  

            // Go to data row onwards
            myRs.next(); 

            // Reiterate the table to look for data where inputted userID exists
            while(myRs.next()){
                if(myRs.getInt("UserID") == userID){
                    this.NRIC = myRs.getString("NRIC");
                    this.firstName = myRs.getString("FirstName");
                    this.middleName = myRs.getString("MiddleName");
                    this.lastName = myRs.getString("LastName");
                    this.gender = myRs.getString("Gender");
                    this.birthday = myRs.getDate("Birthday");
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

    // Returns user NRIC
    public String getNRIC() {
        return this.NRIC;
    }

    // Returns user first name 
    public String getFirstName() {
        return this.firstName;
    }
    
    // Returns user middle name 
    public String getMiddleName() {
        return this.middleName;
    }

    // Returns user last name
    public String getLastName() {
        return this.lastName;
    }

    // Returns user last name
    public String getGender() {
        return this.gender;
    }

    // Returns user birthday
    public String getBirthday() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy"); // MM = numeric month, MMM = month in shortform, MMMM month in longform
        String date = formatter.format(this.birthday);
        return date;
    }

    public static void main(String[] args) {
        Integer userID = 833020;

        NormalUser testUser = new NormalUser(userID);
        System.out.println(testUser.getUsername());
        System.out.println(testUser.getEmail());
        System.out.println(testUser.getRegistrationDate());
        System.out.println(testUser.getAddresses(2));

        System.out.println("Good day, Mr/Ms " + testUser.getLastName() + ", " + testUser.getNRIC());
        System.out.println(testUser.getBirthday());
    }
}
