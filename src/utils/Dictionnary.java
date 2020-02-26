package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Dictionnary {
    HashMap<String, List<String>> dictionnary;
    SortedSet<String> preposition;

    public Dictionnary(String file) {
        constructMaps(file);
    }

    private void constructMaps(String file) {
        dictionnary = new HashMap<>();
        preposition = new TreeSet<>();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(file))) {
            String word;
            String line;
            while ((word = br.readLine()) != null) {
                List<String> entry = new ArrayList<>();
                word = word.split("\\|")[0];
                line = br.readLine();
                String[] words = line.split("\\|");
                for (int i = 1; i < words.length; i++) {
                    entry.add(words[i]);
                }
                dictionnary.put(word, entry);
                if (words[0].equals("(Preposition)"))
                    preposition.add(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getSynonymes(String word) {
        //TODO test gender and number
        return dictionnary.get(word);
    }

    public boolean isPreposition(String word) {
        //TODO test gender and number
        return preposition.contains(word);
    }
}
