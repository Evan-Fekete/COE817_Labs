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
        
        // Key name must have at least 8 characters or 64 bits
        String id, bobInput, message;
        String[] firstBobArr, secondBobArr;
        Random r = new Random();
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        
        try (Socket aliceSock = new Socket(hostName, portNumber);
                BufferedReader in = new BufferedReader(new InputStreamReader(aliceSock.getInputStream()));
                PrintWriter out = new PrintWriter(aliceSock.getOutputStream(), true)) {    
            
            
    }
}
