import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import Account.Account;
import Account.AccountTransaction.TransactionError;
import User.User;
import Server.ServerAccount;
import Server.ServerCheque;
import Server.ServerTransactions;
import Server.ServerUser;
import Transaction.TransactionDetails;
import Cheque.Cheque;

public class BankMenu implements ServerAccount, ServerTransactions {
    private List<Account> accounts = null;
    private User user;
    private Scanner scanner;

    private enum Option {
        EDIT_USER_DETAILS(1),
        CREATE_NEW_ACCOUNT(2),
        DELETE_AN_ACCOUNT(3),
        DEPOSIT(4),
        WITHDRAW(5),
        TRANSFER_FUNDS(6),
        VIEW_TRANSACTIONS(7),
        VIEW_CHEQUES(8),
        EXIT(9);

        private final int value;

        Option(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private enum userOptions {
        RESET_PASSWORD(1),
        UPDATE_ACCOUNT(2),
        DEACTIVATE_ACCOUNT(3),
        EXIT(4);

        private final int value;

        userOptions(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    };

    BankMenu(User user) {
        this.accounts = ServerAccount.findUserAccounts(user.getUserID());
        this.user = user;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    // Main Controller
    public void run() {
        int option = 1;
        scanner = new Scanner(System.in);
        do {
            String[] optionsCommand = accounts.isEmpty() ? new String[] {
                    "1- Edit User Details",
                    "2- Create New Account",
                    "3- Exit"
            }: new String[] {
                            "1- Edit User Details",
                            "2- Create New Account",
                            "3- Delete An Account",
                            "4- Deposit",
                            "5- Withdraw",
                            "6- Transfer Funds",
                            "7- View Transactions",
                            "8- View Cheques",
                            "9- Exit"
            };

            if(!accounts.isEmpty()){
                // Print Available Accounts
                System.out.println("Active Accounts..");
                printTable(Account.PrintHeaders(), accounts);
            }else{
                System.out.println("No Active Accounts...");
            }
    
            try {
                printMenu(optionsCommand);
                System.out.println("What would would want to do? (Key in the Command Number)");
                System.out.print("> ");
                option = scanner.nextInt();
                checkOption(option, Option.values().length);
                System.out.print("\n");
                scanner.nextLine();

                switch (Option.values()[option - 1]) {
                    case EDIT_USER_DETAILS:
                        EditUser();
                        break;
                    case CREATE_NEW_ACCOUNT:
                        CreateAccount();
                        break;

                    default:
                        if (accounts.isEmpty()) {
                            System.out.println(
                                    "Please enter an integer value between 1 and " + Option.values().length + "\n");
                            scanner.nextLine();
                            break;
                        } else { // Option 3 to Exit choosen
                            int accountOption = chooseAccount();
                            if (accountOption != -1) {
                                Account account = accounts.get(accountOption - 1);
                                switch (Option.values()[option - 1]) {
                                    case DELETE_AN_ACCOUNT:
                                        DeleteAccount(account);
                                        break;
                                    case DEPOSIT:
                                        Deposit(account);
                                        break;
                                    case WITHDRAW:
                                        Withdraw(account);
                                        break;
                                    case TRANSFER_FUNDS:
                                        // TODO
                                        break;
                                    case VIEW_TRANSACTIONS:
                                        ViewTransaction(account);
                                        break;
                                    case VIEW_CHEQUES:
                                        ViewCheques(account);
                                        break;
                                    default:
                                        System.out.println("Please enter an integer value between 1 and "
                                                + Option.values().length + "\n");
                                        scanner.nextLine();
                                        break;
                                }
                            }
                            break; // User chose to go back Main Menu from Choose Account or Error
                        }
                    case EXIT:
                        // Clean up
                        scanner.close();
                        break;
                }
            } catch (InputMismatchException | WrongNumberException e) {
                System.out.println("Invalid input. Please enter an integer value.\n");
                option = 1;
                scanner.nextLine();
                continue;
            }
        } while (Option.values()[option - 1] != Option.EXIT);
    }

    // Option 1
    private void EditUser() {
        String[] userOptionsCommand = {
                "1- Reset Password",
                "2- Update User Account",
                "3- Deactivate User Account",
                "4- Return to menu"
        };
        int userOption = -1;

        do {
            try {
                // Display user particulars
                System.out.println("Request to edit user details..");
                List<User> userList = new ArrayList<>();
                userList.add(user);
                printTable(User.PrintHeaders(), userList);

                printMenu(userOptionsCommand);
                System.out.println("What would would want to do? (Key in the Command Number)");
                System.out.print("> ");
                userOption = scanner.nextInt();
                checkOption(userOption, userOptions.values().length);
                System.out.print("\n");
                scanner.nextLine();

                switch (userOptions.values()[userOption - 1]) {
                    case RESET_PASSWORD:
                        ServerUser.resetUserPassword(user);
                        break;
                    case UPDATE_ACCOUNT:
                        ServerUser.getNewUpdates(user);
                        ServerUser.updateUser(user);
                        break;
                    case DEACTIVATE_ACCOUNT:
                        ServerUser.deactivateUser(user);
                        break;
                    case EXIT:
                        break; // return to maun
                    default:
                        System.out.println(
                                "Please enter an integer value between 1 and " + userOptions.values().length + "\n");
                        break;
                }
            } catch (EmptyTableException e) {
                System.out.println(e.getMessage());
                break;
            } catch (InputMismatchException | WrongNumberException e) {
                System.out.println("Invalid input. Please enter an integer value.\n");
                userOption = 1;
            }

        } while (userOptions.values()[userOption - 1] != userOptions.EXIT);
        System.out.print("\n");
    }

    // Option 2
    private void CreateAccount() {

        int userid = user.getUserID();
        String accName = null, accDescString = null;
        double amount = 0;
        boolean isValidInput = false;

        try {
            accName = promptForInput("Key in the Account Name ", String.class);
            accDescString = promptForInput("Account Descriptions", String.class);
            while (!isValidInput) {
                try {
                    amount = promptForInput("Initial Deposit", Double.class);
                    // Check amount
                    validateAmount(amount);
                    isValidInput = true;
                } catch (WrongNumberException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (GoBackException e) {
            return;
        }

        // Send to server
        System.out.println("Creating Account " + accName + "...\n");
        Account acc = ServerAccount.NewAccount(userid, accName, accDescString, amount);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            return;
        }
        if (acc != null) {
            System.out.println("Account is Created\n");
            this.accounts.add(acc);
            try {
                printTable(Account.PrintHeaders(), accounts);
            } catch (EmptyTableException e) {
                e.printStackTrace();
                return;
            }
        } else {
            System.out.println("Error in System..\n");
        }
        return;
    }

    // Option 3
    private void DeleteAccount(Account acc) {
        boolean result = ServerAccount.DeleteAccount(acc.getUserID(), acc.getAccID());
        if (!result) {
            System.out.println("Error...");
        } else {
            // Update Accountlist
            accounts.remove(acc);
            System.out.println("Account Deleted Successfully.\n");
        }
    }

    // Option 4
    private void Deposit(Account acc) {
        double amount = 0;
        boolean isValidInput = false;

        while (!isValidInput) {
            try {
                amount = promptForInput("Enter Deposit Amount", Double.class);
                validateAmount(amount);
                System.out.println("Depositing...\n");
                acc.deposit(amount);
                Thread.sleep(1000);
                System.out.println("Depositied amount $" + amount + " in " + acc.getAccName() + "\n");
                isValidInput = true;
            } catch (WrongNumberException | TransactionError e) {
                // Throw by validateAmount()
                System.out.println(e.getMessage());
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                break;
            } catch (GoBackException e) {
                return;
            }
        }
    }

    // Option 5
    private void Withdraw(Account acc) {
        double amount = 0;
        boolean isValidInput = false;

        while (!isValidInput) {
            try {
                amount = promptForInput("Enter Withdraw Amount", Double.class);
                validateAmount(amount);
                // Send to withdraw
                System.out.println("Withdrawing...\n");
                acc.withdraw(amount);

                Thread.sleep(1000);
                System.out.println("Withdrew amount $" + amount + " in " + acc.getAccName() + "\n");

                isValidInput = true;
            } catch (WrongNumberException | TransactionError e) {
                System.out.println(e.getMessage());
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                break;
            } catch (GoBackException e) {
                return;
            }

        }
    }

    // Option 6
    private void TransferFunds(Account acc) {
        // TODO
    }

    // Option 7
    private void ViewTransaction(Account account) {
        // Print transactions table
        List<TransactionDetails> transactions = ServerTransactions.findUserTransactions(account);

        // Loop controllers and counter
        boolean view = true;
        int pageCount = 1;
        int pageFirstElement = 0;
        int pageLastElement = 10;
        int pages1 = 1;

        // Set first element of page and increment/decrement by 10 if user inputs 2 =
        // next, 1 = back
        pageFirstElement = (pages1 - 1) * 10;

        // Get number of transaction pages for page viewer
        int pages2 = transactions.size() / 10;
        if (transactions.size() % 10 != 0) {
            pages2 = pages2 + 1;
        }

        // Loop as long as user still wants to continue
        while (view) {
            // Get last element of page either 10 per list or remaining of list
            pageLastElement = Math.min(pageFirstElement + 10, transactions.size());

            try {
                List<TransactionDetails> tempList = transactions.subList(pageFirstElement, pageLastElement);
                printTable(TransactionDetails.PrintHeaders(), tempList);

                // Display menu and check for valid input of 1 - 3
                System.out.print("\t\t\t\t\t\tPage: " + pageCount + " / " + pages2);
                System.out.print("\n\n1- Back");
                System.out.print("\n2- Next");
                System.out.println("\n3- Return to menu");
                System.out.print("> ");
                int pageOption = scanner.nextInt();
                checkOption(pageOption, 3);

                // Checks if user tries to go below min or beyond max number of pages
                // Increase/Decrease page counter and page displays
                if (pageOption == 1) {
                    int pageCountTemp = pageCount - 1;
                    checkPageValidity(pageCountTemp, pages2);
                    pageCount--;
                    pageFirstElement = pageFirstElement - 10;
                } else if (pageOption == 2) {
                    int pageCountTemp = pageCount + 1;
                    checkPageValidity(pageCountTemp, pages2);
                    pageCount++;
                    pageFirstElement = pageFirstElement + 10;
                }
                // Exit transaction viewing
                else if (pageOption == 3) {
                    view = false;
                }
            } catch (WrongNumberException e) {
                // Thrown by checkOption
                System.out.println(e.getMessage());
            } catch (EmptyTableException e) {
                e.printStackTrace();
                break;
            }
        }
        // End of while loop
        System.out.print("\n");
    }

    // Option8
    private void ViewCheques(Account account) {
        // Print transactions table
        List<Cheque> Cheques = ServerCheque.findUserCheques(account);

        // Loop controllers and counter
        boolean view = true;
        int pageCount = 1;
        int pageFirstElement = 0;
        int pageLastElement = 10;
        int pages1 = 1;

        // Set first element of page and increment/decrement by 10 if user inputs 2 =
        // next, 1 = back
        pageFirstElement = (pages1 - 1) * 10;

        // Get number of transaction pages for page viewer
        int pages2 = Cheques.size() / 10;
        if (Cheques.size() % 10 != 0) {
            pages2 = pages2 + 1;
        }

        // Loop as long as user still wants to continue
        while (view) {
            // Get last element of page either 10 per list or remaining of list
            pageLastElement = Math.min(pageFirstElement + 10, Cheques.size());

            try {
                List<Cheque> tempList = Cheques.subList(pageFirstElement, pageLastElement);
                printTable(Cheque.PrintHeaders(), tempList);

                // Display menu and check for valid input of 1 - 3
                System.out.print("\t\t\t\t\t\tPage: " + pageCount + " / " + pages2);
                System.out.print("\n\n1- Back");
                System.out.print("\n2- Next");
                System.out.println("\n3- Return to menu");
                System.out.print("> ");
                int pageOption = scanner.nextInt();
                checkOption(pageOption, 3);

                // Checks if user tries to go below min or beyond max number of pages
                // Increase/Decrease page counter and page displays
                if (pageOption == 1) {
                    int pageCountTemp = pageCount - 1;
                    checkPageValidity(pageCountTemp, pages2);
                    pageCount--;
                    pageFirstElement = pageFirstElement - 10;
                } else if (pageOption == 2) {
                    int pageCountTemp = pageCount + 1;
                    checkPageValidity(pageCountTemp, pages2);
                    pageCount++;
                    pageFirstElement = pageFirstElement + 10;
                }
                // Exit transaction viewing
                else if (pageOption == 3) {
                    view = false;
                }
            } catch (WrongNumberException e) {
                // Thrown by checkOption
                System.out.println(e.getMessage());
            } catch (EmptyTableException e) {
                e.printStackTrace();
                break;
            }
        }
        // End of while loop
        System.out.print("\n");
    }

    /*
     * Template for Choosing Account
     */
    private int chooseAccount() {
        int accountOption = -1;
        do {
            try {
                printTable(Account.PrintHeaders(), accounts);
                accountOption = promptForInput("Please choose Account...", Integer.class);
                checkOption(accountOption, accounts.size());
            } catch (WrongNumberException e) {
                System.out.println(e.getMessage());
                accountOption = -1;
            } catch (GoBackException e) {
                return -1;
            }
        } while (accountOption == -1);
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
        System.out.println("Press '-1' to go back to the previous menu.");

        T input = null;
        do {
            System.out.print("> ");
            String userInput = scanner.nextLine();
            if (userInput.equals("-1")) {
                System.out.print("\n");
                throw new GoBackException("Return Control");
            } else if (!userInput.isEmpty()) {
                try {
                    if (type == String.class) {
                        input = type.cast(userInput);
                    } else if (type == Double.class || type == double.class) {
                        input = type.cast(Double.parseDouble(userInput));
                    } else if (type == Integer.class) {
                        input = type.cast(Integer.parseInt(userInput));
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Wrong Input. Try again..");
                }
            }
        } while (input == null);
        System.out.print("\n");
        return input;
    }

    /*
     * Template for Printing info
     */
    private static <T> void printTable(String[] headers, List<T> list) {
        if (list.isEmpty()) {
            throw new EmptyTableException("List is Empty.");
        }

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

        if (amount == 0) {
            return;
        } else if (amount < 0) {
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
        if (number > max || number < 0) {
            throw new WrongNumberException("\nWrong choice lah bang");
        }
    }

    public static void checkPageValidity(int requested, int max) throws WrongNumberException {
        if ((requested < 1) || (requested > max)) {
            throw new WrongNumberException("\nUnable to proceed to page requested");
        }
    }

    private static class GoBackException extends RuntimeException {
        public GoBackException(String errorMessage) {
            super(errorMessage);
        }
    }

    private static class EmptyTableException extends RuntimeException {
        public EmptyTableException(String errorMessage) {
            super(errorMessage);
        }
    }
    /* End of Exception */

}
