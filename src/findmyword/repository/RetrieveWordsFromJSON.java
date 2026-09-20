package findmyword.repository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RetrieveWordsFromJSON {
    private final List<String> words;

    public RetrieveWordsFromJSON(String filePath) {
        this.words = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            Pattern p = Pattern.compile("\"([^\"]*)\"");
            Matcher m = p.matcher(sb.toString());
            while (m.find()) {
                String word = m.group(1);
                if (word.length() == 5) {
                    words.add(word);
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur de lecture du fichier JSON : " + e.getMessage());
        }
    }

    public List<String> getWords() {
        return words;
    }
}
