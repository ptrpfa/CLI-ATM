package Account;

import java.math.BigDecimal;

import Server.ServerAccount;
import picocli.CommandLine;

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
            System.out.println(CommandLine.Help.Ansi.ON.string("@|208 Successful Deposit!|@"));
        }else{
            throw new TransactionError("Error in system...");
        }
    }

    public void Withdraw(Account account, double amount){
        double availableBalance = account.getAvailableBalance();
        double totalBalance = account.getTotalBalance();
        double currentLimit = ServerAccount.getRemainingWithdrawLimit(account.getAccID(), account.getWithdrawLimit());
        if(amount > availableBalance){
            //Cannot withdraw more than the account balance
            throw new TransactionError("*****Insufficient Funds*****\n\n*****Withdrawal Terminated!*****");
        } else if (amount > currentLimit) {
            // Cannot withdraw more than limit set
            throw new TransactionError("*****Amount Exceeds Withdrawal Limit*****\n\n*****Withdrawal Terminated!*****");
        }
        //Checks all true
        boolean result = ServerAccount.AccountWithdrawal(account.getUserID(), account.getAccID(), availableBalance, totalBalance, amount);

        //If Server updated
        if(result){
            availableBalance -= amount;
            totalBalance -= amount;
            account.setAvailableBalance(availableBalance);
            account.setTotalBalance(totalBalance);
            System.out.println(CommandLine.Help.Ansi.ON.string("@|208 Successful Withdrawal!|@"));
        }else{
            throw new TransactionError("Error in system...");
        }
    
    }

    public void transferFunds(Account IssuingAccount, Account ReceivingAccount, double amount){
        double accBalance = IssuingAccount.getAvailableBalance();
        double totalBalance = IssuingAccount.getTotalBalance();
        BigDecimal currentLimit = ServerAccount.getRemainingTransferLimit(IssuingAccount.getAccID(), IssuingAccount.getTransferLimit());

        // Check if accounts are the same
        if(IssuingAccount.getAccID() == ReceivingAccount.getAccID()) {
            throw new TransactionError("*****Cannot transfer to the same account!*****\n\n*****Transaction Terminated!*****\n");
        }
        if(amount > accBalance){
            //Cannot transfer more than the account balance
            throw new TransactionError("*****Insufficient Funds*****\n\n*****Transaction Terminated!*****\n");
        }else if(BigDecimal.valueOf(amount).compareTo(currentLimit) > 0){
            //Cannot transfer more than outgoing transfer limit
            throw new TransactionError("*****Amount Exceeds Transfer Limit*****\n\n*****Transfer Terminated!*****\n");
        }
        //DB query to change the availableBalance
        boolean result = ServerAccount.TransferFunds(IssuingAccount, ReceivingAccount, amount);
        if(result){ //Transfer Successful
            IssuingAccount.setAvailableBalance(accBalance-amount);
            IssuingAccount.setTotalBalance(totalBalance-amount);
            accBalance = ReceivingAccount.getAvailableBalance();
            totalBalance = ReceivingAccount.getTotalBalance();
            ReceivingAccount.setAvailableBalance(accBalance+amount);
            ReceivingAccount.setTotalBalance(accBalance+amount);
            System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nSuccessful internal fund transfer!|@"));
        }else{ //Transfer Failed    
            throw new TransactionError("Transaction at Server failed");
        } 
    }

    public static class TransactionError extends RuntimeException{
        public TransactionError(String message) {
            super(message);
        }
    }        
}


