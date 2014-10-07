/**
 * An interface for all the algorithm in this cryptography system.
 * 
 * @author Su Khai Koh
 */
public interface Algorithm {

    /**
     * A method that encrypt a message. The message can only contain alphabetic
     * characters and the cipher text will be in numeric characters.
     * @param message the message to be encrypted
     * @return cipher text
     */
    public String encrypt(String message);
    
    /**
     * A method that decrypt a cipher text. The cipher text should only contain
     * numeric characters and the message will be in alphabetic characters.
     * @param cipherText the cipher text to be decrypted
     * @return message (plain text)
     */
    public String decrypt(String cipherText);
    
    
}
