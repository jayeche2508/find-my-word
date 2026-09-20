package findmyword.repository;

import findmyword.model.Word;

public class FixedWordRepository extends WordRepository {
    private final Word fixedWord;

    public FixedWordRepository(String wordValue) {
        this.fixedWord = new Word(wordValue);
    }

    @Override
    public Word getWord() {
        return fixedWord;
    }
}
