import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.concurrent.Callable;

import Account.Account;
import Server.SQLConnect;
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
public class Main implements Runnable, Menu{
    
    @Override
    public void run() {
        //Prints the default help Page
        //AnsiConsole.systemInstall();
        CommandLine cmd = new CommandLine(new Main());
        cmd.usage(System.out, Ansi.ON);
        //AnsiConsole.systemUninstall();
    }

    public static void main(String[] args) {
        final CommandLine commandLine = new CommandLine( new Main() );
        commandLine.execute( "Login"); //Change to args if you don't want do default
        CommandLine.ParseResult parseResult = commandLine.getParseResult(); //Get back the user Object returned
        for( CommandLine.ParseResult pr : parseResult.subcommands() )
        {
            System.out.println( pr.commandSpec().commandLine()
                    .getExecutionResult()
                    .toString() );
        }//This part have to change


    }

    @CommandLine.Command
    public String Login(){
        String[] banner = new CommandLine(new Main()).getCommandSpec().usageMessage().header();
        for (String line : banner) {
            //AnsiConsole.systemInstall();
            System.out.println(CommandLine.Help.Ansi.ON.string(line));
            //AnsiConsole.systemUninstall();
        }
        Scanner scanner = new Scanner(System.in);
        //ServerUser testUser = new ServerUser();
        //Console console = System.console();
        //do while loop to check login, condition user is null
        String user = null;
        int counter = 0;
        System.out.printf("欢迎来到大刘银行!\n");
        do{
            System.out.println("Enter username:");
            String username = scanner.nextLine();
            System.out.println("Enter password:");
            String password = scanner.nextLine();
            //Check user details, return user object
            //if(user != null)
            //return user
            counter++;
        }while(counter < 3);
        
        scanner.close();
        return user;
    }

    @Override
    public boolean CreateAccount(int userid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'CreateAccount'");
    }

    @Override
    public double Deposit(Account acc) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'Deposit'");
    }

    @Override
    public boolean Witdraw(Account acc) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'Witdraw'");
    }

    @Override
    public boolean TransferFunds(Account acc) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TransferFunds'");
    }

    @Override
    public void ViewTranscation(Account acc) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'ViewTranscation'");
    }


}