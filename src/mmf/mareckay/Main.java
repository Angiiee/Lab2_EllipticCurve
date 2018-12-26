package mmf.mareckay;

import mmf.mareckay.entity.EllipticCurve;

public class Main {

    public static void main(String[] args){
        EllipticCurve curve = new EllipticCurve(3, 2, 5);
        System.out.println(curve.getContainedPoints());
    }
}
