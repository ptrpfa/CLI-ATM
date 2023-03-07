package Account;

import Server.ServerAccount;

public class AccountTransaction implements ServerAccount{

    public void Deposit(Account account, double amount){
        //Update accBalance
        double availableBalance = account.getAvailableBalance();
        double totalBalance = account.getTotalBalance();
        boolean result = ServerAccount.AccountDeposit(account.getUserID(), account.getAccID(), availableBalance, totalBalance, amount);
        
        //If Server updated
        if(result){
            availableBalance += amount;
            totalBalance += amount;
            account.setAvailableBalance(availableBalance);
            account.setTotalBalance(totalBalance);
        }else{
            throw new TransactionError("Error in system...");
        }
    }

    public void Withdraw(Account account, double amount){
        double limit = account.getWithdrawLimit();
        double availableBalance = account.getAvailableBalance();
        double totalBalance = account.getTotalBalance();
        if(amount > availableBalance){
            //Cannot withdraw more than the account balance
            throw new TransactionError("*****Not Enough Money*****\n*****Withdrawal Terminated!*****");
        }else if(amount > limit){
            //Cannot withdraw more than WithdrawLimit
            throw new TransactionError("*****Withdraw Limit Reached*****\n*****Withdrawal Terminated!*****");
        }
        //Checks all true
        boolean result = ServerAccount.AccountWithdrawal(account.getUserID(), account.getAccID(), availableBalance, totalBalance, amount);

        //If Server updated
        if(result){
            availableBalance -= amount;
            totalBalance -= amount;
            account.setAvailableBalance(availableBalance);
            account.setTotalBalance(totalBalance);
        }else{
            throw new TransactionError("Error in system...");
        }
    
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


