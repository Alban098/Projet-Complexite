package utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static boolean overlap(Rectangle r1, Rectangle r2) {
        return r1.x <= r2.x + r2.width && r1.x + r1.width >= r2.x && r1.y <= r2.y + r2.height && r1.y + r1.height >= r2.y;
    }

    public static boolean isInside(Rectangle r, Point p) {
        return p.x >= r.x && p.x <= r.x + r.width && p.y >= r.y && p.y <= r.y + r.height;
    }

    public static java.util.List<Point> getCoveringSet(int x, int y, int radius) {
        List<Point> list = new ArrayList<>();
        for (int i = 0; i < radius*radius; i+=radius) {
            float r = 2*(float) Math.sqrt(i);
            float O = i/(radius) * 137.50776405003f;
            int x1 = (int) (r * Math.cos(Math.toRadians(O))) + x;
            int y1 = (int) (r * Math.sin(Math.toRadians(O))) + y;
            list.add(new Point (x1, y1));
        }
        return list;
    }

    public static int clamp(int val, int min, int max) {
        return Math.min(max, Math.max(min, val));
    }
    public static double clamp(double val, double min, double max) { return Math.min(max, Math.max(min, val)); }

}
