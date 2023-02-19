package User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Server.MySQLConnect;

class BusinessUser extends User{
    private String UEN;
    private String businessName;

    // public BusinessUser(int userID, String username, String passwordSalt, String passwordHash, String email, String phone, String addressOne, 
    //                     String addressTwo, String addressThree, String postalCode, Date registrationDate, boolean active, 
    //                     String UEN, String businessName) {
                            
    //     super(userID, username, passwordSalt, passwordHash, email, phone, addressOne, addressTwo, addressThree, postalCode, registrationDate, active);
    //     this.UEN = UEN;
    //     this.businessName = businessName;
    // }

    public BusinessUser(int userID) {
        super(userID);

        boolean exists = false;
        MySQLConnect mysqlConnect = new MySQLConnect();

        // Select "NormalUser" database
        String sql = "SELECT * FROM businessuser";

        // Try to connect with inputted UserID
        try {
            PreparedStatement statement = mysqlConnect.connect().prepareStatement(sql);
            ResultSet myRs = statement.executeQuery();  

            // Go to data row onwards
            myRs.next(); 

            // Reiterate the table to look for data where inputted userID exists
            while(myRs.next()){
                if(myRs.getInt("UserID") == userID){
                    this.UEN = myRs.getString("UEN");
                    this.businessName = myRs.getString("BusinessName");
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
    public String getUEN() {
        return this.UEN;
    }

    // Returns user first name 
    public String getBusinessName() {
        return this.businessName;
    }

    public static void main(String[] args) {
        Integer userID = 833022;

        BusinessUser testUser = new BusinessUser(userID);
        System.out.println(testUser.getUsername());
        System.out.println(testUser.getEmail());
        System.out.println(testUser.getRegistrationDate());
        System.out.println(testUser.getAddresses(1));

        System.out.println(testUser.getBusinessName());
        System.out.println(testUser.getUEN());
    }
}
