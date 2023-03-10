package Server;

import java.util.ArrayList;
import java.util.List;

import Cheque.Cheque;
import Cheque.ChequeAccount;
import Account.Account;
import Cheque.ChequeTransaction;

import java.sql.*;

public interface ServerCheque extends SQLConnect {

    // Get list of cheque IDs tied to an account
    public static List<ChequeAccount> getChequeIDs(int accountID) {
        List<ChequeAccount> ChequeIDs = new ArrayList<>();

        String sql = String.format("SELECT * FROM ChequeAccount WHERE AccountID = %s", accountID);
        Connection db = SQLConnect.getDBConnection();

        try {
            PreparedStatement statement = db.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            rs.next();

            while (rs.next()) {
                // Create ChequeAccount object for every cheque id
                ChequeAccount chequeID = new ChequeAccount(rs.getInt("ID"),
                                                           rs.getInt("ChequeID"),
                                                           rs.getInt("accountID"),
                                                           rs.getInt("Type"));
                ChequeIDs.add(chequeID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLConnect.disconnectDB(db);
        }
        return ChequeIDs;
    }

    // Get list of cheque transactions tied to each cheque ID
    public static List<ChequeTransaction> getChequeTransactions(List<ChequeAccount> chequeAccounts) {
        List<ChequeTransaction> chequeTransactions = new ArrayList<>();

        Connection db = SQLConnect.getDBConnection();
        
        try {
            for (int i = 0; i < chequeAccounts.size(); i++) {
                String sql = String.format("SELECT * FROM ChequeTransaction WHERE ChequeID = %s", chequeAccounts.get(i).getChequeID());
                PreparedStatement statement = db.prepareStatement(sql);
                ResultSet rs = statement.executeQuery();

                rs.next();

                // Create ChequeTransaction object for every cheque id
                ChequeTransaction chequeTransaction = new ChequeTransaction(rs.getInt("ID"),
                                                                            rs.getInt("ChequeID"),
                                                                            rs.getInt("TransactionID"),
                                                                            rs.getInt("Type"));
                chequeTransactions.add(chequeTransaction);
                }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLConnect.disconnectDB(db);
        }
        return chequeTransactions;
    }

    // Get list of cheques for an account
    public static List<Cheque> findUserCheques(Account account) {
        // Get ChequeID list based on accountID
        List<ChequeAccount> chequeIDs = ServerCheque.getChequeIDs(account.getAccID());
        
        // Get ChequeTransaction list based on ChequeIDs list
        List<ChequeTransaction> chequeTransactions = ServerCheque.getChequeTransactions(chequeIDs);

        List<Cheque> cheques = new ArrayList<>();

        Connection db = SQLConnect.getDBConnection();
        try {
            for (int i = 0; i < chequeIDs.size(); i++) {
                String sql = String.format("SELECT * FROM Cheque WHERE ChequeID = %s", chequeIDs.get(i).getChequeID());
                PreparedStatement statement = db.prepareStatement(sql);
                ResultSet rs = statement.executeQuery();

                rs.next();

                // Create the Cheque object
                Cheque cheque = new Cheque(rs.getInt("ChequeID"),   
                                           rs.getInt("IssuerAccount"),
                                           rs.getInt("RecipientAccount"),
                                           rs.getInt("IssuingTransaction"),
                                           rs.getInt("ReceivingTransaction"),
                                           rs.getString("ChequeNo"),
                                           rs.getDouble("Value"),
                                           rs.getDate("Date"),
                                           rs.getInt("Status"));

                cheques.add(cheque);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLConnect.disconnectDB(db);
        }

        // Reverse list to view from latest cheques first
        for (int k = 0, j = cheques.size() - 1; k < j; k++)
        {
            cheques.add(k, cheques.remove(j));
        }

        return cheques;
    }
}







//     //Create Cheque
//     public static void createNewTransaction(Cheque Cheque) {
//         String sql = String.format("INSERT INTO cheque (Cheque, IssuerAccount, RecipientAccount, ValueDatetime, Debit, Credit, Balance, Status, Remarks) VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, ?)");
//         String countQuery = "SELECT count(*) FROM Transaction";
        
//         Connection db = SQLConnect.getDBConnection();
//         try {
//             // Template for 2 statements. 1 for Insertion, 1 for Reading Queries
//             PreparedStatement statement = db.prepareStatement(sql);
//             PreparedStatement countStatement = db.prepareStatement(countQuery);
//             ResultSet countRS = countStatement.executeQuery();

//             countRS.next();
//             int transactionNo = countRS.getInt(1);

//             // Fill up update statements with latest particulars for "Transaction" DB
//             statement.setInt(1, transaction.getAccID());
//             statement.setString(2, Integer.toString(transactionNo));
//             statement.setDate(3, transaction.getValueDatetime());
//             statement.setDouble(4, transaction.getDebit());
//             statement.setDouble(5, transaction.getCredit());
//             statement.setDouble(6, transaction.getBalance());
//             statement.setInt(7, transaction.getStatus());
//             statement.setString(8, transaction.getRemarks());

//             // Insert into latest row of DB
//             int rowsInserted = statement.executeUpdate();
//             if (rowsInserted > 0) {
//                 System.out.println("A new user was inserted successfully!");
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//         } finally {
//             SQLConnect.disconnectDB(db);
//         }
//     }

// //     public static void main(String[] args) {
// //         /* Pretend this is work class where user does a transaction */
// //         // Create new transaction, store in transaction object
// //         // Send transaction object to method that adds to DB
// //         TransactionDetails transaction;
// //         Scanner input = new Scanner(System.in);

// //         System.out.println("Enter amount to deposit (debit): ");
// //         double amount = input.nextDouble();

// //         createNewTransaction(transaction);
// //     }
// // }
    