package lysenko.andrii.tetris.components;

/**
 * Created by admin on 13.02.2015.
 */
public class RotationCounter {
    private int value;
    private BrickType type;

    public RotationCounter(BrickType type) { this.type = type; }

    public RotationCounter(RotationCounter counter) {
        this.value = counter.value;
        this.type = counter.type;
    }

    public Rotation getRotation() {
        Rotation r = null;
        if (value == 0)
            r = Rotation.RIGHT;
        else
            r = Rotation.LEFT;
        if (type == BrickType.Z)
            r = r.getOpposite();
        changeValue();
        return r;
    }

    private void changeValue() {
        if (++value > 1)
            value = 0;
    }

    public static void main(String [] args) {
        RotationCounter r = new RotationCounter(BrickType.Z);
        System.out.println(r.getRotation());
        System.out.println(r.getRotation());
        System.out.println(r.getRotation());
    }
}
