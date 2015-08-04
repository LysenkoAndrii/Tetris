package lysenko.andrii.tetris.mvc;


import lysenko.andrii.tetris.misc.DullJFrame;

import java.io.PrintWriter;


/**
 * Created by admin on 10.07.2015.
 */
public class ConsoleView extends View {
    private final PrintWriter writer = new PrintWriter(System.out);
    private final DullJFrame dullFrame = new DullJFrame();

    ConsoleView(int maxX, int maxY) {
        super(maxX, maxY);
    }

    /*
    * Here I used PrintWriter instead of simple System.out.println() in order to gather all
    * required information in a PrintWriter's buffer and print it instantly.
    * I mean that it is better when the whole matrix is printed completely all at once, rather than printed line by line
    */
    @Override
    public void drawGame() {
        refreshScore();
        if(gameOver) {
            writer.println("Game over!".toUpperCase());
            // show this line and freeze view for several seconds
            // then show something like "press any key to continue"
        } else {
            writer.format("%nYour score is %d%n", score);
            for (int y = MIN_Y; y <= MAX_Y; y++) {
                writer.print("  ");
                for (int x = MIN_X; x <= MAX_X; x++) {
                    int id = getIdentifier(x, y);
                    writer.print(((id == 0) ? "." : id) + " ");
                }
                writer.println();
            }
        }
        writer.println();
        writer.flush();
    }

    public DullJFrame getDullJFrame() {
        return dullFrame;
    }
}
