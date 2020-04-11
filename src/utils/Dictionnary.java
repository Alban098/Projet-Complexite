package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Class which represents
 */
public class Dictionnary {
    Map<String, List<String>> dictionnary;
    SortedSet<String> preposition;

    public Dictionnary(String file) {
        constructMaps(file);
    }

    /**
     * method to construct maps
     * @param file the filename of the dictionary
     */
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

        StringBuilder entry = new StringBuilder();
        try (BufferedReader br = Files.newBufferedReader(Paths.get("verbes.json"))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                entry.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * method to get a list of synonyms of a word
     * @param word to find synonym of
     * @return a list of synonyms of word
     */
    public List<String> getSynonyms(String word) {
        return dictionnary.get(word);
    }

    /**
     * method to verify if the word is a preposition or not
     * @param word to verify if it is a preposition
     * @return true if the world is a preposition
     */
    public boolean isPreposition(String word) {
        return preposition.contains(word);
    }
}
