package lab1part2;

public final class VigenereCipher {

    // private VigenereCipher() { }

    /** Helper function used to encode each character for the encrypt function
     * 
     * args:
     * c : character being decoded
     * keyword : key for Vignere Cipher
     * keyPostion : current position, which determines how the char is encoded
     * 
     * returns:
     * dec : decoded char
    */
    public static char encode(char c, String keyword, int keyPosition) {
        // System.out.println(c +" " + keyword.charAt(keyPosition % keyword.length()) + " " + keyword.length());
        
        if (Character.isUpperCase(c)) {
            int shift = Character.toUpperCase(keyword.charAt(keyPosition % keyword.length())) - 'A';
            char enc = (char) (((c - 'A') + shift) % 26 + 'A');
            return enc;
        }
        else if (Character.isLowerCase(c)) {
            int shift = Character.toUpperCase(keyword.charAt(keyPosition % keyword.length())) - 'A';
            char enc = (char) (((c - 'a') + shift) % 26 + 'a');
            return enc;
        }
        else return c;
    }
    
    /** Helper function used to decode each character for the decrypt function
     * 
     * args:
     * c : character being decoded
     * keyword : key for Vignere Cipher
     * keyPostion : current position, which determines how the char is encoded
     * 
     * returns:
     * dec : decoded char
    */
    public static char decode(char c, String keyword, int keyPosition) {
        if (Character.isUpperCase(c)) {
            int shift = Character.toUpperCase(keyword.charAt(keyPosition % keyword.length())) - 'A';
            char dec = (char) (((c - 'A') - shift + 26) % 26 + 'A');
            return dec;
        }
        else if (Character.isLowerCase(c)) {
            int shift = Character.toUpperCase(keyword.charAt(keyPosition % keyword.length())) - 'A';
            char dec = (char) (((c - 'a') - shift + 26) % 26 + 'a');
            return dec;
        }
        else return c;
    }
    
    /** Encrypts a string using the Vigenere Cipher method and returns the encrypted string
     * 
     * args: 
     *  plaintext : String being encrypted
     *  keyword : String used as the key for Vignere Cipher
     * 
     * returns:
     *  encrypted : plaintext string but encrypted
     */
    public static String encrypt(String plaintext, String keyword) {
        StringBuilder encrypted = new StringBuilder();
        char enc;
        
        for (int i = 0, j = 0; i < plaintext.length(); i++) {
            char c = plaintext.charAt(i);

            enc = encode(c, keyword, j);
            
            if (Character.isLowerCase(c) || Character.isUpperCase(c)) {
                j++;
            }
            else;
            
            encrypted.append(enc);
        }
        return encrypted.toString();
    }

    /** Decrypts a string using the Vigenere Cipher method and returns the Decrypted string
     * 
     * args: 
     *  ciphertext : String being decrypted
     *  keyword : String used as the key for Vignere Cipher
     * 
     * returns:
     *  decrypted : plaintext string but encrypted
     */
    public static String decrypt(String ciphertext, String keyword) {
        StringBuilder decrypted = new StringBuilder();
        char dec;

        for (int i = 0, j = 0; i < ciphertext.length(); i++) {
            char c = ciphertext.charAt(i);

            dec = decode(c, keyword, j);
            
            if (Character.isLowerCase(c) || Character.isUpperCase(c)) {
                j++;
            }
            else;
            
            decrypted.append(dec);
        }
        return decrypted.toString();
    }
}