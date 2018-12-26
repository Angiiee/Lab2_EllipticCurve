package mmf.mareckay.entity;

import java.util.Objects;

public class Point {

    private double x;
    private double y;
    private double tmp;

    public Point() {
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point(double x, double y, double tmp) {
        this.x = x;
        this.y = y;
        this.tmp = tmp;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getTmp() {
        return tmp;
    }

    public void setTmp(int tmp) {
        this.tmp = tmp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x &&
                y == point.y &&
                tmp == point.tmp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, tmp);
    }

    @Override
    public String toString() {
        if (x == 0 && y == 0){
            return "Point{ 0 }";
        }
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
