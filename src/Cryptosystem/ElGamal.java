package Cryptography;
import java.math.BigInteger;
import java.util.Random;

/**
 * A class that handling the ElGamal algorithm, including encrypting message and 
 * decrypting a cipher text. The ElGamal algorithm will contain a set of public 
 * key, (p, g, r), and a set of private key, a and k.
 * 
 * @author Su Khai Koh
 */

public class ElGamal implements Algorithm {

    private BigInteger a;      // secret key
    private BigInteger p;      // a large prime number
    private BigInteger g;      // a random number that is less than p
    private BigInteger r;      // g^a
    private BigInteger k;      // a random number between 0 and p
    
    private BigInteger maxValue;    // max value of certain keys
    
    /**
     * Default constructor. Set the maximum value to 10000 and generate all
     * public and private keys.
     */
    public ElGamal() {
        
        maxValue = new BigInteger("10000");
        
        generateAllKeys();
    }
    
    /**
     * Generate the private keys, a and k, and the public keys, p, g, and r.
     */
    public void generateAllKeys() {
        
        generateA();
        generateP();        
        generateK();
        generateG();
        generateR();
    }
    
    /**
     * Generate the secret key, a. The key will be ranged from 1 to the
     * maximum value.
     */
    public void generateA() {
        
        Random random = new Random();
       
        int number = random.nextInt(Integer.parseInt(maxValue.toString()));
        
        a = new BigInteger(Integer.toString(number)); 
    }
    
    /**
     * Generate one of the public key, p, where p is a positive prime number.
     * The p will be range from 1 to the given max value.
     */
    public void generateP() {
        p = Prime.getPrime(1, maxValue.longValue());
    }
    
    /**
     * Generate the public key g, where g is a random number between 1 and p.
     */
    public void generateG() {
        
        Random random = new Random();
        
        while (true) {
            
            long n = 1 + ((long) (random.nextDouble() * p.doubleValue()));
            
            g = new BigInteger(Long.toString(n));
            
            if (g.compareTo(p) < 0) 
                break;
        }
    }
    
    /**
     * Generate one of the public key, r, where r = g^a (mod p)
     */
    public void generateR() {
        r = g.modPow(a, p);
    }
    
    /**
     * Generate a random number k, where 0 < k < p
     */
    public void generateK() {
        
        Random random = new Random();
        
        while (true) {
            
            long n = 1 + ((long) (random.nextDouble() * p.doubleValue()));
            
            k = new BigInteger(Long.toString(n));
            
            if (k.compareTo(p) < 0) 
                break;
        }
    }
    
    /** 
     * Encrypt the given message by using ElGamal algorithm. This algorithm
     * will encrypt two characters at once, and ONLY encrypt alphabetic letters.
     * This encryption will append an 'X' to the given message if the given 
     * message has an odd in length.
     * @param message the message to be encrypted
     * @return the cipher text, in the format of "1234, 5678"
     */
    public String encrypt(String message) {
                        
        // Remove all non-alphanumeric characters
        message = message.replaceAll("[^a-zA-Z0-9]", "");
        
        boolean isOdd = (message.length() & 1) == 1 ? true : false;
        
        // Append an 'X' if the message has an odd length
        if (isOdd)
            message = message + 'X';
        
        boolean randomK = false;
        
        // Randomize k in each block if no k was given
        if (k == null)
            randomK = true;
       
        StringBuilder result = new StringBuilder();
        
        int i = 0;
        while (i < message.length()) {
            
            // Get two characters at once
            String str = message.substring(i, i+=2);
            
            String number = Convert.stringToNumber(str);
            
            if (!number.matches("[0-9]+")) 
                return "Invalid message input.\n"+
                       "Message must contains only alphabetic letters.";
            
            if (randomK)
                generateK();
            
            // Encrypt format: (firstPart, secondPart)
            BigInteger firstPart = g.modPow(k, p);
            BigInteger secondPart = r.modPow(k, p);
            secondPart = secondPart.multiply(new BigInteger(number));
            secondPart = secondPart.mod(p);
            
            result.append(firstPart + ", " + secondPart + "\n");
        }
        
        return result.toString();
    }
    
    /**
     * Decrypt the given cipher text by using ElGamal algorithm. The cipher
     * text should only contain numeric characters.
     * @param cipherText the text to be decoded
     * @return the original message in the form of two characters per block
     */
    public String decrypt(String cipherText) {
        
        cipherText = cipherText.trim().replaceAll("[^a-zA-Z0-9,\\s]", "");
        cipherText = cipherText.replaceAll(",", " ");
        
        String[] texts = cipherText.split("\\s+");
        
        // If the given cipherText has invalid format, then return null
        if ((texts.length & 1) == 1)
            return "Invalid cipher text format.\n"+
                   "Format must be:\n"+
                   "    (1234, 5678)";
        
        // To store the result
        StringBuilder output = new StringBuilder();
        
        for (int i = 0; i < texts.length; i++) {
            
            String firstPart = texts[i];
            String secondPart = texts[++i];
            
            // If any given string contain non numeric character, then return null
            if (!firstPart.matches("[0-9]+") || !secondPart.matches("[0-9]+"))
                return "Invalid cipher text input.\n"+
                       "Cipher text must contains only numeric letters.";
            
            BigInteger pMinus2 = p.subtract(new BigInteger("2"));
            BigInteger gPowerK = new BigInteger(firstPart).modPow(pMinus2, p);
            BigInteger powerA = gPowerK.modPow(a, p);
            BigInteger result = new BigInteger(secondPart).multiply(powerA);
            result = result.mod(p);
            
            // Convert the result from number to alphabetic letters
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
     * Set the private keys for ElGamal algorithm.
     * @param a the private key a, where a is a random number between 1 and maximum value
     * @param k the private key k, where k is a random number between 0 and p
     */
    public void setPrivateKeys(String a, String k) {
        this.a = new BigInteger(a);
        this.k = k.trim().isEmpty() ? null : new BigInteger(k);
    }
    
    /**
     * Set the public keys for ElGamal algorithm.
     * @param p the public key p, where p is a prime number
     * @param g the public key g, where g is a random number between 1 and p
     * @param r the public key r, where r = g^a (mod p)
     */
    public void setPublicKeys(String p, String g, String r) {
        this.p = new BigInteger(p);
        this.g = new BigInteger(g);
        this.r = new BigInteger(r);
    }
    
    /**
     * Get the private key a.
     * @return the private key a
     */
    public String getA() {
        return a.toString();
    }
    
    /**
     * Get the private key k.
     * @return the private key k
     */
    public String getK() {
        return k.toString();
    }
    
    /**
     * Get the public key p.
     * @return the public key p
     */
    public String getP() {
        return p.toString();
    }
    
    /**
     * Get the public key g.
     * @return the public key g
     */
    public String getG() {
        return g.toString();
    }
    
    /**
     * Get the public key r.
     * @return the public key r
     */
    public String getR() {
        return r.toString();
    }
}
