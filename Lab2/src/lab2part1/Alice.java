/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab2part1;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author evan_
 */
public class Alice {
    public static void main(String[] args) {
        
        if (args.length != 2) {
            System.err.println("Error: two arguments needed, host and port number");
            System.exit(1);
        }
        
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        
        try (Socket bobSock = new Socket(hostName, portNumber);
            BufferedReader in = new BufferedReader(new InputStreamReader(bobSock.getInputStream()));
            PrintWriter out = new PrintWriter(bobSock.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {
                
        } catch(IOException e) {
                System.err.println("Client error: " + e.getMessage());
        }
    }
}
