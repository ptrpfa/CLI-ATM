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
        Connection db = SQLConnect.getDBConnection();

        List<ChequeAccount> chequeIDs = ServerCheque.getChequeIDs(account.getAccID());

        List<Cheque> cheques = new ArrayList<>();
        try {
            String sql2 =   """
                            SELECT (SELECT COUNT(*) FROM ChequeAccount WHERE AccountID = %s) - ROW_NUMBER()
                            OVER (ORDER BY ChequeAccount.ChequeID) + 1 as ReverseRowNum, ChequeAccount.*
                            FROM ChequeAccount
                            WHERE AccountID = %s
                            ORDER BY ChequeAccount.ChequeID
                            """.formatted(account.getAccID(), account.getAccID()); 
            PreparedStatement statement2 = db.prepareStatement(sql2);
            ResultSet rs2 = statement2.executeQuery();

            for (int i = 0; i < chequeIDs.size(); i++) {
                String sql = String.format("SELECT * FROM Cheque WHERE ChequeID = %s", chequeIDs.get(i).getChequeID());

                PreparedStatement statement = db.prepareStatement(sql);
                ResultSet rs = statement.executeQuery();

                while (rs.next() && rs2.next()) {
                    // Create the Cheque object
                    Cheque cheque = new Cheque( rs2.getInt("ReverseRowNum"),
                                                rs.getInt("ChequeID"),   
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

    // public static void main(String[] args) {
    //     /* Pretend this is work class where user does a transaction */
    //     List<Cheque> cheques = ServerCheque.findUserCheques(3);

    //     for(int i = 0; i < cheques.size(); i++) {
    //         System.out.println(cheques.get(i).getChequeID());
    //     }
    // }
}
