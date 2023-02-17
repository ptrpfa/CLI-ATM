package Test;
import picocli.CommandLine.Help.Ansi;
//import org.fusesource.jansi.AnsiConsole; //Either this or the picocli one
import picocli.jansi.graalvm.AnsiConsole;

public class header {
    //can change this to any ASCII ART
    //ASCII Art Generator: https://patorjk.com/software/taag/#p=display&f=Graffiti&t=
    public static final String[] header = {
            "@|green .____    .__         ____  __.      .__  __________.__|@",
            "@|green |    |   |__|__ __  |    |/ _|____  |__| \\______   \\__| ____    ____  |@",
            "@|green |    |   |  |  |  \\ |      < \\__  \\ |  |  |     ___/  |/    \\  / ___\\ |@",
            "@|green |    |___|  |  |  / |    |  \\ / __ \\|  |  |    |   |  |   |  \\/ /_/  >|@",
            "@|green |_______ \\__|____/  |____|__ (____  /__|  |____|   |__|___|  /\\___  / |@",
            "@|green         \\/                  \\/    \\/                       \\//_____/  |@",
    };
    public static void Print(){
        //Install ANSI command for Windows
        AnsiConsole.systemInstall();
        for(String x:header){
            System.out.println(Ansi.ON.string(x));
        }
        //Clean Up ANSI command
        AnsiConsole.systemUninstall();
    }

}
