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
                                          rs.getDate("OpeningDate"),
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
}
