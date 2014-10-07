import java.math.BigInteger;
import java.util.Random;

/**
 * This class generate a random prime number.
 * 
 * @author Su Khai Koh
 */
public class Prime {
        
    /**
     * Generate a random prime number.
     * @return a random prime number
     */
    public static BigInteger getPrime() {
        
        long n = new Random().nextLong();
        
        return getPrime(1, n < 0 ? n *= -1 : n);
    }
    
    /**
     * Generate a random prime number within a given range.
     * @param low the lowest range of the possible prime number
     * @param high the highest range of the possible prime number
     * @return a random prime number within a the given low and high numbers
     */
    public static BigInteger getPrime(long low, long high) {
        
        Random random = new Random();

        BigInteger p;   // prime number
        
        do {
            
            long n = ((long)(random.nextDouble() * (high - low))) + low ;

            p = new BigInteger(Long.toString(n)).nextProbablePrime();

        } while (p.compareTo(new BigInteger(Long.toString(high))) > 0);

        return p;
    }
}
