package findmyword.repository;

import findmyword.model.Word;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JsonWordRepository extends WordRepository {
    private final String filePath;
    private final List<Word> words;
    private final Random random;

    public JsonWordRepository(String filePath) {
        this.filePath = filePath;
        this.words = new ArrayList<>();
        this.random = new Random();
        loadWords();
    }

    private void loadWords() {
        RetrieveWordsFromJSON reader = new RetrieveWordsFromJSON(filePath);
        for (String value : reader.getWords()) {
            Word w = new Word(value);
            if (w.isValid()) {
                words.add(w);
            }
        }
    }

    @Override
    public Word getWord() {
        if (words.isEmpty()) {
            throw new IllegalStateException("Mots inexistants.");
        }
        return words.get(random.nextInt(words.size()));
    }
}
