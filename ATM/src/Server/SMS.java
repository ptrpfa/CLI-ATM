package Server;

// Imports
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

// Class for sending SMS messages to users
public class SMS {

    // Program entrypoint (for testing)
    public static void main(String[] args) {

        // Read API keys from settings file
        String apiKeys[] = Settings.getTwilioAPIKeys();

        /* Twilio Setup */
        Twilio.init(apiKeys[0], apiKeys[1]);

        Message message = Message.creator(new PhoneNumber("+6585933198"),  // To number
                                            new PhoneNumber("+15076783139"),  // From number
            "hello hello"                    // SMS body
        ).create();

        System.out.println(message.getSid());

        System.out.println("hello");

    }

}