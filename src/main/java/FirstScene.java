import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import thunder.Camera;
import thunder.Scene;
import thunder.Window;
import thunder.listener.KeyListener;
import thunder.renderer.Shader;
import thunder.renderer.Texture;
import thunder.util.Time;

import java.awt.event.KeyEvent;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class FirstScene extends Scene {

    private String vertexShaderSrc = "#version 330 core\n" +
            "layout (location=0) in vec3 aPos;\n" +
            "layout (location=1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";

    private String fragmentShaderSrc = "#version 330 core\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    color = fColor;\n" +
            "}";

    private int vertexID, fragmentID, shaderProgram;

    private float[] vertexArray = {
            // position             // color                  // UV Coordinates (Texture)
            100f, 0f, 0.0f,      1.0f, 0.0f, 0.0f, 1.0f,   0, 1, // Bottom right 0
            0f, 100f, 0.0f,      0.0f, 1.0f, 0.0f, 1.0f,   0, 1, // Top left     1
            100f, 100f, 0.0f,    1.0f, 0.0f, 1.0f, 1.0f,   0, 1, // Top right    2
            0f, 0f, 0.0f,        1.0f, 1.0f, 0.0f, 1.0f,   0, 1  // Bottom left  3
    };

    // IMPORTANT: Must be in counter-clockwise order
    private int[] elementArray = {
      2, 1, 0, // Top right triangle
      0, 1, 3  // Bottom right triangle
    };

    private int vaoID, vboID, eboID;

    private Shader defaultShader;
    private Texture testTexture;

    public FirstScene() {

    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector3f(0.0f, 0.0f, 50.0f));
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compile();
        this.testTexture = new Texture("assets/images/cool.png");


        // Generate VAO, VBO, and EBO to send to GPU
        vaoID = glGenVertexArrays(); // Create new VAO
        glBindVertexArray(vaoID); // Everything that happens after this line, make sure it happens to THIS vao

        // Create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length); // openGL is expecting float buffer, so we create one based off of the length of our vertex array
        vertexBuffer.put(vertexArray).flip(); // Load our vertex array into the buffer and flip it so it is oriented correctly for openGL format

        // Create VBO and upload the vertex array
        vboID = glGenBuffers(); // Create new VBO
        glBindBuffer(GL_ARRAY_BUFFER, vboID); // Everything that happens after this line will be for the array vboID (vbo is just an array so we use GL_ARRAY_BUFFER)
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW); // Specify buffer we want to send, make it static since we aren't going to change anything in the buffer after now

        // Create the EBO and upload the elements array (same process as above)
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add the vertex attribute pointers
        int positionsSize = 3; // How many values are in the position attribute
        int colorSize = 4; // How many values are in the color attribute
        int uvSize = 2;
        int vertexSizeBytes = (positionsSize + colorSize + uvSize) * Float.BYTES; // Get total size of entry in bytes

        // Specifies attribute 0 (position) with length positionsSize (3) of type float, normalized doesn't matter, with vertexSizeBytes amount of bytes until next ones of these attributes, that starts at 0
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        // Specifies attribute 1 (position) with length colorSize (4) of type float, normalized doesn't matter, with vertexSizeBytes amount of bytes until next ones of these attributes, that starts at index after position (positionsSize)
        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize*Float.BYTES);
        glEnableVertexAttribArray(1);

        // UV attrib
        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionsSize + colorSize)*Float.BYTES);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update(float dt) {
        defaultShader.use();

        // Upload texture to shader
        defaultShader.uploadTexture("TEX_SAMPLER", 0); // Tells shader what GPU slot (0-8 maybe?) the texture is in
        glActiveTexture(GL_TEXTURE0); // Activates the 0 shader slot
        testTexture.bind(); // Binds the texture to this slot (pushes into slot activated above, 0)

        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTime());

        // Bind the VAO that we're using
        glBindVertexArray(vaoID);

        // Enable the vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // Draw the elements with parse type triangles, specify length of array, specify type of values in array (always not negative), and start at 0
        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        defaultShader.detach();
    }
}
