package utils;

import utils.graph.Duplicable;

import java.awt.*;

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

    public void setValue(float value) {
        this.value = value;
    }

    public String getWord() {
        return word;
    }

    public float getValue() {
        return value;
    }

    public Point getPos() {
        return pos;
    }

    public float getBoundingSphereRadius() {
        return boundingSphereRadius;
    }

    public void setBoundingSphereRadius(float boundingSphereRadius) {
        this.boundingSphereRadius = boundingSphereRadius;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    @Override
    public int compareTo(Word o) {
        return word.compareTo(o.word);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Word)
            return word.equals(((Word)obj).word);
        return false;
    }


    @Override
    public int hashCode() {
        return word.hashCode();
    }

    @Override
    public String toString() {
        return word;
    }

    @Override
    public Word duplicate() {
        Word duplicate = new Word(word);
        duplicate.value = value;
        duplicate.pos = new Point(pos);
        duplicate.boundingSphereRadius = boundingSphereRadius;
        duplicate.font = font;
        return duplicate;
    }
}
