package Server;

import java.util.ArrayList;
import java.util.List;

import Account.Account;
import Transcation.TransactionDetails;
import java.sql.*;

public interface ServerTransactions extends SQLConnect {
    public static List<TransactionDetails> findUserTransactions(Account account) {
        String sql = String.format("SELECT * FROM transaction WHERE AccountID = %s", account.getAccID());
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

        // Reverse list to view from latest transaction first
        for (int k = 0, j = Transactions.size() - 1; k < j; k++)
        {
            Transactions.add(k, Transactions.remove(j));
        }

        return Transactions;
    }
    /*
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
    */
}
    