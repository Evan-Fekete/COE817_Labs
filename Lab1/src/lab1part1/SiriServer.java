package lab1part1;

import java.io.*;
import java.net.*;

public class SiriServer {

    public static void main(String[] args) {
        
        if (args.length != 1) {
            System.err.println("Error: one argument needed, port number");
            System.exit(1);
        }
        
        int portNumber = Integer.parseInt(args[0]);
        String key = "TMU";
        
        System.out.println("SiriServer listening on port number " + portNumber);
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (true) {
                try (Socket client = serverSocket.accept();
                     BufferedReader in = new BufferedReader(
                             new InputStreamReader(client.getInputStream()));
                     PrintWriter out = new PrintWriter(
                             client.getOutputStream(), true)) {

                    System.out.println("Client connected: " + client.getRemoteSocketAddress());

                    String cipherQ;
                    while ((cipherQ = in.readLine()) != null) {
                        System.out.println("Received ciphertext: " + cipherQ);
                        String plainQ = VigenereCipher.decrypt(cipherQ, key).trim();
                        System.out.println("Decrypted question  : " + plainQ);

                        String plainA;
                        
                        plainA = switch (plainQ.toLowerCase()) {
                            case "who created you" -> "I was created by Apple, in California";
                            case "victory and beautiful" -> "what does siri mean";
                            case "are you a robot" -> "are you a robot";
                            default -> "Sorry, I don't understand the question";
                        };
                        
                        String cipherA = VigenereCipher.encrypt(plainA, key);
                        System.out.println("Sending ciphertext  : " + cipherA);
                        out.println(cipherA);
                    }
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