package Cheque;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Cheque {
    private int rowNum; 
    private int chequeID; 
    private int issuerAccount; 
    private int recipientAccount; 
    private int issuingTransaction; 
    private int receivingTransaction; 
    private String chequeNumber; 
    private double value; 
    private Date dateTime; 
    private int status;

    public Cheque (int rowNum, int chequeID, int issuerAccount, int recipientAccount, int issuingTransaction, int receivingTransaction,
    String chequeNumber, double value, Date dateTime, int status) {
        this.rowNum = rowNum;
        this.chequeID = chequeID;
        this.issuerAccount = issuerAccount;
        this.recipientAccount = recipientAccount;
        this.issuingTransaction = issuingTransaction;
        this.receivingTransaction = receivingTransaction;
        this.chequeNumber = chequeNumber;
        this.value = value;
        this.dateTime = dateTime;
        this.status = status;
    }

    // Getter
    public int getChequeID() {
        return this.chequeID;
    }

    public int getissuerAccount() {
        return this.issuerAccount;
    }

    public int getRecipientAccount() {
        return this.recipientAccount;
    }

    public int getIssuingTransaction() {
        return this.issuingTransaction;
    }

    public int getReceivingTransaction() {
        return this.receivingTransaction;
    }

    public String getChequeNumber() {
        return this.chequeNumber;
    }

    public double getValue() {
        return this.value;
    }

    public Date getDateTime() {
        return this.dateTime;
    }

    public int getStatus() {
        return this.status;
    }

    // Setters
    public void setChequeID(int chequeID) {
        this.chequeID = chequeID;
    }

    public void setissuerAccount(int issuerAccount) {
        this.issuerAccount = issuerAccount;
    }

    public void setRecipientAccount(int recipientAccount) {
        this.recipientAccount = recipientAccount;
    }

    public void setIssuingTransaction(int issuingTransaction) {
        this.issuingTransaction = issuingTransaction;
    }

    public void setReceivingTransaction(int receivingTransaction) {
        this.receivingTransaction = receivingTransaction;
    }

    public void setChequeNumber(String chequeNumber) {
        this.chequeNumber = chequeNumber;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setDateTime() {
        Date date = new Date();
        this.dateTime = date;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static void encash() {

    }

    public static void clearance() {
        
    }

    public String[] PrintValues(){ 
        String statusString = "";

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy"); 
        String date = formatter.format(this.dateTime);  

        // Transaction status formatting
        if(status < 0) {
            statusString = "@|196 Failed |@";
        }
        else if(status == 1) {
            statusString = "@|118 Success |@";
        }
        else{
            statusString = "@|220 Pending |@";
        }

        String[] values = { Integer.toString(rowNum), Integer.toString(chequeID), Integer.toString(issuerAccount),
                            Integer.toString(recipientAccount), Integer.toString(issuingTransaction), Integer.toString(receivingTransaction), 
                            chequeNumber, Double.toString(value), date, statusString};
        return values;
    }

    public static String[] PrintHeaders(){
        String[] headers = {"No", "Cheque ID", "Issuer Account", "Receiver Account", "Issuer Transaction", "Receiver Transaction", "Cheque Number", "Value", "Date", "Status"};
        return headers;
    }
}
