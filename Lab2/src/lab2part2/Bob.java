package lab2part2;

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
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.PrivateKey;

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
    
    public static byte[] encrypt(String message, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(message.getBytes());
    }
    
    public static String decrypt(byte[] cipherText, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedOriginalArray =  cipher.doFinal(cipherText);
        return new String(decryptedOriginalArray);
    }
    
    public static void main(String[] args) throws Exception {
        
        if (args.length != 1) {
            System.err.println("Error: one argument needed, port number");
            System.exit(1);
        }
        
        String message, id, alicePublicKeyString;
        String[] firstAliceArr, secondAliceArr;
        Random r = new Random();
        byte[] alicePublicKeyBytes, encryptedMessage;
        int portNumber = Integer.parseInt(args[0]);
        
        System.out.println("Bob listening on port number " + portNumber);
        try (ServerSocket bobSock = new ServerSocket(portNumber)) {
            String aliceInput;
            try (Socket aliceSock = bobSock.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(aliceSock.getInputStream()));
                    PrintWriter out = new PrintWriter(aliceSock.getOutputStream(), true)) {
                
                System.out.println("Alice connected: " + aliceSock.getRemoteSocketAddress());
 
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

                //**************************************************************
                // NONCE VERIFICATION SECTION
                //**************************************************************
                
                // System.out.println("Sending Public Key: " + publicKeyString);
                out.println(publicKeyString);
                
                // System.out.println("ALICE PUBLIC KEY : " + alicePublicKeyString);
                
                // Exchange completes wait for Alice to send first message Id||Na
                aliceInput = in.readLine();
                System.out.println("RECEIVED MESSAGE 1 => Alice sent: " + aliceInput);

                // Split Alice's message at || to seperate variables
                firstAliceArr = aliceInput.split(",");
                
                // Encrypt the second message E(PUa,E(PRb,Na))||Nb
                int nonceB = r.nextInt(1000);
                message = firstAliceArr[1];
                encryptedMessage = encrypt(message, alicePublicKey);
                String encryptedMessageBase64 = Base64.getEncoder().encodeToString(encryptedMessage);
                message = encryptedMessageBase64 + "," + nonceB;
                
                // Send encrypted message
                out.println(message);
                
                // Wait for Alice's next message E(PUb,E(PRa,Nb))
                aliceInput = in.readLine();
                System.out.println("RECEIVED MESSAGE 3 => Alice sent: " + aliceInput);
                
                byte[] aliceInputBytes = Base64.getDecoder().decode(aliceInput);
                String decryptedString = decrypt(aliceInputBytes, privateKey);
                
                System.out.println("DECRYPTED MESSAGE 3 => Decoded message: " + decryptedString);
                
                // Check if Nb received from Bob is correct
                if (decryptedString.equals(String.valueOf(nonceB))) {
                    System.out.println("___Nonce has been verified. " + decryptedString + " = " + nonceB);
                }
                else {
                    System.out.println("___Mismatched Nonce Exiting..." + decryptedString + " =/= " + nonceB);
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
