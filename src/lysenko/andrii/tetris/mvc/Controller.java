package lysenko.andrii.tetris.mvc;

import lysenko.andrii.tetris.components.*;

import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.locks.ReentrantLock;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.SwingUtilities;

public class Controller {
    private static Logger log = Logger.getLogger(Controller.class.getName());
    private static Controller instance;
    private Controller.MyManager myManager;
    private final View view;
    private final Model model = new Model();
    private final Scanner scanner = new Scanner(System.in);
    private final ReentrantLock lock = new ReentrantLock();
    public volatile boolean guiBlocked = false;

    static {
        log.setLevel(Level.OFF);
    }

    public Controller(boolean isGraphicalView) {
        int maxX = model.MAX_X;
        int maxY = model.MAX_Y;
        if (isGraphicalView) {
            view = new GraphicalView(maxX, maxY);
        } else {
            view = new ConsoleView(maxX, maxY);
        }
        instance = this;
        this.start();
    }

    public static Controller getInstance() { return instance; }

    /* rewrite this method */
    private void start() {
        initListeners();
        this.drawGame();
        myManager = this.new MyManager();
        log.fine("myManager has been created");
        myManager.start();
        processGame();
        log.fine("MyManager has been started");
    }

    private void initListeners() {
        if (view instanceof GraphicalView) {
            GraphicalView v = (GraphicalView) view;
            Runnable doRun = new Runnable() {
                @Override
                public void run() {
                    v.getJFrame().addKeyListener(new MyKeyAdapter());
                }
            };
            if (SwingUtilities.isEventDispatchThread()) {
                (new Thread(doRun)).start();
            } else {
                try {
                    SwingUtilities.invokeAndWait(doRun);
                } catch (Exception e) {
                    log.log(Level.WARNING, "", e);
                }
            }
        }
    }

    public void nextTurn() {
        log.fine("nextTurn method");
        boolean b = model.isGameOver();
        view.setGameOver(b);
        if (model.brickCanFall()) {
            model.performAction(Move.DOWN);
        } else {
            this.drawGame();
            model.nextBrick();
        }
        this.drawGame();
    }

    private UserAction recognizeUserAction() {
        UserAction action = null;
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
        return action;
    }

    private void processGame() {
        try {
            while (true) {
                    log.fine("processGame: in a lock block");
                    UserAction action = recognizeUserAction();
                    lock.lock();
                        log.log(Level.WARNING, "Controller acquires the lock");
                        if (action != null)
                            model.performAction(action);
                        this.drawGame();
                        log.log(Level.WARNING, "Controller releases the lock");
                    lock.unlock();
            }
        } catch (InputMismatchException e1) {
            log.log(Level.WARNING, "", e1);
            scanner.nextLine();
            processGame();
        } finally {
            if (myManager.isAlive())
                myManager.interrupt();
        }
    }

    public int getScore() { return model.getScore(); }

    public int getIdentifier(int x, int y) {
        return model.getIdentifier(x, y);
    }

    public int getNextBrickIdentifier(int x, int y) {
        Brick brick = model.getNextBrick();
        int id = brick.getIdentifier();
        Cell cell = Cell.getInstance(x, y);
        if (brick.getCells().keySet().contains(cell))
            return id;
        else
            return 0;
    }

    public void drawGame() { view.drawGame(); }

    class MyManager extends Thread {
        @Override
        public void run() {
            while (true) {
                log.fine("MyManager run() method");
                try {
                    Thread.sleep(500);
                    lock.lock();
                        log.log(Level.WARNING, "MyManager acquires the lock");
                        log.fine("MyManager: in a lock block");
                        Controller.this.nextTurn();
                        log.log(Level.WARNING, "MyManager releases the lock");
                    lock.unlock();
                } catch (InterruptedException e1) {
                    log.log(Level.WARNING, "", e1);
                    return;
                }
            }
        }
    }

    private class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            if (guiBlocked)
                return;
            log.info("keyPressed method (start)");
            int code = e.getKeyCode();
            String name = KeyEvent.getKeyText(code);
            log.info(String.format("\'%d\' : \'%s\'", code, name));
            UserAction action;
            switch (name) {
                case "Up" : action = Rotation.LEFT; break;
                case "Left" : action = Move.LEFT; break;
                case "Right" : action = Move.RIGHT; break;
                case "Down" : action = Move.DOWN; break;
                default : log.info("nothing"); return;
            }
            lock.lock();
            log.log(Level.WARNING, "Listener acquires the lock");
            model.performAction(action);
            Controller.this.drawGame();
            log.log(Level.WARNING, "Listener releases the lock");
            lock.unlock();
            log.info("keyPressed method (end)");
        }
    }
}
