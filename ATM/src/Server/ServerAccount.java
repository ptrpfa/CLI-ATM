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

        String insertsql = "INSERT INTO Account VALUES(NULL,?,?,?,?,0,?,?,1000,1000,?,1)";
        String getAccNoSQL = "SELECT MAX(SUBSTR(AccountNo,5, 9)) FROM Account WHERE LEFT(AccountNo, 4) = '407-' AND UserID =" + userid; //set userid var
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
            updateStmt.executeUpdate();

            /*Create New Transaction*/
            //Get Last TransactNo
            String getTransactNoSQL = "SELECT MAX(SUBSTR(TransactionNo,1, 8)) FROM `OOP_ATM`.`Transaction` WHERE  AccountID = 1";
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
            sql = String.format("INSERT INTO Transaction VALUES(NULL,?,?,?,?,?,0,?,1,NULL)");
            updateStmt = db.prepareStatement(sql);
            updateStmt.setInt(1, accID);
            updateStmt.setString(2, transacString);
            updateStmt.setTimestamp(3, Timestamp.valueOf(transactDate));
            updateStmt.setTimestamp(4, Timestamp.valueOf(transactDate));
            updateStmt.setDouble(5, amount);
            updateStmt.setDouble(6, availableBalance);
            rs= updateStmt.executeQuery(); 
            
            if(rs.next()){
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
            updateStmt.executeUpdate();

            return true;
        }catch(SQLException e){
            System.out.println("Error occurred: " + e.getMessage());
        }
        return false;
    }
}
