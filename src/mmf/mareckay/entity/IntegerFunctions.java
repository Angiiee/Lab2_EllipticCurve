package mmf.mareckay.entity;

import java.math.BigInteger;

public final class IntegerFunctions {

    private static final BigInteger ZERO = BigInteger.valueOf(0);
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private static final BigInteger TWO = BigInteger.valueOf(2);
    private static final int[] jacobiTable = {0, 1, 0, -1, 0, -1, 0, 1};

    public static int jacobi(BigInteger A, BigInteger B) {
        BigInteger a, b, v;
        long k = 1;
        k = 1;
        if (B.equals(ZERO)) {
            a = A.abs();
            return a.equals(ONE) ? 1 : 0;
        }
        if (!A.testBit(0) && !B.testBit(0)) {
            return 0;
        }
        a = A;
        b = B;
        if (b.signum() == -1) { // b < 0
            b = b.negate(); // b = -b
            if (a.signum() == -1) {
                k = -1;
            }
        }
        v = ZERO;
        while (!b.testBit(0)) {
            v = v.add(ONE); // v = v + 1
            b = b.divide(TWO); // b = b/2
        }
        if (v.testBit(0)) {
            k = k * jacobiTable[a.intValue() & 7];
        }
        if (a.signum() < 0) { // a < 0
            if (b.testBit(1)) {
                k = -k; // k = -k
            }
            a = a.negate(); // a = -a
        }
        while (a.signum() != 0) {
            v = ZERO;
            while (!a.testBit(0)) { // a is even
                v = v.add(ONE);
                a = a.divide(TWO);
            }
            if (v.testBit(0)) {
                k = k * jacobiTable[b.intValue() & 7];
            }
            if (a.compareTo(b) < 0) { // a < b
                BigInteger x = a;
                a = b;
                b = x;
                if (a.testBit(1) && b.testBit(1)) {
                    k = -k;
                }
            }
            a = a.subtract(b);
        }
        return b.equals(ONE) ? (int) k : 0;
    }

    /**
     * Shanks-Tonelli algorithm.
     */
    public static BigInteger solve(BigInteger a, BigInteger p) throws IllegalArgumentException {

        BigInteger v = null;
        if (a.compareTo(p) == 1) {
            do {
                a = a.subtract(p);
            } while (a.compareTo(p) == 1);
        }
        if (a.compareTo(ZERO) < 0) {
            a = a.add(p);
        }
        if (a.equals(ZERO)) {
            return ZERO;
        }
        if (p.equals(TWO)) {
            return a;
        }

        // p = 3 mod 4
        if (p.testBit(0) && p.testBit(1)) {
            if (jacobi(a, p) == 1) {
                v = p.add(ONE);
                v = v.shiftRight(2);
                return a.modPow(v, p);
                // return --> a^((p+1)/4) mod p
            }
            throw new IllegalArgumentException("No solution for : " + a + ", " + p);
        }
        long t = 0;
        // initialization
        // compute k and s, where p = 2^s (2k+1) + 1

        BigInteger k = p.subtract(TWO); // k = p-1
        long s = 0;
        while (!k.testBit(0)) { // while k is even
            s++; // s = s+1
            k = k.shiftRight(1); // k = k/2
        }

        k = k.subtract(ONE); // k = k - 1
        k = k.shiftRight(1); // k = k/2
        BigInteger r = a.modPow(k, p); // r = a^k mod p

        BigInteger n = r.multiply(r).remainder(p); // n = r^2 % p
        n = n.multiply(a).remainder(p); // n = n * a % p
        r = r.multiply(a).remainder(p); // r = r * a %p

        if (n.equals(ONE)) {
            return r;
        }
        // non-quadratic residue
        BigInteger z = TWO; // z = 2
        while (jacobi(z, p) == 1) {
            // while z quadratic residue
            z = z.add(ONE); // z = z + 1
        }

        v = k;
        v = v.multiply(TWO); // v = 2k
        v = v.add(ONE); // v = 2k + 1
        BigInteger c = z.modPow(v, p); // c = z^v mod p

        // iteration
        while (n.compareTo(ONE) == 1) { // n > 1
            System.out.println("k" + k);

            k = n; // k = n
            t = s; // t = s
            s = 0;

//            while (!k.equals(ONE)) { // k != 1
//                k = k.multiply(k).remainder(p); // k = k^2 % p
//                s++; // s = s + 1
//                System.out.println("456");
//            }

            t -= s; // t = t - s
            if (t == 0) {
                throw new IllegalArgumentException("No solution for : " + a + ", " + p);
            }

            v = ONE;
            for (long i = 0; i < t - 1; i++) {
                v = v.shiftLeft(1); // v = 1 * 2^(t - 1)
            }
            c = c.modPow(v, p); // c = c^v mod p
            r = r.multiply(c).remainder(p); // r = r * c % p
            c = c.multiply(c).remainder(p); // c = c^2 % p
            n = n.multiply(c).mod(p); // n = n * c % p
        }
        return r;
    }

    // (d,u,v) is d = gcd(a,b) = ua + vb
    public static BigInteger[] extgcd(BigInteger a, BigInteger b) {
        BigInteger u = ONE;
        BigInteger v = ZERO;
        BigInteger d = a;
        if (b.signum() != 0) {
            BigInteger v1 = ZERO;
            BigInteger v3 = b;
            while (v3.signum() != 0) {
                BigInteger[] tmp = d.divideAndRemainder(v3);
                BigInteger q = tmp[0];
                BigInteger t3 = tmp[1];
                BigInteger t1 = u.subtract(q.multiply(v1));
                u = v1;
                d = v3;
                v1 = t1;
                v3 = t3;
            }
            v = d.subtract(a.multiply(u)).divide(b);
        }
        return new BigInteger[]{d, u, v};
    }

    // a mod m
    public static long mod(long a, long m) {
        long result = a % m;
        if (result < 0) {
            result += m;
        }
        return result;
    }

    // a^-1 mod m
    public static int modInverse(int a, int mod) {
        return BigInteger.valueOf(a).modInverse(BigInteger.valueOf(mod))
                .intValue();
    }

    public static boolean isSolutionExists(double a, int p) {
        if (a >= p) {
            do {
                a -= p;
            } while (a > p);
        }
        return Math.pow(a, (p - 1) * 0.5) % p == 1;
    }

    public static long prepareParam(int a, int b, int x) {
        return (long) Math.pow(x, 3) + a * x + b;
    }
}