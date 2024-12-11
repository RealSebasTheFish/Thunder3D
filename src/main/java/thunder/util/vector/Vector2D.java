package thunder.util.vector;

public class Vector2D {
    public double x;
    public double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean valid() {
        return !(this.x == -1 && this.y == -1);
    }
}
