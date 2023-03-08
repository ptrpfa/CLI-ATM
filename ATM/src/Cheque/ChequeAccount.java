package Cheque;

public class ChequeAccount {
    private int ID;
    private int chequeID; 
    private int accountID; 
    private int type; 

    public ChequeAccount (int ID, int chequeID, int accountID, int type) {
        this.ID = ID;
        this.chequeID = chequeID;
        this.accountID = accountID;
        this.type = type;
    }

    // Getter
    public int getID() {
        return this.ID;
    }

    public int getChequeID() {
        return this.chequeID;
    }

    public int getAccountID() {
        return this.accountID;
    }

    public int getType() {
        return this.type;
    }

    // Setters
    public void setID(int ID) {
        this.ID = ID;
    }

    public void setChequeID(int chequeID) {
        this.chequeID = chequeID;
    }

    public void setRecipientAccount(int accountID) {
        this.accountID = accountID;
    }

    public void setIssuingTransaction(int type) {
        this.type = type;
    }
}
