package lab1part2;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class SiriClient {
    public static void main(String[] args) {
        
        if (args.length != 2) {
            System.err.println("Error: two arguments needed, host and port number");
            System.exit(1);
        }
        
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        String key = "TMU";
        
        try (Socket socket = new Socket(hostName, portNumber);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to SiriServer at " + hostName + ":" + portNumber);
            System.out.println("Type your question (\"Bye\" to quit):\n");

            while (true) {
                System.out.print("Q: ");
                String line = scanner.nextLine();
                
                if (line.trim().equals("Bye")) break;

                String cipherQ = VigenereCipher.encrypt(line, key);
                System.out.println("[Encrypted question: " + cipherQ + "]");
                out.println(cipherQ);

                String cipherA = in.readLine();
                if (cipherA == null) break;
                System.out.println("[Encrypted answer: " + cipherA + "]");

                String plainA = VigenereCipher.decrypt(cipherA, key);
                System.out.println("A: " + plainA + "\n");
            }
            System.out.println("Client shutting down.");
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}