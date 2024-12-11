package thunder.util.vector;

public class VectorDefinition {
    public Vector3D forwards;
    public Vector3D right;
    public Vector3D up;

    public VectorDefinition(Vector3D forwards, Vector3D right, Vector3D up) {
        this.forwards = forwards;
        this.right = right;
        this.up = up;
    }
}
