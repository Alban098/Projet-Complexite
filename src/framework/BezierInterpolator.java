package framework;

import utils.Utils;

import java.awt.*;
import java.util.*;
import java.util.List;

class BezierInterpolator {

    private Map<Double, Point> anchorPoints;
    private List<Point> controlPoints;

    BezierInterpolator() {
        controlPoints = new ArrayList<>();
        anchorPoints = new TreeMap<>();
    }

    Point interpolate(double value) {
        if (anchorPoints.size() <= 1)
            throw new IllegalStateException("Not enough points to interpolate : " + anchorPoints.size() + " < 2");
        double lower = 0;
        double upper = 1;
        double last = 0;
        for (double val : anchorPoints.keySet()) {
            if (val >= value && last < value) {
                lower = last;
                upper = val;
            }
            last = val;
        }

        double intervalPercent = (value - lower) / (upper - lower);
        if (lower == 0) {
            return quadraticBezier(controlPoints.get(0), controlPoints.get(1), controlPoints.get(2), intervalPercent);
        }
        if (upper == 1) {
            int size = controlPoints.size();
            return quadraticBezier(controlPoints.get(size-3),controlPoints.get(size-2),controlPoints.get(size-1), intervalPercent);
        }
        int lowerIndex = controlPoints.indexOf(new Point(anchorPoints.get(lower)));
        return cubicBezier(controlPoints.get(lowerIndex), controlPoints.get(lowerIndex + 1), controlPoints.get(lowerIndex + 2), controlPoints.get(lowerIndex + 3), intervalPercent);
    }

    void addPoint(Point point, double value) {
        anchorPoints.put(value, point);
        computeControlPoints();
    }

    void removePoint(double value) {
        anchorPoints.remove(value);
    }


    private void computeControlPoints() {
        controlPoints.clear();
        for (Point p : anchorPoints.values())
            controlPoints.add(new Point(p));
        if (controlPoints.size() > 2) {
            List<Point> result = new ArrayList<>();
            result.add(controlPoints.get(0));
            for (int i = 1; i < controlPoints.size() - 1; i++) {
                Point p0 = new Point(controlPoints.get(i - 1));
                Point p1 = new Point(controlPoints.get(i));
                Point p2 = new Point(controlPoints.get(i + 1));

                Point mid01 = Point.midPoint(p0, p1);
                Point mid12 = Point.midPoint(p1, p2);

                Point bis = Point.bisector(p0, p1, p2);
                Point bisVector = Point.relative(p1, bis);

                Point normal = Point.product(Point.relative(p1, p2).normalized(), bisVector).normalized();
                Point perpVector = Point.product(normal, bisVector).normalized();
                Point perp01 = Point.add(p1, perpVector);
                Point perp12 = Point.sub(p1, perpVector);

                double angle01 = 3.1415927 - Point.angle(perp01, p1, mid01);
                double a01 = Point.relative(p1, mid01).mag();
                double d01 = Math.abs(a01 * Math.cos(angle01));
                Point p01 = Point.add(p1, Point.relative(p1, perp01).normalized().mult(d01/p1.getWeight()));

                double angle12 = 3.1415927 - Point.angle(perp12, p1, mid12);
                double a12 = Point.relative(p1, mid12).mag();
                double d12 = Math.abs(a12 * Math.cos(angle12));
                Point p12 = Point.add(p1, Point.relative(p1, perp12).normalized().mult(d12/p1.getWeight()));
                result.add(p01);
                result.add(p1);
                result.add(p12);
            }
            result.add(controlPoints.get(controlPoints.size() - 1));
            controlPoints.clear();
            controlPoints.addAll(result);
        } else if (controlPoints.size() == 2){
            controlPoints.clear();
            controlPoints.add(new Point(anchorPoints.get(0.0)));
            controlPoints.add(new Point(anchorPoints.get(0.0)));
            controlPoints.add(new Point(anchorPoints.get(1.0)));
            controlPoints.add(new Point(anchorPoints.get(1.0)));
        }
    }

    private Point quadraticBezier(Point p0, Point p1, Point p2, double alpha) {
        Point p01 = Point.add(p0, Point.sub(p1, p0).mult(alpha));
        Point p12 = Point.add(p1, Point.sub(p2, p1).mult(alpha));
        return Point.add(p01, Point.sub(p12, p01).mult(alpha));
    }

    private Point cubicBezier(Point p0, Point p1, Point p2, Point p3, double alpha) {
        Point p01 = Point.add(p0, Point.sub(p1, p0).mult(alpha));
        Point p12 = Point.add(p1, Point.sub(p2, p1).mult(alpha));
        Point p23 = Point.add(p2, Point.sub(p3, p2).mult(alpha));
        return quadraticBezier(p01, p12, p23, alpha);
    }
}

class Point {

    private double x;
    private double y;
    private double z;
    private double weight;

    Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.weight = 1;
    }

    double getWeight() {
        return weight;
    }

    Point setWeight(double weight) {
        this.weight = weight;
        return this;
    }

    Point(Color color) {
        this.x = color.getRed();
        this.y = color.getGreen();
        this.z = color.getBlue();
        this.weight = 1;
    }

    Point(Point p) {
        this.x = p.x;
        this.y = p.y;
        this.z = p.z;
        this.weight = p.weight;
    }

    Color toColor() {
        int r = Utils.clamp((int) x, 0, 255);
        int g = Utils.clamp((int) y, 0, 255);
        int b = Utils.clamp((int) z, 0, 255);
        return new Color(r, g, b);
    }

    double mag() {
        return Math.sqrt(x*x + y*y + z*z);
    }

    Point normalized() {
        if (mag() == 0)
            return new Point(0, 0, 0);
        return new Point(x/mag(), y/mag(), z/mag());
    }

    Point mult(double a) {
        x*=a;
        y*=a;
        z*=a;
        return this;
    }

    static Point relative(Point p1, Point p2) {
        return new Point(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
    }

    static Point add(Point p1, Point p2) {
        return new Point(p2.x + p1.x, p2.y + p1.y, p2.z + p1.z);
    }

    static Point sub(Point p1, Point p2) {
        return new Point(p1.x - p2.x, p1.y - p2.y, p1.z - p2.z);
    }

    static Point midPoint(Point p1, Point p2) {
        return new Point((p2.x + p1.x)/2, (p2.y + p1.y)/2, (p2.z + p1.z)/2);
    }

    static double angle(Point p0, Point p1, Point p2) {
        Point v0 = relative(p1, p0).normalized();
        Point v1 = relative(p1, p2).normalized();
        double dot = v1.x*v0.x + v1.y*v0.y + v1.z*v0.z;
        return Math.acos(dot);
    }

    static Point bisector(Point p0, Point p1, Point p2) {
        Point v0 = relative(p1, p0);
        Point v1 = relative(p1, p2);
        Point bis = add(v1.normalized(), v0.normalized());
        return add(p1, bis);
    }

    static Point product(Point p0, Point p1) {
        double x = p0.y*p1.z - p0.z*p1.y;
        double y = p0.z*p1.x - p0.x*p1.z;
        double z = p0.x*p1.y - p0.y*p1.x;
        return new Point(x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point)
            return (x == ((Point) obj).x && y == ((Point) obj).y && z == ((Point) obj).z && weight == ((Point) obj).weight);
        return false;
    }

    @Override
    public int hashCode() {
        return Integer.valueOf((int) x << 8 + (int) y << 16 + (int) z << 24).hashCode() + super.hashCode();
    }
}