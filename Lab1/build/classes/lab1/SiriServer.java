import java.io.*;
import java.net.*;
import java.util.*;

public class SiriServer {

    private static final int PORT = 12345;
    private static final String KEY = "TMU";

    /* ---------- tiny Q/A database (keys exactly as users type) ---------- */
    private static final Map<String, String> ANSWERS = Map.of(
        "who created you",      "I was created by Apple.",
        "what does siri mean",  "victory and beautiful",
        "are you a robot",      "I am a virtual assistant."
    );

    /* ---------- Vigenère helpers (same as client) ---------- */
    private static String encrypt(String plain)  { return vig(plain, KEY, true);  }
    private static String decrypt(String cipher) { return vig(cipher, KEY, false); }

    private static String vig(String text, String key, boolean enc) {
        StringBuilder out = new StringBuilder();
        int keyLen = key.length();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c < 32 || c > 126) { out.append(c); continue; }
            char k = key.charAt(i % keyLen);
            int shift = k - 32;
            int base = 32, range = 95;
            int pos = c - base;
            if (enc) pos = (pos + shift) % range;
            else     pos = (pos - shift + range) % range;
            out.append((char)(base + pos));
        }
        return out.toString();
    }

    /* ---------- server main ---------- */
    public static void main(String[] args) {
        System.out.println("SiriServer listening on port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {               // one client at a time
                try (Socket client = serverSocket.accept();
                     BufferedReader in  = new BufferedReader(
                             new InputStreamReader(client.getInputStream()));
                     PrintWriter out = new PrintWriter(
                             client.getOutputStream(), true)) {

                    System.out.println("Client connected: " + client.getRemoteSocketAddress());

                    String cipherQ;
                    while ((cipherQ = in.readLine()) != null) {
                        System.out.println("Received ciphertext: " + cipherQ);
                        String plainQ = decrypt(cipherQ).trim();
                        System.out.println("Decrypted question  : " + plainQ);

                        /* ------ key matching: keep spaces, ignore case ------ */
                        String lookupKey = plainQ.toLowerCase(Locale.ROOT)
                                               .replaceAll("[^a-z0-9 ]", "") // spaces allowed
                                               .trim();
                        String plainA = ANSWERS.getOrDefault(lookupKey,
                                "Sorry, I don't understand the question.");
                        String cipherA = encrypt(plainA);
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