package Cheque;

public class ChequeTransaction {
    private int ID; 
    private int chequeID; 
    private int transactionID; 
    private int type; 

    public ChequeTransaction (int ID, int chequeID, int transactionID, int type) {
        this.ID = ID;
        this.chequeID = chequeID;
        this.transactionID = transactionID;
        this.type = type;
    }

    // Getter
    public int getID() {
        return this.ID;
    }

    public int getChequeID() {
        return this.chequeID;
    }

    public int getTransactionID() {
        return this.transactionID;
    }

    public int getIssuingTransaction() {
        return this.type;
    }

    // Setters
    public void setID(int ID) {
        this.ID = ID;
    }

    public void setChequeID(int chequeID) {
        this.chequeID = chequeID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public void setType(int type) {
        this.type = type;
    }
}
