package ENCRYPTION;

public class ValueCalculator {

    public static long power(long a, long b, long p)
    {
        if (b == 1)
            return a;
        else
            return (((long)Math.pow(a, b)) % p);
    }
}
