import javax.swing.*;
import java.io.IOException;
import java.util.Locale;
/* This class runs the game by utilizing the Word and GUI classes. This class's purpose is simply just to run the
 * game in the intended sequence and communicate between classes.
 */
public class Driver {
    public static void main(String[] args) throws InterruptedException, IOException {
            GUI gui = new GUI();
        JOptionPane.showMessageDialog(null, "Hello! Welcome to Wordoodle!\n" +
                "The way this game operates is simple:\n" +
                "   1. Select an option between 3 to 6 words \n" +
                "   2. A MxM grid will appear once you select an option (M = the number of words selected)\n" +
                "   3. You will have M guesses to guess the randomly generated M-long word. There will be \n" +
                "      various hints to help you narrow your choices down and help you guess the word! \n" +
                "   4. Alternatively, you can select a theme, where a 3-7 letter word will be generated based \n" +
                "      on the theme selected. Good luck and have fun!", "Tutorial", 1);
            gui.intro(); // Runs the introduction which then runs the game
        }
}
