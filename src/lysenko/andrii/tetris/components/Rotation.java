package lysenko.andrii.tetris.components;

/**
 * Created by admin on 31.01.2015.
 */
public enum Rotation implements UserAction {
    LEFT,
    RIGHT;

    private Rotation opposite;

    static {
        LEFT.opposite = RIGHT;
        RIGHT.opposite = LEFT;
    }

    public Rotation getOpposite() { return opposite; }
}
