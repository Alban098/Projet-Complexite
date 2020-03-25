package utils;

import utils.graph.WeightedGraph;

import java.util.ArrayList;
import java.util.List;

public class Sentence {

    List<Word> words;

    public Sentence() {
        this.words = new ArrayList<>();
    }

    public List<Word> getWords() {
        return words;
    }

    public void addWord(Word word) {
        if (words.contains(word))
            return;
        words.add(word);
    }

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
