package User;

import java.sql.Date;

public class BusinessUser extends User{
    private String UEN;
    private String businessName;

    // Test
    public BusinessUser() {
    }

    public BusinessUser(int userID, String username, String passwordSalt, String passwordHash, String email, String phone, String addressOne, 
    String addressTwo, String addressThree, String postalCode, Date registrationDate, int userType, boolean active, 
    String UEN, String businessName) {

        super(userID, username, passwordSalt, passwordHash, email, phone, addressOne, addressTwo, addressThree, postalCode, registrationDate, userType, active);
        this.UEN = UEN;
        this.businessName = businessName;
    }

    // Returns user NRIC
    public String getUEN() {
        return this.UEN;
    }

    // Returns user first name 
    public String getBusinessName() {
        return this.businessName;
    }

    // Hold UEN to push for updates afterwards
    public void setUEN(String UEN) {
        this.UEN = UEN;
    }

    // Hold business name to push for updates afterwards
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
}
