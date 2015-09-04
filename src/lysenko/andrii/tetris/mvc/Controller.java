package lysenko.andrii.tetris.mvc;


import lysenko.andrii.tetris.components.*;
import lysenko.andrii.tetris.misc.MyInputListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.locks.ReentrantLock;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class Controller {
    private static Logger log = Logger.getLogger(Controller.class.getName());
    private static Controller instance;
    private Thread ticker;
    private final View view;
    private final Model model = new Model();
    public final ReentrantLock lock = new ReentrantLock();

    /* flag that becomes true whenever any line as being
     * destroyed, makes key listener return with nothing done */
    public volatile boolean guiBlocked = false;

    public final Object obj = new Object();

    static {
        log.setLevel(Level.OFF);
    }

    private Controller(boolean isGraphicalView) {
        int maxX = model.MAX_X;
        int maxY = model.MAX_Y;
        if (isGraphicalView) {
            view = new GraphicalView(maxX, maxY);
        } else {
            view = new ConsoleView(maxX, maxY);
        }
        instance = this;
    }

    public static Controller getInstance() { return instance; }

    public static Controller getInstance(boolean isGraphicalView) {
        if (instance == null)
            instance = new Controller(isGraphicalView);
        return instance;
    }

    /* rewrite this method */
    public void start() {
        initListeners();
        this.drawGame();
        ticker = this.new Ticker();
        ticker.start();
        log.fine("Ticker has been started");
    }

    private void initListeners() {
        if (view instanceof GraphicalView) {
            GraphicalView v = (GraphicalView) view;
            Thread doRun = new Thread(() -> v.getJFrame().addKeyListener(new GraphicalKeyAdapter()));
            doRun.start();
        } else if (view instanceof ConsoleView) {
            ConsoleView v = (ConsoleView) view;
            Thread doRun = new Thread(() -> v.getDullJFrame().addInputListener(new ConsoleInputAdapter()));
            doRun.start();
        }
    }

    public void newGame() {
        lock.lock();
            guiBlocked = true;
            model.newGame();
            if (ticker.isAlive())
                ticker.interrupt();
            try {
                if (ticker.isAlive())
                    ticker.join();
            } catch (InterruptedException e) {
                log.log(Level.WARNING, "", e);
            }
            view.setGameOver(false);
            this.drawGame();
            ticker = this.new Ticker();
            ticker.start();
            guiBlocked = false;
        lock.unlock();
    }

    public void nextTurn() {
        log.fine("nextTurn method");
        boolean b = model.isGameOver();
        view.setGameOver(b);
        if (model.brickCanFall())
            model.performAction(Move.DOWN);
        else
            model.nextBrick();
        this.drawGame();
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

    /**
     * Delegates drawing game to View layer.
     * should be invoked only in "locked" context
     * */
    public void drawGame() {
        lock.lock();
            Controller.inform("drawGame() method acquired lock");
            synchronized(obj) {
                view.drawGame();
                try {
                    Controller.inform("starting waiting");
                    obj.wait();
                } catch (InterruptedException e) {
                    log.log(Level.WARNING, "", e);
                }
                Controller.inform("ended waiting");
            }
        Controller.inform("drawGame() method releases lock");
        lock.unlock();
    }

    //public void

    static void inform(String s) {
        String threadName = Thread.currentThread().getName();
        log.info(String.format("%s says: %s", threadName, s));
    }

    /*
     * This class moves current brick down every latency period of time
     */
    private class Ticker extends Thread {
        final long latency = 500;

        public Ticker() {
            super("Ticker");
        }

        @Override
        public void run() {
            boolean b = true;
            while (b) {
                log.fine("Ticker run() method");
                try {
                    Thread.sleep(latency);
                    lock.lock();
                        Controller.inform("Ticker's run method acquired the lock");
                        Controller.this.nextTurn();
                        Controller.inform("Ticker's run method releases the lock");
                    lock.unlock();
                    b = ! (isInterrupted() || Controller.this.model.isGameOver());
                } catch (InterruptedException e1) {
                    //log.log(Level.WARNING, "", e1);
                    return;
                }
            }
            //guarantees that there will be a notification
            // to a user when the game is over
            lock.lock();
                Controller.this.nextTurn();
            lock.unlock();
        }
    }

    private class GraphicalKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            if (guiBlocked)
                return;
            if (Controller.this.model.isGameOver()) {
                new Thread(() -> Controller.this.newGame()).start();
                return;
            }
            int code = e.getKeyCode();
            String name = KeyEvent.getKeyText(code);
            log.info(String.format("\'%d\' : \'%s\'", code, name));
            UserAction action;
            switch (name) {
                case "Up" : action = Rotation.LEFT; break;
                case "Left" : action = Move.LEFT; break;
                case "Right" : action = Move.RIGHT; break;
                case "Down" : action = Move.DOWN; break;
                default: action = null;
            }
            new Thread(() -> {
                lock.lock();
                Controller.inform("Listener acquired the lock");
                model.performAction(action);
                Controller.inform("Listener releases the lock");
                Controller.this.drawGame();
                lock.unlock();
            }).start();
        }
    }

    private class ConsoleInputAdapter implements MyInputListener {
        @Override
        public void inputPerformed(UserAction action) {
            if (Controller.this.model.isGameOver()) {
                Controller.this.newGame();
                return;
            }
            lock.lock();
                log.info("ConsoleInputAdapter acquired the lock");
                model.performAction(action);
                Controller.this.drawGame();
                log.info("ConsoleInputAdapter releases the lock");
            lock.unlock();
        }
    }
}
