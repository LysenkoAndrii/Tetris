package lysenko.andrii.tetris;

import lysenko.andrii.tetris.mvc.Controller;

/**
 * Created by admin on 27.07.2015.
 */
public class Main {
    public static void main(String[] args) {
        boolean graphicalView;
        if (args.length == 1)
            graphicalView = args[0].equals("-graphical");
        else
            graphicalView = false;
        Controller controller = new Controller(graphicalView);
    }
}
