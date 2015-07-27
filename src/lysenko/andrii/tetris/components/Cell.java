package lysenko.andrii.tetris.components;

import java.util.HashSet;
import java.util.StringJoiner;

/**
 * Created by admin on 30.01.2015.
 */
public class Cell {
    private final int x, y;
    private static HashSet<Cell> pool = new HashSet<Cell>();
    private static int counter;

    private Cell(int x, int y) {
        counter++;
        this.x = x;
        this.y = y;
    }

    public static Cell getInstance(int x, int y) {
        for (Cell cell : pool)
            if ((cell.getX() == x) && (cell.getY() == y))
                return cell;
        Cell newCell = new Cell(x, y);
        pool.add(newCell);
        return newCell;
    }

    public static Cell getInstance(Cell cell) {
        int x = cell.getX();
        int y = cell.getY();
        return getInstance(x, y);
    }

    public static Cell getCellLeftTo(Cell cell) {
        int y = cell.getY();
        int x = cell.getX() - 1;
        return getInstance(x, y);
    }

    public static Cell getCellRightTo(Cell cell) {
        int y = cell.getY();
        int x = cell.getX() + 1;
        return getInstance(x, y);
    }

    public static Cell getCellBelow(Cell cell) {
        int y = cell.getY() + 1;
        int x = cell.getX();
        return getInstance(x, y);
    }

    public int getX() { return this.x; }

    public int getY() { return this.y; }

    public static int getCounter() { return counter; }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Cell))
            return false;
        Cell that = (Cell) obj;
        return (that.x == this.x) && (that.y == this.y);
    }

    @Override
    public int hashCode(){
        return 42 * this.x * this.y;
    }

    //required for debugging
    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(";", "(", ")");
        joiner.add(""+x);
        joiner.add(""+y);
        return joiner.toString();
    }

    @Override
    public void finalize() { counter--; }
}