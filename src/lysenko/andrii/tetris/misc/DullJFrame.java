package lysenko.andrii.tetris.misc;


import lysenko.andrii.tetris.components.Move;
import lysenko.andrii.tetris.components.Rotation;
import lysenko.andrii.tetris.components.UserAction;
import lysenko.andrii.tetris.mvc.ConsoleView;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.NoSuchElementException;


/**
 * This is an empty substitute of JFrame to invoke addInputListener on it;
 * Used by ConsoleView
 */
public class DullJFrame {
    private static Logger log = Logger.getLogger(ConsoleView.class.getName());

    static {
        log.setLevel(Level.OFF);
    }

    public void addInputListener(MyInputListener listener) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                UserAction action;
                int n = scanner.nextInt();
                if (n == 5)
                    action = Move.DOWN;
                else if (n == 4)
                    action = Move.LEFT;
                else if (n == 6)
                    action = Move.RIGHT;
                else if (n == 1)
                    action = Rotation.LEFT;
                else if (n == 2)
                    action = Rotation.RIGHT;
                else
                    continue;
                log.info(action.toString());
                listener.inputPerformed(action);
            } catch (InputMismatchException e) {
                log.log(Level.WARNING, "", e);
                scanner.nextLine();
            } catch (NoSuchElementException e2) {
                /* comment this line to suppress exception when tetris closes */
                //log.log(Level.WARNING, "", e2);
                scanner.close();
                log.info("Console input stream is closed");
                break;
            }
        }
    }
}
