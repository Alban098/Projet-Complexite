package utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Class which represents a paragraph
 * composed of sentences
 */
public class Paragraph {

    List<Sentence> sentences;

    public Paragraph() {
        this.sentences = new ArrayList<>();
    }

    public List<Sentence> getSentences() {
        return sentences;
    }

    /**
     * method to add a sentence to a paragraph
     * @param sentence to add to
     */
    public void addSentence(Sentence sentence) {
        sentences.add(sentence);
    }
}
