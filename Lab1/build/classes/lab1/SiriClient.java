import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class SiriClient {

    /* ---------- Vigenère helpers (same key as server) ---------- */
    private static final String KEY = "TMU";

    private static String encrypt(String plain) {
        return vig(plain, KEY, true);
    }

    private static String decrypt(String cipher) {
        return vig(cipher, KEY, false);
    }

    private static String vig(String text, String key, boolean enc) {
        StringBuilder out = new StringBuilder();
        int keyLen = key.length();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c < 32 || c > 126) {          // keep non-printable
                out.append(c);
                continue;
            }
            char k = key.charAt(i % keyLen);
            int shift = k - 32;               // printable ASCII 32-126
            int base = 32;
            int range = 95;                   // 126-32+1
            int pos = c - base;
            if (enc) pos = (pos + shift) % range;
            else pos = (pos - shift + range) % range;
            out.append((char) (base + pos));
        }
        return out.toString();
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port = 12345;

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to Siri server at " + host + ":" + port);
            System.out.println("Type your question (empty line to quit):\n");

            while (true) {
                System.out.print("Q: ");
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) break;

                String cipherQ = encrypt(line);
                System.out.println("[Encrypted question: " + cipherQ + "]");
                out.println(cipherQ);          // send ciphertext

                String cipherA = in.readLine(); // wait for ciphertext answer
                if (cipherA == null) break;
                System.out.println("[Encrypted answer: " + cipherA + "]");

                String plainA = decrypt(cipherA);
                System.out.println("A: " + plainA + "\n");
            }
            System.out.println("Client shutting down.");
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}