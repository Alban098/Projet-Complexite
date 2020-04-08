import utils.*;
import utils.graph.*;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class Test extends JPanel {


    public static void main(String[] args) throws Exception {
        Dictionnary dictionnary = new Dictionnary("thes_fr.dat");
        WeightedGraph<Word> graph = new WeightedGraph<>();
        String text = getFormattedText("text.txt");
        List<Word> textWords = new ArrayList<>();
        String[] paragraphsArray = text.split("¶");

        for (String paragraphString : paragraphsArray) {
            Paragraph paragraph = new Paragraph();
            String[] sentencesArray = paragraphString.split("\\.( )*");
            for (String sentenceString : sentencesArray) {
                String[] words = sentenceString.split(" ");
                Sentence sentence = new Sentence();
                for (String word : words) {
                    if (!dictionnary.isPreposition(word)) {
                        Word w = new Word(word);
                        sentence.addWord(w);
                        if (textWords.contains(w)) {
                            Word inList = textWords.get(textWords.indexOf(w));
                            inList.setValue(inList.getValue() + 1);
                            w.setValue(inList.getValue());
                        }
                        else
                            textWords.add(w);
                        graph.add(w);
                    }
                }
                sentence.connect(graph);
                paragraph.addSentence(sentence);
            }
        }
        List<Pair<Word>> best = graph.getStrongestConnection(3);
        GraphRenderer renderer = GraphRenderer.initInstance(1920, 1080, "test5.png", "mask1080.png");
        renderer.renderGraphs(graph, 1.5f, true);
        Simulation simulation = new Simulation();
        simulation.run();
    }

    private static String getFormattedText(String file) {
        StringBuilder text = new StringBuilder();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replaceAll("[',\":()\\[\\]{};/\\\\]",  " ");
                line = line.replaceAll("( )+", " ");
                line = line.replaceAll(" .{1,2} ", " ");
                line = line.replaceAll(" .{1,2} ", " ");
                line = line.replaceAll(" .{1,2} ", " ");
                line = line.replaceAll("^.{1,2} ", "");
                line = line.replaceAll("^.{1,2} ", "");
                line = line.replaceAll("^.{1,2} ", "");
                line = line.toLowerCase();
                text.append(line).append("¶");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }
}
