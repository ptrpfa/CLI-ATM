import picocli.CommandLine;
//import java.io.Console;
import java.util.Scanner;

@CommandLine.Command(name = "login")
class Login implements Runnable {
    @Override
    public void run() {
        String[] banner = new CommandLine(new Main()).getCommandSpec().usageMessage().header();
        for (String line : banner) {
            //AnsiConsole.systemInstall();
            System.out.println(CommandLine.Help.Ansi.ON.string(line));
            //AnsiConsole.systemUninstall();
        }
        main();
    }
    public static void main(){
        Scanner scanner = new Scanner(System.in);
        //Console console = System.console();
        //do while loop to check login, condition user is null
        System.out.println("欢迎来到本次银行!");
        System.out.println("请输入ID:");
        int userID = scanner.nextInt();
        System.out.println("\n请输入密码:");
        //User.checkUser(userID,password);
        String password = scanner.nextLine();
        //password = AES256.encrypt(password);
        //String password = new String(console.readPassword("\n请输入密码："));
        System.out.println(password);
        System.out.println("\n请稍等...正搜查您的账本~");
        scanner.close();
    }
}