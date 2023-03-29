package test;
//import picocli.CommandLine;
//import pl.mjaron.etudes.Str;
//import pl.mjaron.etudes.Table;
//
////import java.io.Console;
//import java.util.Scanner;
//import Server.ServerAccount;
//import Server.ServerUser;
//import Account.Account;
//import User.User;
//import User.NormalUser;
//import User.BusinessUser;
//import java.util.List;
//
//@CommandLine.Command(name = "login")
//class Login implements Runnable {
//    @Override
//    public void run() {
//        String[] banner = new CommandLine(new Main()).getCommandSpec().usageMessage().header();
//        for (String line : banner) {
//            //AnsiConsole.systemInstall();
//            System.out.println(CommandLine.Help.Ansi.ON.string(line));
//            //AnsiConsole.systemUninstall();
//        }
//        //main();
//    }
//
//    public static void main(String[] args){
//        Scanner scanner = new Scanner(System.in);
//        ServerUser testUser = new ServerUser();
//        //Console console = System.console();
//        //do while loop to check login, condition user is null
//        System.out.println("Welcome to 🐱LKP Bank!");
//        System.out.println("Enter username:");
//        String username = scanner.nextLine();
//        System.out.println("Enter password:");
//        String password = scanner.nextLine();
//
//        // int[] store = testUser.checkUser(username, password/*"Na0m1_N30", "0nlyf4ns"*/);
//
//        // if(store[1] == 1){
//        //     NormalUser user = testUser.findNormalUser(store[0]);
//
//        //     testUser.updateNormalUser(user);
//
//        //     System.out.println("\nGood day Mr/Ms " + user.getLastName() + ", " + user.getFirstName());
//        //     System.out.println("You have been a member since " + user.getRegistrationDate());
//        //     System.out.println("Your birthday is coming soon! At: " + user.getBirthday());
//        // }
//
//        // //password = AES256.encrypt(password);
//        // //String password = new String(console.readPassword("\n请输入密码："));
//        // System.out.println(password);
//        // System.out.println("\n请稍�?...正搜查您的账本~");
//
//        scanner.close();
//        // ServerAccount Serveracc = new ServerAccount();
//        // List<Account> accounts = Serveracc.findUserAccounts(user.getUserID());
//        // Account.displayHeader();
//        // for (Account acc : accounts) {
//        //     acc.display();
//        // }
//    }
//}