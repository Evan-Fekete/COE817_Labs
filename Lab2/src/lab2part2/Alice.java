package lab2part2;

import java.io.*;
import java.net.Socket;
import java.util.Base64;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import java.io.*;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.PrivateKey;
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
        
        if (args.length != 2) {
            System.err.println("Error: two arguments needed, host and port number");
            System.exit(1);
        }

        Random r = new Random();
        String hostName = args[0], id, message, bobPublicKeyString, bobInput;
        byte[] bobPublicKeyBytes, encryptedMessage;
        String[] bobArr;
        int portNumber = Integer.parseInt(args[1]);
        
        try (Socket aliceSock = new Socket(hostName, portNumber);
                BufferedReader in = new BufferedReader(new InputStreamReader(aliceSock.getInputStream()));
                PrintWriter out = new PrintWriter(aliceSock.getOutputStream(), true)) {    
            
            // Concatenate nonce to id
            id = "Alice";
            int nonceA = r.nextInt(1000);
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
            out.println(message);
            
            // Wait for Bob's response
            bobInput = in.readLine();
            
            System.out.println("Bob sent: " + bobInput);
            
            // Split Alice's message at ||
            bobArr = bobInput.split(",");
            
            byte[] bobArrBytes = Base64.getDecoder().decode(bobArr[0]);
            String decryptedString = decrypt(bobArrBytes, privateKey);
            
            // System.out.println(decryptedString);
            
            // Check if Na received from Bob is correct
            if (decryptedString.equals(String.valueOf(nonceA))) {
                System.out.println("___Nonce has been verified. " + decryptedString + " = " + nonceA);
            }
            else {
                System.out.println("___Mismatched Nonce Exiting..." + decryptedString + " =/= " + nonceA);
                System.exit(0);
            }
            
            // Encrypt the third message E(PUb,E(PRa,Nb))
            encryptedMessage = encrypt(bobArr[1], bobPublicKey);
            String encryptedMessageBase64 = Base64.getEncoder().encodeToString(encryptedMessage);
            
            out.println(encryptedMessageBase64);
            
            System.out.println("Alice is finished...");
        }   
    }
}
