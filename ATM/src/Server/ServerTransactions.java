package Server;

import java.util.ArrayList;
import java.util.List;
import Transcation.TransactionDetails;
import java.sql.*;

public class ServerTransactions implements SQLConnect {
    public List<TransactionDetails> findUserAccounts(int accID) {
        String sql = String.format("SELECT * FROM transaction WHERE AccountID = %s", accID);
        Connection db = SQLConnect.getDBConnection();
        List<TransactionDetails> Transactions = new ArrayList<>();
        try {
            PreparedStatement statement = db.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {

                // Create the Transaction object
                TransactionDetails Transaction = new TransactionDetails(rs.getInt("TransactionID"),
                        rs.getInt("AccountID"),
                        rs.getString("TransactionNo"),
                        rs.getDate("Datetime"),
                        rs.getDate("ValueDatetime"),
                        rs.getDouble("Debit"),
                        rs.getDouble("Credit"),
                        rs.getDouble("Balance"),
                        rs.getInt("Status"),
                        rs.getString("Remarks"));

                Transactions.add(Transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLConnect.disconnectDB(db);
        }
        return Transactions;
    }
    
    //Create Transaction
    public void createNewTransaction(int accID) {
        String sql = String.format(
                "INSERT INTO transaction(AccountID,TransactionNo,Datetime,ValueDatetime,Debit,Credit,Balance,Status,Remarks) VALUES (?,?,?,?,?,?,?,?,?)");
        Connection db = SQLConnect.getDBConnection();
        List<TransactionDetails> CreateTransactions = new ArrayList<>();
        try {
            PreparedStatement statement = db.prepareStatement(sql);
            statement.setInt(1, accID);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLConnect.disconnectDB(db);
        }
    }
    
    public static void main(String[] args) {
        ServerTransactions Trans = new ServerTransactions();
        List<TransactionDetails> TransList = Trans.findUserAccounts(2318);
        
        for (int i = 0; i < TransList.size(); i++) {
            System.out.println(TransList.get(i).getTransNo());
            System.out.println(TransList.get(i).getRemarks());
        }
    }
}