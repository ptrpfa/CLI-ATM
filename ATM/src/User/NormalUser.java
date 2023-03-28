package User;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NormalUser extends User {
    private String NRIC;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private Date birthday;
    
    // Test
    public NormalUser() {
    }

    // Constructor taking data from DB including NULLs
    public NormalUser(int userID, String username, String email, String phone, String addressOne, 
                      String addressTwo, String addressThree, String postalCode, Date registrationDate, int userType, boolean active, 
                      String NRIC, String firstName, String middleName, String lastName, String gender, Date birthday) {

        super(userID, username, email, phone, addressOne, addressTwo, addressThree, postalCode, registrationDate, userType, active);
        this.NRIC = NRIC;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthday = birthday;
    }

    // Constructor taking in object type
    public NormalUser(User user,
                      String NRIC, String firstName, String middleName, String lastName, String gender, Date birthday) {
        super(user);
        this.NRIC = NRIC;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthday = birthday;
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
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String date = formatter.format(this.birthday);
        return date;
    }

    // Hold NRIC to push for updates afterwards
    public void setNRIC(String IC) {
        this.NRIC = IC;
    }

    // Hold first, middle and last names to push for updates afterwards
    public void setAllNames(String firstName, String middleName, String lastName) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
    }

    // Hold gender to push for updates afterwards
    public void setGender(String gender) {
        this.gender = gender;
    }

    // Hold gender to push for updates afterwards
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
