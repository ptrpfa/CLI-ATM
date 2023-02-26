package Account;

import java.util.List;

import Server.ServerAccount;

public class AccountService implements ServerAccount{

    public Account CreateAccount(int userid, String accName, String description, double initialDeposit){
        //Create new Account
        //Send to DB Service 
        Account acc = ServerAccount.NewAccount(userid, accName, description, initialDeposit);
        return acc;
    }

    public boolean closeAccount(){
        return true; //if no error
    }
}
