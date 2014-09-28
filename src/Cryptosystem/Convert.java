package Cryptography;

/**
 * A class that handling all the convension of the characters, such as from
 * alphabetic letters to numeric numbers, vice versa, or from a binary form
 * of number into a character.
 * 
 * @author Su Khai Koh
 */
public class Convert {

    // Total number of alphabetic characters
    public static final int ALPHABETIC_CHARACTERS = 26;
    
    /**
     * Convert the given string to a number format.
     * i.e A    -->    00
     *     B    -->    01
     *     AB   -->    0001
     *     ZA   -->    2500
     * @param string the string to be converted
     * @return the number of the given string
     */
    public static String stringToNumber(String string) {
        
        string = string.toUpperCase();
        
        int n;
        
        StringBuilder result = new StringBuilder();
        
        if (string.length() == 1) 
            result.append("00");
        
        for (int i = 0; i < string.length(); i++) {

            n = string.charAt(i) - 'A';

            if (n < 10)
                result.append("0");

            result.append(n);
        }
        
        return result.toString();
    }
    
    /**
     * Convert the given number into a string. The maximum characters in the
     * given number must be 4 or less.
     * i.e 00    -->    A
     *     0000  -->    AA
     *     0101  -->    BB
     *     2500  -->    ZA
     * @param number the number to be converted
     * @return the string of the given number
     */
    public static String numberToString(String number) {
        
        // Pad the left with '0'
        number = ("0000" + number).substring(number.length());
        
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < number.length(); i++) {
            
            String str = Integer.toString((number.charAt(i) - '0')) + 
                         Integer.toString((number.charAt(++i) - '0'));
            
            int n = Integer.parseInt(str);
            
            n %= ALPHABETIC_CHARACTERS;
            
            // Convert the number back to character
            char c = (char)(n + 'A');
            
            result.append(c);
        }
        
        return result.toString();
    }
    
    /** 
     * Get the int value of the given character. The given character has to be
     * alphabetic letter. Return the int value of the given character.
     * i.e A --> 0
     *     B --> 1
     *     Z --> 25
     * @param c alphabetic letter to be converted
     * @return the int value of the given character
     */
    public static int getValue(char c) {
        
        c = Character.toUpperCase(c);
        
        int value = c - 'A';
        
        return value;
    }
    
    /** 
     * Convert a string of binary format to a character. Return an alphabetic 
     * letter of the given binary string.
     * i.e 00000 --> A
     *     00001 --> B
     *     11001 --> Z
     * @param binaryForm a string of binary format, i.e 01010
     * @return an alphabetic letter of the given binary format string
     */
    public static char binaryToCharacter(String binaryForm) {
        
        int value = Integer.parseInt(binaryForm, 2);
        
        return (char) (value + 'A');
    }
}
