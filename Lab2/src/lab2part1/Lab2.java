/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package lab2part1;

import java.util.Scanner;
import javax.crypto.SecretKey;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.SecretKeyFactory;

/**
 *
 * @author evan_
 */
public class Lab2 {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        // Enter message for DES to encrypt
        System.out.println("Enter message: ");
        Scanner userMsg = new Scanner(System.in);
        
        //Generate a DES Key
        String K = "Key";
        DESKeySpec desKeySpec = new DESKeySpec(K.getBytes());
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey1 = secretKeyFactory.generateSecret(desKeySpec);
        
        
        
    }
    
}
