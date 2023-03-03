import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import Account.Account;
import Account.AccountService;
import Account.AccountTransaction.TransactionError;
import User.User;
import Server.ServerAccount;

class Menu implements ServerAccount, ServerTransactions {
    private List<Account> accounts;
    private List<TransactionDetails> transactions;
    private User user;
    private Scanner scanner;

    Menu(User user){
        List<Account> accounts = ServerAccount.findUserAccounts(user.getUserID());
        this.accounts = accounts;
        this.user = user;
        scanner = new Scanner(System.in);
    }

    public List<Account> getAccounts(){
        return accounts;
    }

    public List<TransactionDetails> getTransactions() {
        return transactions;
    }

    public void run() {
        String[] options = {
            "1- Create New Account",
            "2- Deposit",
            "3- Withdraw",
            "4- Transfer Funds",
            "5- View Transcations",
            "6- Exit"
        };
        // Print Available Accounts
        System.out.println("\nActive Accounts..");
        printTable(Account.PrintHeaders(), accounts);

        System.out.println("\n");
        int option = -1;
        do {
            int accountOption = -1;
            printMenu(options);
            System.out.println("What would would want to do? (Key in the Command Number)");
            System.out.print("> "); 
            try {
                option = scanner.nextInt();
                System.out.println("\n");
                switch (option) {
                    case 1:
                        scanner.nextLine();
                        CreateAccount();
                        break;
                    case 2:
                        do {
                            try {
                                System.out.println("Please choose Account to transact from...");
                                printTable(Account.PrintHeaders(), accounts);
                                System.out.print("\n> ");
                                accountOption = scanner.nextInt();
                                checkOption(accountOption, accounts.size());
                            }catch(WrongNumberException e){
                                System.out.println(e.getMessage());
                                accountOption = -1;
                                scanner.nextLine();
                            } catch (InputMismatchException e) {
                                System.out.println("Wrong Input. Try again..\n");
                                accountOption = -1;
                                scanner.nextLine();
                            }
                        }while(accountOption == -1);
                        Deposit(accounts.get(accountOption-1));
                        break;
                    case 3: 
                        //Withdraw(); 
                        break;
                    case 5:
                        do {
                            try {
                                System.out.println("Please choose Account to transact from...");
                                printTable(Account.PrintHeaders(), accounts);
                                System.out.print("\n> ");
                                accountOption = scanner.nextInt();
                                NumberChecker.checkOption(accountOption, accounts.size());
                                transactions = ServerTransactions.findUserTransactions(accounts.get(accountOption - 1));
                                printTable(TransactionDetails.PrintHeaders(), transactions);
                                System.out.print("\n> ");
                            } catch (WrongNumberException e) {
                                System.out.println(e.getMessage());
                                accountOption = -1;
                                scanner.nextLine();
                            } catch (InputMismatchException e) {
                                System.out.println("Wrong Input. Try again..\n");
                                accountOption = -1;
                                scanner.nextLine();
                            }
                        } while (accountOption == -1);
                        break;
               }
           }
           catch (Exception ex){
               System.out.println("Please enter an integer value between 1 and " + options.length);
               scanner.next();
               System.out.println(">");
           }
       }while(option != 6);
    }

    // Option 1
    private void CreateAccount() throws InterruptedException {
        AccountService service = new AccountService();
        int userid = user.getUserID();
        String accName = null, accDescString = null;

        System.out.println("Key in the Account Name");
        System.out.print("> ");
        accName = scanner.nextLine();
        System.out.println("Account Descriptions");
        System.out.print("> ");
        accDescString = scanner.nextLine();

        double amount = 0;
        boolean isValidInput = false;
        String input;
        while(!isValidInput){
            try{
                System.out.println("Initial Deposit");
                System.out.print("> ");
                input = scanner.next();
                amount = validateAmount(input);
                isValidInput = true;

            } catch (WrongNumberException e) {
                System.out.println(e.getMessage());
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Wrong Input. Try again..");
                scanner.nextLine();
            }
        }

        // Send to server
        System.out.println("Creating Account " + accName + "...");
        Account acc = service.CreateAccount(userid, accName, accDescString, amount);
        Thread.sleep(1000);
        if (acc != null) {
            System.out.println("Account is Created\n");
            this.accounts.add(acc);
            printTable(Account.PrintHeaders(), accounts);
            System.out.println("\n");
            scanner.nextLine();
        } else {
            System.out.println("Error in System..\n");
            scanner.nextLine();
        }
    }

    public void Deposit(Account acc) throws InterruptedException {
        double amount = 0;
        boolean isValidInput = false;
        String input;
        //Allow user to choose account
        while(!isValidInput){
            try{
                System.out.println("Enter Deposit Amount");
                System.out.print("> ");
                input = scanner.next();
                amount = validateAmount(input);
                System.out.println("Depositing...\n");
                acc.deposit(amount);
                Thread.sleep(1000);
                System.out.println("Depositied amount $" + amount + " in " + acc.getAccName() );
                printTable(Account.PrintHeaders(), accounts);
                System.out.println("\n");

                scanner.nextLine();
                isValidInput = true;
            }catch(WrongNumberException e){
                System.out.println(e.getMessage());
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Wrong Input. Try again..");
                scanner.nextLine();
            }catch(TransactionError e){
                System.out.println(e.getMessage());
                scanner.nextLine();
            }   
        }
    }

    // public void Withdraw() throws Exception {
    // double amount = 0;
    // boolean isValidInput = false;

    // while(!isValidInput){
    // try{
    // System.out.println("Enter Withdrawal Amount");
    // System.out.print("> ");
    // amount = scanner.nextDouble();
    // NumberChecker.checkNegative(amount);
    // acc.withdraw(amount);
    // isValidInput = true;
    // }catch(WrongNumberException e){
    // System.out.println(e.getMessage());
    // }catch(Exception e){
    // System.out.println(e.getMessage());
    // }
    // }

    // }

    // public boolean TransferFunds(Account acc) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'TransferFunds'");
    // }

    // public void ViewTranscation(Account acc) {
    // // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method
    // 'ViewTranscation'");
    // }

    // Template for printing Option Commands
    public static void printMenu(String[] options) {
        for (String option : options) {
            System.out.println(option);
        }
    }

    // Template for Printing info
    public static <T> void printTable(String[] headers, List<T> list) {
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
    }

    /*User Defined Exception*/ 
    private static class WrongNumberException extends Exception {
        public WrongNumberException(String errorMessage) {
            super(errorMessage);
        }
    }
    
    public static double validateAmount(String input) throws WrongNumberException {
        
        double amount = Double.parseDouble(input);
        if (amount < 0) {
            throw new WrongNumberException("Amount cannot be negative!\n");
        }
        // Check if it has at most 2 decimal
        String[] parts = input.split("\\.");
        if (parts.length == 2 && parts[1].length() > 2) {
            throw new WrongNumberException("Amount entered cannot have more than 2 decimal places.");
        }
        return amount;
    
    }

    public static void checkOption(int number, int max) throws WrongNumberException {
        if (number > max) {
            throw new WrongNumberException("Wrong Choice lah bang\n");
        }
    }

    /*End of Exception */

}


