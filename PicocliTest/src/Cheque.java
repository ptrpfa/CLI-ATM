import java.util.Date;

public class Cheque {
    private int chequeID;
    private int issuerAcc;
    private int recipientAcc;
    private int recivingTransc;
    private double value;
    private Date chequeDate;
    private boolean status;

    //Only Server?
    public Cheque(int chequeID, int issuerAcc, int recipientAcc, int recivingTransc, double value, Date chequeDate, boolean status) {
        this.chequeID = chequeID;
        this.issuerAcc = issuerAcc;
        this.recipientAcc = recipientAcc;
        this.recivingTransc = recivingTransc;
        this.value = value;
        this.chequeDate = chequeDate;
        this.status = status;
    }

    //Methods
    public boolean encash(){
        return true;
    }
    public boolean clearance(){
        return true;
    }

    public int getChequeID() {
        return chequeID;
    }

    public void setChequeID(int chequeID) {
        this.chequeID = chequeID;
    }

    public int getIssuerAcc() {
        return issuerAcc;
    }

    public void setIssuerAcc(int issuerAcc) {
        this.issuerAcc = issuerAcc;
    }

    public int getRecipientAcc() {
        return recipientAcc;
    }

    public void setRecipientAcc(int recipientAcc) {
        this.recipientAcc = recipientAcc;
    }

    public int getRecivingTransc() {
        return recivingTransc;
    }

    public void setRecivingTransc(int recivingTransc) {
        this.recivingTransc = recivingTransc;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Date getChequeDate() {
        return chequeDate;
    }

    public void setChequeDate(Date chequeDate) {
        this.chequeDate = chequeDate;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
