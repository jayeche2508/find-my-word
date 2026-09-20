package findmyword.model;

public class Word {
    private final String value;

    public Word(String value) {
        this.value = value.trim().toLowerCase();
    }

    public String getValue() {
        return value;
    }

    public boolean isValid() {
        return hasExactLength() && hasOnlyLetters() && hasNoRepeat();
    }

    public boolean hasExactLength() {
        return value.length() == 5;
    }

    public boolean hasOnlyLetters() {
        return value.matches("^[a-z]+$");
    }

    public boolean hasNoRepeat() {
        for (int i = 0; i < value.length(); i++) {
            for (int j = i + 1; j < value.length(); j++) {
                if (value.charAt(i) == value.charAt(j)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean contains(char c) {
        return value.indexOf(c) != -1;
    }

    public char charAt(int i) {
        return value.charAt(i);
    }

    public int length() {
        return value.length();
    }
}
