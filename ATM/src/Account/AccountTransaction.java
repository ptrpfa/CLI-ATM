package Account;
public class AccountTranscation {
    private double amount;
    private Account account;
    public AccountTranscation(Account account, double amount) {
        this.account = account;
        this.amount = amount;
    }
    public boolean Deposit(){
        //Update accBalance
        //Set try catch to see if set will return SQL IO exception
        account.setAvailableBalance(amount);
        //if ok return true
        return true;
    }
    public boolean Withdraw() throws Exception{
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

    public boolean transferFunds() throws Exception {
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
}
