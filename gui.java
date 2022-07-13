import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;

/* This class runs the GUI and creates the actual visual wordle. It will track the user's answer via ActionListeners
 * and KeyListeners as well as DocumentListeners. It will process the word, determine if it meets the criteria to be
 * considered, and evaluated to see if the word is correct or not. Various hints will be given to the user. The user
 * has the size of the wordle number of attempts (i.e. 3 attempts if the wordle is 3 letters long) to guess the word.
 * The answer will be revealed at the end or if the user guesses correctly.
 */

public class GUI {

    protected boolean isTheme = false; // If there is a theme
    protected String theme = ""; // Theme
    protected int numOfWords; // Number of Words the user desires
    private String userAnswer; // User's answer
    private Set<Integer> visitedSet; // Visited Rows
    private int index = 0; // Index (used to help identify the user's answer at different rows) of user's answer in array
    private int copyOfWordSize = 0; // Mutable limit used for the for-loops
    private String arrAnswer[]; // User's answer in array format
    JTextField entry[] = new JTextField[numOfWords*numOfWords]; // Number of entries

    private static Object sharedLock = new Object(); // Used to make the Driver wait for the user's response
    // Options that the user can select from
    JButton option1 = new JButton("3 Words");
    JButton option2 = new JButton("4 Words");
    JButton option3 = new JButton("5 Words");
    JButton option4 = new JButton("6 Words");
    JButton option5 = new JButton("Wordle Based on Theme");
    JLabel clarify = new JLabel("Generate Random Wordle or Select a themed Wordle");
    String themes[] = {"Cars", "Pokemon", "DBZ", "Animal"};
    JComboBox<String> thematicOptions = new JComboBox<>(themes);

    JFrame window = new JFrame("Wordoodle"); // Window name
    JFrame tutorial = new JFrame("Wordoodle"); // Window name

    // Constructor
    public GUI () {
        userAnswer = "";
        visitedSet = new HashSet<>();
    }
    /* Takes in the wordle (answer), the length of the wordle, and an object from the Word class (used to evaluate
     * whether the user's answer is in the word bank). This method calls on other methods to determine the next course
     * of action based on the user's answer. This is essentially a mini-driver of its own.
     */
    public void runGame(String wordle, int wordSize, Word word) throws InterruptedException {
        System.out.println(wordle + "|" + wordSize);
        JPanel panel = new JPanel(new SpringLayout());
        entry = new JTextField[wordSize*wordSize]; // Instantiates the number of entries
        arrAnswer = new String[wordSize*wordSize]; // Instantiates the number of letters possible
        copyOfWordSize = wordSize; // Instantiates the copy of the word size

        for (int i = 0; i < wordSize*wordSize; i++) {
            arrAnswer[i] = ""; // Makes each index "empty"
        }
        ImageIcon image = new ImageIcon(getClass().getResource("wordlebg.png"));
        JLabel background = new JLabel(image);
        background.setSize(800,900);
        window.add(background);
        window.setSize(800, 800);
        window.getContentPane().add(panel);
        window.setResizable(false);
        /* The user clicks the 'enter' key when they have their word is ready.
         *  It will then process the other letters in the boxes before it.
         */
        KeyAdapter lastInput = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    /* Converts the answer array to the String userAnswer. The reason behind is this is due to the fact
                     * the user can enter their answer arbitrarily, meaning they do not have to start with the first box.
                     *  They could type out of the word from right to left. This would cause errors, primarily
                     * OutOfBoundExceptions and the user's answer being all jumbled up due to the unorthodox entry of
                     * words. The array prevents all mentioned.
                     */
                    for (int i = index; i < copyOfWordSize; i++) {
                        userAnswer += arrAnswer[i];
                    }
                    //System.out.println(userAnswer);
                    if (isWordleProcessable(wordSize, word)) { // Process word
                        isAnswerCorrect(wordSize, wordle); // Evaluate word
                    }
                }
                // Backspaced character is removed at the particular index
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                   for (int index = 0; index < wordSize*wordSize; index++) {
                       if (e.getSource().equals(entry[index])) {
                           visitedSet.remove(index);
                           arrAnswer[index] = "";
                       }
                   }
                }
            }
        };
        DocumentListener prevInput = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                for (int i = 0; i < wordSize; i++) {
                    // Ensures that each text field can only add one element and only records one letter unless deleted
                    if (((Document)e.getDocument()).getLength() == 1 &&
                            !(visitedSet.contains(i + index))) {
                        // Allows user to enter letters in any order
                        if (entry[i + index].getText().isBlank()) {
                            continue;
                        }
                        //System.out.println(entry[i + index].getText().toLowerCase(Locale.ROOT) + '|'+ i);
                        arrAnswer[i + index] = entry[i + index].getText().toLowerCase(Locale.ROOT);
                        visitedSet.add(i + index);
                        break;
                    }
                }
            }
            @Override
            public void removeUpdate(DocumentEvent e) {}
            @Override
            public void changedUpdate(DocumentEvent e) {}
        };
        // Instantiates each text box entry
        for (int index = 0; index < wordSize*wordSize; index++) {
            entry[index] = new JTextField();
            entry[index].setFont(new Font("Goudy Stout", Font.BOLD, 65)); // Font size and style
            entry[index].setHorizontalAlignment(SwingConstants.CENTER); // Centers text
            entry[index].setDocument(new JTextFieldLimit(1));
            entry[index].addKeyListener(lastInput);
            entry[index].getDocument().addDocumentListener(prevInput);
            if (index >= wordSize) {
                entry[index].setEditable(false);
            }
            panel.add(entry[index]);
        }
        // Must remain under the for loop
        SpringUtilities.makeGrid(panel,
                wordSize, wordSize,
                10, 100,
                10, 10);
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /* This method asks the user how many words they want the Wordle to be,
     * ranging from 3 words to 6 words.
     */
    public void intro() throws InterruptedException {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        // Background for the frame
        panel.setBackground(Color.WHITE);
        ImageIcon image = new ImageIcon(getClass().getResource("intro_art.png"));
        JLabel background = new JLabel(image);
        background.setSize(700,350);
        tutorial.add(background, BorderLayout.NORTH);
        // Listens for the user's click
        ActionListener input = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == option1) {
                    numOfWords = 3;
                    tutorial.setVisible(false);
                    Word wordle = new Word(numOfWords, isTheme, theme);
                    try {
                        runGame(wordle.wordleWord, wordle.wordleLength, wordle);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } else if (e.getSource() == option2) {
                    numOfWords = 4;
                    tutorial.setVisible(false);
                    Word wordle = new Word(numOfWords, isTheme, theme);
                    try {
                        runGame(wordle.wordleWord, wordle.wordleLength, wordle);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } else if (e.getSource() == option3) {
                    numOfWords = 5;
                    tutorial.setVisible(false);
                    Word wordle = new Word(numOfWords, isTheme, theme);
                    try {
                        runGame(wordle.wordleWord, wordle.wordleLength, wordle);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } else if (e.getSource() == option4) {
                    numOfWords = 6;
                    tutorial.setVisible(false);
                    Word wordle = new Word(numOfWords, isTheme, theme);
                    try {
                        runGame(wordle.wordleWord, wordle.wordleLength, wordle);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    // Theme Option
                    theme = thematicOptions.getItemAt
                            (thematicOptions.getSelectedIndex()).toLowerCase(Locale.ROOT);
                    numOfWords = theme.length();
                    isTheme = true;
                    tutorial.setVisible(false);
                    Word wordle = new Word(numOfWords, isTheme, theme);
                    try {
                        runGame(wordle.wordleWord, wordle.wordleLength, wordle);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
        // Button Positioning
        c.gridx = 2;
        c.gridy = 6;
        c.ipady = 10;
        c.insets = new Insets(0,0,0,10);
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(option1, c);

        c.gridx = 4;
        panel.add(option2, c);

        c.gridx = 6;
        panel.add(option3, c);

        c.gridx = 8;
        panel.add(option4, c);

        c.gridx = 5;
        c.gridy = 7;
        c.insets = new Insets(0,0,30,10);
        panel.add(option5, c);

        option1.addActionListener(input);
        option2.addActionListener(input);
        option3.addActionListener(input);
        option4.addActionListener(input);
        option5.addActionListener(input);

        c.gridy = 10;
        panel.add(thematicOptions, c);

        panel.setPreferredSize(new Dimension(700, 350));
        tutorial.add(panel, BorderLayout.SOUTH);

        tutorial.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tutorial.setSize(700, 700);
        tutorial.setResizable(false);
        tutorial.getContentPane().add(panel);
        tutorial.setVisible(true);
    }

    /* Evaluates and determines if the user's answer meets the criteria for the game to continue:
     * 1. The word is the size of which the length of the wordle the user selected in the beginning
     * 2. The word is the vast word bank
     */
    private boolean isWordleProcessable(int wordSize, Word word) {
        // If the userAnswer does not meet the requirements, then the user has to reenter a word
        if (userAnswer.length() != wordSize) {
            // Pop-up that tells user to enter a word with the designated word size
            JOptionPane.showMessageDialog(null, "Enter a word that has "+wordSize+
                    " letters!");
            userAnswer = "";
            return false;
        }
        try {
            if (!(word.isWord(userAnswer, isTheme))) {
                // Pop-up that tells user to enter another word that is the word bank
                JOptionPane.showMessageDialog(null, "Enter a word that is in" +
                        " the word bank!");
                userAnswer = "";
                return false;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return true;
    }
    /* Assuming the word meets the criteria to be processed, the user's answer will be compared to the answer:
     * Case 1 (Correct Answer): The user answer is the wordle generated. The game ends with a pop-up.
     * Case 2 (Incorrect Answer): The row typed on is locked, and now the user must type on the next row. The 'evaluation'
     * method will give hints to the user.
     */
    private boolean isAnswerCorrect(int wordSize, String wordle) {
        // If the userAnswer matches the wordle, the user wins
        if (userAnswer.equals(wordle)) {
            retry(true, wordle);
        } else {
            // If the user loses
            if (copyOfWordSize == wordSize * wordSize) {
                retry(false, wordle);
            }
            evaluate(wordle, userAnswer);
            userAnswer = ""; // Resets the userAnswer
            // If the user still has attempts:
            index += wordSize; // Increments so that the index is on the next row
            if (index != wordSize * wordSize) {
                // Locks the previous row(s) after user attempts
                for (int i = 0; i < copyOfWordSize; i++) {
                    entry[i].setEditable(false);
                }
                copyOfWordSize += wordSize; // Increments so that limit ends at the next row's last entry
                // Unlocks the next row
                for (int i = index; i < copyOfWordSize; i++) {
                    entry[i].setEditable(true);
                }
            }
        }
        return false;
    }
    public void retry(boolean isCorrect, String wordle) {
        JFrame frame = new JFrame("Retry?");
        GridBagConstraints c = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        if (!(isCorrect)) {
            ImageIcon image =
                    new ImageIcon(getClass().getResource("loserWDL.png"));
            JLabel background = new JLabel(image);
            background.setSize(500,250);
            frame.add(background, BorderLayout.NORTH);
            JOptionPane.showMessageDialog(null, "Wrong! The word was \n'" + wordle + "'");
        } else {
            ImageIcon image =
                    new ImageIcon(getClass().getResource("winnerWDL.png"));
            JLabel background = new JLabel(image);
            background.setSize(500,250);
            frame.add(background, BorderLayout.NORTH);
        }
        JButton yes = new JButton("Yes");
        JButton no = new JButton("No");
        ActionListener retry = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == yes) {
                    try {
                        window.dispose();
                        tutorial.dispose();
                        frame.setVisible(false);
                        Driver.main(new String[]{});
                    } catch (InterruptedException | IOException ex) {
                        ex.printStackTrace();
                    }
                } else if (e.getSource() == no) {
                    System.exit(0);
                }
            }
        };
        yes.addActionListener(retry);
        no.addActionListener(retry);

        c.gridx = 4;
        c.gridy = 10;
        c.ipady = 10;
        c.insets = new Insets(0,0,0,100);
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(yes, c);
        c.insets = new Insets(0,100,0,0);
        c.gridx = 8;
        panel.add(no, c);

        panel.setPreferredSize(new Dimension(500, 500));
        frame.add(panel, BorderLayout.SOUTH);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500,500);
        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }
    /* This method will evaluate the user's answer based on the wordle
     * Case 1 (Correct letter, Right place): The text box will highlight green
     * Case 2 (Letter not present in wordle): The text box will highlight grey
     * Case 3 (Correct letter, Wrong place); The text box will highlight yellow
     */
    Map<Integer, Boolean> map = new HashMap<>();
    private void evaluate(String wordle, String userAnswer) {
        map.clear();
        for (int i = 0; i < userAnswer.length(); i++) {
            int multiple = numOfCharInString(wordle, userAnswer.charAt(i));
            int counter = 0;
            if (userAnswer.charAt(i) == wordle.charAt(i)) {
                // Highlight green if letter is in correct spot
                entry[index + i].setBackground(Color.GREEN);
                //map.put(i, true);
            } else if (wordle.contains(arrAnswer[index + i]) &&
                    userAnswer.charAt(i) != wordle.charAt(i)) {
                // Highlight yellow if letter is in the word but not correct spot
                entry[index + i].setBackground(Color.YELLOW);
            } else {
                // Highlight grey if letter is not the word
                entry[index + i].setBackground(Color.GRAY);
            }
        }
    }

    private int numOfCharInString(String s, Character c) {
        int num = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                num++;
            }
        }
        return num;
    }
}
