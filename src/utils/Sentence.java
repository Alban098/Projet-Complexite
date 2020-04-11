package utils;

import utils.graph.WeightedGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Class which represents a sentence
 * composed of words
 */
public class Sentence {

    List<Word> words;

    public Sentence() {
        this.words = new ArrayList<>();
    }

    public List<Word> getWords() {
        return words;
    }

    /**
     * method to add a word to a sentence
     * @param word to add to the sentence
     */
    public void addWord(Word word) {
        if (words.contains(word))
            return;
        words.add(word);
    }

    /**
     * method to connect to words
     * @param graph in which to connect the words
     */
    public void connect(WeightedGraph<Word> graph) {
        for (Word w1 : words) {
            for (Word w2 : words) {
                if (!w1.equals(w2))
                    graph.connect(w1, w2, 0.5f);
            }
        }
    }

    @Override
    public String toString() {
        String val = "";
        for (Word w : words)
            val += " " + w.getWord();
        return val;
    }
}
