package Server;

//Imports
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import Account.Account;

public interface ServerAccount extends SQLConnect{

    public static List<Account> findUserAccounts(int userID){
        String sql = String.format("SELECT * FROM account WHERE UserID = %s",userID);
        Connection db = SQLConnect.getDBConnection();
        List<Account> accounts = new ArrayList<>();
        try {
            PreparedStatement statement = db.prepareStatement(sql);
            ResultSet rs= statement.executeQuery();  
            
            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("OpeningDate");
                java.util.Date datetime = new java.util.Date(timestamp.getTime());
                //Create the Acc object
                Account acc = new Account(rs.getInt("AccountID"),
                                          rs.getInt("UserID"),
                                          rs.getString("AccountNo"),
                                          rs.getString("Name"),
                                          rs.getString("Description"),
                                          rs.getDouble("HoldingBalance"),
                                          rs.getDouble("AvailableBalance"),
                                          rs.getDouble("TotalBalance"),
                                          rs.getDouble("TransferLimit"),
                                          rs.getDouble("WithdrawalLimit"),
                                          datetime,
                                          rs.getBoolean("Active"));

                accounts.add(acc);
            } 
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLConnect.disconnectDB(db);
        }
        return accounts;
    }

    public static Account NewAccount(int userid, String accName, String description, double initialDeposit){
        java.util.Date datetime = new Date();
        Timestamp timestamp = new Timestamp(datetime.getTime());
        int accNo = 0;

        String insertsql = "INSERT INTO account VALUES(NULL,?,?,?,?,0,?,?,NULL,NULL,?,1)";
        String getAccNoSQL = "SELECT MAX(RIGHT(AccountNo, 9)) FROM Account WHERE LEFT(AccountNo, 4) = '407-' AND UserID =" + userid; //set userid var
        
        Connection db = SQLConnect.getDBConnection();
        
        PreparedStatement ps;
        ResultSet rs;
        Account acc = null;
        try{
            //Get last Acc no
            ps = db.prepareStatement(getAccNoSQL);
            rs = ps.executeQuery();
            if(rs.next()){
                accNo = rs.getInt(1)+1;
            }

            //Insert into db
            ps = db.prepareStatement(insertsql,  Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userid);
            ps.setString(2, "407-" + accNo);
            ps.setString(3, accName);
            ps.setString(4, description);
            ps.setDouble(5, initialDeposit);
            ps.setDouble(6, initialDeposit);
            ps.setTimestamp(7, timestamp);
            int row = ps.executeUpdate();
            if(row > 0){
                rs = ps.getGeneratedKeys();
                if(rs.next()){
                    int accID = rs.getInt(1);
                    acc =  new Account(accID, userid, "407-"+ accNo, accName, description, 0, initialDeposit, initialDeposit, 0, 0, timestamp, true);
                    return acc;
                }
            }

        }catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLConnect.disconnectDB(db);
        }
        return null;
    }
}
