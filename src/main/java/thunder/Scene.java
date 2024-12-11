package thunder;

public abstract class Scene {

    public Camera camera;

    public Scene() {

    }

    public void init() {

    }

    public abstract void update(float dt);

}
