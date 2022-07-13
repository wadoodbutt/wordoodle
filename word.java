import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Random;

/* This class generates a random word based on the text files located on this computer. The word can be 3 to 6 letters
 * long. The user's answer can also be evaluated and checked to see if the word is the word bank.
 */
public class Word {

    public String wordleWord;
    public int wordleLength;
    Random r = new Random();

    public Word (int length, boolean isTheme, String theme) {
        if (isTheme) {
            switch (theme) {
                case "pokemon":
                    try {
                        wordleWord = generateThemePokemon();
                        wordleLength = wordleWord.length();
                        wordleWord = wordleWord.toLowerCase(Locale.ROOT);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else {
            if (length == 3) {
                try {
                    wordleLength = 3;
                    wordleWord = generate3LetterWord();
                    wordleWord = wordleWord.toLowerCase(Locale.ROOT);
                } catch (IOException e) {
                }
            } else if (length == 4) {
                try {
                    wordleLength = 4;
                    wordleWord = generate4LetterWord();
                    wordleWord = wordleWord.toLowerCase(Locale.ROOT);
                } catch (IOException e) {
                }
            } else if (length == 5) {
                try {
                    wordleLength = 5;
                    wordleWord = generate5LetterWord();
                    wordleWord = wordleWord.toLowerCase(Locale.ROOT);
                } catch (IOException e) {
                }
            } else {
                try {
                    wordleLength = 6;
                    wordleWord = generate6LetterWord();
                    wordleWord = wordleWord.toLowerCase(Locale.ROOT);
                } catch (IOException e) {
                }
            }
        }
    }

    public boolean isWord (String userAnswer, boolean isTheme) throws IOException {
        if (isTheme) {
            return themeConstrict(userAnswer);
        }
        switch (wordleWord.length()) {
            case 3:
                BufferedReader reader1 = new BufferedReader(
                        new FileReader(getClass().getResource("3letterwords.txt").getFile()));
                for (String curr = reader1.readLine();
                     curr != null; curr = reader1.readLine()) {
                    if (curr.toLowerCase(Locale.ROOT).equals(userAnswer)) {
                        return true;
                    }
                }
                return false;
            case 4:
                BufferedReader reader2 = new BufferedReader(
                        new FileReader(getClass().getResource("4letterwords.txt").getFile()));
                for (String curr = reader2.readLine();
                     curr != null; curr = reader2.readLine()) {
                    if (curr.toLowerCase(Locale.ROOT).equals(userAnswer)) {
                        return true;
                    }
                }
                return false;
            case 5:
                BufferedReader reader3 = new BufferedReader(
                        new FileReader(getClass().getResource("5letterwords.txt").getFile()));
                for (String curr = reader3.readLine();
                     curr != null; curr = reader3.readLine()) {
                    if (curr.toLowerCase(Locale.ROOT).equals(userAnswer)) {
                        return true;
                    }
                }
                return false;
            case 6:
                BufferedReader reader4 = new BufferedReader(
                        new FileReader(getClass().getResource("6letterwords.txt").getFile()));
                for (String curr = reader4.readLine();
                     curr != null; curr = reader4.readLine()) {
                    if (curr.toLowerCase(Locale.ROOT).equals(userAnswer)) {
                        return true;
                    }
                }
                return false;
        }
        return false;
    }

    private boolean themeConstrict(String userAnswer) throws IOException {
        BufferedReader reader1 = new BufferedReader(
                new FileReader(getClass().getResource("worldwordbank.txt").getFile()));
        for (String curr = reader1.readLine();
             curr != null; curr = reader1.readLine()) {
            if (curr.toLowerCase(Locale.ROOT).equals(userAnswer)) {
                return true;
            }
        }
        return false;
    }

    public String generate3LetterWord() throws IOException {
        BufferedReader reader = new BufferedReader(
                new FileReader(getClass().getResource("3letterwords.txt").getFile()));
        int rng = r.nextInt(1014);
        String word = "";

        int counter = 0;
        for (String curr = reader.readLine();
             curr != null; curr = reader.readLine()) {
            if (rng == counter) {
                word = curr;
            }
            counter++;
        }
        return word;
    }

    public String generate4LetterWord() throws IOException {
        BufferedReader reader = new BufferedReader(
                new FileReader(getClass().getResource("4letterwords.txt").getFile()));
        int rng = r.nextInt(3130);
        String word = "";

        int counter = 0;
        for (String curr = reader.readLine();
             curr != null; curr = reader.readLine()) {
            if (rng == counter) {
                word = curr;
            }
            counter++;
        }
        return word;
    }

    public String generate5LetterWord() throws IOException {
        BufferedReader reader = new BufferedReader(
                new FileReader(getClass().getResource("5letterwords.txt").getFile()));
        int rng = r.nextInt(5758);
        String word = "";

        int counter = 0;
        for (String curr = reader.readLine();
             curr != null; curr = reader.readLine()) {
            if (rng == counter) {
                word = curr;
            }
            counter++;
        }
        return word;
    }

    public String generate6LetterWord() throws IOException {
        BufferedReader reader = new BufferedReader(
                new FileReader(getClass().getResource("6letterwords.txt").getFile()));
        int rng = r.nextInt(1228);
        String word = "";

        int counter = 0;
        for (String curr = reader.readLine();
             curr != null; curr = reader.readLine()) {
            if (rng == counter) {
                word = curr;
            }
            counter++;
        }
        return word;
    }

    public String generateThemePokemon() throws IOException {
        return null;
    }

    public String generateThemeDBZ() throws IOException {
        return null;
    }

    public String generateThemeAnimal() throws IOException {
        return null;
    }
}
