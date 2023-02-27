import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Timer;
import java.util.concurrent.Callable;

import Account.Account;
import Server.SQLConnect;
import Server.ServerUser;
import User.BusinessUser;
import User.NormalUser;
import User.User;
//import Server.ServerUser;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Ansi;
// import picocli.jansi.graalvm.AnsiConsole;
import picocli.CommandLine.Help.TextTable.Cell;

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
        // for( CommandLine.ParseResult pr : parseResult.subcommands() )
        // {
        User user = pr.commandSpec().commandLine().getExecutionResult(); // Return user object from login
        if(user != null) {
            System.out.println("Welcome " + user.getUsername() + "\n");
            Menu menu = new Menu(user);
            menu.run();
        }
        else {
            System.out.println("The bank will now self destruct");
            Thread.sleep(5000);
            System.out.println("Boom");
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
        int counter = 0;
        System.out.printf("欢迎来到大刘银行!\n");
        User testUser = null; 

        do{
            System.out.println("Enter username:");
            String username = scanner.nextLine();
            System.out.println("Enter password:");
            String password = scanner.nextLine();
            //Check user details, return user object
            //if(user != null)
        
            testUser = serverUser.checkUser(username, password); 
            
            if (testUser instanceof NormalUser) {
                NormalUser user = (NormalUser) testUser;
                
                return user;
            }
            else if (testUser instanceof BusinessUser) {
                BusinessUser user = (BusinessUser) testUser;
                
                return user;
            }
            else {
                System.out.println("Invalid login try again buto");
            }
            //return user
            counter++;
        } while(counter < 1);
        
        scanner.close();
        return testUser;
    }


}