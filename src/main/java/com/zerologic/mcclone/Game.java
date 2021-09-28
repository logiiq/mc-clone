package com.zerologic.mcclone;

import com.zerologic.mcclone.components.gui.uitext.UIFontLoader;
import com.zerologic.mcclone.components.gui.uitext.UIText;
import com.zerologic.mcclone.engine.*;

import com.zerologic.mcclone.world.World;

import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.glfw.GLFW.*;


import com.zerologic.mcclone.components.Camera;

// NOTE: PLEASE ensure that when drawing vertexes you use the correct WINDING ORDER
// WHICH IS COUNTER CLOCKWISE

public class Game {
    private static Window gameWindow = new Window(1920, 1080, "Minecraft Clone", true);
    private static Shader gameShader = new Shader("src/main/resources/shaders/vertex.glsl", "src/main/resources/shaders/fragment.glsl", true);
    private static Shader textShader = new Shader("src/main/resources/shaders/text/vertex.glsl", "src/main/resources/shaders/text/fragment.glsl", false);

    //-90deg yaw faces negative z direction!
    private static Camera mainCamera = new Camera(-2.0f,  128.0f, 0.0f, 0.0f, 90.0f);
    private static UIText fpsCounter = new UIText("FPS: 00", 50.0f, 0.0f, 0.0f);

    private static UIText playerPos = new UIText("X:00 Y:00 Z:00", 50f);
    private static UIText chunkSize = new UIText("XX chunks", 50f);

    private static boolean generate = true;

    void init() {
        // Initialize program base
        gameWindow.init();
        Input.init();

        // Initialize shaders
        gameShader.init();
        textShader.init();

        mainCamera.init(); // Initialize camera

        UIFontLoader.init(textShader, "C:/Windows/Fonts/Arial.ttf"); // Initialize the font
        // Initialize text objects
        playerPos.init();
        chunkSize.init();

        World.init(10); // Initialize world

        // GL stuff
        glEnable(GL_CULL_FACE); // Enable face culling
        glEnable(GL_BLEND);
        glBlendFunc(GL_DST_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        initCallbacks(); // Initialize callbacks

        autoposition(); // Set positions of all UI elements
    }

    void initCallbacks() {
        // Set framebuffer size callback
        glfwSetFramebufferSizeCallback(gameWindow.id(), (window, width, height) -> {
            gameWindow.setDimensions(width, height);

            // Important to set viewport again
            glViewport(0, 0, width, height);

            // Reset projection matrices
            Game.getGameShader().use();
            Game.getGameShader().initMatrices();
            UIFontLoader.getShader().use();
            UIFontLoader.getShader().initMatrices();

            autoposition(); // autoposition all UI elements to their respective areas
        });
    }

    void loop() {
        glClearColor(0.1f, 0.3f, 0.6f, 1.0f);

        int frames = 0;
        float time = 0f;
        while(!glfwWindowShouldClose(gameWindow.id())) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Print FPS
            if(((float)glfwGetTime() - time) >= 1.0f) {
                time = (float) glfwGetTime();
                fpsCounter.setText("FPS: " + frames);

                frames = 0;
            }
            mainCamera.update();
            Renderer.drawWorld();
            //World.draw();

            if (generate)
                World.update();

            // Draw UI after everything else so we dont have strange rendering issues
            Renderer.draw(fpsCounter);

            chunkSize.setText(World.chunksSize() + " chunks");
            Renderer.draw(chunkSize);

            playerPos.setText("X: " + (int)mainCamera.x() + " Y: " + (int)mainCamera.y() + " Z:" + (int)mainCamera.z());
            Renderer.draw(playerPos);

            glfwSwapBuffers(gameWindow.id());
            glfwPollEvents();
            Input.update();
            Time.calcTime();
            frames++;
        }
    }

    void autoposition() {
        playerPos.setPos(0.0f, gameWindow.height() - playerPos.height());
        chunkSize.setPos(gameWindow.width() - chunkSize.width(), gameWindow.height() - chunkSize.height());
    }

    // Getter methods
    public static Window getWindow() {
        return gameWindow;
    }

    public static Shader getGameShader() { return gameShader; }

    public static Camera getMainCamera() { return mainCamera; }

    public static boolean generate() {
        return generate;
    }

    // Setter methods
    public static void setGenerate(boolean g) {
        generate = g;
    }

    void clean() {
        GL.destroy();
        glfwDestroyWindow(gameWindow.id());
        glfwTerminate();
    }

    void run() {
        init();
        loop();
        clean();
    }

    public static void main(String[] args) {
        new Game().run();
    }
}
