package User;

import java.text.SimpleDateFormat;
import java.util.Date;

import Server.AES256;

public class User {
    private int userID;
    private String username;
    private String email;
    private String phone;
    private String addressOne;
    private String addressTwo;
    private String addressThree;
    private String postalCode;
    private Date registrationDate;
    private int userType;
    private boolean active;
    private String censoredPass;

    // Test
    public User() {
    }

    // Constructor taking data from DB including NULLs
    public User(int userID, String username, String email, String phone, String addressOne, 
                String addressTwo, String addressThree, String postalCode, Date registrationDate, int userType, boolean active) { 

        this.userID = userID;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.addressOne = addressOne;
        this.addressTwo = addressTwo;
        this.addressThree = addressThree;
        this.postalCode = postalCode;
        this.registrationDate = registrationDate;
        this.userType = userType;
        this.active = active;
    }

    // Constructor taking in object type
    public User(User user) { 
        this.userID = user.userID;
        this.username = user.username;
        this.email = user.email;
        this.phone = user.phone;
        this.addressOne = user.addressOne;
        this.addressTwo = user.addressTwo;
        this.addressThree = user.addressThree;
        this.postalCode = user.postalCode;
        this.registrationDate = user.registrationDate;
        this.userType = user.userType;
        this.active = user.active;
    }

    // Returns user userID
    public int getUserID() {
        return this.userID;
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
            String address = this.addressOne;
            return address;
        }
        else if(option == 2) {
            String address = this.addressTwo;
            return address;
        }
        else {
            String address = this.addressThree;
            return address;
        }
    }

    // Returns user postal code
    public String getPostalCode() {
        return this.postalCode;
    }
    
    // Returns user registration date in dd-MM-yyyy format
    public String getRegistrationDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy"); 
        String date = formatter.format(this.registrationDate);
        return date;
    }
  
    // Returns user activation status. 1 - active, 0 - inactive
    public int getUserType() {
        return this.userType;
    }

    // Returns user type
    public int getActive() {
        if(this.active == true) {
            return 1;
        }
        else {
            return 0;
        }
    }

    // Hold userID 
    public void setUserID(int ID) {
        this.userID = ID;
    }
    
    // Hold username to push for updates afterwards
    public void setUsername(String username) {
        this.username = username;
    }

    // Hold username to push for updates afterwards
    public void setEmail(String email) {
        this.email = email;
    }

    // Hold username to push for updates afterwards
    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Hold username to push for updates afterwards
    public void setAddress(String addressOne, String addressTwo, String addressThree, String postalCode) {
        this.addressOne = addressOne;
        this.addressTwo = addressTwo;
        this.addressThree = addressThree;
        this.postalCode = postalCode;
    }

    // Hold registration date
    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    // Hold user activation status. 1 - active, 0 - inactive
    public void setActive(int active){
        if(active == 1) {
            this.active = true;
        }
        else{
            this.active = false;
        }
    }

    // Hold user's half censored password for display
    public void setCensorPass(String passwordSalt, String passwordHash){
        this.censoredPass = censoredPass(passwordSalt, passwordHash);
    }

    // Method to hide half of password with * for display
    private String censoredPass(String passwordSalt, String passwordHash) {        
        String decryptedPass = AES256.decrypt(passwordHash, passwordSalt);
        int half = decryptedPass.length() / 2;

        String censoredText = censor(decryptedPass, half + 1);

        return censoredText;
    }

    // Method to hide half of password with * for display
    private String censor(String text, int n) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        if (n < 1 || n > text.length()) {
            return text;
        }

        String censorString = "";
        for (int i = 0; i < n; i++) {
            censorString += "*";
        }

        String censoredText = censorString + text.substring(n);

        return censoredText;
    }

    //Printing Methods
    public String[] PrintValues(){   
        String[] values = {Integer.toString(userID), username, censoredPass, email, phone, addressOne, addressTwo, addressThree, postalCode};
        return values;
    }

    public static String[] PrintHeaders(){
        String[] headers = {"UserID", "Username", "Password", "Email", "Phone", "Address One", "Address Two", "Address Three", "Postal Code"};
        return headers;
    }
}