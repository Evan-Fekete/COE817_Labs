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
        
        int portNumber;
        String hostName;
        
        if (args.length == 0) {
            portNumber = 4444;
            hostName = "localhost";
        }
        else {
            portNumber = Integer.parseInt(args[1]);
            hostName = args[0];
        }
        
        // Key name must have at least 8 characters or 64 bits
        String key = "GreatKeyName", id, bobInput, message;
        String[] firstBobArr, secondBobArr;
        Random r = new Random();
        
        try (Socket aliceSock = new Socket(hostName, portNumber);
                BufferedReader in = new BufferedReader(new InputStreamReader(aliceSock.getInputStream()));
                PrintWriter out = new PrintWriter(aliceSock.getOutputStream(), true)) {    
            
            System.out.println("Connected to Bob: " + aliceSock.getRemoteSocketAddress() + "\n");
            
            // Define ID and generate nonce (0-999)
            id = "Alice";
            int nonce = r.nextInt(1000);
            
            // Concatenate nonce to id
            message = id + "," + nonce;
            
            // Send first message (IDa || Na)
            System.out.println("Sending: " + message);
            out.println(message);
            
            // Wait for Bob to send response (Nb||E(KAB,[IDb||NA]))
            bobInput = in.readLine();
            System.out.println("RECEIVED MESSAGE 2=> Received from Bob: " + bobInput);
            
            // Split Bob's response at commas
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
            System.out.println("DECRYPTED MESSAGE 2=> The decoded data is: " + decodedDataString + "\n");
            
            // Split Bob's encrypted message at commas
            secondBobArr = decodedDataString.split(",");
            
            // Check if Nb received from Bob is correct
            if (secondBobArr[1].equals(String.valueOf(nonce))) {
                System.out.println("Nonce has been verified." + "\n");
            }
            else {
                System.out.println("Mismatched Nonce Exiting..." + "\n");
                System.exit(0);
            }
            
            // Prepare final message for encryption 
            message = id + "," + firstBobArr[0];
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                    
            // Encrypt plaintext
            byte[] encryptedMessageBytes = cipher.doFinal(message.getBytes());
            String encryptedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);
            
            System.out.println("Sending: " + message + "\n");
            
            // Send final message to Bob (E(Kab,[IDa|Nb]))
            out.print(encryptedMessage);
            
            System.out.println("Alice is finished...");
        } catch(IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}
