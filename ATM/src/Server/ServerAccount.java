package Server;

//Imports
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import Account.Account;

public interface ServerAccount extends SQLConnect{

    public static List<Account> findUserAccounts(int userID){
        String sql = String.format("SELECT * FROM Account WHERE UserID = %s",userID);
        Connection db = SQLConnect.getDBConnection();
        List<Account> accounts = new ArrayList<>();
        try {
            PreparedStatement statement = db.prepareStatement(sql);
            ResultSet rs= statement.executeQuery();  
            
            while (rs.next()) {
                boolean active = rs.getBoolean("Active");
                if(active){ 
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
                                            active);

                    accounts.add(acc);
                }
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

        String insertsql = "INSERT INTO Account VALUES(NULL,?,?,?,?,0,?,?,1000,1000,?,1)";
        String getAccNoSQL = "SELECT MAX(SUBSTR(AccountNo,5, 9)) FROM Account WHERE LEFT(AccountNo, 4) = '407-';";
        Connection db = SQLConnect.getDBConnection();
        
        PreparedStatement ps;
        ResultSet rs;
        Account acc = null;
        try{            
            //Get last Acc no
            ps = db.prepareStatement(getAccNoSQL);
            rs = ps.executeQuery();
            if(rs.next()){
                if(rs.getString(1) != null){
                    accNo = Integer.parseInt(rs.getString(1))+1;
                }
            }
            String accIDString = String.format("407-%09d",accNo); 
           
            //Insert into db
            ps = db.prepareStatement(insertsql,  Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userid);
            ps.setString(2, accIDString);
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
                    acc =  new Account(accID, userid, accIDString, accName, description, 0, initialDeposit, initialDeposit, 0, 0, timestamp, true);
                    return acc;
                }
            }

        }catch (SQLException e) {
            e.printStackTrace();
        
        }catch(Exception e){
            e.printStackTrace();

        } finally {
            SQLConnect.disconnectDB(db);
        }

        return acc;
    }

    public static boolean AccountDeposit(int userID, int accID, double availableBalance, double totalBalance, double amount){

        Connection db = SQLConnect.getDBConnection();
        try{
            //Update Database
            String sql = String.format("UPDATE Account SET AvailableBalance = ?, TotalBalance = ? WHERE UserId = %d AND AccountID = %d", userID, accID);
            availableBalance += amount;
            totalBalance += amount;

            PreparedStatement updateStmt = db.prepareStatement(sql);
            updateStmt.setDouble(1, availableBalance);
            updateStmt.setDouble(2, totalBalance);
            int row = updateStmt.executeUpdate();

            if(row < 0){
                return false;
            }
            /*Create New Transaction*/
            //Get Last TransactNo
            String getTransactNoSQL = "SELECT MAX(SUBSTR(TransactionNo,1, 8)) FROM Transaction;";
            updateStmt = db.prepareStatement(getTransactNoSQL);
            ResultSet rs = updateStmt.executeQuery();
            int transactionNo = 0;
            if(rs.next()){
                if(rs.getString(1) != null){
                    transactionNo = Integer.parseInt(rs.getString(1))+1;
                }
            }
            
            //Get Current TimeStamp
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currentDate = new Date();
            String transactDate = dateFormat.format(currentDate);
            // Get Transaction Month in MM 
            SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
            String month = monthFormat.format(currentDate);
            // Get Transaction Year in YYYY '2023'
            SimpleDateFormat yearFormat = new SimpleDateFormat("YYYY");
            String year = yearFormat.format(currentDate);
            //Format TransactionNo
            String transacString = String.format("%08d-%s-%s", transactionNo, month, year);
            //Insert into Transaction Table
            sql = String.format("INSERT INTO Transaction VALUES(NULL,?,?,?,?,?,0,?,1,\"ATM-Deposit\")");
            updateStmt = db.prepareStatement(sql);
            updateStmt.setInt(1, accID);
            updateStmt.setString(2, transacString);
            updateStmt.setTimestamp(3, Timestamp.valueOf(transactDate));
            updateStmt.setTimestamp(4, Timestamp.valueOf(transactDate));
            updateStmt.setDouble(5, amount);
            updateStmt.setDouble(6, availableBalance);
            row = updateStmt.executeUpdate(); 

            if(row > 0 ){
                return true;
            }
        }catch(SQLException e){
            System.out.println("Error occurred: " + e.getMessage());
        }
        return false;
    }

    public static boolean AccountWithdrawal(int userID, int accID, double availableBalance, double totalBalance, double amount){

        Connection db = SQLConnect.getDBConnection();
        try{
            //Update Database
            String sql = String.format("UPDATE Account SET AvailableBalance = ?, TotalBalance = ? WHERE UserId = %d AND AccountID = %d", userID, accID);
            availableBalance -= amount;
            totalBalance -= amount;

            PreparedStatement updateStmt = db.prepareStatement(sql);
            updateStmt.setDouble(1, availableBalance);
            updateStmt.setDouble(2, totalBalance);
            int row = updateStmt.executeUpdate();

            if(row < 0){
                return false;
            }

            /*Create New Transaction*/
            //Get Last TransactNo
            String getTransactNoSQL = "SELECT MAX(SUBSTR(TransactionNo,1, 8)) FROM `OOP_ATM`.`Transaction` WHERE  AccountID =" + accID;
            updateStmt = db.prepareStatement(getTransactNoSQL);
            ResultSet rs = updateStmt.executeQuery();
            int transactionNo = 0;
            if(rs.next()){
                if(rs.getString(1) != null){
                    transactionNo = Integer.parseInt(rs.getString(1))+1;
                }
            }
            
            //Get Current TimeStamp
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currentDate = new Date();
            String transactDate = dateFormat.format(currentDate);
            // Get Transaction Month in MM 
            SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
            String month = monthFormat.format(currentDate);
            // Get Transaction Year in YYYY '2023'
            SimpleDateFormat yearFormat = new SimpleDateFormat("YYYY");
            String year = yearFormat.format(currentDate);
            //Format TransactionNo
            String transacString = String.format("%08d-%s-%s", transactionNo, month, year);
            //Insert into Transaction Table
            sql = String.format("INSERT INTO Transaction VALUES(NULL,?,?,?,?,0,?,?,1,\"ATM-Withdrawal\")");
            updateStmt = db.prepareStatement(sql);
            updateStmt.setInt(1, accID);
            updateStmt.setString(2, transacString);
            updateStmt.setTimestamp(3, Timestamp.valueOf(transactDate));
            updateStmt.setTimestamp(4, Timestamp.valueOf(transactDate));
            updateStmt.setDouble(5, amount);
            updateStmt.setDouble(6, availableBalance);
            row = updateStmt.executeUpdate(); 

            if(row > 0 ){
                return true;
            }

        }catch(SQLException e){
            System.out.println("Error occurred: " + e.getMessage());
        }
        return false;
    }

    public static boolean DeactivateAccount(int userID, int accID){
        Connection db = SQLConnect.getDBConnection();

        try{
            String sql = "UPDATE Account SET Active=0 WHERE UserID=? AND AccountID=?";
            PreparedStatement updateStmt = db.prepareStatement(sql);
            updateStmt.setInt(1, userID);
            updateStmt.setInt(2, accID);
            int row = updateStmt.executeUpdate(); 

            if(row > 0 ){
                return true;
            }
        }catch(SQLException e){
            System.out.println("Error occurred: " + e.getMessage());
        }
        return false;
        
    }

    public static boolean TransferFunds(Account IssuingAccount, Account ReceivingAccount, double amount){
        Connection db = SQLConnect.getDBConnection();

        try{
            String issuingSQL = "UPDATE Account SET AvailableBalance=?,TotalBalance=? WHERE UserID=? AND AccountID=?"; //Issuing
            String recievingSQL = "UPDATE Account SET AvailableBalance=?,TotalBalance=? WHERE UserID=? AND AccountID=?"; //Recieving
            
            //Prepare the issuingAccount Details
            PreparedStatement updateStmt = db.prepareStatement(issuingSQL);
            double availableBalance = IssuingAccount.getAvailableBalance();
            double totalBalance = IssuingAccount.getTotalBalance();

            updateStmt.setDouble(1, (availableBalance-amount));
            updateStmt.setDouble(2, (totalBalance - amount));
            updateStmt.setInt(3,IssuingAccount.getUserID());
            updateStmt.setInt(4, IssuingAccount.getAccID());
            int row = updateStmt.executeUpdate(); 

            if(row < 0 ){ //< 0, never update
                return false;
            }

            //Prepare the RecievingAccount Details
            updateStmt = db.prepareStatement(recievingSQL);
            availableBalance = ReceivingAccount.getAvailableBalance();
            totalBalance = ReceivingAccount.getTotalBalance();

            updateStmt.setDouble(1, (availableBalance+amount));
            updateStmt.setDouble(2, (totalBalance+amount));
            updateStmt.setInt(3, ReceivingAccount.getUserID());
            updateStmt.setInt(4, ReceivingAccount.getAccID());
            row = updateStmt.executeUpdate(); 

            if(row < 0 ){ //< 0, never update
                return false;
            }

            //Create Transaction Details, check above methods
            // Set remarks of Transaction for transfers to: \"ATM-Transfer\" to allow support of transfer limit
            ;;;
            return true;
        }catch(SQLException e){
            System.out.println("Error occurred: " + e.getMessage());
        }
        return false;
    }
    
    public static double getRemainingWithdrawLimit(int accID, double dbLimit) {

        // Initialise remaining limit
        double limit = 0;

        // SQL to get the sum of all withdrawals (to add checks in remarks too)
        String sql = "SELECT Temp.CurrentDay, Temp.Sum FROM (SELECT DATE_FORMAT(ValueDatetime, \"%d/%m/%Y\") AS 'CurrentDay', SUM(Credit) AS 'Sum' FROM `Transaction` WHERE AccountID = ? AND Remarks LIKE \"%ATM-Withdrawal%\" GROUP BY DATE_FORMAT(ValueDatetime, \"%d/%m/%Y\")) AS Temp WHERE Temp.CurrentDay = DATE_FORMAT(CURDATE() , \"%d/%m/%Y\");";
        
        // Establish a connection with the database
        Connection db = SQLConnect.getDBConnection();
        try {
            PreparedStatement statement = db.prepareStatement(sql);
            statement.setString(1, String.valueOf(accID));
            ResultSet rs= statement.executeQuery();  
            // Update current limit
            if (!rs.isBeforeFirst() ) {    
                limit = dbLimit;
            } 
            else {
                // Get sum of all withdrawals within the day
                rs.next();
                limit = dbLimit - rs.getDouble("Sum");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLConnect.disconnectDB(db);
        }
        
        // Return limit
        return limit;
        
    }

    public static boolean updateWithdrawalLimit(int accID, double newLimit){
        Connection db = SQLConnect.getDBConnection();
        try{
            String sql = "UPDATE Account SET WithdrawalLimit=? WHERE AccountID=?";
            PreparedStatement updateStmt = db.prepareStatement(sql);
            updateStmt.setDouble(1, newLimit);
            updateStmt.setInt(2, accID);
            int row = updateStmt.executeUpdate(); 
            if(row > 0 ){
                return true;
            }
        }catch(SQLException e){
            System.out.println("Error occurred: " + e.getMessage());
        }
        return false;
    }

}
