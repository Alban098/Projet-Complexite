package utils;

import utils.graph.Duplicable;

import java.awt.*;

/**
 * Class which represents a word
 */
public class Word implements Comparable<Word>, Duplicable<Word> {

    private String word;
    private float value;
    private Point pos;
    private float boundingSphereRadius;
    private Font font;
    public boolean rendered = true;

    public Word(String word) {
        this.word = word;
        pos = new Point();
        boundingSphereRadius = 1;
        value = 1;
        setFont(new Font("TimesRoman", Font.PLAIN, (int) (getValue() * 10)));
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public float getValue() { return value; }

    public void setValue(float value) {
        this.value = value;
    }

    public String getWord() {
        return word;
    }

    public Point getPos() {
        return pos;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    public float getBoundingSphereRadius() {
        return boundingSphereRadius;
    }

    public void setBoundingSphereRadius(float boundingSphereRadius) {
        this.boundingSphereRadius = boundingSphereRadius;
    }

    /**
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(Word o) {
        return word.compareTo(o.word);
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Word)
            return word.equals(((Word)obj).word);
        return false;
    }

    /**
     *
     * @return
     */
    @Override
    public Word duplicate() {
        Word duplicate = new Word(word);
        duplicate.value = value;
        duplicate.pos = new Point(pos);
        duplicate.boundingSphereRadius = boundingSphereRadius;
        duplicate.font = font;
        return duplicate;
    }

    @Override
    public int hashCode() {
        return word.hashCode();
    }

    @Override
    public String toString() {
        return word;
    }


}
