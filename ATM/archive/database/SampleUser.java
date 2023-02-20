/* Sample class implementation for using DBConnection */

import java.util.*;

public class SampleUser implements DBConnection {

    // Instance attributes (Generic User class)
    protected int userid;
    protected String username;
    protected String passwordSalt;
    protected String passwordHash;
    protected String email;
    protected String phone;
    protected String addressOne;
    protected String addressTwo;
    protected String addressThree;
    protected String postalCode;
    protected Date RegistrationDate;
    protected boolean active;

}