package lab2part2;

import java.io.*;
import javax.crypto.*;
import java.security.*;
import java.net.Socket;
import java.util.Base64;
import java.util.Random;
import java.security.spec.X509EncodedKeySpec;

/**
 *
 * @author evan_
 */
public class Alice {
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
        String hostName;
        
        if (args.length == 0) {
            portNumber = 4444;
            hostName = "localhost";
        }
        else {
            portNumber = Integer.parseInt(args[1]);
            hostName = args[0];
        }
        
        Random r = new Random();
        String id, message, bobPublicKeyString, bobInput, encryptedMessage;
        byte[] bobPublicKeyBytes;
        String[] bobArr;
        
        try (Socket aliceSock = new Socket(hostName, portNumber);
                BufferedReader in = new BufferedReader(new InputStreamReader(aliceSock.getInputStream()));
                PrintWriter out = new PrintWriter(aliceSock.getOutputStream(), true)) {    
            
            System.out.println("Connected to Bob: " + aliceSock.getRemoteSocketAddress() + "\n");

            // Define ID and generate nonce (0-999)
            id = "Alice";
            int nonceA = r.nextInt(1000);
            
            // Concatenate nonce to id
            message = id + "," + nonceA;
            
            // Generate RSA key pair
            KeyPair keyPair = generateRSAKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            
            //******************************************************************
            // KEY EXCHANGE SECTION
            //******************************************************************
            
            byte[] publicKeyBytes = publicKey.getEncoded();
            String publicKeyString = Base64.getEncoder().encodeToString(publicKeyBytes);
            
            // System.out.println("Sending Public Key: " + publicKeyString);
            out.println(publicKeyString);
            
            bobPublicKeyString = in.readLine();
            bobPublicKeyBytes = Base64.getDecoder().decode(bobPublicKeyString);
            X509EncodedKeySpec ks = new X509EncodedKeySpec(bobPublicKeyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey bobPublicKey = kf.generatePublic(ks);
            
            // System.out.println("BOB PUBLIC KEY : " + bobPublicKeyString);
            
            //**************************************************************
            // NONCE VERIFICATION SECTION
            //**************************************************************
                
            // Send first message to Bob (Id||Na)
            System.out.println("Sending: " + message + "\n");
            out.println(message);
            
            // Get Bob's response
            bobInput = in.readLine();
            System.out.println("RECEIVED MESSAGE 2 => Bob sent: " + bobInput);
            bobArr = bobInput.split(",");
            
            String decryptedString = decrypt(bobArr[0], privateKey);
            
            System.out.println("DECRYPTED MESSAGE 2 => Decoded message: " + decryptedString + "\n");
            
            // Check if Na received from Bob is correct
            if (decryptedString.equals(String.valueOf(nonceA))) {
                System.out.println("Nonce has been verified. " + decryptedString + " = " + nonceA + "\n");
            }
            else {
                System.out.println("Mismatched Nonce Exiting..." + decryptedString + " =/= " + nonceA + "\n");
                System.exit(0);
            }
            
            // Encrypt and send the third message E(PUb,E(PRa,Nb))
            encryptedMessage = encrypt(bobArr[1], bobPublicKey);
            
            System.out.println("Sending: " + encryptedMessage + "\n");
            out.println(encryptedMessage);
            
            System.out.println("Alice is finished...");
        }   
    }
}
