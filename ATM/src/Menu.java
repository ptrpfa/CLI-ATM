import java.util.List;
import java.util.Scanner;

import javax.xml.transform.Source;

import Account.Account;
import Account.AccountService;
import User.User;
import Server.ServerAccount;

// import java.util.Scanner;

// import Account.Account;
// import Server.ServerAccount;

// public class Menu {
//    public static Scanner scanner = new Scanner(System.in);

//    public static void printMenu(String[] options ){
//        for (String option : options){
//            System.out.println(option);
//        }
//        System.out.print("What would would want to do: ");
//    }
//    public static void main(String[] args) {
//        String[] options = {
//            "1- Create New Account",
//            "2- Deposit",
//            "3- Withdraw",
//            "4- Transfer Funds",
//            "5- View Transcations",
//            "6- Exit"
//        };
//        Scanner scanner = new Scanner(System.in);
//        int option = -1;
//        do{
//            printMenu(options);
//            try {
//                option = scanner.nextInt();
//                switch (option){
//                    case 1:
//                         CreateAccount();
//                         break;
//                    case 2:
//                         Deposit();
//                         break;
//                    case 3: 
//                         Witdraw(); 
//                         break;
//                }
//            }
//            catch (Exception ex){
//                System.out.println("Please enter an integer value between 1 and " + options.length);
//                scanner.next();
//            }
//        }while(option != 6);
//    }

//    private void CreateAccount(int userid){
//        System.out.print("Name of Accout:");
//        String accName = scanner.nextLine();
//        System.out.println("Any Descripiton (Default: NIL):");
//        String accDescription = scanner.nextLine();
//        Account acc = new Account(userid, accName, accDescription);
//        ServerAccount accServer = new ServerAccount();
//        accServer.NewAccount(acc);
//    }

//    private void Deposit(Account acc){
//        System.out.print("Amount to Deposit: ");
//        double amount = scanner.nextDouble();
//        //Send to server to put in the amount
//        //double available = acc.getAvailableBalance(amount);
//        //System.out.println(available);
//    }
//    private void Withdraw(Acc acc){
//        System.out.print("Amount to Deposit: ");
//        double amount = scanner.nextDouble();
//    }
// }

class Menu implements ServerAccount{
    private List<Account> accounts;
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

    static void printMenu(String[] options){
        for (String option : options){
            System.out.println(option);
        }
    }

    static void printAcc(List<Account> accounts){
        System.out.printf("| %-15s | %-20s | %10s %n", "Account Number", "Account NAME", "Total Balance");
        System.out.printf("------------------------------------------------------------%n");
        //To do 
        for( Account acc : accounts){   
            acc.display();
        }
    }

    static void printTranscations(){
        //JR MAMA
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

        int option = -1;
        do{
            printMenu(options);
            System.out.println("What would would want to do? (Key in the Command Number)");
            System.out.print("> "); 
            try {
                option = scanner.nextInt();
                switch (option){
                    case 1:
                        CreateAccount();
                        break;
                    case 2:
                        Deposit();
                        break;
                    case 3: 
                        Withdraw(); 
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
    
    private Account AccountMenu(){
        //Print out all the accounts and set options number
        String[] options = new String[accounts.size()];
        String option;
        int choice = 0;
        boolean isValidInput = false;

        for(int i = 1; i <= accounts.size(); i++){
            option = String.format("%d - %s",i,(accounts.get(i-1)).getAccNo());
            options[i-1] = option;
        }
        //Print the Options
        printMenu(options);

        while(!isValidInput){
            System.out.println("Please choose Transcation Account:");
            System.out.print(">");
            choice = scanner.nextInt();
            try{
                NumberChecker.checkOption(choice, accounts.size());
                isValidInput = true;
            }catch(WrongNumberException e){
                System.out.println(e.getMessage());
            }
        }
        Account acc = accounts.get(choice-1);
        return acc;
    }

    private void CreateAccount() {
        AccountService service = new AccountService();
        int userid = user.getUserID();

        System.out.println("Key in the Account Name");
        System.out.print("> ");
        String accName = scanner.nextLine();
        System.out.println("Account Descriptions");
        System.out.print("> ");
        String accDescString = scanner.nextLine();
        double amount = 0;
        boolean isValidInput = false;

        while(!isValidInput){
            try{
                System.out.println("Initial Deposit");
                System.out.print("> ");
                amount = scanner.nextDouble();
                NumberChecker.checkNegative(amount);
                isValidInput = true;
            }catch(WrongNumberException e){
                System.out.println(e.getMessage());
            }
        }

        Account acc = service.CreateAccount(userid, accName, accDescString, amount);
        if(acc != null){
            System.out.println("Account is Created\n");
            this.accounts.add(acc);
            printAcc(accounts);
        }else{
            System.out.println("Error in System..\n");
        }
    }

    public void Deposit() {
        double amount = 0;
        boolean isValidInput = false;
        //Allow user to choose account
        Account acc = AccountMenu();
        while(!isValidInput){
            try{
                System.out.println("Enter Deposit Amount");
                System.out.print("> ");
                amount = scanner.nextDouble();
                NumberChecker.checkNegative(amount);
                acc.deposit(amount);
                isValidInput = true;
            }catch(WrongNumberException e){
                System.out.println(e.getMessage());
            }
        }
    }

    public void Withdraw() throws Exception {
        double amount = 0;
        boolean isValidInput = false;
        //Allow user to choose account
        Account acc = AccountMenu();

        while(!isValidInput){
            try{
                System.out.println("Enter Withdrawal Amount");
                System.out.print("> ");
                amount = scanner.nextDouble();
                NumberChecker.checkNegative(amount);
                acc.withdraw(amount);
                isValidInput = true;
            }catch(WrongNumberException e){
                System.out.println(e.getMessage());
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        }

    }

    // public boolean TransferFunds(Account acc) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'TransferFunds'");
    // }

    // public void ViewTranscation(Account acc) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'ViewTranscation'");
    // }
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
    public static void checkOption(int number, int max) throws WrongNumberException{
        if(number > max){
            throw new WrongNumberException("Wrong Choice lah bang\n");
        }
    }
}