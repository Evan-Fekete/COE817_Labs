package lab2part3;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.Base64;

public class Alice_DigitalSignature {
    private static final int PORT = 8888;
    private static final String MESSAGE = "This is a secret message from Alice";
    
    public static void main(String[] args) {
        try {
            // Generate RSA key pair
            System.out.println("=== Alice: Generating RSA Key Pair ===");
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();
            System.out.println("Key pair generated successfully\n");
            
            // Connect to Bob
            System.out.println("=== Alice: Connecting to Bob ===");
            Socket socket = new Socket("localhost", PORT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connected to Bob\n");
            
            // Send public key to Bob
            out.writeObject(publicKey);
            out.flush();
            System.out.println("Public key sent\n");
            
            // Receive nonce from Bob
            String nonceBob = (String) in.readObject();
            System.out.println("Received Nonce from Bob: " + nonceBob);
            System.out.println("(This nonce prevents replay attacks)\n");
            
            // Create message with nonce and sign it
            String messageWithNonce = MESSAGE + "||" + nonceBob;
            System.out.println("Original Message: " + MESSAGE);
            System.out.println("Message with Nonce: " + messageWithNonce);
            
            // Sign the message with nonce
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(messageWithNonce.getBytes());
            byte[] digitalSignature = signature.sign();
            
            String signatureBase64 = Base64.getEncoder().encodeToString(digitalSignature);
            System.out.println("\nDigital Signature (Base64): ");
            System.out.println(signatureBase64.substring(0, Math.min(80, signatureBase64.length())) + "...");
            System.out.println("Signature length: " + digitalSignature.length + " bytes\n");
            
            // Send message, nonce, and signature to Bob
            out.writeObject(MESSAGE);
            out.writeObject(nonceBob);
            out.writeObject(digitalSignature);
            out.flush();
            System.out.println("Sent: Message + Nonce + Signature\n");
            
            // Receive verification result
            String verificationResult = (String) in.readObject();
            System.out.println("Verification Result from Bob: " + verificationResult);
            
            // Close connection
            in.close();
            out.close();
            socket.close();
            System.out.println("\n=== Alice: Connection Closed ===");
            
        } catch (Exception e) {
            System.err.println("Alice Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
