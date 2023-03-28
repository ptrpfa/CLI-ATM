package Server;

import java.util.ArrayList;
import java.util.List;

import Account.Account;
import Transaction.TransactionDetails;

import java.sql.*;

public interface ServerTransactions extends SQLConnect {
    // Get transactions for specific account
    public static List<TransactionDetails> findUserTransactions(Account account) {
        Connection db = SQLConnect.getDBConnection();
        
        String sql = String.format("SELECT * FROM Transaction WHERE AccountID = %s", account.getAccID());
        String sql2 =   """
                        SELECT (SELECT COUNT(*) FROM Transaction WHERE AccountID = %s) - ROW_NUMBER()
                        OVER (ORDER BY Transaction.TransactionNo) + 1 as ReverseRowNumber, Transaction.*
                        FROM Transaction
                        WHERE AccountID = %s
                        ORDER BY Transaction.TransactionNo
                        """.formatted(account.getAccID(), account.getAccID()); 

        List<TransactionDetails> Transactions = new ArrayList<>();
        try {
            PreparedStatement statement = db.prepareStatement(sql);
            PreparedStatement statement2 = db.prepareStatement(sql2);
            ResultSet rs = statement.executeQuery();
            ResultSet rs2 = statement2.executeQuery();
            
            while (rs.next() && rs2.next()) {
                // Create the Transaction object
                TransactionDetails Transaction = new TransactionDetails(rs2.getInt("ReverseRowNumber"),
                                                                        rs.getInt("TransactionID"),
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

    //Create Transaction
    public static void createNewTransaction(TransactionDetails transaction) {
        String sql = String.format("INSERT INTO Transaction (AccountID, TransactionNo, Datetime, ValueDatetime, Debit, Credit, Balance, Status, Remarks) VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, ?)");
        String countQuery = "SELECT count(*) FROM Transaction";
        
        Connection db = SQLConnect.getDBConnection();
        try {
            // Template for 2 statements. 1 for Insertion, 1 for Reading Queries
            PreparedStatement statement = db.prepareStatement(sql);
            PreparedStatement countStatement = db.prepareStatement(countQuery);
            ResultSet countRS = countStatement.executeQuery();

            countRS.next();
            int transactionNo = countRS.getInt(1);

            // Fill up update statements with latest particulars for "Transaction" DB
            statement.setInt(1, transaction.getAccID());
            statement.setString(2, Integer.toString(transactionNo));
            statement.setDate(3, transaction.getValueDatetime());
            statement.setDouble(4, transaction.getDebit());
            statement.setDouble(5, transaction.getCredit());
            statement.setDouble(6, transaction.getBalance());
            statement.setInt(7, transaction.getStatus());
            statement.setString(8, transaction.getRemarks());

            // Insert into latest row of DB
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new user was inserted successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLConnect.disconnectDB(db);
        }
    }

//     public static void main(String[] args) {
//         /* Pretend this is work class where user does a transaction */
//         // Create new transaction, store in transaction object
//         // Send transaction object to method that adds to DB
//         TransactionDetails transaction;
//         Scanner input = new Scanner(System.in);

//         System.out.println("Enter amount to deposit (debit): ");
//         double amount = input.nextDouble();

//         createNewTransaction(transaction);
//     }
}
    