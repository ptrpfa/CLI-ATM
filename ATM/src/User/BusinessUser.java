package User;

import java.util.Date;

public class BusinessUser extends User{
    private String UEN;
    private String businessName;

    // Test
    public BusinessUser() {
    }

    // Constructor taking data from DB including NULLs
    public BusinessUser(int userID, String username, String email, String phone, String addressOne, 
                        String addressTwo, String addressThree, String postalCode, Date registrationDate, int userType, boolean active, 
                        String UEN, String businessName) {

        super(userID, username, email, phone, addressOne, addressTwo, addressThree, postalCode, registrationDate, userType, active);
        this.UEN = UEN;
        this.businessName = businessName;
    }

    // Constructor taking in object type
    public BusinessUser(User user,
                        String UEN, String businessName) {
        super(user);
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
