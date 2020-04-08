package framework;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A custom body that uses an image instead.
 * @author William Bittle
 * @version 3.2.1
 * @since 3.2.0
 */
public class GraphBody extends SimulationBody {
    /** The image to use, if required */
    public BufferedImage image;

    public int hash;

    /* (non-Javadoc)
     * @see org.dyn4j.samples.SimulationBody#renderFixture(java.awt.Graphics2D, double, org.dyn4j.dynamics.BodyFixture, java.awt.Color)
     */
    @Override
    protected void renderFixture(Graphics2D g, double scale, BodyFixture fixture, Color color) {
        // do we need to render an image?
        if (this.image != null) {
            // get the shape on the fixture
            Convex convex = fixture.getShape();
            // check the shape type
            if (convex instanceof org.dyn4j.geometry.Rectangle) {
                org.dyn4j.geometry.Rectangle r = (Rectangle)convex;
                Vector2 c = r.getCenter();
                double w = r.getWidth();
                double h = r.getHeight();
                g.drawImage(image,
                        (int)Math.ceil((c.x - w / 2.0) * scale),
                        (int)Math.ceil((c.y - h / 2.0) * scale) + (int)Math.ceil(-h * scale),
                        (int)Math.ceil(w * scale),
                        (int)Math.ceil(-h * scale),
                        null);
            } else if (convex instanceof Circle) {
                // cast the shape to get the radius
                Circle c = (Circle) convex;
                double r = c.getRadius();
                Vector2 cc = c.getCenter();
                int x = (int)Math.ceil((cc.x - r) * scale);
                int y = (int)Math.ceil((cc.y - r) * scale);
                int w = (int)Math.ceil(r * 2 * scale);
                // lets us an image instead
                g.drawImage(image, x, y+w, w, -w, null);
            }
        } else {
            // default rendering
            super.renderFixture(g, scale, fixture, color);
        }
    }

}