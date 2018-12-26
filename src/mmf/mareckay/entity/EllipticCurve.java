package mmf.mareckay.entity;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Objects;

public class EllipticCurve {

    private int a_coefficient;
    private int b_coefficient;
    private int p_coefficient;
    private ArrayList<Point> containedPoints;

    public EllipticCurve() {
    }

    public EllipticCurve(int a_coefficient, int b_coefficient, int p_coefficient) {
        this.a_coefficient = a_coefficient;
        this.b_coefficient = b_coefficient;
        this.p_coefficient = p_coefficient;
        this.containedPoints = processContainedPoints();
    }

    public int getA_coefficient() {
        return a_coefficient;
    }

    public void setA_coefficient(int a_coefficient) {
        this.a_coefficient = a_coefficient;
    }

    public int getB_coefficient() {
        return b_coefficient;
    }

    public void setB_coefficient(int b_coefficient) {
        this.b_coefficient = b_coefficient;
    }

    public int getP_coefficient() {
        return p_coefficient;
    }

    public void setP_coefficient(int p_coefficient) {
        this.p_coefficient = p_coefficient;
    }

    public ArrayList<Point> getContainedPoints() {
        return containedPoints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EllipticCurve that = (EllipticCurve) o;
        return a_coefficient == that.a_coefficient &&
                b_coefficient == that.b_coefficient &&
                p_coefficient == that.p_coefficient &&
                Objects.equals(containedPoints, that.containedPoints);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a_coefficient, b_coefficient, p_coefficient, containedPoints);
    }

    @Override
    public String toString() {
        return "EllipticCurve{" +
                "a_coefficient=" + a_coefficient +
                ", b_coefficient=" + b_coefficient +
                ", p_coefficient=" + p_coefficient +
                ", containedPoints=" + containedPoints +
                '}';
    }

    private ArrayList<Point> processContainedPoints() {
        ArrayList<Point> allPoints = new ArrayList<>();
        allPoints.add(new Point(0,0));
        for (int i = 1; i < p_coefficient; ++i) {
            long newParam = IntegerFunctions.prepareParam(a_coefficient, b_coefficient, i);
            if (IntegerFunctions.isSolutionExists(newParam, p_coefficient)) {
                BigInteger bi = IntegerFunctions.solve(BigInteger.valueOf(newParam), BigInteger.valueOf(p_coefficient));
                allPoints.add(new Point(i, bi.intValue()));
                allPoints.add(new Point(i, bi.negate().intValue() + p_coefficient));
            }
        }
        return allPoints;
    }

    public static Point useEvklid(double a, double b) {
        if (a == 0) {
            return new Point(0, 1, b);
        } else {
            Point d = useEvklid(b % a, a);
            double nx = d.getY() - (int) (b / a) * d.getX();
            double ny = d.getX();
            return new Point(nx, ny, d.getTmp());
        }
    }

    public Point addPoint(Point p, Point q) {
        Point result = new Point();
        double alfa = (p.getY() - q.getY()) / (p.getX() - q.getX());
        result.setX(Math.pow(alfa, 2) - p.getX() - q.getX());
        result.setY(-p.getY() + alfa * (p.getX() - result.getX()));
        return result;
    }

    private boolean checkPointSatisfiesCurve(Point point) {
        double tmp1 = Math.pow(point.getY(), 2);
        double tmp2 = Math.pow(point.getX(), 3) + a_coefficient * point.getX() + b_coefficient;
        return tmp1 % p_coefficient == tmp2 % p_coefficient;
    }

    //
    static long pow_mod(long x, long n, long p) {
        if (n == 0) return 1;
        if ((n & 1) == 0)
            return (pow_mod(x, n - 1, p) * x) % p;
        x = pow_mod(x, n / 2, p);
        return (x * x) % p;
    }

    /* Takes as input an odd prime p and n < p and returns r
     * such that r * r = n [mod p]. */
    public static long tonelli_shanks(long n, long p) {
        long s = 0;
        long q = p - 1;
        while ((q & 1) == 0) {
            q /= 2;
            ++s;
        }
        if (s == 1) {
            long r = pow_mod(n, (p + 1) / 4, p);
            if ((r * r) % p == n) return r;
            return 0;
        }
        // Find the first quadratic non-residue z by brute-force search
        long z = 1;

        while (pow_mod(++z, (p - 1) / 2, p) != p - 1) ;
        long c = pow_mod(z, q, p);
        long r = pow_mod(n, (q + 1) / 2, p);
        long t = pow_mod(n, q, p);
        long m = s;
        while (t != 1) {
            long tt = t;
            long i = 0;
            while (tt != 1) {
                tt = (tt * tt) % p;
                ++i;
                if (i == m) return 0;
            }
            long b = pow_mod(c, pow_mod(2, m - i - 1, p - 1), p);
            long b2 = (b * b) % p;
            r = (r * b) % p;
            t = (t * b2) % p;
            c = b2;
            m = i;
        }
        return r;
        //return 0;
    }
}
