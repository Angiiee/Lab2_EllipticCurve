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
        allPoints.add(new Point(0, 0));
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
        double px = p.getX();
        double py = p.getY();
        double qx = q.getX();
        double qy = q.getY();

        double rx = 0;
        double ry = 0;

        if (px == qx) {
            if (py == -qy) {
                rx = 0;
                ry = 0;
            } else if (py == qy && py != 0 && qy != 0) {
                double alfa = (3 * px * px) / (2 * py * py);
                rx = Math.pow(alfa, 2) - 2 * px;
                ry = -py + alfa * (px - rx);
            } else if (py == qy && py == 0 && qy == 0) {
                rx = 0;
                ry = 0;
            }
        } else {
            double alfa = (py - qy) / (px - qx);
            rx = Math.pow(alfa, 2) - px - qx;
            ry = -py + alfa * (px - rx);
        }
        result.setX(rx);
        result.setY(ry);
        return result;
    }

    private boolean checkPointSatisfiesCurve(Point point) {
        double tmp1 = Math.pow(point.getY(), 2);
        double tmp2 = Math.pow(point.getX(), 3) + a_coefficient * point.getX() + b_coefficient;
        return tmp1 % p_coefficient == tmp2 % p_coefficient;
    }

    public int getOrder(Point p) {
        boolean flag = false;
        int N = this.containedPoints.size();
        int i = N;
        for (int j = 1; j <= N; j++) {
            if (N % j == 0) {
                Point ptmp = p;
                do {
                    ptmp = addPoint(p, ptmp);
                    j--;
                } while (j > 0);
                if (ptmp.getX() == 0 && ptmp.getY() == 0) {
                    i = j;
                    flag = true;
                    break;
                }
            }
        }
        return i;
    }

}
