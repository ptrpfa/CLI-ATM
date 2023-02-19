package Server;

//Imports
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Account.Account;

public class ServerAccount implements SQLConnect{
    public List<Account> findUserAccounts(int userID){
        String sql = String.format("SELECT * FROM account WHERE UserID = %s",userID);
        Connection db = SQLConnect.super.getDBConnection();
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
            SQLConnect.super.disconnectDB(db);
        }
        return accounts;
    }
    public void NewAccount(Account acc){
        java.util.Date datetime = acc.getOpeningDate();
        Timestamp timestamp = new Timestamp(datetime.getTime());
        
        String sql = String.format("INSERT INTO account VALUES(NULL, %d,?,?,?,%f,%f,%f,%f,%f,?,%b)",
                                            acc.getUserID(),acc.getHoldingBalance(),acc.getAvailableBalance(),
                                            acc.getTotalBalance(), acc.getTransferLimit(), acc.getWithdrawLimit(), acc.isAccActive());
        Connection db = SQLConnect.super.getDBConnection();
        try{
            PreparedStatement ps = db.prepareStatement(sql);
            ps.setString(1,acc.getAccNo() );
            ps.setString(2,acc.getAccName() );
            ps.setString(3,acc.getDescription());
            ps.setTimestamp(4, timestamp); 
            int row = ps.executeUpdate();

            // rows affected
            System.out.println(row);
        }catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLConnect.super.disconnectDB(db);
        }
        //return true;
    }
}
