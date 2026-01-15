/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package lab1;

import java.net.*;
import java.io.*;

/**
 *
 * @author evan_
 */
public class Client {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Error: two arguments needed, host and port number");
            System.exit(1);
        }
        
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        
        try (
            Socket cliSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(cliSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(cliSocket.getInputStream()));
        ) {
            // TODO add logic for sending and receiving from Server
            // Evan added this comment on github
        } catch (IOException e) {
            System.err.println("IO Connection not found for " + hostName);
            System.exit(1);
        }
    }
    
}

