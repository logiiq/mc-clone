package com.zerologic.mcclone.engine;

import com.zerologic.mcclone.Game;
import com.zerologic.mcclone.components.gui.uitext.UIFontLoader;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {
    private static int monitorWidth;
    private static int monitorHeight;

    private long id;
    private int width;
    private int height;
    private String title;
    private long monitor;

    private GLFWVidMode mode;

    boolean defaultHints;
    boolean fullscreen;

    /**
     * Create a new window with the following parameters, should only be instantiated once.
     * @param width The width of the window
     * @param height The height of the window
     * @param title The title of the window
     * @param defaultHints Whether or not to use default GLFW window hints
     */
    public Window(int width, int height, String title, boolean defaultHints) {
        this.width = width;
        this.height = height;
        this.title = title;
        this.defaultHints = defaultHints;
    }

    public void init() {
        if(!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        if(defaultHints) {
            glfwDefaultWindowHints();
        }

        monitor = glfwGetPrimaryMonitor();
        mode = glfwGetVideoMode(monitor);

        id = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        glfwMakeContextCurrent(id);
        glfwSwapInterval(0);

        monitorWidth = mode.width();
        monitorHeight = mode.height();

        glfwSetWindowPos(id, monitorWidth / 2 - width / 2, monitorHeight / 2 - height / 2);

        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);

        // Set framebuffer size callback
        glfwSetFramebufferSizeCallback(id, (window, width, height) -> {
            this.width = width;
            this.height = height;

            System.out.println("here");

            // Important to set viewport again
            glViewport(0, 0, width, height);

            Game.getGameShader().use();
            Game.getGameShader().initMatrices();

            UIFontLoader.getShader().use();
            UIFontLoader.getShader().initMatrices();
        });
    }

    // Setter methods
    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void switchFullscreen() {
        fullscreen = !fullscreen;

        if(fullscreen) {
            glfwSetWindowMonitor(id, monitor, 0, 0, mode.width(), mode.height(), mode.refreshRate());
        } else {
            glfwSetWindowMonitor(id, NULL, 0, 0, 1920, 1080, mode.refreshRate());
            glfwSetWindowPos(id, mode.width()/2 - width/2, mode.height()/2 - height/2);
        }
    }

    // Getter methods
    public long id() {
        return this.id;
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    public String title() {
        return this.title;
    }
}
