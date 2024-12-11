package thunder;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {
    private Matrix4f projectionMatrix, viewMatrix;
    private Vector3f position;

    public Camera (Vector3f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        adjustProjection();
    }

    public void adjustProjection() {
        projectionMatrix.identity();
        // Left, right, bottom, top
        projectionMatrix.perspective((float) (Math.PI/4.0f), 1.7778f, 0.1f, 100.0f);

    }

    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.viewMatrix.identity();
        this.viewMatrix = viewMatrix.lookAt(new Vector3f(position.x, position.y, 5.5f), cameraFront.add(position.x, position.y, position.z), cameraUp.add(position.x, position.y, position.z));

        return this.viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }
}