package Algorithm;

/**
 * Class for Murmurhash2. Given a string and a seed, hash32(String str, int seed) produces a "pseudo-random" integer from Integer.MIN_VALUE to Integer.MAX_VALUE
 * @author XPGT4620
 *
 */
public final class Murmur2 {

    private Murmur2() {}

    public static int hash32(String str, int seed) {
        final byte[] bytes = str.getBytes();
        return hash32(bytes, bytes.length, seed );
    }

    public static int hash32(final byte[] bytes, int length, int seed) {
    
    	final int const1 = 0x5bd1e995;
    	
        final int const2 = 24;

        
        int hash = seed^length;
        
        int partLen = length/4;

        for (int i=0; i<partLen; i++) {
            
        	final int i2 = i*4;
            
            int hashInterm = (bytes[i2+0]&0xff) +((bytes[i2+1]&0xff)<<8)
                    +((bytes[i2+2]&0xff)<<16) +((bytes[i2+3]&0xff)<<24);
            
            hashInterm *= const1;
            
            hashInterm ^= hashInterm >>> const2;
            
            hashInterm *= const1;
            
            hash *= const1;
            
            hash ^= hashInterm;
        }

        switch (length%4) {
            
        	case 3: hash ^= (bytes[(length&~3) +2]&0xff) << 16;
            
            case 2: hash ^= (bytes[(length&~3) +1]&0xff) << 8;
            
            case 1: hash ^= (bytes[length&~3]&0xff);
            
            hash *= const1;
        }

        hash ^= hash >>> 13;
        
        hash *= const1;
        
        hash ^= hash >>> 15;

        return hash;
    }

}