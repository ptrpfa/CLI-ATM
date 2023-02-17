import picocli.CommandLine;
import picocli.CommandLine.Command;
// import picocli.CommandLine.ArgGroup;
// import picocli.CommandLine.Parameters;
// import picocli.CommandLine.Option;
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
        subcommands = {Login.class},
        description = "@|blue 你的黑钱 project |@",
        mixinStandardHelpOptions = true,
        version = "bank-teller 1.0"
        )
//End Command Header

public class Main implements Runnable {
    public void run() {
        //AnsiConsole.systemInstall();
        CommandLine cmd = new CommandLine(new Main());
        cmd.usage(System.out, Ansi.ON);
        //AnsiConsole.systemUninstall();
    }

    public static void main(String[] args) {

        CommandLine.run(new Main(), args);
    }

    //Set args to run
    //@Parameters(index = "0", description = "The file whose checksum to calculate.")
    //Set options
    //@Option(names = {"-l", "--login"}, description = "Login to bank account", required = false)boolean login;

    // prints banner every time the command is invoked
//
//    static class ClientArgs {
//        @Option(names = "-clientMode", required = false) boolean clientMode;
//        @Option(names = "-c", required = true) String c;
//        @Option(names = "-d", required = true) String d;
//    }
//
//    static class ServerArgs {
//        @Option(names = "-serverMode", required = true) boolean serverMode;
//        @Option(names = "-e", required = true) String e;
//        @Option(names = "-f", required = true) String f;
//    }
//
//    static class Args {
//        @ArgGroup(exclusive = false, multiplicity = "1", heading = "CLIENT mode args%n")
//        ClientArgs clientArgs;
//
//        @ArgGroup(exclusive = false, multiplicity = "1", heading = "SERVER mode args%n")
//        ServerArgs serverArgs;
//    }
//
//    @ArgGroup(exclusive = true, multiplicity = "1")
//    Args args;

}

