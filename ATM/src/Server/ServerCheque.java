package Server;

import java.util.ArrayList;
import java.util.List;

import Cheque.Cheque;
import Cheque.ChequeAccount;
import Account.Account;
import Cheque.ChequeTransaction;

import java.sql.*;

public interface ServerCheque extends SQLConnect {
    // Get transactions for specific account
    public static List<Cheque> findUserCheques(ChequeAccount chequeAccount, ChequeTransaction chequeTransaction) {

        ChequeAccount chequeAccount = new ChequeAccount (int ID, int chequeID, accountID.getAccID(), int type) {
            this.ID = ID;
            this.chequeID = chequeID;
            this.accountID = accountID;
            this.type = type;
        }


        String sql = String.format("SELECT * FROM Cheque WHERE AccountID = %s", account.getAccID());
        Connection db = SQLConnect.getDBConnection();
        List<Cheque> Cheques = new ArrayList<>();
        try {
            PreparedStatement statement = db.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {

                // Create the Cheque object
                Cheque Cheque = new Cheque(rs.getInt("ChequeID"),
                        rs.getInt("IssuerAccount"),
                        rs.getInt("RecipientAccount"),
                        rs.getInt("IssuingTransaction"),
                        rs.getInt("ReceivingTransaction"),
                        rs.getString("ChequeNo"),
                        rs.getDouble("Value"),
                        rs.getDate("Date"),
                        rs.getInt("Status"));

                Cheques.add(Cheque);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLConnect.disconnectDB(db);
        }

        // Reverse list to view from latest transaction first
        for (int k = 0, j = Cheques.size() - 1; k < j; k++)
        {
            Cheques.add(k, Cheques.remove(j));
        }

        return Cheques;
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
    