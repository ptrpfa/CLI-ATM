package Server;

// Imports
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.twilio.exception.AuthenticationException;
import com.twilio.exception.ApiException;
import java.util.Random;

public class SMS {

    // Function to send an SMS message to a specified number (Twilio trial account limited to sending SMS to verified phone numbers)
    public static boolean sendSMS(String receiverNo, String messageBody) {
        
        // Initialise message delivery boolean
        boolean delivered = false;
        
        // Read API keys from settings file
        String apiKeys[] = Settings.getTwilioAPIKeys();
        String senderNo = apiKeys[2];

        // Overwrite receiverNo to always be a pre-verified number specified in the configuration file (Proof-of-Concept only, Twilio only allows the sending of SMS to pre-verified numbers on the platform)
        receiverNo = apiKeys[3];
        
        try {
            // Initialise Twilio object
            Twilio.init(apiKeys[0], apiKeys[1]);

            // Create and send an SMS message
            Message message = Message.creator(new PhoneNumber(receiverNo), new PhoneNumber(senderNo), messageBody).create();

            // Check if message was successfully sent
            if(message.getErrorCode() == null) {
                delivered = true;
            }
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

    // Function to generate a numbers OTP of a specified length
    public static String generateOTP(int length) {
        // Create random object
        Random rand = new Random();
        
        // Initialise OTP
        String OTP = "";
        for(int i = 0; i < length; i++) {
            OTP += (char)(rand.nextInt(10) + '0');
        }
        // Return OTP
        return OTP;
    }

}