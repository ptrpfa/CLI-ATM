//import java.util.Scanner;
//
//import Account.Account;
//import Server.ServerAccount;
//
//public class Menu {
//    public static Scanner scanner = new Scanner(System.in);
//
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
//
//                        break;
//                    case 2:
//                        Deposit();
//                        break;
//                    case 3: Witdraw(); break;
//                }
//            }
//            catch (Exception ex){
//                System.out.println("Please enter an integer value between 1 and " + options.length);
//                scanner.next();
//            }
//        }while(option != 6);
//    }
//
//    private void CreateAccount(int userid){
//        System.out.print("Name of Accout:");
//        String accName = scanner.nextLine();
//        System.out.println("Any Descripiton (Default: NIL):");
//        String accDescription = scanner.nextLine();
//        Account acc = new Account(userid, accName, accDescription);
//        ServerAccount accServer = new ServerAccount();
//        accServer.NewAccount(acc);
//    }
//
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
//}
