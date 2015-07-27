package lysenko.andrii.tetris.components;

import java.util.*;

/**
 * Created by admin on 30.01.2015.
 */
public class Brick{
    private BrickType type;
    private static BrickType[] types = BrickType.class.getEnumConstants();
    private HashMap<Cell, Boolean> cells = new HashMap<Cell, Boolean>(4);
    private RotationCounter counter;

    public Brick(BrickType type){
        this(type.getCells(), type, null);
    }

    private Brick(HashMap<Cell, Boolean> cells, BrickType type, RotationCounter counter){
        this.cells = new HashMap<Cell, Boolean>(cells.size());
        for(Map.Entry<Cell, Boolean> pair : cells.entrySet())
            this.cells.put(Cell.getInstance(pair.getKey()), pair.getValue());
        this.type = type;
        if (counter == null)
            this.counter = new RotationCounter(type);
        else
            this.counter = new RotationCounter(counter);
    }

    public static Brick getRandom() { return new Brick(BrickType.getRandom()); }

    public Brick copy(){
        return new Brick(this.getCells(), this.getType(), this.counter);
    }

    public BrickType getType() { return type; }

    public int getIdentifier() { return type.getIdentifier(); }

    public void move(Move move){
        HashMap<Cell, Boolean> temp = new HashMap<Cell, Boolean>(cells.size());
        for (Map.Entry<Cell, Boolean> pair : cells.entrySet()) {
            Cell c = pair.getKey();
            Boolean b = pair.getValue();
            if(move == Move.LEFT)
                temp.put(Cell.getCellLeftTo(c), b);
            else if(move == Move.DOWN)
                temp.put(Cell.getCellBelow(c), b);
            else if(move == Move.RIGHT)
                temp.put(Cell.getCellRightTo(c), b);
        }
        cells = temp;
    }

    public void rotate(Rotation rotation) {
        if (type == BrickType.O)
            return;
        else if ((type == BrickType.S) || (type == BrickType.Z))
            rotation = counter.getRotation();
        Cell centre = null;
        for (Map.Entry<Cell, Boolean> pair : cells.entrySet())
            if (pair.getValue()) // if a cell is a centre
                centre = Cell.getInstance(pair.getKey());
        if (centre == null)
            throw new NullPointerException();
        HashMap<Cell, Boolean> temp = new HashMap<Cell, Boolean>();
        for (Map.Entry<Cell, Boolean> pair : cells.entrySet()) {
            Cell old = pair.getKey();
            int x = centre.getX() - old.getX();
            int y = centre.getY() - old.getY();
            if (rotation == Rotation.LEFT)
                y *= -1;
            else
                x *= -1;
            int i = x;
            x = y;
            y = i;
            //System.out.format("(%d:%d)%n", x, y);
            x += centre.getX();
            y += centre.getY();
            Cell next = Cell.getInstance(x, y);
            //System.out.format("(%d:%d)%n", x, y);
            temp.put(next, pair.getValue());
        }
        cells = temp;
    }

    public void removeCell(Cell cell){
        cells.remove(cell);
    }

    public HashMap<Cell, Boolean> getCells(){ return cells; }

    public void setCells(Set<Cell> cells) {
        this.cells.clear();
        for (Cell c : cells)
            this.cells.put(c, false);
    }


    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Brick))
            return false;
        Brick that = (Brick) obj;
        return that.getCells().equals(this.getCells());
    }

    @Override
    public int hashCode(){
        return 42;
    }
}