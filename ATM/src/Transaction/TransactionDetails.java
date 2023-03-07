package Transaction;
import java.sql.Date;

public class TransactionDetails {
    private int transID;
    private int accID;
    private String transno;
    private Date Datetime;
    private Date ValueDatetime;
    private double debit;
    private double credit;
    private double balance;
    private int status;
    private String Remarks;

    //is this only for backend to pull the data and set the data?
    public TransactionDetails(int transID, int accID, String transno, Date datetime, Date valueDatetime, double debit, double credit, double balance, int status, String remarks) {
        this.transID = transID;
        this.accID = accID;
        this.transno = transno;
        this.Datetime = datetime;
        this.ValueDatetime = valueDatetime;
        this.debit = debit;
        this.credit = credit;
        this.balance = balance;
        this.status = status;
        this.Remarks = remarks;
    }
    
    //Pull data for this accID
    public TransactionDetails(int accID) {
        this.accID = accID;
    }

    //Setters (Omit allow setting accID and transID) <- only backend can set
    public void setDatetime(Date datetime) {
        Datetime = datetime;
    }

    public void setValueDatetime(Date valueDatetime) {
        ValueDatetime = valueDatetime;
    }

    public void setDebit(double debit) {
        this.debit = debit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setRemarks(String remarks) {
        this.Remarks = remarks;
    }
    public void setTransno(String transno) {
        this.transno = transno;
    }

    //Getter
    public int getTransID() {
        return transID;
    }

    public int getAccID() {
        return accID;
    }
    public String getTransNo() {
        return transno;
    }

    public Date getDatetime() {
        return Datetime;
    }

    public Date getValueDatetime() {
        return ValueDatetime;
    }

    public double getDebit() {
        return debit;
    }

    public double getCredit() {
        return credit;
    }

    public double getBalance() {
        return balance;
    }

    public int getStatus() {
        return status;
    }

    public String getRemarks() {
        return Remarks;
    }

    // Methods
    private boolean UpdateStatus(){
        return true;
    }

    public void updateBalance() {
        this.balance = this.balance + this.debit;
    }

    //Printing Methods
    public String[] PrintValues(){   
        String datetime = Datetime.toString();
        String[] values = {transno, datetime, Double.toString(debit),Double.toString(credit),Double.toString(balance),Integer.toString(status),Remarks};
        //System.out.printf("| %-15s | %20s | %02f %n", accNo, accName,  totalBalance);
        return values;
    }

    public static String[] PrintHeaders(){
        String[] headers = {"Transaction Number", "Date", "Debit", "Credit", "Balance", "Status", "Remarks"};
        return headers;
    }
}