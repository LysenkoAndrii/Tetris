package lysenko.andrii.tetris;

import lysenko.andrii.tetris.mvc.Controller;

/**
 * Main class.
 * Created by admin on 27.07.2015.
 */
public class Main {
    public static void main(String[] args) {
        boolean graphicalView = args.length == 1
                && ( args[0].equals("-graphical") || args[0].equals("-g") );
        Controller controller = Controller.getInstance(graphicalView);
        controller.start();
    }
}
