import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import Account.Account;
import Account.AccountTransaction.TransactionError;
import User.User;
import picocli.CommandLine;
import Server.ServerAccount;
import Server.ServerCheque;
import Server.ServerTransactions;
import Server.ServerUser;
import Server.SMS;
import Transaction.TransactionDetails;
import Cheque.Cheque;

public class BankMenu implements ServerAccount, ServerTransactions {

    // Object attributes
    private List<Account> accounts = null;
    private User user;
    public final static Scanner scanner = new Scanner(System.in);

    // Constructor
    BankMenu(User user) {
        this.accounts = ServerAccount.findUserAccounts(user.getUserID());
        this.user = user;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    /* Miscellaneous Functions */
    // Main menu options' mapping
    private enum Option {
        EXIT(0),
        EDIT_USER_DETAILS(1),
        CREATE_NEW_ACCOUNT(2),
        DEACTIVATE_AN_ACCOUNT(3),
        DEPOSIT(4),
        WITHDRAW(5),
        TRANSFER_FUNDS(6),
        VIEW_TRANSACTIONS(7),
        VIEW_CHEQUES(8),
        UPDATE_WITHDRAWAL_LIMIT(9),
        UPDATE_TRANSFER_LIMIT(10);

        private final int value;

        Option(int value) {
            this.value = value;
        }

        public static String getStringValue(int intValue) {
            for (Option option : Option.values()) {
                if (option.value == intValue) {
                    return option.toString();
                }
            }
            return null;
        }

    }

    // User sub-menu options' mapping
    private enum userOptions {
        EXIT(0),
        RESET_PASSWORD(1),
        UPDATE_ACCOUNT(2),
        DEACTIVATE_ACCOUNT(3);

        private final int value;

        userOptions(int value) {
            this.value = value;
        }
    };

    // Main Controller
    public void run() {
        int option = 1;
        do {
            // Set menu list according to whether the logged in user has a Bank account
            String[] optionsCommand = accounts.isEmpty() ? new String[] {
                    "@|39 1- Edit User Details|@",
                    "@|39 2- Create New Account|@",
                    "@|51 \nPress '0' to Log Out.|@"
            }
                    : new String[] {
                            "@|39 1- Edit User Details|@",
                            "@|39 2- Create New Account|@",
                            "@|39 3- Deactivate An Account|@",
                            "@|39 4- Deposit|@",
                            "@|39 5- Withdraw|@",
                            "@|39 6- Transfer Funds|@",
                            "@|39 7- View Transactions|@",
                            "@|39 8- View Cheques|@",
                            "@|39 9- Update Withdrawal Limit|@",
                            "@|39 10- Update Transfer Limit|@",
                            "@|51 \nPress '0' to Log Out.|@"
                    };

            // Print user's active accounts
            if (!accounts.isEmpty()) {
                // Print Available Accounts
                System.out.println(CommandLine.Help.Ansi.ON.string("@|51 Active Accounts..|@"));
                printTable(Account.PrintHeaders(), accounts);
            } else {
                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 No Active Accounts...|@"));
            }

            // Run menu
            try {
                printMenu(optionsCommand);
                System.out.println(CommandLine.Help.Ansi.ON.string("@|51 What do you want to do?|@"));
                System.out.print("> ");
                option = scanner.nextInt();
                checkOption(option, Option.values().length - 1);
                System.out.print("\n");
                scanner.nextLine();

                switch (Option.values()[option]) {
                    case EXIT:
                        // Clean up
                        System.out.println(CommandLine.Help.Ansi.ON.string("@|51 Thank you for using Bank ATM!|@"));
                        scanner.close();
                        System.exit(1);
                    case EDIT_USER_DETAILS:
                        EditUser();
                        break;
                    case CREATE_NEW_ACCOUNT:
                        CreateAccount();
                        break;
                    default:
                        if (accounts.isEmpty()) {
                            System.out.println(
                                    "Please enter an integer value between 0 and " + Option.values().length + "\n");
                            scanner.nextLine();
                            break;
                        } else {
                            if (Option.getStringValue(option) == "TRANSFER_FUNDS")
                            {
                                System.out.println("Select the Issuing Account...");
                                int accountOption = chooseAccount();
                                TransferFunds(accounts.get(accountOption - 1));
                                break;
                            }
                            int accountOption = chooseAccount();
                            if (accountOption != -1) {
                                Account account = accounts.get(accountOption - 1);
                                switch (Option.values()[option]) {
                                    case DEACTIVATE_AN_ACCOUNT:
                                        DeactivateAccount(account);
                                        break;
                                    case DEPOSIT:
                                        Deposit(account);
                                        break;
                                    case WITHDRAW:
                                        Withdraw(account);
                                        break;
                                    // case TRANSFER_FUNDS:
                                    //     TransferFunds(account);
                                    //     break;
                                    case VIEW_TRANSACTIONS:
                                        ViewTransaction(account);
                                        break;
                                    case VIEW_CHEQUES:
                                        ViewCheques(account);
                                        break;
                                    case UPDATE_WITHDRAWAL_LIMIT:
                                        UpdateWithdrawalLimit(account);
                                        break;
                                    case UPDATE_TRANSFER_LIMIT:
                                        UpdateTransferLimit(account);
                                        break;
                                    default:
                                        // System.out.println("Please enter an integer value between 0 and "
                                        // + Option.values().length + "\n");
                                        // scanner.nextLine();
                                        // break;
                                        throw new WrongNumberException("Please enter an integer value between 0 and "
                                                + Option.values().length + "\n");
                                }
                            }
                            break; // User chose to go back Main Menu from Choose Account or Error
                        }
                }
            } catch (WrongNumberException e) {
                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 " + e.getMessage() + "|@"));
            } catch (InputMismatchException i) {
                System.out.println(
                        CommandLine.Help.Ansi.ON.string("@|208 \nInvalid input. Please enter an integer.|@\n"));
            } catch (NoSuchElementException e) {
                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 \nNo input available.|@\n"));
            } finally {
                option = 1;
                scanner.nextLine();
            }
        } while (Option.values()[option] != Option.EXIT);
    }

    // Option 1
    private void EditUser() {
        String[] userOptionsCommand = {
                "@|39 1- Reset Password|@",
                "@|39 2- Update User Details|@",
                "@|39 3- Deactivate User Account|@",
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
                System.out.print("\n");
                userOption = promptForInput("@|51 What do you want to do?|@", Integer.class);
                checkOption(userOption, userOptions.values().length - 1);
                System.out.print("\n");
                scanner.nextLine();

                switch (userOptions.values()[userOption]) {
                    case RESET_PASSWORD:
                        ServerUser.resetUserPassword(user);
                        break;
                    case UPDATE_ACCOUNT:
                        ServerUser.getNewUpdates(scanner, user);
                        break;
                    case DEACTIVATE_ACCOUNT:
                        ServerUser.deactivateUser(scanner, user);
                        break;
                    case EXIT:
                        break; // return to maun
                    default:
                        // System.out.println("Please enter an integer value between 0 and " +
                        // userOptions.values().length + "\n");
                        break;
                }
            } catch (EmptyTableException e) {
                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 " + e.getMessage() + "|@"));
                break;
            } catch (InputMismatchException | WrongNumberException e) {
                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 " + e.getMessage() + "|@"));
                userOption = 1;
            } catch (GoBackException e) {
                break;
            }

        } while (userOptions.values()[userOption] != userOptions.EXIT);
        System.out.print("\n");
    }

    // Option 2
    private void CreateAccount() {
        int userid = user.getUserID();
        String accName = null, accDescString = null;
        double amount = 0;
        boolean isValidInput = false;

        try {
            accName = promptForInput("@|51 Enter the Account Name|@", String.class);
            accDescString = promptForInput("\n@|51 Account Description|@", String.class);
            while (!isValidInput) {
                try {
                    amount = promptForInput("\n@|51 Initial Deposit|@", Double.class);
                    // Check amount
                    validateAmount(amount);
                    isValidInput = true;
                } catch (WrongNumberException e) {
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|208 " + e.getMessage() + "|@"));
                }
            }
        } catch (GoBackException e) {
            return;
        }

        // Send to server
        System.out.println(CommandLine.Help.Ansi.ON.string("@|46 Creating Account " + accName + "...|@\n"));
        Account acc = ServerAccount.NewAccount(userid, accName, accDescString, amount);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            return;
        }
        if (acc != null) {  
            System.out.println(CommandLine.Help.Ansi.ON.string("@|208 Account has been created!\n|@"));
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
    private void DeactivateAccount(Account acc) {
        boolean result = ServerAccount.DeactivateAccount(acc.getUserID(), acc.getAccID());
        if (!result) {
            System.out.println("Error...");
        } else {
            // Update Accountlist
            accounts.remove(acc);
            System.out.println("Account Deactivated Successfully.\n");
        }
    }

    // Option 4
    private void Deposit(Account acc) {
        double amount = 0;
        boolean isValidInput = false;

        while (!isValidInput) {
            try {
                amount = promptForInput("\n@|51 Enter Deposit Amount|@", Double.class);
                validateAmount(amount);
                System.out.println(CommandLine.Help.Ansi.ON.string("@|46 Depositing...\n|@"));
                acc.deposit(amount);
                Thread.sleep(1000);
                System.out.println(CommandLine.Help.Ansi.ON
                        .string("@|208 Depositied amount $" + amount + " in " + acc.getAccName() + "\n|@"));
                isValidInput = true;
            } catch (WrongNumberException | TransactionError e) {
                // Throw by validateAmount()
                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 " + e.getMessage() + "|@"));
            } catch (InterruptedException e) {
                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 " + e.getMessage() + "|@"));
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
        double currentLimit = ServerAccount.getRemainingWithdrawLimit(acc.getAccID(), acc.getWithdrawLimit());
        System.out.println(CommandLine.Help.Ansi.ON
                .string("\n@|208 Current remaining withdrawal limit is: $" + currentLimit + "|@"));
        if (currentLimit > 0) {
            while (!isValidInput) {
                try {
                    amount = promptForInput("@|51 Enter Withdraw Amount|@", Double.class);
                    validateAmount(amount);
                    // Send to withdraw
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|46 Withdrawing...\n|@"));
                    acc.withdraw(amount);

                    Thread.sleep(1000);
                    System.out.println(CommandLine.Help.Ansi.ON
                            .string("@|208 Withdrew amount $" + amount + " in " + acc.getAccName() + " account\n|@"));

                    isValidInput = true;
                } catch (WrongNumberException | TransactionError e) {
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|208 " + e.getMessage() + "|@"));
                } catch (InterruptedException e) {
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|208 " + e.getMessage() + "|@"));
                    break;
                } catch (GoBackException e) {
                    return;
                }

            }
        } else {
            System.out.println(CommandLine.Help.Ansi.ON.string(
                    "@|208 \nYou have reached your daily withdrawal limit! Please try again another day or update your limit first.|@"));
        }
    }

    // Option 6
    private void TransferFunds(Account IssuingAccount) { // Recieve the IssuingAccount from Run
        String[] transferCommand = {
                "1- Transfer funds between own Accounts",
                "2- Transfer funds to another Account"
        };
        boolean isValidInput = false;
        BigDecimal currentLimit = ServerAccount.getRemainingTransferLimit(IssuingAccount.getAccID(),
                IssuingAccount.getTransferLimit());
        System.out.println(
                CommandLine.Help.Ansi.ON.string("@|208 Current remaining transfer limit is: $" + currentLimit + "|@"));
        if (currentLimit.compareTo(BigDecimal.ZERO) > 0) {
            while (!isValidInput) {
                printMenu(transferCommand);
                System.out.println();
                try {
                    int transactionOption = promptForInput("Which Transaction Operation?", Integer.class);
                    // Check if transOption is within the size of the transferCommand
                    checkOption(transactionOption, transferCommand.length);
                    switch (transactionOption) {
                        
                        case 1:// Transfer to Own Account
                        try {
                            System.out.println("Select the Receiving Account...");
                            int accountOption = chooseAccount();
                            Account RecivingAccount = accounts.get(accountOption - 1);
                            double transferAmount = 0;
                            while (transferAmount == 0) { // Loop till transfer amount correct
                                // Get Transfer Amount
                                transferAmount = promptForInput("Transfer Amount:", Double.class);
                                if (transferAmount == 0) {
                                    System.out.println("Unable to proceed with amount keyed. Try again");
                                } else {
                                    validateAmount(transferAmount);
                                    // Send to AccountTransaction Method
                                    IssuingAccount.transferFunds(RecivingAccount, transferAmount);
                                }
                            }
                            break;
                        }
                            catch (GoBackException e) {
                                return;
                            }
                        case 2: // Transfer to others account, Account, account number
                        try{
                            String receivingAccountNumber = promptForInput(
                                    "Enter the account number of the receiving account:", String.class);
                            Account receivingAccount = ServerAccount.findAccountbyNumber(receivingAccountNumber);

                            if (receivingAccount == null) {
                                throw new TransactionError("Invalid account number.");
                            }
                            double amount = 0;
                            while (amount == 0) { // Loop till transfer amount correct
                                // Get Transfer Amount
                                amount = promptForInput("Transfer Amount:", Double.class);
                                if (amount == 0) {
                                    System.out.println("Unable to proceed with amount keyed. Try again");
                                } else {
                                    validateAmount(amount);
                                    // Send to AccountTransaction Method
                                    IssuingAccount.transferFunds(receivingAccount, amount);
                                }
                            }
                            break;
                        } catch (GoBackException e) {
                            return;
                        }

                        default:
                            System.out.println(CommandLine.Help.Ansi.ON.string("@|208 Wrong Input. Try Again.|@"));
                            break;
                    }
                    isValidInput = true;
                } catch (WrongNumberException | TransactionError e) {
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|208 " + e.getMessage() + "|@"));
                } catch (GoBackException e) {
                    return;
                }
            }
        } else {
            System.out.println(CommandLine.Help.Ansi.ON.string(
                    "@|208 \nYou have reached your daily transfer limit! Please try again another day or update your limit first.|@"));
        }

    }

    // Option 7
    private void ViewTransaction(Account account) {
        // Print transactions table
        List<TransactionDetails> transactions = ServerTransactions.findUserTransactions(account);

        // Print user's cheques only if available
        if (!transactions.isEmpty()) {
            // Print Available Accounts
            System.out.println(CommandLine.Help.Ansi.ON.string("@|51 Account Transactions..|@"));
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
                    System.out.println(CommandLine.Help.Ansi.ON
                            .string("@|51 \t\t\t\t\t\tPage: " + pageCount + " / " + pages2 + "|@"));
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|39 1- Back|@"));
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|39 2- Next\n|@"));
                    System.out.println(
                            CommandLine.Help.Ansi.ON.string("@|51 Press '0' to go back to the previous menu.|@"));
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|51 What do you want to do?|@"));
                    System.out.print("> ");
                    int pageOption = scanner.nextInt();
                    checkOption(pageOption, 2);

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
                    else if (pageOption == 0) {
                        view = false;
                    }
                } catch (WrongNumberException e) {
                    // Thrown by checkOption
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|208 " + e.getMessage() + "|@"));
                } catch (EmptyTableException e) {
                    e.printStackTrace();
                    break;
                }
            }
        } else {
            System.out.println(CommandLine.Help.Ansi.ON.string("@|208 No transactions to view...|@"));
        }
        // End of while loop
        System.out.print("\n");
    }

    // Option 8
    private void ViewCheques(Account account) {
        // Print transactions table
        List<Cheque> cheques = ServerCheque.findUserCheques(account);

        // Print user's cheques only if available
        if (!cheques.isEmpty()) {
            // Print Available Accounts
            System.out.println(CommandLine.Help.Ansi.ON.string("@|51 Cheque Transactions..|@"));

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
            int pages2 = cheques.size() / 10;
            if (cheques.size() % 10 != 0) {
                pages2 = pages2 + 1;
            }

            // Loop as long as user still wants to continue
            while (view) {
                // Get last element of page either 10 per list or remaining of list
                pageLastElement = Math.min(pageFirstElement + 10, cheques.size());

                try {
                    List<Cheque> tempList = cheques.subList(pageFirstElement, pageLastElement);
                    printTable(Cheque.PrintHeaders(), tempList);

                    // Display menu and check for valid input of 1 - 3
                    System.out.println(CommandLine.Help.Ansi.ON
                            .string("@|51 \t\t\t\t\tPage: " + pageCount + " / " + pages2 + "|@"));
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|39 1- Back|@"));
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|39 2- Next\n|@"));
                    System.out.println(
                            CommandLine.Help.Ansi.ON.string("@|51 Press '0' to go back to the previous menu.|@"));
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|51 What do you want to do?|@"));
                    System.out.print("> ");
                    int pageOption = scanner.nextInt();
                    checkOption(pageOption, 2);

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
                    else if (pageOption == 0) {
                        view = false;
                    }
                } catch (WrongNumberException e) {
                    // Thrown by checkOption
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|208 " + e.getMessage() + "|@"));
                } catch (EmptyTableException e) {
                    e.printStackTrace();
                    break;
                }
            }
        } else {
            System.out.println(CommandLine.Help.Ansi.ON.string("@|208 No cheques to view...|@"));
        }
        // End of while loop
        System.out.print("\n");
        return;
    }

    // Option 9
    private void UpdateWithdrawalLimit(Account acc) {
        // Sentinel variable
        boolean isValidInput = false;
        while (!isValidInput) {
            try {
                double newLimit = promptForInput(String
                        .format("@|51 Enter a new withdrawal limit (current limit is $%s): |@", acc.getWithdrawLimit()),
                        Double.class);
                if (newLimit == acc.getWithdrawLimit()) {
                    System.out.println(
                            CommandLine.Help.Ansi.ON.string("@|208 New limit is the same as the current limit!\n|@"));
                    continue;
                } else {
                    // Send an OTP to user's registered phone no first
                    boolean isVerified = false;
                    boolean smsSent = false;
                    String otp = SMS.generateOTP(6);
                    String input_otp = null;

                    // Sends verification message to phone
                    System.out.println("\nPlease confirm this request before proceeding.");
                    smsSent = SMS.sendSMS(user.getPhone(),
                            "An attempt to update your withdrawal limit has been made. Please contact the bank if you did not make this request.\nYour Bank OTP is: "
                                    + otp);
                    if (smsSent) {
                        System.out.println(CommandLine.Help.Ansi.ON.string(String.format(
                                "@|208 A One-Time Passcode has been sent to your registered phone number (%s).|@",
                                user.getPhone())));
                    }

                    // Sends verification message to user's phone and check for verification status
                    while (!isVerified) {
                        System.out.printf("Enter your OTP (%s): ", otp);
                        input_otp = scanner.nextLine();
                        if (otp.equals(input_otp)) {
                            isVerified = true;
                            System.out.println(
                                    CommandLine.Help.Ansi.ON.string("@|208 Proceeding with your update request...|@"));
                        } else {
                            System.out.println(
                                    CommandLine.Help.Ansi.ON.string("@|208 Wrong OTP entered! Please try again. |@"));
                        }
                    }

                    // Validate amount
                    validateAmount(newLimit);
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|208 Updating withdrawal limit...\n|@"));
                    acc.setWithdrawLimit(newLimit);
                    isValidInput = ServerAccount.updateWithdrawalLimit(acc.getAccID(), newLimit);
                    if (!isValidInput) {
                        System.out.println(CommandLine.Help.Ansi.ON.string("@|208 Error...|@"));
                    } else {
                        System.out.println(CommandLine.Help.Ansi.ON
                                .string("@|118 Your withdrawal limit has been updated successfully!\n|@"));
                    }

                }
            } catch (WrongNumberException e) {
                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 " + e.getMessage() + "|@"));
            } catch (GoBackException e) {
                return;
            }
        }

    }

    // Option 10
    private void UpdateTransferLimit(Account acc) {
        // Sentinel variable
        boolean isValidInput = false;
        // Scanner
        Scanner input = new Scanner(System.in);
        while (!isValidInput) {
            try {
                double newLimit = promptForInput(String
                        .format("@|51 Enter a new transfer limit (current limit is $%s): |@", acc.getTransferLimit()),
                        Double.class);
                if (newLimit == acc.getTransferLimit()) {
                    System.out.println(
                            CommandLine.Help.Ansi.ON.string("@|208 New limit is the same as the current limit!\n|@"));
                    continue;
                } else {
                    // Send an OTP to user's registered phone no first
                    boolean isVerified = false;
                    boolean smsSent = false;
                    String otp = SMS.generateOTP(6);
                    String input_otp = null;

                    // Sends verification message to phone
                    System.out.println("\nPlease confirm this request before proceeding.");
                    smsSent = SMS.sendSMS(user.getPhone(),
                            "An attempt to update your transfer limit has been made. Please contact the bank if you did not make this request.\nYour Bank OTP is: "
                                    + otp);
                    if (smsSent) {
                        System.out.println(CommandLine.Help.Ansi.ON.string(String.format(
                                "@|208 A One-Time Passcode has been sent to your registered phone number (%s).|@",
                                user.getPhone())));
                    }

                    // Sends verification message to user's phone and check for verification status
                    while (!isVerified) {
                        System.out.printf("Enter your OTP (%s): ", otp);
                        input_otp = input.nextLine();
                        if (otp.equals(input_otp)) {
                            isVerified = true;
                            System.out.println(
                                    CommandLine.Help.Ansi.ON.string("@|208 Proceeding with your update request...|@"));
                        } else {
                            System.out.println(
                                    CommandLine.Help.Ansi.ON.string("@|208 Wrong OTP entered! Please try again. |@"));
                        }
                    }

                    validateAmount(newLimit);
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|208 Updating transfer limit...\n|@"));
                    acc.setTransferLimit(newLimit);
                    isValidInput = ServerAccount.updateTransferLimit(acc.getAccID(), newLimit);
                    if (!isValidInput) {
                        System.out.println(CommandLine.Help.Ansi.ON.string("@|208 Error...|@"));
                    } else {
                        System.out.println(CommandLine.Help.Ansi.ON
                                .string("@|118 Your transfer limit has been updated successfully!\n|@"));
                    }

                }
            } catch (WrongNumberException e) {
                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 " + e.getMessage() + "|@"));
            } catch (GoBackException e) {
                return;
            }
        }

    }

    /*
     * Template for Choosing Account
     */
    private int chooseAccount() {
        int accountOption = -1;
        do {
            try {
                printTable(Account.PrintHeaders(), accounts);
                accountOption = promptForInput("@|51 Please choose Account...|@", Integer.class);
                checkOption(accountOption, accounts.size());
            } catch (WrongNumberException e) {
                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 " + e.getMessage() + "|@"));
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
            System.out.println(CommandLine.Help.Ansi.ON.string(option));
        }
    }

    /*
     * Tempate for asking input
     */
    private <T> T promptForInput(String prompt, Class<T> type) {

        System.out.println(CommandLine.Help.Ansi.ON.string(prompt));
        System.out.println(CommandLine.Help.Ansi.ON.string("@|51 Press '0' to go back to the previous menu.|@"));

        T input = null;

        do {
            System.out.print("> ");
            String userInput = scanner.nextLine();
            if (userInput.equals("0")) {
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
                    System.out.println(CommandLine.Help.Ansi.ON.string("@|208 Wrong Input. Try again..|@"));
                }
            }
        } while (input == null);
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

            if (item instanceof Cheque) {
                values[i] = ((Cheque) item).PrintValues();
            }

            if (item instanceof User) {
                values[i] = ((User) item).PrintValues();
            }
        }

        String line;
        // Determine the maximum width of each column
        int[] colWidths = new int[headers.length];
        int[] tempColWidths = new int[headers.length];
        int attrLength = 0;
        for (int i = 0; i < headers.length; i++) {
            colWidths[i] = headers[i].length();

            for (int j = 0; j < values.length; j++) {
                attrLength = values[j][i].length();
                colWidths[i] = Math.max(colWidths[i], attrLength);
                tempColWidths[i] = colWidths[i];
            }

            if (headers[i].equals("Debit")) {
                colWidths[i] = colWidths[i] - 8;
            }
            if (headers[i].equals("Credit")) {
                colWidths[i] = colWidths[i] - 8;
            }
            if (headers[i].equals("Status")) {
                colWidths[i] = colWidths[i] - 8;
            }
        }

        System.out.print("\n");
        // Print the table header
        if(!headers[0].equals("No")) {
            line = String.format("| %-3s", "No");
            System.out.print(line);
        }

        for (int i = 0; i < headers.length; i++) {
            line = String.format("| %-" + colWidths[i] + "s ", headers[i]);
            System.out.print(CommandLine.Help.Ansi.ON.string(line));
        }
        System.out.print("|\n");

        // Print the horizontal line below the header
        if(!headers[0].equals("No")) {
            System.out.print("+----");
        }
        for (int i = 0; i < headers.length; i++) {
            line = String.format("+-%-" + colWidths[i] + "s-", "-").replace(' ', '-');
            System.out.print(CommandLine.Help.Ansi.ON.string(line));
        }
        System.out.print("+\n");

        // Print the table values
        for (int i = 0; i < values.length; i++) {
            if(!headers[0].equals("No")) {
                line = String.format("| %-2d ", i + 1);
                System.out.print(line);
            }

            for (int j = 0; j < headers.length; j++) {
                line = String.format("| %-" + tempColWidths[j] + "s ", values[i][j]);
                System.out.print(CommandLine.Help.Ansi.ON.string(line));
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
            throw new WrongNumberException("\nInvalid input. Enter 0 to " + max + ".\n");
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
