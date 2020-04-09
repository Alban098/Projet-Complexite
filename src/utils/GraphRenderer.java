package utils;

import utils.graph.Clique;
import utils.graph.Node;
import utils.graph.Triplet;
import utils.graph.WeightedGraph;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Class which represents
 */
public class GraphRenderer {

    private static final int NODE_RADIUS = 40;
    private static final int MAX_ITERARTION = 1000;

    private int sizeX;
    private int sizeY;

    private String file;
    private BufferedImage renderingTarget;
    private BufferedImage mask;

    private Map<Word, Integer> count;
    private Map<Word, List<Point>> duplicate;
    private Map<Word, Color> wordColorMap;

    private Map<WeightedGraph<Word>, BufferedImage> textures;
    private Map<WeightedGraph<Word>, Map<WeightedGraph<Word>, Float>> connections;

    private Stack<Color> colors;

    private Map<WeightedGraph<Word>, Triplet<Integer>> rendered;
    private static GraphRenderer instance;

    public static GraphRenderer getInstance() {
        return instance;
    }

    public static GraphRenderer initInstance(int sizeX, int sizeY, String outputFile, String maskImage) throws Exception {
        if (instance == null)
            return new GraphRenderer(sizeX, sizeY, outputFile, maskImage);
        throw new Exception("An instance already exist");
    }

    private GraphRenderer(int sizeX, int sizeY, String outputFile, String maskImage) throws IOException {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.file = outputFile;
        renderingTarget = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_4BYTE_ABGR);
        mask = ImageIO.read(new File(maskImage));
        rendered = new HashMap<>();
        count = new HashMap<>();
        colors = new Stack<>();
        wordColorMap = new HashMap<>();
        duplicate = new HashMap<>();
        textures = new HashMap<>();
        connections = new HashMap<>();
        instance = this;
    }

    public int getSizeX() {
        return sizeX;
    }

    public void setSizeX(int sizeX) {
        this.sizeX = sizeX;
        renderingTarget = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_4BYTE_ABGR);
    }

    public int getSizeY() {
        return sizeY;
    }

    public void setSizeY(int sizeY) {
        this.sizeY = sizeY;
        renderingTarget = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_4BYTE_ABGR);
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
        renderingTarget = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_4BYTE_ABGR);
    }

    public Map<WeightedGraph<Word>, BufferedImage> getTextures() {
        return textures;
    }

    public Map<WeightedGraph<Word>, Map<WeightedGraph<Word>, Float>> getConnections() {
        return connections;
    }

    /**
     *
     * @param graph
     * @param threshold
     * @param save
     * @throws Exception
     */
    public void renderGraphs(WeightedGraph<Word> graph, float threshold, boolean save) throws Exception {
        graph.simplify(threshold);

        colors.clear();
        colors.push(new Color(0x696969));colors.push(new Color(0x556b2f));colors.push(new Color(0xa0522d));colors.push(new Color(0x8b0000));colors.push(new Color(0x808000));colors.push(new Color(0x483d8b));colors.push(new Color(0x008000));colors.push(new Color(0x3cb371));colors.push(new Color(0xbc8f8f));colors.push(new Color(0x008080));colors.push(new Color(0x4682b4));colors.push(new Color(0x9acd32));colors.push(new Color(0xdaa520));colors.push(new Color(0x8fbc8f));colors.push(new Color(0x00ced1));colors.push(new Color(0xff8c00));colors.push(new Color(0xc71585));colors.push(new Color(0x00ff00));colors.push(new Color(0x8a2be2));colors.push(new Color(0x00bfff));colors.push(new Color(0xf4a460));colors.push(new Color(0xb0c4de));colors.push(new Color(0x1e90ff));colors.push(new Color(0xdb7093));colors.push(new Color(0xeee8aa));colors.push(new Color(0xdda0dd));colors.push(new Color(0x7b68ee));colors.push(new Color(0x8b008b));colors.push(new Color(0xff4500));colors.push(new Color(0xadff2f));colors.push(new Color(0x1e90ff));colors.push(new Color(0xfa8072));colors.push(new Color(0xda70d6));colors.push(new Color(0x7fffd4));colors.push(new Color(0xffd700));colors.push(new Color(0x32cd32));colors.push(new Color(0xdc143c));

        Graphics2D g = renderingTarget.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, sizeX, sizeY);
        List<Clique<Word>> cliques = graph.getCliques();
        boolean complete;
        int nbIteration = 0;
        do {
            if (nbIteration > MAX_ITERARTION)
                throw new Exception("Unable to find a valide disposition, try reducine NODE_RADIUS");
            rendered.clear();
            count.clear();
            duplicate.clear();
            textures.clear();
            wordColorMap.clear();
            connections.clear();
            complete = true;
            for (Clique<Word> clique : cliques) {
                if (!initClique(clique, graph)) {
                    complete = false;
                    break;
                }
            }
            nbIteration++;
        } while (!complete);

        for (Map.Entry<Word, Integer> entry : count.entrySet())
            wordColorMap.put(entry.getKey(), (entry.getValue() > 1 ? colors.pop() : Color.WHITE));

        for (Map.Entry<WeightedGraph<Word>, Triplet<Integer>> subSet1 : rendered.entrySet()) {
            WeightedGraph<Word> graph1 = subSet1.getKey();
            Triplet<Integer> t1 = subSet1.getValue();
            Map<WeightedGraph<Word>, Float> list = new HashMap<>();
            for (Map.Entry<WeightedGraph<Word>, Triplet<Integer>> subSet2 : rendered.entrySet()) {
                WeightedGraph<Word> graph2 = subSet2.getKey();
                Triplet<Integer> t2 = subSet2.getValue();
                if (graph2.equals(graph1)) continue;
                float weight = 0;
                for (Word w1 : graph1.getSet()) {
                    for (Word w2 : graph2.getSet()) {
                        float connection = graph.getConnectionWeight(w1, w2);
                        if (connection > threshold)
                            weight += connection;
                    }
                }
                if (weight > 0) {
                    g.setStroke(new BasicStroke((float)(1/(1+Math.exp(-weight/10 + 5))) * 50));
                    g.setColor(Color.BLUE);
                    g.drawLine(t1.getV1(), t1.getV2(), t2.getV1(), t2.getV2());
                    list.put(graph2, weight);
                }
            }
            connections.put(graph1, list);
        }

        for (Triplet<Integer> triplet : rendered.values()) {
            g.setColor(Color.LIGHT_GRAY);
            g.setStroke(new BasicStroke(10));
            g.fillOval(triplet.getV1() - triplet.getV3() - 5*NODE_RADIUS / 4, triplet.getV2() - triplet.getV3() - 5*NODE_RADIUS / 4, triplet.getV3() * 2 + 5*NODE_RADIUS / 2, triplet.getV3() * 2 + 5*NODE_RADIUS / 2);
            g.setColor(Color.BLUE);
            g.drawOval(triplet.getV1() - triplet.getV3() - 5*NODE_RADIUS / 4, triplet.getV2() - triplet.getV3() - 5*NODE_RADIUS / 4, triplet.getV3() * 2 + 5*NODE_RADIUS / 2, triplet.getV3() * 2 + 5*NODE_RADIUS / 2);
        }
        for (WeightedGraph<Word> gr : rendered.keySet())
            drawGraph(g, gr, threshold);
        if (save)
            ImageIO.write(renderingTarget, "png", new File(file));
    }

    /**
     *
     * @param g
     * @param graph
     * @param threshold
     */
    private void drawGraph(Graphics2D g, WeightedGraph<Word> graph, float threshold) {
        Triplet<Integer> graphInfos = rendered.get(graph);
        int d = graphInfos.getV3();
        BufferedImage img = new BufferedImage(2*(d + 5*NODE_RADIUS/4), 2*(d + 5*NODE_RADIUS/4), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D imgGraphics2D = img.createGraphics();
        imgGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        imgGraphics2D.setColor(Color.LIGHT_GRAY);
        imgGraphics2D.setStroke(new BasicStroke(10));
        imgGraphics2D.fillOval(img.getWidth() / 2 - graphInfos.getV3() - 5*NODE_RADIUS / 4, img.getHeight() / 2 - graphInfos.getV3() - 5*NODE_RADIUS / 4, graphInfos.getV3() * 2 + 5*NODE_RADIUS / 2, graphInfos.getV3() * 2 + 5*NODE_RADIUS / 2);
        imgGraphics2D.setColor(Color.BLUE);

        for (Word word : graph.getSet()) {
            Point pos = word.getPos();
            Node<Word> node = graph.getNode(word);
            word.rendered = false;
            if (node == null) continue;
            Map<Node<Word>, Float> neighbors = node.getNeighbors();
            for (Node<Word> n : neighbors.keySet()) {
                Word w = n.getValue();
                float weight = graph.getConnectionWeight(word, w);
                if (weight >= threshold) {
                    word.rendered = true;
                    Point pos2 = w.getPos();
                    g.setStroke(new BasicStroke((float)(1/(1+Math.exp(-weight/2 + 5))) * 50));
                    g.setColor(new Color(0, 0, 0, Math.min(1, (float)(1/(1+Math.exp(-weight + 5))))));
                    g.drawLine(pos.x, pos.y, pos2.x, pos2.y);
                    Point posbis = new Point(word.getPos().x - graphInfos.getV1() + img.getWidth() / 2, word.getPos().y - graphInfos.getV2() + img.getHeight() / 2);
                    Point pos2bis = new Point(w.getPos().x - graphInfos.getV1() + img.getWidth() / 2, w.getPos().y - graphInfos.getV2() + img.getHeight() / 2);
                    imgGraphics2D.setStroke(new BasicStroke((float)(1/(1+Math.exp(-weight/2 + 5))) * 50));
                    imgGraphics2D.setColor(new Color(0, 0, 0, Math.min(1, (float)(1/(1+Math.exp(-weight + 5))))));
                    imgGraphics2D.drawLine(posbis.x, posbis.y, pos2bis.x, pos2bis.y);
                }
            }
        }
        for (Word word : graph.getSet())
            if (word.rendered) {
                drawCenteredString(g, word, wordColorMap.get(word));
                Word w2 = word.duplicate();
                w2.setPos(new Point(word.getPos().x - graphInfos.getV1() + img.getWidth() / 2, word.getPos().y - graphInfos.getV2() + img.getHeight() / 2));
                drawCenteredString(imgGraphics2D, w2, wordColorMap.get(word));
            }
        textures.put(graph, img);
    }

    /**
     * method to initialise
     * @param clique
     * @param reference
     * @return
     */
    private boolean initClique(Clique<Word> clique, WeightedGraph<Word> reference) {
        WeightedGraph<Word> cGraph = clique.toGraph(reference);
        float initialAngle = 0;
        float d = (float) (NODE_RADIUS*1.2/Math.sin(3.14159265 / cGraph.size()));
        int x, y;
        boolean overlap;
        int nbIter = 0;
        do {
            if (nbIter == MAX_ITERARTION) return false;
            overlap = false;
            x = (int) (new Random().nextInt((int) (sizeX - 2 * (d + (d + 5*NODE_RADIUS/4)))) + d + (d + 5*NODE_RADIUS/4));
            y = (int) (new Random().nextInt((int) (sizeY - 2 * (d + (d + 5*NODE_RADIUS/4)))) + d + (d + 5*NODE_RADIUS/4));
            for (Triplet<Integer> info : rendered.values()) {
                int distSquared = (x - info.getV1())*(x - info.getV1()) + (y - info.getV2())*(y - info.getV2());
                if (distSquared < Math.pow(info.getV3() + 2*d + NODE_RADIUS, 2)) {
                    overlap = true;
                    break;
                }
            }
            if (!overlap) {
                for (Point p : Utils.getCoveringSet(x, y, (int) (d + 5*NODE_RADIUS/4) / 2)) {
                    int maskX = (int) ((double)p.x/renderingTarget.getWidth() * mask.getWidth());
                    int maskY = (int) ((double)p.y/renderingTarget.getHeight() * mask.getHeight());
                    if (mask.getRGB(maskX, maskY) == Color.BLACK.getRGB()) {
                        overlap = true;
                        break;
                    }
                }
            }
            nbIter++;
        } while (overlap);
        rendered.put(cGraph, new Triplet<>(x, y, (int)(d)));
        int i = 0;
        for (Word word : cGraph.getSet()) {
            word.setPos(new Point((int) (x + d * Math.cos(i * 6.283184 / cGraph.size() + initialAngle)), (int) (y + d * Math.sin(i * 6.283184 / cGraph.size() + initialAngle))));
            word.setFont(new Font("MONOSPACED", Font.PLAIN,  (int)(3*NODE_RADIUS/word.getWord().length())));
            word.setBoundingSphereRadius(NODE_RADIUS);
            i++;
            if (count.containsKey(word)) {
                duplicate.get(word).add(word.getPos());
                count.replace(word, count.get(word) + 1);
            } else {
                List<Point> list = new ArrayList<>();
                list.add(word.getPos());
                duplicate.put(word, list);
                count.put(word, 1);
            }
        }
        return true;
    }

    /**
     * method to draw
     * @param g
     * @param word
     * @param background
     */
    private void drawCenteredString(Graphics g, Word word, Color background) {
        FontMetrics metrics = g.getFontMetrics(word.getFont());
        Rectangle rect = new Rectangle(word.getPos().x - metrics.stringWidth(word.getWord()) / 2, word.getPos().y - metrics.getHeight() / 2, metrics.stringWidth(word.getWord()), metrics.getHeight());
        int sx = rect.x + (rect.width - metrics.stringWidth(word.getWord())) / 2;
        int sy = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g.setFont(word.getFont());
        g.setColor(background);
        ((Graphics2D)g).setStroke(new BasicStroke(5));
        g.fillOval((int) (rect.x + rect.width/2f - NODE_RADIUS), (int) (rect.y + rect.height/2f - NODE_RADIUS), NODE_RADIUS*2, NODE_RADIUS*2);
        g.setColor(Color.BLACK);
        g.drawOval((int) (rect.x + rect.width/2f - NODE_RADIUS), (int) (rect.y + rect.height/2f - NODE_RADIUS), NODE_RADIUS*2, NODE_RADIUS*2);
        g.drawString(word.getWord(), sx, sy);
    }
}
