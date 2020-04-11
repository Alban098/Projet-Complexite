import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import framework.*;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.DetectResult;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.RopeJoint;
import org.dyn4j.geometry.*;
import utils.ColorGradient;
import utils.GraphRenderer;
import utils.Word;
import utils.graph.WeightedGraph;


public final class Simulation extends SimulationFrame {

    /** The serial version id */
    private static final long serialVersionUID = -8518496343422955267L;
    private static final int DRAG = 1;
    private static final int STOP = 2;

    /** The picking radius */
    private static final double PICKING_RADIUS = 0.1;

    /** A point for tracking the mouse click */
    private Point point;
    private int action;

    /** The world space mouse point */
    private Vector2 worldPoint = new Vector2();

    /** The picking results */
    private List<DetectResult> results = new ArrayList<DetectResult>();
    private Body selected;

    private ColorGradient joinGradient;


    public Simulation() {
        super("Physics graph", 50);
        MouseAdapter ml = new CustomMouseAdapter();
        this.canvas.addMouseMotionListener(ml);
        this.canvas.addMouseWheelListener(ml);
        this.canvas.addMouseListener(ml);
        joinGradient = new ColorGradient(Color.GREEN, 1.0, Color.RED, 1.0);
        joinGradient.addColor(Color.YELLOW, 0.5, 1.0);
    }

    /**
     * Initialize the simulation
     */
    @Override
    protected void initializeWorld() {
        this.world.setGravity(World.ZERO_GRAVITY);

        Map<Integer, GraphBody> graphBodyMap = new HashMap<>();

        SimulationBody wallr = new SimulationBody();
        wallr.addFixture(Geometry.createRectangle(.6, getHeight()));
        wallr.translate(getWidth()/100.0, 0);
        wallr.setMass(MassType.INFINITE);
        wallr.setColor(Color.RED);
        world.addBody(wallr);
        SimulationBody walll = new SimulationBody();
        walll.addFixture(Geometry.createRectangle(.6, getHeight()));
        walll.translate(-getWidth()/100.0, 0);
        walll.setMass(MassType.INFINITE);
        walll.setColor(Color.RED);
        world.addBody(walll);
        SimulationBody wallt = new SimulationBody();
        wallt.addFixture(Geometry.createRectangle(getWidth(), 1));
        wallt.translate(0, -getHeight()/100.0);
        wallt.setMass(MassType.INFINITE);
        wallt.setColor(Color.RED);
        world.addBody(wallt);
        SimulationBody wallb = new SimulationBody();
        wallb.addFixture(Geometry.createRectangle(getWidth(), 1));
        wallb.translate(0, getHeight()/100.0);
        wallb.setMass(MassType.INFINITE);
        wallb.setColor(Color.RED);
        world.addBody(wallb);

        for (Map.Entry<WeightedGraph<Word>, BufferedImage> entry : GraphRenderer.getInstance().getTextures().entrySet()) {
            GraphBody ball = new GraphBody();
            ball.hash = entry.getKey().hashCode();
            ball.image = entry.getValue();
            ball.addFixture(Geometry.createCircle(ball.image.getWidth() / (3 * scale)), 1000, 0.1, 0.8);
            ball.setMass(MassType.NORMAL);
            ball.setAngularDamping(10000);
            graphBodyMap.put(ball.hash, ball);
            this.world.addBody(ball);
        }
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        for (Map.Entry<WeightedGraph<Word>, Map<WeightedGraph<Word>, Float>> entry : GraphRenderer.getInstance().getConnections().entrySet()) {
            int hash = entry.getKey().hashCode();
            GraphBody body1 = graphBodyMap.get(hash);
            if (body1 != null) {
                for (Map.Entry<WeightedGraph<Word>, Float> entry2 : entry.getValue().entrySet()) {
                    Body body2 = graphBodyMap.get(entry2.getKey().hashCode());
                    if (body2 != null) {
                        boolean duplicate = false;
                        for (Joint joint : world.getJoints()) {
                            if ((joint.getBody1().equals(body1) && joint.getBody2().equals(body2)) || (joint.getBody1().equals(body2) && joint.getBody2().equals(body1))) {
                                duplicate = true;
                                break;
                            }
                        }
                        if (duplicate)
                            continue;
                        RopeJoint pin = new RopeJoint(body1, body2, new Vector2(0, 0), new Vector2(0, 0));
                        pin.setLimits(2, 20 / (1 + Math.exp(-(100 - entry2.getValue()) / 20.0 + 1)));
                        pin.setCollisionAllowed(true);
                        world.addJoint(pin);
                        if (entry2.getValue() > max)
                            max = entry2.getValue();
                        if (entry2.getValue() < min)
                            min = entry2.getValue();
                    }
                }
            }
        }
        System.out.println(min + " " + max);
    }

    /**
     * Render the scene to the canvas
     * @param g the graphics object to render to
     * @param elapsedTime the elapsed time from the last update
     */
    protected void render(Graphics2D g, double elapsedTime) {
        for (Joint joint : world.getJoints()) {
            if (joint instanceof RopeJoint) {
                Vector2 p1 = joint.getBody1().getWorldCenter();
                Vector2 p2 = joint.getBody2().getWorldCenter();
                double val = ((RopeJoint) joint).getUpperLimit();
                val = (100 + (Math.log(1/(val / 20) - 1) - 1) * 20)/300;
                if (!p1.equals(p2)) {
                    Vector2 p3 = new Vector2(p1.x - p2.x, p1.y - p2.y);
                    double alpha = Math.acos(p3.x/p3.getMagnitude()) + Math.PI/2;
                    Vector2 offset = new Vector2(val * Math.cos(alpha), val * Math.sin(alpha));
                    Vector2[] vertices = {
                            new Vector2(p1.x + offset.x, p1.y + offset.y),
                            new Vector2(p2.x + offset.x, p2.y + offset.y),
                            new Vector2(p2.x - offset.x, p2.y - offset.y),
                            new Vector2(p1.x - offset.x, p1.y - offset.y)
                    };
                    if (Geometry.getWinding(vertices) < 0)
                        Geometry.reverseWinding(vertices);
                    double up = ((RopeJoint) joint).getUpperLimit();
                    double low = ((RopeJoint) joint).getLowerLimit();
                    double percent = (new Vector2(p2.x - p1.x, p2.y - p1.y).getMagnitude() - low)/(up - low);
                    Graphics2DRenderer.render(g, Geometry.createPolygon(vertices), this.scale, joinGradient.getColor(percent));
                }
            }
        }

        super.render(g, elapsedTime);

        if (this.point != null) {
            AffineTransform tx = g.getTransform();
            g.translate(this.worldPoint.x * this.scale, this.worldPoint.y * this.scale);
            Graphics2DRenderer.render(g, Geometry.createCircle(PICKING_RADIUS), this.scale, Color.GREEN);
            g.setTransform(tx);
        }
        if (this.selected != null) {
            AffineTransform tx = g.getTransform();
            g.translate(this.selected.getWorldCenter().x * this.scale, this.selected.getWorldCenter().y * this.scale);
            Graphics2DRenderer.render(g, Geometry.createCircle(2*PICKING_RADIUS), this.scale, Color.BLUE);
            g.setTransform(tx);
        }
    }

    /**
     * Update the scene
     * @param g the graphics object to render to
     * @param elapsedTime the elapsed time from the last update
     */
    @Override
    protected void update(Graphics2D g, double elapsedTime) {
        super.update(g, elapsedTime);
        g.scale(2, 2);
        final double scale = this.scale;
        this.results.clear();

        Convex convex = Geometry.createCircle(PICKING_RADIUS);
        Transform transform = new Transform();
        double x = 0;
        double y = 0;

        if (this.point != null) {
            x =  (this.point.getX() - this.canvas.getWidth() * 0.5) / scale;
            y = -(this.point.getY() - this.canvas.getHeight() * 0.5) / scale;
            this.worldPoint.set(x, y);

            transform.translate(x, y);

            if (selected == null) {
                this.world.detect(convex, transform,null,false,false,false, this.results);
                if (results.size() > 0)
                    selected = results.get(0).getBody();
            }
            if (selected != null && selected.getFixture(0).getShape() instanceof Circle) {
                if (action == DRAG) {
                    selected.setAsleep(false);
                    selected.setLinearVelocity(worldPoint.x - selected.getWorldCenter().x, worldPoint.y - selected.getWorldCenter().y);
                } else if (action == STOP) {
                    selected.setAsleep(false);
                    selected.setLinearVelocity(0, 0);
                    selected.setAngularVelocity(0);
                }
            }
        }
    }


    private final class CustomMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            point = new Point(e.getX(), e.getY());
            if (e.getButton() == MouseEvent.BUTTON1)
                action = DRAG;
            if (e.getButton() == MouseEvent.BUTTON3)
                action = STOP;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            point = new Point(e.getX(), e.getY());
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            point = null;
            selected = null;
        }
    }
}
