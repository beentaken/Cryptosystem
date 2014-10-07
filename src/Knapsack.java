import java.math.BigInteger;
import java.util.Random;

/**
 * A class that handling the Knapsack algorithm, including encrypting a
 * message and decrypting a cipher text. This Knapsack is designed to
 * have a weight count of 5 in both the superincreasing set and public key.
 * 
 * @author Su Khai Koh
 */

public class Knapsack implements Algorithm {

    private BigInteger[] W;     // W = S x a (mod m)
    private BigInteger[] S;     // Super-increasing set, WEIGHT_COUNT 5
    private BigInteger   m;     // modulo, which is > sum of everything in S
    private BigInteger   a;     // secret key (prime)
    private BigInteger   z;     // a^-1 (mod m)

    private int weightCount;

    private BigInteger maxValue;
    
    /**
     * Default constructor. Set the weight count and maximum value for certain
     * keys, and generate all public and private keys.
     */
    public Knapsack() {
        
        weightCount = 5;    // default to 5
        maxValue = new BigInteger("10000");
        
        generateAllKeys();
        
    }
    
    /**
     * Generate all public and private keys.
     */
    public void generateAllKeys() {
        
        generateS();
        generateM();
        generateA();
        generateW();
        generateZ();
    }
    
    /**
     * Generate the super-increasing set, S, the number of term in the set is
     * equal to the given weight count, and each term is greater than the sum of 
     * preceding terms.
     */
    public void generateS() {
        
        S = new BigInteger[weightCount];

        Random random = new Random();
              
        int bitLength = maxValue.bitLength();
        
        // maxValue / weightCount
        BigInteger gapValue = maxValue.divide(new BigInteger(Integer.toString(weightCount)));
        
        BigInteger total = BigInteger.ZERO;

        for (int i = 0; i < S.length; i++) {
            
            BigInteger value;
            do {
                value = new BigInteger(bitLength, random);
            } while (value.compareTo(gapValue) > 0);
            
            S[i] = value.add(total);
            
            // Sum up the preceding terms
            total = total.add(value);
            total = total.add(total);
        }
    }

    /**
     * Generate the private key m, where m is a modulo and also greater than the 
     * sum of all numbers in S.
     */
    public void generateM() {

        BigInteger total = BigInteger.ZERO;

        for (int i = 0; i < S.length; i++) 
            total = total.add(S[i]);
        
        // Generate a random number and add it into m
        BigInteger addition = new BigInteger(weightCount + 1, new Random());
        
        // m > sum of everything in S
        m = total.add(addition);
    }

    /**
     * Generate the private key a, where a has no factor in common with the
     * modulus.
     */
    public void generateA() {
                
        do {
            a = Prime.getPrime(1, maxValue.longValue());
        } while (a.mod(m).equals(BigInteger.ZERO));        
    }

    /**
     * Generate the public key W, where each term in W is equal to the term in
     * S multiply by the private key a and mod m.
     */
    public void generateW() {

        W = new BigInteger[S.length];

        BigInteger number;

        for (int i = 0; i < S.length; i++) {

            number = S[i].multiply(a);
            number = number.mod(m);
            // W[i] = S[i] x a (mod m)
            W[i] = number;
        }
    }
    
    /** 
     * Generate z, where z = a^-1 (mod m)
     */
    public void generateZ() {
        z = a.modInverse(m);
    }

    /** 
     * Encrypt the given message by using Knapsack algorithm. This algorithm 
     * will encrypt one character at once, and ONLY encrypt alphabetic letters.
     * The weight of the private key S and public key W has to be equal.
     * @param message the message to be encrypted
     * @return the cipher text, in the format of "1234 5678 1122 3344"
     */
    public String encrypt(String message) {
        
        if (S.length != W.length)
            return "Weight of S key and weight of W key is different.";
        
        // Remove all non-alphanumeric letters
        message = message.replaceAll("[^a-zA-Z0-9]", "");

        message = message.toUpperCase();
        
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < message.length(); i++) {
            
            // If character is not a letter, return 
            if (!Character.isLetter(message.charAt(i)))
                return "Invalid message input.\n"+
                       "Message must contains only alphabetic letters.";
            
            String c = Character.toString(message.charAt(i));
            
            BigInteger total = BigInteger.ZERO;
            
            // Get the value of the character
            int value = Convert.getValue(c.charAt(0));
            
            // Check for every bit in the value to the public key W
            for (int j = W.length - 1, bitPosition = 1; j >= 0; j--) {
                
                if ((value & bitPosition) != 0)
                    total = total.add(W[j]);
                
                bitPosition <<= 1;  // Check next bit
            }
            
            result.append(total + "\n");
        }
        
        return result.toString();
    }

    /**
     * Decrypt the given cipher text by using Knapsack algorithm. The cipher
     * text should only contain numeric characters.
     * @param cipherText the text to be decoded
     * @return the original message in the form of one character per block
     */
    public String decrypt(String cipherText) {
        
        if (S.length != W.length)
            return "Weight of S key and weight of W key is different.";
        
        // Recalculate the inverse of a
        generateZ();
        
        cipherText = cipherText.trim().replaceAll("[^a-zA-Z0-9,\\s]", "");
        cipherText = cipherText.replaceAll(",", " ");
        
        String[] texts = cipherText.split("\\s+");
        
        StringBuilder result = new StringBuilder();
        
        for (String t : texts) {
            
            if (!t.matches("[0-9]+"))
                return "Invalid cipher text input.\n"+
                       "Cipher text must contains only numeric letters.";
            
            // Total = t x a^-1 (mod m)
            BigInteger total = new BigInteger(t).multiply(z);
            total = total.mod(m);
                        
            StringBuilder binary = new StringBuilder();
            
            // Build the binary form
            for (int i = S.length-1; i >= 0; i--) {
                if (total.compareTo(S[i]) >= 0) {
                    binary.insert(0, "1");
                    total = total.subtract(S[i]);
                } else {
                    binary.insert(0, "0");
                }
            }
            
            char c = Convert.binaryToCharacter(binary.toString());
            result.append(c + "\n");
        }
        
        return result.toString();
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
     * Set all the private keys for Knapsack algorithm.
     * @param m the private key m, where m is a modulo
     * @param a the private key a, where a has no common factor to m
     * @param S the private key S, where S is a super-increasing set
     */
    public void setPrivateKeys(String m, String a, String S) {
        this.m = new BigInteger(m);
        this.a = new BigInteger(a);
        
        // Remove all non-alphanumeric letters
        S = S.replaceAll("[^a-zA-Z0-9,\\s]", "");
        S = S.replaceAll(",", " ");
        
        String[] terms = S.split("\\s+");
        
        weightCount = terms.length;
        
        this.S = new BigInteger[weightCount];
        
        for (int i = 0; i < terms.length; i++) 
            this.S[i] = new BigInteger(terms[i]);
    }
    
    /**
     * Set all the public keys for Knapsack algorithm.
     * @param W the public key W, where W[index] = S[index] x a (mod m)
     */
    public void setPublicKeys(String W) {
        
        // Remove all non-alphanumeric letters
        W = W.replaceAll("[^a-zA-Z0-9,\\s]", "");
        W = W.replaceAll(",", " ");
        
        String[] terms = W.split("\\s+");
        
        this.W = new BigInteger[terms.length];
        
        for (int i = 0; i < terms.length; i++) 
            this.W[i] = new BigInteger(terms[i]);
    }
    
    /** 
     * Get the private key m, where m is a modulo.
     * @return the modulo, m
     */
    public String getM() { return m.toString(); }
    
    /** 
     * Get the private key, a.
     * @return the private key, a 
     * */
    public String getA() { return a.toString(); }

    /** 
     * Get the private key S, where S is a super-increasing set.
     * @return the private key S in the format as 123, 234, 345, 456, 567
     */
    public String getS() {

        StringBuilder str = new StringBuilder("");

        for (int i = 0; i < S.length; i++) {
            str.append(S[i]);
            str.append(i < S.length-1 ? ", " : "");
        }

        return str.toString();
    }
    
    /** 
     * Get a set of public key, W.
     * @return the public key W in the format as 234, 345, 456, 567, 678
     */
    public String getW() {

        StringBuilder str = new StringBuilder("");

        for (int i = 0; i < W.length; i++) {
            str.append(W[i]);
            str.append(i < W.length-1 ? ", " : "");
        }

        return str.toString();
    }
}
