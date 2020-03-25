package utils;

import java.util.ArrayList;
import java.util.List;

public class Paragraph {

    List<Sentence> sentences;

    public Paragraph() {
        this.sentences = new ArrayList<>();
    }

    public List<Sentence> getSentences() {
        return sentences;
    }

    public void addSentence(Sentence sentence) {
        sentences.add(sentence);
    }
}
