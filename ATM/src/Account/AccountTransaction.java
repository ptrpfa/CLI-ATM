package Account;

import Server.ServerAccount;

public class AccountTransaction implements ServerAccount{

    public void Deposit(Account account, double amount){
        //Update accBalance
        boolean result = ServerAccount.AccountDeposit(account.getUserID(), account.getAccID(), amount);
        
        //If Server updated
        if(result){
            account.setAvailableBalance(amount);
        }else{
            throw new TransactionError("Error in system...");
        }
    }

    public boolean Withdraw(Account account, double amount) throws Exception{
        double limit = account.getWithdrawLimit();
        double accBalance = account.getAvailableBalance();
        if(amount > accBalance){
            //Cannot withdraw more than the account balance
            throw new Exception("*****不够钱啦！*****\n*****Transaction Terminated!*****");
        }else if(amount > limit){
            //Cannot withdraw more than WithdrawLimit
            throw new Exception("*****达到提权限额！*****\n*****Transaction Terminated!*****");
        }
        //DB query to change the availableBalance
        //Set the available balance, total balance
        //Can maybe return acc Balance
        return true;
    }

    public boolean transferFunds(Account account, double amount) throws Exception {
        double xferLimit = account.getTransferLimit();
        double accBalance = account.getAvailableBalance();
        if(amount > accBalance){
            //Cannot withdraw more than the account balance
            throw new Exception("*****不够钱，你转这么多kan！*****\n*****Transaction Terminated!*****");
        }else if(amount > xferLimit){
            //Cannot withdraw more than WithdrawLimit
            throw new Exception("*****不够钱转账！*****\n*****Transaction Terminated!*****");
        }
        //DB query to change the availableBalance
        //Set the available balance, total balance
        //Can maybe return acc Balance
        return true;
    }

    public static class TransactionError extends RuntimeException{
        public TransactionError(String message) {
            super(message);
        }
    }        
}


