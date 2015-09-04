package lysenko.andrii.tetris.mvc;


import lysenko.andrii.tetris.components.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;


/**
 * Created by admin on 30.01.2015.
 */
public class Model {
    private Logger log = Logger.getLogger(Model.class.getName());
    private int score;

    /*
    * a field for a tetris game is represented by a matrix with dimension 10x20;
    * possible values:
    *                   x - [0; 9] (total - 10)
    *                   y - [0; 19](total - 20)
     */
    public final int   MAX_X = 9,
                        MAX_Y = 19,
                        MIN_X = 0,
                        MIN_Y = 0;

    private Brick current;
    private Brick next;

    /* deadBricks are non-active Brick objects*/
    private HashSet<Brick> deadBricks;
    private static Model instance;
    private boolean gameOver = false;
    // @latency is passed as an argument in Thread.sleep() method.
    private long latency = 400; //400

    private BrickType deb = BrickType.J; // this required only for debugging purpose

    /*
    * This variable tells us when we can consider @current as either a distinct brick or as a part of @deadBricks
    */
    private boolean currentVisible = true;

    Model() {
        log.setLevel(Level.OFF);
        deadBricks = new HashSet<Brick>();
        current = Brick.getRandom();
        next = Brick.getRandom();
        //current = new Brick(deb); // uncomment for debugging purpose
        //next = new Brick(deb); // uncomment for debugging purpose
    }

    public void nextBrick() {
        if (gameOver)
            return;
        deadBricks.add(current);
        setScore();
        try {
            checkConsistency(next);
        } catch (CellConsistencyException e) {
            setGameOver();
            return;
        }
        current = next;
        log.info("a new brick has been generated to @current");
        next = Brick.getRandom();
        //current = new Brick(deb); // delete this line;
        //next = new Brick(deb); // delete this line;
        if (!brickCanFall())
            gameOver = true;
    }

    public void performAction(UserAction action) {
        if (action == null)
            return;
        if (action instanceof Move)
            this.moveBrick((Move) action);
        else
            this.rotateBrick((Rotation) action);
    }

    private void rotateBrick(Rotation rotation) {
        try {
            Brick temp = current.copy();
            temp.rotate(rotation);
            checkBounds(temp);
            checkConsistency(temp);
            current = temp;
        } catch (BrickOutOfBoundsException | CellConsistencyException e1){
            log.warning(e1.getClass().getName());
        }
    }

    private void setGameOver() {
        Controller.getInstance().guiBlocked = true;
        log.info("setting gameOver flag");
        gameOver = true;
        Controller.getInstance().nextTurn();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e2) {
            log.log(Level.WARNING, "", e2);
        }
        log.info("gameOver flag is set");
        Controller.getInstance().guiBlocked = false;
    }

    private void moveBrick(Move move) {
        try {
            Brick temp = current.copy();
            temp.move(move);
            checkBounds(temp);
            checkConsistency(temp);
            current = temp;
            // uncomment if you want to count every fall to a score
            //if (move == Move.DOWN)
            //    score++;
            log.info("@current variable is set on move: "+move);
        } catch (IllegalPositionException e1){
            log.warning(e1.getClass().getName());
        }
    }

    /*
    * This method moves down every cell with a Y value smaller than given argument.
    * @arg y - the line that has just been destroyed
     */
    private void moveCellsDown(int y){
        log.info("in @moveBrickDown method"); // todo: temporary
        Iterator<Brick> iterator = deadBricks.iterator();
        while (iterator.hasNext()) {
            Brick brick = iterator.next();
            if (brick.getCells().isEmpty()) {
                iterator.remove();
                continue;
            }
            HashSet<Cell> temp = new HashSet<Cell>();
            for (Cell c : brick.getCells().keySet()) {
                if (y > c.getY())
                    temp.add(Cell.getCellBelow(c));
                else
                    temp.add(c);
            }
            brick.setCells(temp);
        }
    }

    /* checks whether current brick has space below */
    public boolean brickCanFall(){
        boolean result = true;
        Brick temp = current.copy();
        temp.move(Move.DOWN);
        try {
            checkConsistency(temp);
            checkBounds(temp);
        } catch (IllegalPositionException e1){
            result = false;
            log.info("@current has not got any free space below" + e1);
        }
        return result;
    }

    private void checkConsistency(Brick temp) throws CellConsistencyException {
        Brick brick = temp.copy();
        Iterator<Brick> iterator = deadBricks.iterator();
        while (iterator.hasNext()){
            HashMap<Cell, Boolean> cells = iterator.next().getCells();
            for(Cell cell : brick.getCells().keySet())
                if(cells.containsKey(cell))
                    throw new CellConsistencyException();
        }
    }

    private void setScore(){
        int i = destroyLines();
        switch(i){
            case 1:
                score += 100;
                break;
            case 2:
                score += 300;
                break;
            case 3:
                score += 700;
                break;
            case 4:
                score += 1500;
                break;
            default:
                break; // XXX note that putting break statement into default case in not compulsory
        }
    }

    private void checkBounds(Brick temp) throws BrickOutOfBoundsException {
        Brick brick = temp.copy();
        Iterator<Cell> iterator = brick.getCells().keySet().iterator();
        while(iterator.hasNext()){
            Cell cell = iterator.next();
            int x = cell.getX();
            int y = cell.getY();
            boolean xb = (x < MIN_X) || (x > MAX_X);
            boolean yb = (y < MIN_Y) || (y > MAX_Y);
            if (xb || yb)
                throw new BrickOutOfBoundsException();
        }
    }

    private int destroyLines() {
        currentVisible = false;
        Controller.getInstance().guiBlocked = true;
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int y = MAX_Y; y >= MIN_Y; y--) {
            boolean isLineFull = true; // if line is completely fulled by cells
            for (int x = MIN_X; x <= MAX_X; x++) {
                if (getIdentifier(x, y) == 0) {
                    isLineFull = false;
                    break;
                }
            }
            if (isLineFull) {
                Iterator<Brick> iterator = deadBricks.iterator();
                while (iterator.hasNext()) {
                    Brick temp = iterator.next();
                    for(int x = MIN_X; x <= MAX_X; x++) {
                        Cell cell = Cell.getInstance(x, y);
                        if (temp.getCells().containsKey(cell))
                            temp.removeCell(cell);
                    }
                }
                try {
                    Controller.getInstance().drawGame();
                    Thread.sleep(latency);
                } catch (InterruptedException e1) {
                    log.warning(e1.toString());
                }
                list.add(0, y);
            }
        }
        try {
            for(Integer y : list) {
                this.moveCellsDown(y);
                Controller.inform("calling drawGame");
                Controller.getInstance().drawGame();
                Controller.inform("deleted one line");
                Thread.sleep(latency);
            }
        } catch (InterruptedException e1) {
            log.warning(e1.toString());
        }
        Controller.getInstance().guiBlocked = false;
        currentVisible = true;
        return list.size();
    }

    public int getIdentifier(int x, int y) {
        Cell cell = Cell.getInstance(x, y);
        HashSet<Brick> total = new HashSet<Brick>(deadBricks);
        if(currentVisible)
            total.add(current);
        Iterator<Brick> iterator = total.iterator();
        while (iterator.hasNext()) {
            Brick current = iterator.next();
            if (current.getCells().containsKey(cell))
                return current.getIdentifier();
        }
        return 0;
    }

    public Brick getNextBrick() { return next; }

    public boolean isGameOver() { return gameOver; }

    public int getScore() { return score; }

    public void newGame() {
        deadBricks = new HashSet<Brick>();
        current = Brick.getRandom();
        next = Brick.getRandom();
        score = 0;
        gameOver = false;
    }
}