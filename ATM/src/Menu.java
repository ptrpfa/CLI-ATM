import java.util.InputMismatchException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import Account.Account;
import Account.AccountService;
import Account.AccountTransaction.TransactionError;
import User.User;
import Server.ServerAccount;
import Server.ServerTransactions;
import Transcation.TransactionDetails;

class Menu implements ServerAccount, ServerTransactions {
    private List<Account> accounts;
    private List<TransactionDetails> transactions;
    private User user;
    private Scanner scanner;

    Menu(User user){
        List<Account> accounts = ServerAccount.findUserAccounts(user.getUserID());
        this.accounts = accounts;
        this.user = user;
    }

    public List<Account> getAccounts(){
        return accounts;
    }

    public void run(){
        String[] options = {
            "1- Create New Account",
            "2- Deposit",
            "3- Withdraw",
            "4- Transfer Funds",
            "5- View Transcations",
            "6- Exit"
        };
        scanner = new Scanner(System.in);
        // Print Available Accounts
        System.out.println("Active Accounts..");
        printTable(Account.PrintHeaders(), accounts);

        int option = -1, accountOption = -1;
        do {
            printMenu(options);
            
            try {
                System.out.println("What would would want to do? (Key in the Command Number)");
                System.out.print("> ");
                option = scanner.nextInt();
                System.out.print("\n");
                scanner.nextLine();
                switch (option){
                    case 1:
                        CreateAccount();
                        break;

                    case 2:
                        accountOption = chooseAccount();
                        if(accountOption != -1){
                            Deposit(accounts.get(accountOption-1));
                        }
                        break;
                        
                    case 3: 
                        accountOption = chooseAccount();
                        if(accountOption != -1){
                            Withdraw(accounts.get(accountOption-1));
                        }
                        break;
                    // case 5:
                    //     do {
                    //         try {
                    //             System.out.println("Please choose Account to transact from...");
                    //             printTable(Account.PrintHeaders(), accounts);
                    //             System.out.print("\n> ");
                    //             accountOption = scanner.nextInt();
                    //             checkOption(accountOption, accounts.size());
                    //             transactions = ServerTransactions.findUserTransactions(accounts.get(accountOption - 1));
                    //             printTable(TransactionDetails.PrintHeaders(), transactions);
                    //             System.out.print("\n");
                    //         } catch (WrongNumberException e) {
                    //             System.out.println(e.getMessage());
                    //             accountOption = -1;
                    //             scanner.nextLine();
                    //         } catch (InputMismatchException e) {
                    //             System.out.println("Wrong Input. Try again..\n");
                    //             accountOption = -1;
                    //             scanner.nextLine();
                    //         }
                    //     } while (accountOption == -1);
                    //     break;
                    case 6:
                        //Clean up
                        scanner.close();
                        break;
                    default:
                        System.out.println("Please enter an integer value between 1 and " + options.length + "\n");
                        break;
               }
           }catch (InputMismatchException ex){
               System.out.println("Please enter an integer value between 1 and " + options.length + "\n");
               option = -1;
           }catch(Exception e){
                e.printStackTrace();
                option = -1;
           }
       }while(option != 6);

    }

    // Option 1
    private void CreateAccount() throws InterruptedException {

        AccountService service = new AccountService();
        int userid = user.getUserID();
        String accName = null, accDescString = null;
        double amount = 0;
        boolean isValidInput = false;

        try{
            accName = promptForInput("Key in the Account Name ", String.class);
            accDescString = promptForInput("Account Descriptions", String.class);
            while(!isValidInput){
                try{
                    amount = promptForInput("Initial Deposit", Double.class);
                    //Check amount    
                    validateAmount(amount);
                    isValidInput = true;
                }catch (WrongNumberException e) {
                    System.out.println(e.getMessage());
                }
            }
        }catch(GoBackException e){
            return;
        }

        // Send to server
        System.out.println("Creating Account " + accName + "...\n");
        Account acc = service.CreateAccount(userid, accName, accDescString, amount);
        Thread.sleep(1000);
        if (acc != null) {
            System.out.println("Account is Created\n");
            this.accounts.add(acc);
            printTable(Account.PrintHeaders(), accounts);
        } else {
            System.out.println("Error in System..\n");
        }
        return;
    }

    //Option 2
    public void Deposit(Account acc) throws InterruptedException {
        double amount = 0;
        boolean isValidInput = false;

        while(!isValidInput){
            try{
                amount = promptForInput("Enter Deposit Amount", Double.class);
                validateAmount(amount);
                System.out.println("Depositing...\n");
                acc.deposit(amount);
                Thread.sleep(1000);
                System.out.println("Depositied amount $" + amount + " in " + acc.getAccName() );
                printTable(Account.PrintHeaders(), accounts);

                isValidInput = true;
            }catch(WrongNumberException e){
                //Throw by validateAmount()
                System.out.println(e.getMessage());
            }catch(TransactionError e){
                //Throw by acc.deposit()
                System.out.println(e.getMessage());
            }catch(GoBackException e){
                return;
            }
    
        }
    }

    //Option 3
    public void Withdraw(Account acc) throws InterruptedException{
        double amount = 0;
        boolean isValidInput = false;

        while(!isValidInput){
            try{
                amount = promptForInput("Enter Deposit Amount", Double.class);
                validateAmount(amount);
                //Send to withdraw
                System.out.println("Withdrawing...\n");
                acc.withdraw(amount);   

                Thread.sleep(1000);
                System.out.println("Withdrew amount $" + amount + " in " + acc.getAccName() );
                printTable(Account.PrintHeaders(), accounts);

                isValidInput = true;
            }catch(WrongNumberException e){
                //Throw by validateAmount()
                System.out.println(e.getMessage());
            }catch(TransactionError e){
                //Throw by acc.deposit()
                System.out.println(e.getMessage());
            }catch(GoBackException e){
                return;
            }
       
        }
    }

    // public boolean TransferFunds(Account acc) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'TransferFunds'");
    // }

    // public void ViewTranscation(Account acc) {
    // // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method
    // 'ViewTranscation'");
    // }
    
    /*
     * Template for Choosing Account
    */
    private int chooseAccount(){
        int accountOption = -1;
        do {
            try {
                printTable(Account.PrintHeaders(), accounts);
                accountOption = promptForInput("Please choose Account to transact from...", Integer.class); 
                checkOption(accountOption, accounts.size());
            }catch(WrongNumberException e){
                System.out.println(e.getMessage());
                accountOption = -1;
            } catch (GoBackException e) {
                return -1;
            }
        }while(accountOption == -1);
        return accountOption;
    }

    /*
    * Template for printing Option Commands
    */
    private void printMenu(String[] options) {
        for (String option : options) {
            System.out.println(option);
        }
    }
    
    /*
     * Tempate for asking input
    */
    private <T> T promptForInput(String prompt, Class<T> type) {
        
        System.out.println(prompt);
        System.out.println("Press '4' to go back to the previous menu.");
    
        T input = null;
        do{
            System.out.print("> ");
            String userInput = scanner.nextLine();
            if (userInput.equals("4")) {
                System.out.print("\n");
                throw new GoBackException("Return Control");
            } else if (!userInput.isEmpty()) {
                try {
                    if (type == String.class) {
                        input = type.cast(userInput);
                    } else if (type == Double.class || type == double.class) {
                        input = type.cast(Double.parseDouble(userInput));
                    } else if (type == Integer.class){
                        input = type.cast(Integer.parseInt(userInput));
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Wrong Input. Try again..");
                }
            }
        }while (input == null);
        System.out.print("\n");
        return input;
    }

    /*
     * Template for Printing info
    */
    private static <T> void printTable(String[] headers, List<T> list) {
        // Get the values
        String[][] values = new String[list.size()][headers.length];
        for (int i = 0; i < list.size(); i++) {
            T item = list.get(i);
            if (item instanceof Account) {
                values[i] = ((Account) item).PrintValues();
            }
            
            if (item instanceof TransactionDetails) {
                values[i] = ((TransactionDetails) item).PrintValues();
            }
        }

        String line;
        // Determine the maximum width of each column
        int[] colWidths = new int[headers.length];
        int attrLength = 0;
        for (int i = 0; i < headers.length; i++) {
            colWidths[i] = headers[i].length();
            for (int j = 0; j < values.length; j++) {
                attrLength = values[j][i].length();
                colWidths[i] = Math.max(colWidths[i], attrLength);
            }
        }

        // Print the table header
        line = String.format("| %-3s", "No");
        System.out.print(line);
        for (int i = 0; i < headers.length; i++) {
            line = String.format("| %-" + colWidths[i] + "s ", headers[i]);
            System.out.print(line);
        }
        System.out.print("|\n");

        // Print the horizontal line below the header
        System.out.print("+----");
        for (int i = 0; i < headers.length; i++) {
            line = String.format("+-%-" + colWidths[i] + "s-", "-").replace(' ', '-');
            System.out.print(line);
        }
        System.out.print("+\n");

        // Print the table values
        for (int i = 0; i < values.length; i++) {
            line = String.format("| %-2d ", i + 1);
            System.out.print(line);
            for (int j = 0; j < headers.length; j++) {
                line = String.format("| %-" + colWidths[j] + "s ", values[i][j]);
                System.out.print(line);
            }
            System.out.print("|\n");
        }
        System.out.print("\n");
    }

    /*
    * User Defined Exception
    */ 
    private static class WrongNumberException extends Exception {
        public WrongNumberException(String errorMessage) {
            super(errorMessage);
        }
    }
    
    public static void validateAmount(double amount) throws WrongNumberException {
        
        if(amount == 0){
            return;
        }else if (amount < 0) {
            throw new WrongNumberException("Amount cannot be negative!\n");
        }
        // Check if it has at most 2 decimal
        String input = Double.toString(amount);
        String[] parts = input.split("\\.");
        if (parts.length == 2 && parts[1].length() > 2) {
            throw new WrongNumberException("Amount entered cannot have more than 2 decimal places.\n");
        }
    }

    public static void checkOption(int number, int max) throws WrongNumberException {
        if (number > max) {
            throw new WrongNumberException("Wrong Choice lah bang\n");
        }
    }

    private static class GoBackException extends RuntimeException  {
        public GoBackException(String errorMessage) {
            super(errorMessage);
        }
    }
    
    /*End of Exception */


}


