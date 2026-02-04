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
import java.util.Scanner;
import javax.crypto.SecretKey;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.SecretKeyFactory;

/**
 *
 * @author evan_
 */
public class Bob {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        int portNumber = Integer.parseInt(args[0]);
        
        if (args.length != 1) {
            System.err.println("Error: one argument needed, port number");
            System.exit(1);
        }
        
        System.out.println("SiriServer listening on port number " + portNumber);
        try (ServerSocket bobSock = new ServerSocket(portNumber)) {
            while (true) {
                try (Socket aliceSock = bobSock.accept();
                     BufferedReader in = new BufferedReader(
                             new InputStreamReader(aliceSock.getInputStream()));
                     PrintWriter out = new PrintWriter(
                             aliceSock.getOutputStream(), true)) {

                    System.out.println("Alice connected: " + aliceSock.getRemoteSocketAddress());

                    // Enter message for DES to encrypt

                    // Generate a DES Key
                    
                    // Create a cipher instance to be used for encryption and decryption
                    
                    // Initialize cipher for encryption
                    
                    // Encrypt the plaintext
                    
                    // Encode the encrypted bytes for readability
                    
                    // Initialize the cipher for decryption
                    
                    // Decrypt the ciphertext
                    
                    
                    System.out.println("Client disconnected.");
                } catch (IOException e) {
                    System.err.println("Handler error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
        
    }
    
}
