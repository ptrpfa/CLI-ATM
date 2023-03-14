import java.text.ParseException;
import java.util.Scanner;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Ansi;
import Server.ServerUser;
import User.BusinessUser;
import User.NormalUser;
import User.User;

// import picocli.jansi.graalvm.AnsiConsole;

//Common Header Banner
@Command(name = "bank-teller", header = {
    "@|red \n██████╗  █████╗ ███╗   ██╗██╗  ██╗     █████╗ ████████╗███╗   ███╗ |@",
    "@|208 ██╔══██╗██╔══██╗████╗  ██║██║ ██╔╝    ██╔══██╗╚══██╔══╝████╗ ████║ |@",
    "@|220 ██████╔╝███████║██╔██╗ ██║█████╔╝     ███████║   ██║   ██╔████╔██║ |@",
    "@|green ██╔══██╗██╔══██║██║╚██╗██║██╔═██╗     ██╔══██║   ██║   ██║╚██╔╝██║ |@",
    "@|blue ██████╔╝██║  ██║██║ ╚████║██║  ██╗    ██║  ██║   ██║   ██║ ╚═╝ ██║ |@",
    "@|165 ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═══╝╚═╝  ╚═╝    ╚═╝  ╚═╝   ╚═╝   ╚═╝     ╚═╝ |@"

},
    description = "@|blue OOP ATM Project\n |@",
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

    // Program entrypoint
    public static void main(String[] args) throws InterruptedException {        
        System.setProperty("picocli.ansi", "false");
        final CommandLine commandLine = new CommandLine( new Main() );
        commandLine.execute(args); //Change to args if you want do default

        CommandLine.ParseResult parseResult = commandLine.getParseResult(); //Get back the user Object returned
        CommandLine.ParseResult subcommand = parseResult.subcommand();
        if(subcommand !=null && subcommand.commandSpec().name() == "Login"){
            CommandLine.ParseResult pr = (parseResult.subcommands()).get(0);
            User user = pr.commandSpec().commandLine().getExecutionResult(); // Return user object from login
            if(user != null) {
                System.out.println(CommandLine.Help.Ansi.ON.string("@|51 \nWelcome " + user.getUsername() + ",\n|@"));
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
    }

    @CommandLine.Command
    public User Login() { // Login option
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
        System.out.println(CommandLine.Help.Ansi.ON.string("@|51 \nPlease login to access our bank services 🏦\n|@"));
        User user = null; 

        do{
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
        
            user = serverUser.checkUser(username, password); 
            if(user != null){
                if (user instanceof NormalUser) {
                    NormalUser newUser = (NormalUser) user;
                    return newUser;
                }
                else if (user instanceof BusinessUser) {
                    BusinessUser newUser = (BusinessUser) user;
                    return newUser;
                }
            }else { //No User
                System.out.println(CommandLine.Help.Ansi.ON.string("@|208 Invalid login. " + counter + " tries left.\n|@"));
            }
            //return user
            counter--;
        } while(counter >= 0);
        
        scanner.close();
        return user;
    }

    @CommandLine.Command
    public void Registration() throws ParseException { // User Registration option
        ServerUser.registerUser();
        System.out.println("Success creation of user. Please Login below!\n");
        Login();
    }
}