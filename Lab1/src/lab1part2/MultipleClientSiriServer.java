package lab1part2;

import java.io.*;
import java.net.*;
import java.net.Socket;

class ClientHandler extends Thread {
    private Socket clientSocket;
    
    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }
    
    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
            
            while (true) {
                String key = "TMU";
                String cipherQ;
                while ((cipherQ = in.readLine()) != null) {
                    System.out.println("Receiving from Client Address: "+ clientSocket.getInetAddress());
                    System.out.println("Received ciphertext from: " + " " + cipherQ);
                    String plainQ = VigenereCipher.decrypt(cipherQ, key).trim();
                    System.out.println("Decrypted question  : " + plainQ);

                    String plainA;

                    plainA = switch (plainQ.toLowerCase()) {
                        case "who created you?" -> "I was created by Apple";
                        case "what does siri mean?" -> "victory and beautiful";
                        case "are you a robot?" -> "I am a virtual assistant";
                        case "what is love?" -> "baby don't hurt me";
                        default -> "Sorry, I don't understand the question";
                    };

                    String cipherA = VigenereCipher.encrypt(plainA, key);
                    System.out.println("Sending ciphertext  : " + cipherA);
                    out.println(cipherA);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class MultipleClientSiriServer{
    
    public static void main(String[] args) {
        
        int portNumber = Integer.parseInt(args[0]);
        
        System.out.println("SiriServer listening on port number " + portNumber);
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("\nClient connected: " + client.getRemoteSocketAddress() + "\n");
                new ClientHandler(client).start();
            }
        }
        catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}