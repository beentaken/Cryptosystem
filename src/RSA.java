import java.math.BigInteger;

/**
 * A class that handling the RSA algorithm, including encrypting message and 
 * decrypting a cipher text. The RSA algorithm will contain a set of public key, 
 * (n, e), and a set of private key, p, q, and d.
 * 
 * @author Su Khai Koh
 */

public class RSA implements Algorithm {

    private BigInteger p;   // prime number
    private BigInteger q;   // prime number
    private BigInteger n;   // p*q
    private BigInteger m;   // (p-1)(q-1)
    private BigInteger e;   // public key
    private BigInteger d;   // inverse of e mod (p-1)(q-1)

    private BigInteger maxValue;

    /**
     * Default constructor. Set the maximum value to certain keys and generate
     * all public and private keys.
     */
    public RSA() {
        
        maxValue = new BigInteger("10000");
        
        generateAllKeys();
    }
    
    /**
     * Generate all private and public keys.
     */
    public void generateAllKeys() {
        
        generateP();
        generateQ();
        generateN();
        generateM();
        generateE();
        generateD();
    }
    
    /**
     * Generate the private key, p, where p is a prime number ranged from 1
     * to the maximum value.
     */
    public void generateP() {
        p = Prime.getPrime(1, maxValue.longValue());
    }
    
    /**
     * Generate the private key, q, where q is a prime number ranged from 1
     * to the maximum value.
     */
    public void generateQ() {
        q = Prime.getPrime(1, maxValue.longValue());
    }
    
    /**
     * Generate the public key, n, where n = p*q
     */
    public void generateN() {
        n = p.multiply(q);
    }
    
    /**
     * Generate the modulo, m, where m = (p-1)(q-1)
     */
    public void generateM() {
        // m = (p-1)(q-1)
        m = p.subtract(BigInteger.ONE).multiply((q.subtract(BigInteger.ONE)));
    }
    
    /**
     * Generate the public key e, where 1 < e < n
     */
    public void generateE() {
        while (true) {
            
            // 0 < e < n
            e = Prime.getPrime(1, n.longValue());
            
            if (e.compareTo(n) < 0)
                break;
        }
    }
    
    /**
     * Generate the private key d, where d = e^-1 [mod (p-1)(q-1)]
     */
    public void generateD() {
        // d = e^-1 [mod (p-1)(q-1)]
        d = e.modInverse(m);
    }
    
    /** 
     * Encrypt the given message by using RSA algorithm. This algorithm will 
     * encrypt two characters at once, and ONLY encrypt alphabetic letters.
     * This encryption will append an 'X' to the given message if the given 
     * message has an odd in length.
     * @param message the message to be encrypted
     * @return the cipher text, in the format of "1234 5678 1122 3344"
     */
    public String encrypt(String message) {
        
        // Remove all non-alphanumeric letters
        message = message.replaceAll("[^a-zA-Z0-9]", "");
        
        boolean isOdd = (message.length() & 1) == 1 ? true : false;
        
        // Append an 'X' if the message has an odd length
        if (isOdd)
            message = message + 'X';
        
        StringBuilder result = new StringBuilder();
        
        int i = 0;
        while (i < message.length()) {
            
            // Get two characters at once
            String str = message.substring(i, i+=2);
                        
            String number = Convert.stringToNumber(str);
            
            if (!number.matches("[0-9]+")) 
                return "Invalid message input.\n"+
                       "Message must contains only alphabetic letters.";
            
            result.append(new BigInteger(number).modPow(e, n) + "\n");
        }

        return result.toString();
    }
    
    /**
     * Decrypt the given cipher text by using RSA algorithm. The cipher text
     * should only contain numeric characters.
     * @param cipherText the text to be decoded
     * @return the original message in the form of two characters per block
     */
    public String decrypt(String cipherText) {

        cipherText = cipherText.trim().replaceAll("[^a-zA-Z0-9,\\s]", "");
        cipherText = cipherText.replaceAll(",", " ");
        
        String[] texts = cipherText.split("\\s+");
        
        StringBuilder output = new StringBuilder();
        
        for (String t : texts) {
                        
            if (!t.matches("[0-9]+"))
                return "Invalid cipher text input.\n"+
                       "Cipher text must contains only numeric letters.";
            
            BigInteger result = new BigInteger(t).modPow(d, n);
            
            output.append(Convert.numberToString(result.toString()) + "\n");
        }
        
        return output.toString();
    }

    /**
     * Set the maximum value for certain keys.
     * @param mv maximum value
     */
    public void setMaxValue(String mv) {
        this.maxValue = new BigInteger(mv);
    }
    
    /**
     * Get the maximum value for certain keys.
     * @return maximum value
     */
    public String getMaxValue() {
        return maxValue.toString();
    }
    
    /**
     * Set all the private keys for RSA algorithm.
     * @param p the private key p, where p is a prime number
     * @param q the private key q, where q is a prime number
     * @param d the private key d, where d is inverse of e
     */
    public void setPrivateKeys(String p, String q, String d) {
        this.p = new BigInteger(p);
        this.q = new BigInteger(q);
        this.d = new BigInteger(d);
    }
    
    /**
     * Set all the public keys for RSA algorithm.
     * @param n the public key n, where n = p*q
     * @param e the public key e, where e is a random number between 1 and n
     */
    public void setPublicKeys(String n, String e) {
        this.n = new BigInteger(n);
        this.e = new BigInteger(e);
    }
    
    /** 
     * Get the prime number p 
     * @return the prime number p
     */
    public String getP() { 
        return p.toString(); 
    }
    
    /** 
     * Get the prime number q
     * @return the prime number q
      */
    public String getQ() { 
        return q.toString(); 
    }
    
    /** 
     * Get the public key n, where n = p*q 
     * @return the public key n
     */
    public String getN() { 
        return n.toString(); 
    }
    
    /** 
     * Get the modulo m, where m = (p-1)(q-1) 
     * @return the modulo m
     */
    public String getM() { 
        return m.toString(); 
    }
    
    /** 
     * Get the public key e 
     * @return the public key e
     */
    public String getE() { 
        return e.toString(); 
    }
    
    /** 
     * Get the private key d, where d = e^-1 (mod m) 
     * @return the private key d
     */
    public String getD() { 
        return d.toString(); 
    }
}
