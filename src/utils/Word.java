package utils;

public class Word implements Comparable<Word>{

    private String word;
    private float value;

    public Word(String word) {
        this.word = word;
        this.value = 1;
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
}
