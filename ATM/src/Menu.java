import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import Account.Account;
import Account.AccountService;
import User.User;
import Server.ServerAccount;
import Server.ServerTransactions;
import Server.ServerUser;
import Transaction.TransactionDetails;

class Menu implements ServerAccount, ServerTransactions {
    private List<Account> accounts;
    private List<TransactionDetails> transactions;
    private User user;
    private Scanner scanner;

    Menu(User user) {
        List<Account> accounts = ServerAccount.findUserAccounts(user.getUserID());
        this.accounts = accounts;
        this.user = user;
        scanner = new Scanner(System.in);
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public List<TransactionDetails> getTransactions() {
        return transactions;
    }

    public void run() {
        String[] options = {
                "1- Edit User Details",
                "2- Create New Account",
                "3- Deposit",
                "4- Withdraw",
                "5- Transfer Funds",
                "6- View Transactions",
                "7- Exit"
        };
        String[] userOptions = {
            "\n1- Reset Password",
            "2- Update User Account",
            "3- Deactivate User Account",
            "4- Return to menu"
        };

        int option = -1;

        do {
            // Print Available Accounts
            System.out.println("\nActive Accounts..");
            printTable(Account.PrintHeaders(), accounts);
    
            System.out.println("\n");
            int accountOption = -1;
            printMenu(options);
            System.out.println("\nWhat do you want to do? (Key in the Command Number)");
            System.out.print("> ");

            try {
                option = scanner.nextInt();
                System.out.println("\n");
                switch(option) {
                    // View user settings
                    case 1:
                        try {
                            int userOption = -1;
                            do {
                                System.out.println("Request to edit user details..");
                                List<User> userList = new ArrayList<>();
                                userList.add(user);

                                printTable(User.PrintHeaders(), userList);
                                printMenu(userOptions);
                                System.out.println();
                                System.out.println("What do you want to do?");
                                System.out.print("> ");
                                userOption = scanner.nextInt();
                                NumberChecker.checkOption(userOption, userOptions.length);

                                switch(userOption) {
                                    // Calls user reset password menu and process
                                    case 1:
                                        ServerUser.resetUserPassword(user);
                                        break;

                                    // Calls user update menu and process
                                    case 2:
                                        ServerUser.getNewUpdates(user);
                                        ServerUser.updateUser(user);
                                        break;

                                    // Calls user deactivation menu and process
                                    case 3:
                                        ServerUser.deactivateUser(user);
                                        break;
                                }                            
                            } while (userOption != 4);
                        } catch (WrongNumberException e) {
                            System.out.println(e.getMessage());
                            accountOption = -1;
                            scanner.nextLine();
                        } catch (InputMismatchException e) {
                            System.out.println("Wrong Input. Try again..\n");
                            accountOption = -1;
                            scanner.nextLine();
                        }
                        break;

                    // Create new account
                    case 2:
                        scanner.nextLine();
                        CreateAccount();
                        break;

                    // Deposit funds
                    case 3:
                        do {
                            try {
                                System.out.println("Please choose Account to transact from...");
                                printTable(Account.PrintHeaders(), accounts);
                                System.out.print("\n> ");
                                accountOption = scanner.nextInt();
                                NumberChecker.checkOption(accountOption, accounts.size());
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
                        Deposit(accounts.get(accountOption - 1));
                        break;

                    // Withdraw funds
                    case 4:
                        // Withdraw();
                        break;

                    // View account transactions
                    case 6:
                        do {
                            try {
                                // Prints account table
                                System.out.println("Please choose Account to view transactions from...");
                                printTable(Account.PrintHeaders(), accounts);

                                // Get user input on account to view
                                System.out.print("\n> ");
                                accountOption = scanner.nextInt();
                                NumberChecker.checkOption(accountOption, accounts.size());

                                // Print transactions table
                                transactions = ServerTransactions.findUserTransactions(accounts.get(accountOption - 1));
                                
                                // Loop controllers and counter
                                boolean view = true;
                                int pageCount = 1;
                                int pageStart = 0;
                                int pageEnd = 10;

                                // Loop as long as user still wants to continue
                                while(view){
                                    // Get number of transaction pages
                                    int pages = transactions.size() / 10;
                                    
                                    try{
                                        List<TransactionDetails> tempList = transactions.subList(pageStart, pageEnd);
                                        printTable(TransactionDetails.PrintHeaders(), tempList);

                                        // Display menu and check for valid input of 1 - 3
                                        System.out.print("\t\t\t\t\t\tPage: " + pageCount + " / " + pages);
                                        System.out.print("\n\n1- Back");
                                        System.out.print("\n2- Next");
                                        System.out.println("\n3- Return to menu");
                                        System.out.print("> ");
                                        int pageOption = scanner.nextInt();
                                        NumberChecker.checkOption(pageOption, 3);
                                        
                                        // Checks if user tries to go below min or beyond max number of pages
                                        // Increase/Decrease page counter and page displays
                                        if(pageOption == 1){
                                            int pageCountTemp = pageCount - 1;
                                            NumberChecker.checkPageValidity(pageCountTemp, pages);
                                            pageCount--;
                                            pageStart = pageStart - 10;
                                            pageEnd = pageEnd - 10;
                                        }
                                        else if(pageOption == 2){
                                            int pageCountTemp = pageCount + 1;
                                            NumberChecker.checkPageValidity(pageCountTemp, pages);
                                            pageCount++;
                                            pageStart = pageStart + 10;
                                            pageEnd = pageEnd + 10;
                                        }
                                        // Exit transaction viewing
                                        else if(pageOption == 3){
                                            view = false;
                                        }
                                    } catch (WrongNumberException e) {
                                        System.out.println(e.getMessage());
                                        scanner.nextLine();
                                    }
                                }

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
            } catch (Exception ex) {
                System.out.println("Please enter an integer value between 1 and " + options.length);
                scanner.next();
                System.out.println(">");
            }
        } while (option != 7);
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

        while (!isValidInput) {
            try {
                System.out.println("Initial Deposit");
                System.out.print("> ");
                amount = scanner.nextDouble();
                NumberChecker.checkNegative(amount);
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
    
    public void Deposit(Account acc) {
        double amount = 0;
        boolean isValidInput = false;
        // Allow user to choose account
        while (!isValidInput) {
            try {
                System.out.println("Enter Deposit Amount");
                System.out.print("> ");
                amount = scanner.nextDouble();
                NumberChecker.checkNegative(amount);
                acc.deposit(amount);
                isValidInput = true;
            } catch (WrongNumberException e) {
                System.out.println(e.getMessage());
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Wrong Input. Try again..");
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
    // // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method
    // 'TransferFunds'");
    // }

    // public void ViewTransaction(Account acc) {
    // // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method
    // 'ViewTransaction'");
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

            if (item instanceof User) {
                values[i] = ((User) item).PrintValues();
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
        line = String.format("\n| %-3s", "No");
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
}

class WrongNumberException extends Exception {
    public WrongNumberException(String errorMessage) {
        super(errorMessage);
    }
}

class NumberChecker {
    public static void checkNegative(double number) throws WrongNumberException {
        if (number < 0) {
            throw new WrongNumberException("Amount cannot be negative!\n");
        }
    }

    public static void checkOption(int number, int max) throws WrongNumberException {
        if (number > max) {
            throw new WrongNumberException("\nWrong choice lah bang");
        }
    }

    public static void checkPageValidity(int requested, int max) throws WrongNumberException {
        if ((requested < 1) || (requested > max)) {
            throw new WrongNumberException("\nUnable to proceed to page requested");
        }
    }
}
