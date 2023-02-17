package Account;
public class AccountManagement {
    private Account account;
    public AccountManagement(){

    }
    public boolean CreateAccount(){
        //Call SQL to populate the above details
        //use try and catch
        //Return the accID
        account.setAccID(0); //Set the returned accID
        return true; //if no error
    }
    public boolean closeAccount(){
        return true; //if no error
    }
}
