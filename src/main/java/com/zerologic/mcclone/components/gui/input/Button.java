package com.zerologic.mcclone.components.gui.input;

import static org.lwjgl.opengl.GL33.*;

import static org.lwjgl.glfw.GLFW.*;

import com.zerologic.mcclone.Game;
import com.zerologic.mcclone.engine.Input;
import com.zerologic.mcclone.engine.Renderer;
import com.zerologic.mcclone.components.gui.uitext.UIText;
import org.joml.*;

public class Button {

    private int VAO, VBO, EBO;

    private final UIText text;
    private final Vector4f dim; // xpos, ypos, width, height (xyzw)

    private final float[] mousePos = Input.getMousePosReference();

    private boolean drawBorder = false;

    // Initialize with empty lambda functions
    private Runnable rEnter = (() -> {});
    private Runnable rExit = (() -> {});
    private Runnable rMouseDown = (() -> {});
    private Runnable rMouseUp = (() -> {});

    // Booleans to control mouse inputs
    private boolean hovered = false;
    private boolean executed = false;
    private boolean clicked = false;

    private Vector4f color;
    private Vector4f hoverColor;
    private Vector4f clickColor;

    private Vector4f currentColor;

    public Button() { this("No text", 50f, 0f, 0f); }

    public Button(String text, float fontSize) {
        this(text, fontSize, 0f, 0f);
    }

    public Button(String text, float fontSize, float x, float y) {
        this.text = new UIText(text, fontSize, x, y);
        this.dim = new Vector4f(x, y, this.text.width(), fontSize);
        this.text.setPos((x + this.text.width() / 2f) - this.text.width() / 2f, y + this.text.height() / 2f - this.text.height() / 2f);

        this.color = new Vector4f(0f, 0f, 0f, 1f); // Idle color of button
        this.hoverColor = new Vector4f(1f, 1f, 1f, 1f); // Hovered color
        this.clickColor = new Vector4f(1f, 0f, 0f, 1f); // Clicked color

        this.currentColor = color;

        this.text.setColor(currentColor);

        init();
    }

    private void init() {
        float[] data = {
                // Vertex positions	   // Tex coords
                0.0f,  0.0f,   0.0f, 1.0f, // Top left
                dim.z, 0.0f,   1.0f, 1.0f, // Top right
                dim.z, dim.w,  1.0f, 0.0f, // Bottom right
                0.0f,  dim.w,  0.0f, 0.0f  // Bottom left
        };

        int[] indices = {
                0, 1, 2, 3, 1, 2, 0, 3
        };

        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.VBO);
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);

        EBO = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 4, GL_FLOAT, false, 16, 0);
        glEnableVertexAttribArray(0);

        glLineWidth(1f);

        // Cleanup
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    // Use renderer draw calls before doing any raw drawing
    public void draw() {
        //Renderer.draw(text);

        update();
        if(drawBorder) {
            glBindVertexArray(VAO);
            glDrawArrays(GL_LINES, 0, 8);
        }
    }

    private void update() {
        if(hovered && glfwGetMouseButton(Game.getWindow().id(), 0) == GLFW_PRESS) {
            if(clicked)
                return;

            mouseDownFunc();
            clicked = true;
        }

        if(clicked && hovered && glfwGetMouseButton(Game.getWindow().id(), 0) == GLFW_RELEASE) {
            mouseUpFunc();
            clicked = false;
        }

        if(mousePos[0] >= x() && mousePos[0] <= x() + width() &&
                mousePos[1] >= y() && mousePos[1] <= y() + height()) {
            if(hovered) {
                return;
            }

            enterFunc();
            setHovered(true);
            executed = false;
        } else {
            if(!executed && hovered) {
                exitFunc();
                executed = true;
            }
            setHovered(false);
        }
    }

    // When we update the text component of the button, we need to ensure that the text color is update at the same time
    // as the button to avoid weird effects
    public void setColor(float r, float g, float b, float a) {
        this.color = new Vector4f(r, g, b, a);
        text.setColor(r, g, b, a);
    }

    public void setHoverColor(float r, float g, float b, float a) {
        this.hoverColor = new Vector4f(r, g, b, a);
    }

    public void setClickColor(float r, float g, float b, float a) {
        this.clickColor = new Vector4f(r, g, b, a);
    }

    public void setPos(float x, float y) {
        this.dim.x = x;
        this.dim.y = y;
        this.text.setPos(x, y);
    }

    // Mouse events
    protected void enterFunc() {
        currentColor = hoverColor;
        text.setColor(currentColor);
        rEnter.run();
    }

    protected void exitFunc() {
        currentColor = color;
        text.setColor(currentColor);
        rExit.run();
    }

    protected void mouseDownFunc() {
        currentColor = clickColor;
        text.setColor(currentColor);
        rMouseDown.run();
    }

    protected void mouseUpFunc() {
        currentColor = hoverColor;
        text.setColor(currentColor);
        rMouseUp.run();
    }

    public void onEnter(Runnable r) {
        rEnter = r;
    }

    public void onExit(Runnable r) {
        rExit = r;
    }

    public void onMouseDown(Runnable r) {
        rMouseDown = r;
    }

    public void onMouseUp(Runnable r) {
        rMouseUp = r;
    }

    // Accessors
    public String text() {
        return this.text.text();
    }

    public float x() {
        return dim.x;
    }

    public float y() {
        return dim.y;
    }

    public float width() {
        return dim.z();
    }

    public float height() {
        return dim.w();
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public void drawBorder(boolean draw) {
        this.drawBorder = draw;
    }
}
