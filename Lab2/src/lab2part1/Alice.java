package lab2part1;

import java.io.*;
import java.net.Socket;
import java.util.Base64;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 *
 * @author evan_
 */
public class Alice {
    public static void main(String[] args) throws Exception {
        
        if (args.length != 2) {
            System.err.println("Error: two arguments needed, host and port number");
            System.exit(1);
        }
        
        // Key name must have at least 8 characters or 64 bits
        String key = "GreatKeyName", id, bobInput, message;
        String[] firstBobArr, secondBobArr;
        Random r = new Random();
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        
        try (Socket aliceSock = new Socket(hostName, portNumber);
                BufferedReader in = new BufferedReader(new InputStreamReader(aliceSock.getInputStream()));
                PrintWriter out = new PrintWriter(aliceSock.getOutputStream(), true)) {    
            
            // Concatenate nonce to id
            id = "Alice";
            int nonce = r.nextInt(1000);
            message = id + "," + nonce;
            
            // Send first message (IDa || Na)
            System.out.println("Sending: " + message);
            out.println(message);
            
            // Wait for Bob to send response (Nb||E(KAB,[IDb||NA]))
            bobInput = in.readLine();
            System.out.println("Received: " + bobInput);
            
            // Split Bob's response at commas
            // First half of response is Nb
            // Second half of response is E(Kab, [IDb || Na])
            firstBobArr = bobInput.split(",");
            
            // Generate a DES Key
            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = secretKeyFactory.generateSecret(desKeySpec);
            
            // Create a cipher instance
            Cipher cipher = Cipher.getInstance("DES");
            
            // Initialize the cipher for decryption
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            
            // Decrypt the ciphertext
            byte[] decodedData = cipher.doFinal(Base64.getDecoder().decode(firstBobArr[1]));
            String decodedDataString = new String(decodedData);
            System.out.println("The decoded data is: " + decodedDataString);
            
            // Split Bob's encrypted message at commas
            // First half of response is IDb
            // Second half of response is Na
            secondBobArr = decodedDataString.split(",");
            
            // Check if Nb received from Bob is correct
            if (secondBobArr[1].equals(String.valueOf(nonce))) {
                System.out.println("Nonce has been verified. ");
            }
            else {
                System.out.println("Mismatched Nonce Exiting...");
                System.exit(0);
            }
            
            //
            message = id + "," + firstBobArr[0];
            
            // Initialize cipher for encryption
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                    
            // Encrypt plaintext
            byte[] cipherTextArray = cipher.doFinal(message.getBytes());
            String cipherTextString = Base64.getEncoder().encodeToString(cipherTextArray);
            
            System.out.println("Sending: " + message);
            
            // Send final message to Bob (E(Kab,[IDa|Nb]))
            out.print(cipherTextString);
            
            System.out.println("Alice disconnected.");
        } catch(IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}
