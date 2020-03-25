package utils;

import utils.graph.Clique;
import utils.graph.Node;
import utils.graph.Triplet;
import utils.graph.WeightedGraph;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GraphRenderer {

    private int sizeX;
    private int sizeY;

    private String file;
    private BufferedImage renderingTarget;
    private BufferedImage mask;

    private Map<WeightedGraph<Word>, Triplet<Integer>> rendered;

    public GraphRenderer(int sizeX, int sizeY, String outputFile, String maskImage) throws IOException {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.file = outputFile;
        renderingTarget = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_4BYTE_ABGR);
        //mask = ImageIO.read(new File(maskImage));
        rendered = new HashMap<>();
    }

    public void renderGraphs(WeightedGraph<Word> graph, float threshold) throws IOException {
        graph.simplify(threshold);
        List<Clique<Word>> cliques = graph.getCliques();
        for (Clique<Word> clique : cliques) {
            initClique(clique, graph);
        }
        Graphics2D g = renderingTarget.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, sizeX, sizeY);
        for (WeightedGraph<Word> gr : rendered.keySet())
            drawGraph(g, gr, threshold);
        ImageIO.write(renderingTarget, "png", new File(file));
    }

    public void drawGraph(Graphics2D g, WeightedGraph<Word> graph, float threshold) {
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
                    g.setStroke(new BasicStroke((float)(1/(1+Math.exp(-weight/2 + 3))) * 10));
                    g.setColor(new Color(0, 0, 0, Math.min(1, (float)(1/(1+Math.exp(-weight/5 + 1))))));
                    g.drawLine(pos.x, pos.y, pos2.x, pos2.y);
                }
            }
        }
        for (Word word : graph.getSet())
            if (word.rendered)
                drawCenteredString(g, word);
    }

    public void initClique(Clique<Word> clique, WeightedGraph<Word> reference) {
        WeightedGraph<Word> cGraph = clique.toGraph(reference);
        float initialAngle = 0;
        float d = 20*cGraph.size();
        int x, y;
        boolean overlap;
        do {
            overlap = false;
            x = (int) (new Random().nextInt((int) (sizeX - 2.2 * d)) + d);
            y = (int) (new Random().nextInt((int) (sizeY - 2.2 * d)) + d);
            for (Triplet<Integer> info : rendered.values()) {
                int distSquared = (x - info.getV1())*(x - info.getV1()) + (y - info.getV2())*(y - info.getV2());
                if (distSquared < Math.pow(1.2*info.getV3() + 1.2*d, 2)) {
                    overlap = true;
                    break;
                }
            }
        } while (overlap);
        rendered.put(cGraph, new Triplet<>(x, y, (int)(d)));
        int i = 0;
        for (Word word : cGraph.getSet()) {
            word.setPos(new Point((int) (x + d * Math.cos(i * 6.283184 / cGraph.size() + initialAngle)), (int) (y + d * Math.sin(i * 6.283184 / cGraph.size() + initialAngle))));
            word.setFont(new Font("TimesRoman", Font.PLAIN, (int) (word.getValue() * 7)));
            FontMetrics metrics = renderingTarget.getGraphics().getFontMetrics(word.getFont());
            word.setBoundingBox(new Rectangle(word.getPos().x - metrics.stringWidth(word.getWord()) / 2, word.getPos().y - metrics.getHeight() / 2, metrics.stringWidth(word.getWord()), metrics.getHeight()));
            i++;
        }
    }

    public void drawCenteredString(Graphics g, Word word) {
        Rectangle rect = word.getBoundingBox();
        FontMetrics metrics = g.getFontMetrics(word.getFont());
        int sx = rect.x + (rect.width - metrics.stringWidth(word.getWord())) / 2;
        int sy = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g.setFont(word.getFont());
        g.setColor(Color.ORANGE);
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
        g.setColor(Color.BLACK);
        g.drawString(word.getWord(), sx, sy);
    }
}
