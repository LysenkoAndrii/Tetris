package lysenko.andrii.tetris.mvc;


import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by admin on 10.07.2015.
 */
public abstract class View {
    protected boolean gameOver;
    protected static final Logger log = Logger.getLogger(GraphicalView.class.getSimpleName());
    protected int score;
    protected final int MIN_X = 0, MAX_X, MIN_Y = 0, MAX_Y;

    static {
        log.setLevel(Level.OFF);
    }

    View(int maxX, int maxY) {
        this.MAX_X = maxX;
        this.MAX_Y = maxY;
    }

    abstract void drawGame();

    public void setGameOver(boolean b) { this.gameOver = b; }

    protected void refreshScore() {
        this.score = Controller.getInstance().getScore();
    }

    protected int getIdentifier(int x, int y) {
        return Controller.getInstance().getIdentifier(x, y);
    }
}
