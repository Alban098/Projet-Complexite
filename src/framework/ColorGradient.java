package framework;

import utils.Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Class which correspond to
 */
public class ColorGradient {

    BezierInterpolator interpolator;

    public ColorGradient(Color start, double w1, Color end, double w2) {
        interpolator = new BezierInterpolator();
        interpolator.addPoint(new Point(start).setWeight(w1), 0.0);
        interpolator.addPoint(new Point(end).setWeight(w2), 1.0);
    }

    /**
     * method which add a color to
     * @param color the color to add
     * @param value of the point
     * @param weight of the point
     */
    public void addColor(Color color, double value, double weight) {
        interpolator.addPoint(new Point(color).setWeight(weight), value);
    }

    /**
     * method which remove a color from
     * @param value
     */
    public void removeColor(double value) {
        interpolator.removePoint(value);
    }

    /**
     * mathod to get a color from
     * @param value
     * @return the color to get
     */
    public Color getColor(double value) {
        return interpolator.interpolate(Utils.clamp(value, 0.0, 1.0)).toColor();
    }

    /**
     * method to save
     * @param width
     * @param height
     * @param file
     */
    public void saveToFile(int width, int height, String file) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setStroke(new BasicStroke(1));
        for (int i = 0; i < width; i++) {
            g.setColor(getColor((float)i/width));
            g.drawLine(i, 0, i, 100);
        }
        try {
            ImageIO.write(image, "png", new File(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

