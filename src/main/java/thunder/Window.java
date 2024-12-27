package thunder;
/* Credits to: GamesWithGabe
While the code may have changed significantly since inception, the boilerplate code
was heavily inspired by GamesWithGabe's "Coding a 2D game Engine in Java"
*/
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import thunder.listener.KeyListener;
import thunder.listener.MouseListener;
import thunder.util.Time;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private int width, height;
    private String title;
    private long glfwWindow;

    private static Window window = null;

    private static Scene currentScene = null;
    private static int nos;

    private Window(Scene initialScene) {
        this.width = 1920;
        this.height = 1080;
        this.title = "Mario";
        Window.currentScene = initialScene;
    }

    public static void changeScene(Scene scene) {
        Window.currentScene = scene;
        // Any scene initialization
        scene.init();
    }

    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window(null);
        }

        return Window.window;
    }

    public static Window get(Scene initialScene) {
        if (Window.window == null) {
            Window.window = new Window(initialScene);
        }

        return Window.window;
    }

    public void run() {
        System.out.println("Hellow LWJGL " + Version.getVersion() + "!");
        init();
        loop();

        // Free the memory occupied by the window
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init() {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        // Configure GLFW (resizeable, closeable, etc.)
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create the GLFW window (returns long which is memory address of where window is)
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window");
        }

        // Set our callbacks
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);

        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync (go as fast as you can, match monitor)
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        // THIS LINE IS CRITICAL FOR COMPATIBILITY (don't forget)
        GL.createCapabilities();

        Window.currentScene.init();
    }

    public void loop() {
        float beginTime = Time.getTime();
        float endTime = Time.getTime();
        float dt = -1.0f;

        while(!glfwWindowShouldClose(glfwWindow)) {
            // Poll events
            glfwPollEvents();

            // Clear canvas
            glClearColor(255.0f, 255.0f, 255.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            // Check to see if first frame has happened
            if (dt >= 0) {
                currentScene.update(dt);
            }

            glfwSwapBuffers(glfwWindow);

            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }
}
