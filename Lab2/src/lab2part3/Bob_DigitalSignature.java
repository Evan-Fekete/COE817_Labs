package lab2part3;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.Base64;
import java.util.Random;

public class Bob_DigitalSignature {
    private static final int PORT = 8888;
    
    public static void main(String[] args) {
        try {
            // Start server
            System.out.println("=== Bob: Starting Server ===");
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Bob is waiting for Alice on port " + PORT + "...\n");
            
            // Accept connection from Alice
            Socket socket = serverSocket.accept();
            System.out.println("=== Bob: Alice Connected ===\n");
            
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            
            // Receive Alice's public key
            PublicKey alicePublicKey = (PublicKey) in.readObject();
            System.out.println("Received Alice's public key");
            System.out.println("Algorithm: " + alicePublicKey.getAlgorithm());
            System.out.println("Format: " + alicePublicKey.getFormat() + "\n");
            
            // Generate and send nonce to Alice
            String nonceBob = generateNonce();
            System.out.println("Generated Nonce: " + nonceBob);
            System.out.println("Purpose: This fresh nonce ensures the signature is unique to this session");
            out.writeObject(nonceBob);
            out.flush();
            System.out.println("Nonce sent to Alice\n");
            
            // Receive message, nonce, and signature from Alice
            String message = (String) in.readObject();
            String receivedNonce = (String) in.readObject();
            byte[] receivedSignature = (byte[]) in.readObject();
            
            System.out.println("Received Message: " + message);
            System.out.println("Received Nonce: " + receivedNonce);
            String signatureBase64 = Base64.getEncoder().encodeToString(receivedSignature);
            System.out.println("Received Signature (Base64): ");
            System.out.println(signatureBase64.substring(0, Math.min(80, signatureBase64.length())) + "...");
            System.out.println("Signature length: " + receivedSignature.length + " bytes\n");
            
            // Verify nonce matches
            System.out.println("=== Verifying Nonce (Replay Attack Check) ===");
            if (!nonceBob.equals(receivedNonce)) {
                System.out.println("ERROR: Nonce mismatch! Possible replay attack detected!");
                out.writeObject("VERIFICATION FAILED: Invalid nonce");
                out.flush();
                socket.close();
                serverSocket.close();
                return;
            }
            System.out.println("Nonce verified: " + receivedNonce);
            System.out.println("This confirms the message is fresh (not replayed)\n");
            
            // Step 5: Verify digital signature
            System.out.println("=== Verifying Digital Signature ===");
            String messageWithNonce = message + "||" + receivedNonce;
            System.out.println("Reconstructed message for verification: " + messageWithNonce);
            
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(alicePublicKey);
            signature.update(messageWithNonce.getBytes());
            boolean isVerified = signature.verify(receivedSignature);
            
            System.out.println("\n--- VERIFICATION RESULTS ---");
            if (isVerified) {
                System.out.println("Signature VERIFIED successfully!");
                System.out.println("Message authenticity confirmed (sender is Alice)");
                System.out.println("Message integrity confirmed (not modified)");
                System.out.println("Replay attack prevented (fresh nonce)");
                out.writeObject("VERIFICATION SUCCESS: Signature is valid and message is authentic");
            } else {
                System.out.println("Signature VERIFICATION FAILED!");
                System.out.println("Message may be tampered or from unauthorized sender");
                out.writeObject("VERIFICATION FAILED: Invalid signature");
            }
            out.flush();
            
            // Close connection
            in.close();
            out.close();
            socket.close();
            serverSocket.close();
            System.out.println("\n=== Bob: Server Closed ===");
            
        } catch (Exception e) {
            System.err.println("Bob Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static String generateNonce() {
        Random random = new Random();
        long nonce = Math.abs(random.nextLong());
        return String.valueOf(nonce);
    }
}
