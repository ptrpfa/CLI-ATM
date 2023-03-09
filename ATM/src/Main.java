import java.util.Scanner;

import Server.SMS;
import Server.ServerUser;
import User.BusinessUser;
import User.NormalUser;
import User.User;
//import Server.ServerUser;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Ansi;
// import picocli.jansi.graalvm.AnsiConsole;

//Common Header Banner
@Command(name = "bank-teller", header = {
    "@|green .____    .__         ____  __.      .__  __________.__|@",
    "@|green |    |   |__|__ __  |    |/ _|____  |__| \\______   \\__| ____    ____  |@",
    "@|green |    |   |  |  |  \\ |      < \\__  \\ |  |  |     ___/  |/    \\  / ___\\ |@",
    "@|green |    |___|  |  |  / |    |  \\ / __ \\|  |  |    |   |  |   |  \\/ /_/  >|@",
    "@|green |_______ \\__|____/  |____|__ (____  /__|  |____|   |__|___|  /\\___  / |@",
    "@|green         \\/                  \\/    \\/                       \\//_____/  |@"
},
    description = "@|blue 你的黑钱 Project\n |@",
    mixinStandardHelpOptions = true,
    version = "bank-teller 1.0",
    optionListHeading = "Options are:%n",
    commandListHeading = "Commands are: %n"
)
//End Command Header
public class Main implements Runnable{
    
    @Override
    public void run() {
        //Prints the default help Page
        //AnsiConsole.systemInstall();
        CommandLine cmd = new CommandLine(new Main());
        cmd.usage(System.out, Ansi.ON);
        //AnsiConsole.systemUninstall();
    }

    public static void main(String[] args) throws InterruptedException {
        final CommandLine commandLine = new CommandLine( new Main() );
        commandLine.execute( "Login"); //Change to args if you want do default

        CommandLine.ParseResult parseResult = commandLine.getParseResult(); //Get back the user Object returned
        CommandLine.ParseResult pr = (parseResult.subcommands()).get(0);
        User user = pr.commandSpec().commandLine().getExecutionResult(); // Return user object from login
        if(user != null) {
            System.out.println("\nWelcome " + user.getUsername() + "\n");
            //Run the Menu
            BankMenu menu = new BankMenu(user);
            menu.run();
        }
        else {
            System.out.println("The bank will now self destruct in 3");
            Thread.sleep(1000);
            System.out.print("2\n");
            Thread.sleep(1000);
            System.out.print("1\n");
            Thread.sleep(1000);
            System.out.print("Boom");
            System.exit(-1);
        }
    }

    @CommandLine.Command
    public User Login(){
        String[] banner = new CommandLine(new Main()).getCommandSpec().usageMessage().header();

        for (String line : banner) {
            //AnsiConsole.systemInstall();
            System.out.println(CommandLine.Help.Ansi.ON.string(line));
            //AnsiConsole.systemUninstall();
        }

        Scanner scanner = new Scanner(System.in);
        ServerUser serverUser = new ServerUser();
        //Console console = System.console();
        //do while loop to check login, condition user is null
        int counter = 2;
        System.out.printf("欢迎来到大刘银行!\n");
        User user = null; 

        do{
            System.out.println("Enter username:");
            String username = scanner.nextLine();
            System.out.println("Enter password:");
            String password = scanner.nextLine();
        
            user = serverUser.checkUser(username, password); 
            if(user != null){
                //OTP
                //SMS.sendSMS("+6585933198", "hello");
                if (user instanceof NormalUser) {
                    NormalUser newUser = (NormalUser) user;
                    return newUser;
                }
                else if (user instanceof BusinessUser) {
                    BusinessUser newUser = (BusinessUser) user;
                    return newUser;
                }
            }else { //No User
                System.out.println("Invalid login. " + counter + " tries left.\n");
            }
            //return user
            counter--;
        } while(counter >= 0);
        
        scanner.close();
        return user;
    }


}