package Account;
import java.util.Date;

import pl.mjaron.etudes.Table;

public class Account {
    private int accID;
    private int userID;
    private String accNo;
    private String accName;
    private String description;
    private double holdingBalance;
    protected double availableBalance;
    private double totalBalance;
    protected double transferLimit;
    protected double withdrawLimit;
    private Date openingDate;
    private boolean accActive;
    private AccountTransaction transaction;

    public Account(int accID, int userID, String accNo, String accName, String description, double holdingBalance, double availableBalance, double totalBalance, double transferLimit, double withdrawLimit, Date openingDate, boolean accActive) {
        this.accID = accID;
        this.userID = userID;
        this.accNo = accNo;
        this.accName = accName;
        this.description = description;
        this.holdingBalance = holdingBalance;
        this.availableBalance = availableBalance;
        this.totalBalance = totalBalance;
        this.transferLimit = transferLimit;
        this.withdrawLimit = withdrawLimit;
        this.openingDate = openingDate;
        this.accActive = accActive;
        this.transaction = new AccountTransaction();
    }

    //Connect to the db and set the var too
    protected void setAccID(int accID){this.accID= accID;}

    public void setAccNo(String accNo) {this.accNo = accNo;}

    public void setAccName(String accName) {
        this.accName = accName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHoldingBalance(double holdingBalance) {
        this.holdingBalance = holdingBalance;
    }

    public void setAvailableBalance(double availableBalance) {
        this.availableBalance = availableBalance;
    }

    public void setTotalBalance(double totalBalance) {
        this.totalBalance = totalBalance;
    }

    public void setTransferLimit(double transferLimit) {
        this.transferLimit = transferLimit;
    }

    public void setWithdrawLimit(double withdrawLimit) {
        this.withdrawLimit = withdrawLimit;
    }

    public void setAccActive(boolean accActive) {
        this.accActive = accActive;
    }

    public int getAccID() {
        return accID;
    }

    public String getAccNo() {return accNo;}

    public int getUserID() {
        return userID;
    }

    public String getAccName() {
        return accName;
    }

    public String getDescription() {
        return description;
    }

    public double getHoldingBalance() {
        return holdingBalance;
    }

    public double getAvailableBalance() {
        return availableBalance;
    }

    public double getTotalBalance() {
        return totalBalance;
    }

    public double getTransferLimit() {
        return transferLimit;
    }

    public double getWithdrawLimit() {
        return withdrawLimit;
    }

    public Date getOpeningDate() {
        return openingDate;
    }

    public boolean isAccActive() {
        return accActive;
    }
    
    //Printing Methods
    public void display(){   
        System.out.printf("| %-15s | %20s | %02f %n", accNo, accName,  totalBalance);
    }
}
