package Server;

//Imports
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import Account.Account;

public interface ServerAccount extends SQLConnect{

    public static List<Account> findUserAccounts(int userID){
        String sql = String.format("SELECT * FROM account WHERE UserID = %s",userID);
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

        String insertsql = "INSERT INTO account VALUES(NULL,?,?,?,?,0,?,?,0,0,?,1)";
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
            String accIDString = String.format("607-%09d",accNo); 
           
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
    public static boolean AccountDeposit(int userID, int accID, double amount){

        Connection db = SQLConnect.getDBConnection();
        try{
            // Get the current value from the table
            Statement stmt = db.createStatement();
            String sql = String.format("SELECT AvailableBalance, TotalBalance FROM Account WHERE UserId = %d AND AccountID = %d", userID, accID);
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            double availableBalance = rs.getDouble("AvailableBalance");
            double totalBalance = rs.getDouble("TotalBalance");
            
            //Update Database
            sql = String.format("UPDATE Account SET AvailableBalance = ?, TotalBalance = ? WHERE UserId = %d AND AccountID = %d", userID, accID);
            availableBalance += amount;
            totalBalance += amount;

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
