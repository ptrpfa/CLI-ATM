package Server;

// Imports
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.twilio.exception.AuthenticationException;
import com.twilio.exception.ApiException;


public class SMS {

    // Function to send an SMS message to a specified number (Twilio trial account limited to sending SMS to verified phone numbers)
    public static boolean sendSMS(String receiverNo, String messageBody) {
        
        // Initialise message delivery boolean
        boolean delivered = true; // set to true for now
        
        // Read API keys from settings file
        String apiKeys[] = Settings.getTwilioAPIKeys();
        String senderNo = apiKeys[2];

        try {
            // Initialise Twilio object
            Twilio.init(apiKeys[0], apiKeys[1]);

            // Create and send an SMS message
            Message message = Message.creator(new PhoneNumber(receiverNo), new PhoneNumber(senderNo), messageBody).create();
            String messageSID = message.getSid();
            System.out.println(messageSID);

            // Send HTTP request and get response to check message delivery status
            // something here...
            ;;;

        }
        catch(AuthenticationException e) {
            System.out.println("Twilio authentication error! " + e);
        }
        catch(ApiException e) {
            System.out.println("Twilio API error! " + e);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        // Return message delivery status to caller
        return delivered;
    }


    // Program entrypoint (for testing)
    public static void main(String[] args) {
        sendSMS("+6585933198", "hi");
        System.out.println("hello");

    }

}