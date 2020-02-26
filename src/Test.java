import utils.Dictionnary;
import utils.Word;
import utils.graph.Node;
import utils.graph.WeightedGraph;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Test {

    public static void main(String[] args) {
        Dictionnary dictionnary = new Dictionnary("thes_fr.dat");
        WeightedGraph<Word> graph = new WeightedGraph<>();

        String text = getFormattedText("text.txt");
        Map<Word, Integer> textWords = new HashMap<>();
        String[] paragraphs = text.split("¶");
        for (String paragraph : paragraphs) {
            SortedSet<Word> paragraphWords = new TreeSet<>();
            String[] sentences = paragraph.split("\\.( )*");
            for (String sentence : sentences) {
                String[] words = sentence.split(" ");
                SortedSet<Word> sentenceWords = new TreeSet<>();
                for (String word : words) {
                    if (!dictionnary.isPreposition(word)) {
                        Word w = new Word(word);
                        paragraphWords.add(w);
                        sentenceWords.add(w);
                        if (textWords.containsKey(w))
                            textWords.replace(w, textWords.get(w) + 1);
                        else
                            textWords.put(w, 1);
                        graph.add(new Word(word));
                    }
                }
                for (Word w1 : sentenceWords) {
                    for (Word w2 : sentenceWords) {
                        if (!w1.equals(w2))
                            graph.connect(w1, w2, 0.5f);
                    }
                }
            }
            for (Word w1 : paragraphWords) {
                for (Word w2 : paragraphWords) {
                    if (!w1.equals(w2))
                        graph.connect(w1, w2, 0.05f);
                }
            }
        }
        for (Word word : textWords.keySet()) {
            word.setValue(textWords.get(word));
            System.out.println(word.getWord() + " : " + word.getValue());
        }
    }

    public static String getFormattedText(String file) {
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
