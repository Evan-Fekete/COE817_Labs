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

import java.security.KeyPair;
import java.security.KeyPairGenerator;

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
    
    public static void main(String[] args) throws Exception {
        
        if (args.length != 1) {
            System.err.println("Error: one argument needed, port number");
            System.exit(1);
        }
        
        String message, id;
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

                // Split Alice's message at commas to seperate variables
                firstAliceArr = aliceInput.split(",");

            } catch (IOException e) {
                System.err.println("Handler error: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
        
    }
    
}
