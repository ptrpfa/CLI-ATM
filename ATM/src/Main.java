
import java.io.Console;
import java.text.ParseException;
import java.util.Arrays;

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
        CommandLine cmd = new CommandLine(this);
        cmd.usage(System.out, Ansi.ON);
        //AnsiConsole.systemUninstall();
    }

    // Program entrypoint
    public static void main(String[] args) throws InterruptedException {        
        System.setProperty("picocli.ansi", "false");
        final CommandLine commandLine = new CommandLine( new Main() );
        try{
            commandLine.parseArgs(args);
        }catch(CommandLine.UnmatchedArgumentException ex){
            String errLine = "Invalid command or argument: " + ex.getCommandLine().getUnmatchedArguments().get(0);
            System.err.println(CommandLine.Help.Ansi.ON.string("@|red " + errLine + "|@"));
            commandLine.usage(System.err, CommandLine.Help.Ansi.ON);
            System.exit(1);
        }

        CommandLine.ParseResult parseResult = commandLine.getParseResult(); 
        if (!parseResult.hasSubcommand()) {
            new Main().run();
        }else{
            commandLine.execute(args);
            CommandLine.ParseResult subcommand = parseResult.subcommand();
            if(subcommand != null && (subcommand.commandSpec().name().equals("Login") || Arrays.asList(subcommand.commandSpec().aliases()).contains("login"))) {
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

    }

    @CommandLine.Command(name = "Login", aliases = {"login", "init"}, description = "@|yellow Logs in User to the system. |@")
    public User Login() { // Login option
        String[] banner = new CommandLine(new Main()).getCommandSpec().usageMessage().header();

        for (String line : banner) {
            //AnsiConsole.systemInstall();
            System.out.println(CommandLine.Help.Ansi.ON.string(line));
            //AnsiConsole.systemUninstall();
        }

        ServerUser serverUser = new ServerUser();
        
        //do while loop to check login, condition user is null
        int counter = 2;
        System.out.println(CommandLine.Help.Ansi.ON.string("@|51 \nPlease login to access our bank services 🏦\n|@"));
        User user = null; 
        Console console = System.console();
    
        do{
            String username = console.readLine("Enter username: ");
            char[] passwordChars = console.readPassword("Enter password (Hidden for Security): ");
            String password = new String(passwordChars);
        
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

        return user;
    }

    @CommandLine.Command(name = "Registration", aliases = {"register", "new", "signup"}, description = "@|yellow Register new User to the system. |@")
    public void Registration() throws ParseException { // User Registration option
        ServerUser.registerUser();
        System.out.println("Success creation of user. Please Login below!\n");
        Login();
    }
}
