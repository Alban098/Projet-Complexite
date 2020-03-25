package utils;

import java.awt.*;

public class Utils {

    public static boolean overlap(Rectangle r1, Rectangle r2) {
        return r1.x <= r2.x + r2.width && r1.x + r1.width >= r2.x && r1.y <= r2.y + r2.height && r1.y + r1.height >= r2.y;
    }

    public static boolean isInside(Rectangle r, Point p) {
        return p.x >= r.x && p.x <= r.x + r.width && p.y >= r.y && p.y <= r.y + r.height;
    }

}
