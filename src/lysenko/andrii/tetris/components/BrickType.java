package lysenko.andrii.tetris.components;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by admin on 31.01.2015.
 */
public enum BrickType {
    T (1),
    I (2),
    S (3),
    Z (4),
    O (5),
    L (6),
    J (7);

    private final int identifier;
    private final HashMap<Cell, Boolean> cells = new HashMap<Cell, Boolean>();
    private final static Random random = new Random();

    static {
        T.cells.put(Cell.getInstance(5, 0), false);
        T.cells.put(Cell.getInstance(5, 1), true);
        T.cells.put(Cell.getInstance(6, 1), false);
        T.cells.put(Cell.getInstance(4, 1), false);

        I.cells.put(Cell.getInstance(5, 0), false);
        I.cells.put(Cell.getInstance(5, 1), false);
        I.cells.put(Cell.getInstance(5, 2), true);
        I.cells.put(Cell.getInstance(5, 3), false);

        S.cells.put(Cell.getInstance(4, 0), false);
        S.cells.put(Cell.getInstance(4, 1), true);
        S.cells.put(Cell.getInstance(5, 1), false);
        S.cells.put(Cell.getInstance(5, 2), false);

        Z.cells.put(Cell.getInstance(5, 0), false);
        Z.cells.put(Cell.getInstance(5, 1), true);
        Z.cells.put(Cell.getInstance(4, 1), false);
        Z.cells.put(Cell.getInstance(4, 2), false);

        O.cells.put(Cell.getInstance(4, 0), false);
        O.cells.put(Cell.getInstance(4, 1), false);
        O.cells.put(Cell.getInstance(5, 0), false);
        O.cells.put(Cell.getInstance(5, 1), false);

        L.cells.put(Cell.getInstance(4, 0), false);
        L.cells.put(Cell.getInstance(4, 1), true);
        L.cells.put(Cell.getInstance(4, 2), false);
        L.cells.put(Cell.getInstance(5, 2), false);

        J.cells.put(Cell.getInstance(5, 0), false);
        J.cells.put(Cell.getInstance(5, 1), true);
        J.cells.put(Cell.getInstance(5, 2), false);
        J.cells.put(Cell.getInstance(4, 2), false);
    }

    private BrickType (int identifier) {
        this.identifier = identifier;
    }

    public HashMap<Cell, Boolean> getCells() { return cells; }

    public int getIdentifier() { return identifier; }

    public static BrickType getRandom() {
        int index = random.nextInt(values().length);
        return values()[index];
    }
}