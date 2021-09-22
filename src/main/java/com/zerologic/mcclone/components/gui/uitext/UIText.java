package com.zerologic.mcclone.components.gui.uitext;

import com.zerologic.mcclone.Game;
import com.zerologic.mcclone.engine.Shader;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.stb.*;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL40.*;

import static org.lwjgl.stb.STBTruetype.*;

import java.nio.FloatBuffer;
import java.util.Vector;

// TODO: upgrade text renderer so that it stores the vertex positions all in a singular VBO so we only use a single draw call.

public class UIText {

    private String text;

    private final FloatBuffer x = BufferUtils.createFloatBuffer(1);
    private final FloatBuffer y = BufferUtils.createFloatBuffer(1);
    private final Vector<STBTTAlignedQuad> bakedChars = new Vector<>();

    private Vector2f pos; // X and Y of the actual quad
    private Vector2f size; // Width and height of the object
    private Vector4f color; // RGBA

    private float fontSize;

    private int VAO, VBO, EBO;

    static int[] indices = {
            3, 2, 1,
            3, 1, 0
    };

    public UIText(int text, float fontSize) {
        this(Integer.toString(text), fontSize, 0f, 0f);
    }

    public UIText(int text, float fontSize, float x, float y) {
        this(Integer.toString(text), fontSize, x, y);
    }

    public UIText(float text, float fontSize) {
        this(Float.toString(text), fontSize, 0f, 0f);
    }

    public UIText(float text, float fontSize, float x, float y) {
        this(Float.toString(text), fontSize, x, y);
    }

    public UIText(String text, float fontSize) {
        this(text, fontSize, 0f, 0f);
    }

    // Super constructor
    public UIText(String text, float fontSize, float x, float y) {
        this.text = text;
        this.fontSize = fontSize;
        this.pos = new Vector2f(x, y); // Set pos
        this.color = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f); // Set font color to black by default
    }

    public void init() {
        // Activate the shader and set the color
        UIFontLoader.getShader().use();
        UIFontLoader.getShader().setVector4f(this.color, "color");

        // Initialize fontloader with given size and clear buffers and baked chars, reset origin
        UIFontLoader.generateBitmap(fontSize); // Load a font bitmap with the desired size

        bakedChars.clear();
        resetPosBuffers();

        // Create baked chars for each of the characters in the string
        for (int i = 0; i < text.length(); i++) {

            // If newline, set virtual cursor accordingly
            if (text.charAt(i)=='\n') {
                // Virtual cursor positions
                // float vCursorX = this.x.get(0);
                float vCursorY = this.y.get(0);
                this.putPosBuffer(0f, getNewlineYOff(vCursorY)); // Put these 2 values in the x and y pos buffers respectively
                continue;
            }

            STBTTAlignedQuad q = STBTTAlignedQuad.create();
            stbtt_GetBakedQuad(UIFontLoader.getFontBySize(fontSize).getCharData(), UIFontLoader.getFontBySize(fontSize).getBmpSize(), UIFontLoader.getFontBySize(fontSize).getBmpSize(), text.charAt(i) - 32, x, y, q, true);

            bakedChars.add(q);
        }

        // Get the true width and height of the text object
        float maxWidth = 0f;
        float maxHeight = 0f;
        for (STBTTAlignedQuad q : bakedChars) {
            if (q.x1() > maxWidth) {
                maxWidth = q.x1();
            }

            if (q.y1() > maxHeight) {
                maxHeight = q.y1();
            }
        }

        size = new Vector2f(maxWidth, maxHeight);

        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);

        EBO = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 4, GL_FLOAT, false, 16, 0);
        glEnableVertexAttribArray(0);

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        Shader.use(0);
    }

    // Draw only code, must set shader in renderer class!
    public void draw() {
        // Activate text shader to change color of each text object
        UIFontLoader.getShader().use();
        UIFontLoader.getShader().setVector4f(this.color, "color");

        glBindTexture(GL_TEXTURE_2D, UIFontLoader.getFontBySize(fontSize).getTextureID());
        glBindVertexArray(VAO);

        for(STBTTAlignedQuad q : bakedChars) {
            float[] charVerts = {
                q.x0(), q.y0(),   q.s0(), q.t0(), // Top left
                q.x1(), q.y0(),   q.s1(), q.t0(), // Top right
                q.x1(), q.y1(),   q.s1(), q.t1(), // Bottom right
                q.x0(), q.y1(),   q.s0(), q.t1()  // Bottom left
            };

            glBindBuffer(GL_ARRAY_BUFFER, VBO);
            glBufferData(GL_ARRAY_BUFFER, charVerts, GL_DYNAMIC_DRAW);

            glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
        }
    }

    private float getNewlineYOff(float vCursorY) {
        return vCursorY + (((UIFontLoader.getFontBySize(fontSize).ascent() - UIFontLoader.getFontBySize(fontSize).descent() + UIFontLoader.getFontBySize(fontSize).lineGap()) * UIFontLoader.getFontBySize(fontSize).scale()));
    }

    private void resetPosBuffers() {
        x.position(0);
        x.put(0);
        x.flip();

        y.position(0);
        y.put(UIFontLoader.getFontBySize(fontSize).ascent() * UIFontLoader.getFontBySize(fontSize).scale());
        y.flip();
    }

    private void putPosBuffer(float xpos, float ypos) {
        x.position(0);
        x.put(xpos);
        x.flip();

        y.position(0);
        y.put(ypos);
        y.flip();
    }

    public void setColor(float r, float g, float b, float a) {
        // Check if color isn't the same as current so no need to create a new vec every time
        if (!(color.x == r && color.y == g && color.z == b && color.w == a))
        {
            color = new Vector4f(r, g, b, a);
        }
    }

    public void setColor(Vector4f color) {
        if(!color.equals(this.color)) {
            this.color = color;
        }
    }

    public float x() {
        return pos.x;
    }

    public float y() {
        return pos.y;
    }

    public float width() {
        return size.x;
    }

    public float height() {
        return size.y;
    }

    public Vector2f pos() {
        return this.pos;
    }

    public void setPos(float x, float y) {
        pos.x = x;
        pos.y = y;
    }

    public String text() {
        return this.text;
    }

    // Now extremely efficient/lightweight in most cases
    public void setText(String value) {
        if (!value.equals(text)) {
            text = value;
            init();
        }
    }

    public void setText(int value) {
        if (!Integer.toString(value).equals(text)) {
            text = Integer.toString(value);
            init();
        }
    }

    public float fontSize() {
        return this.fontSize;
    }

    public void setFontSize(float value) {
        fontSize = value;
        init();
    }
}