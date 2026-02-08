package lab2part2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Base64;
import javax.crypto.*;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

/**
 *
 * @author evan_
 */
public class Bob {
    public static KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }
    
    public static String encrypt(String message, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedMessageBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedMessageBytes);
    }
    
    public static String decrypt(String encryptedMessage, PrivateKey privateKey) throws Exception {
        byte[] encryptedMessageBytes = Base64.getDecoder().decode(encryptedMessage);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedOriginalArray =  cipher.doFinal(encryptedMessageBytes);
        return new String(decryptedOriginalArray);
    }
    
    public static void main(String[] args) throws Exception {
        
        int portNumber;
        
        if (args.length == 0) {
            portNumber = 4444;
        }
        else {
            portNumber = Integer.parseInt(args[0]);
        }
        
        String message, alicePublicKeyString, encryptedMessage;
        String[] firstAliceArr;
        Random r = new Random();
        byte[] alicePublicKeyBytes;
        
        System.out.println("Bob listening on port number " + portNumber);
        try (ServerSocket bobSock = new ServerSocket(portNumber)) {
            String aliceInput;
            try (Socket aliceSock = bobSock.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(aliceSock.getInputStream()));
                    PrintWriter out = new PrintWriter(aliceSock.getOutputStream(), true)) {
                
                System.out.println("Alice connected: " + aliceSock.getRemoteSocketAddress() + "\n");
 
                // Generate RSA key pair
                KeyPair keyPair = generateRSAKeyPair();
                PublicKey publicKey = keyPair.getPublic();
                PrivateKey privateKey = keyPair.getPrivate();
                
                //**************************************************************
                // KEY EXCHANGE SECTION
                //**************************************************************
                
                alicePublicKeyString = in.readLine();
                alicePublicKeyBytes = Base64.getDecoder().decode(alicePublicKeyString);
                X509EncodedKeySpec ks = new X509EncodedKeySpec(alicePublicKeyBytes);
                KeyFactory kf = KeyFactory.getInstance("RSA");
                PublicKey alicePublicKey = kf.generatePublic(ks);
                
                byte[] publicKeyBytes = publicKey.getEncoded();
                String publicKeyString = Base64.getEncoder().encodeToString(publicKeyBytes);

                // System.out.println("Sending Public Key: " + publicKeyString);
                out.println(publicKeyString);
                
                // System.out.println("ALICE PUBLIC KEY : " + alicePublicKeyString);
                
                //**************************************************************
                // NONCE VERIFICATION SECTION
                //**************************************************************
                
                // Exchange completes wait for Alice to send first message Id||Na
                aliceInput = in.readLine();
                System.out.println("RECEIVED MESSAGE 1 => Alice sent: " + aliceInput + "\n");

                // Split Alice's message at || to seperate variables
                firstAliceArr = aliceInput.split(",");
                
                // Encrypt the second message E(PUa,E(PRb,Na))||Nb
                int nonceB = r.nextInt(1000);
                message = firstAliceArr[1];
                encryptedMessage = encrypt(message, alicePublicKey);
                message = encryptedMessage + "," + nonceB;
                
                // Send encrypted message
                System.out.println("Sending: " + message + "\n");
                out.println(message);
                
                // Wait for Alice's next message E(PUb,E(PRa,Nb))
                aliceInput = in.readLine();
                System.out.println("RECEIVED MESSAGE 3 => Alice sent: " + aliceInput);
                
                String decryptedString = decrypt(aliceInput, privateKey);
                
                System.out.println("DECRYPTED MESSAGE 3 => Decoded message: " + decryptedString + "\n");
                
                // Check if Nb received from Bob is correct
                if (decryptedString.equals(String.valueOf(nonceB))) {
                    System.out.println("Nonce has been verified. " + decryptedString + " = " + nonceB + "\n");
                }
                else {
                    System.out.println("Mismatched Nonce Exiting..." + decryptedString + " =/= " + nonceB + "\n");
                    System.exit(0);
                }

            } catch (IOException e) {
                System.err.println("Handler error: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
        
        System.out.println("Bob is finished...");
    }
    
}
