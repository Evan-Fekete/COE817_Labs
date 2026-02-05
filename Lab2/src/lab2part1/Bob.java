/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package lab2part1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.SecretKeyFactory;
import java.util.Base64;

/**
 *
 * @author evan_
 */
public class Bob {
    public static void main(String[] args) throws Exception {
        
        if (args.length != 1) {
            System.err.println("Error: one argument needed, port number");
            System.exit(1);
        }
        
        // Key name must have at least 8 characters or 64 bits
        String secretKey = "GreatKeyName", message, id;
        String[] firstAliceArr, secondAliceArr;
        Random r = new Random();
        int portNumber = Integer.parseInt(args[0]);
        
        System.out.println("Bob listening on port number " + portNumber);
        try (ServerSocket bobSock = new ServerSocket(portNumber)) {
            String aliceInput;

            try (Socket aliceSock = bobSock.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(aliceSock.getInputStream()));
                    PrintWriter out = new PrintWriter(aliceSock.getOutputStream(), true)) {

                System.out.println("Alice connected: " + aliceSock.getRemoteSocketAddress());

                // Once connection is established wait for Alice to send first message
                aliceInput = in.readLine();
                System.out.println("Alice sent: " + aliceInput);

                //  Split Alice's message at commas to seperate variables
                firstAliceArr = aliceInput.split(",");

                // Create message for DES to encrypt (IDb || Na)
                id = "Bob";
                int nonce = r.nextInt(1000);
                message = id + "," + firstAliceArr[1];

                System.out.println("Encrypting: " + message);

                // Generate a DES Key
                DESKeySpec desKeySpec = new DESKeySpec(secretKey.getBytes());
                SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DES");
                SecretKey secretKey1 = secretKeyFactory.generateSecret(desKeySpec);

                // Create a cipher instance to be used for encryption and decryption
                Cipher cipher = Cipher.getInstance("DES");

                // Initialize cipher for encryption
                cipher.init(Cipher.ENCRYPT_MODE, secretKey1);

                // Encrypt plaintext
                byte[] cipherTextArray = cipher.doFinal(message.getBytes());

                // Encode encrypted bits to properly display encrypted plaintext
                String cipherTextString = Base64.getEncoder().encodeToString(cipherTextArray);
                System.out.println("Cipher text of Bob Message is " + cipherTextString);

                // Send message with Nonce to Alice (Nb||E(KAB,[IDb||NA]))
                message = nonce + "," + cipherTextString;
                out.println(message);
                aliceInput = in.readLine();

                // Initialize the cipher for decryption
                cipher.init(Cipher.DECRYPT_MODE, secretKey1);
                byte[] decodedData = cipher.doFinal(Base64.getDecoder().decode(aliceInput));
                String decodedDataString = new String(decodedData);

                // Decrypt the ciphertext
                System.out.println("The decoded data is: " + decodedDataString);
                secondAliceArr = decodedDataString.split(",");

                if (secondAliceArr[1].trim().equals(String.valueOf(nonce))) {
                    System.out.println("Nonce has been verified. ");
                }
                else {
                    System.out.println("Mismatched Nonce Exiting...");
                    System.exit(0);
                }

                System.out.println("Bob disconnected.");
            } catch (IOException e) {
                System.err.println("Handler error: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
        
    }
    
}
