package com.zerologic.mcclone.engine;

import com.zerologic.mcclone.Game;
import com.zerologic.mcclone.world.World;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.opengl.GL40.*;

import static org.lwjgl.glfw.GLFW.*;

public class Input {

    private static long window = Game.getWindow().id();

    private final static float[] mpos = new float[2];
    private final static float[] moffset = new float[2];

    static float oldX = 0.0f, oldY = 0.0f;
    static float sensitivity = 0.1f;

    private static boolean firstMouse = true;
    private static boolean drawMode = true;
    private static boolean inputMode = true;

    public static void init() {
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                glfwSetWindowShouldClose(window, true);
            }

            if (key == GLFW_KEY_F && action == GLFW_PRESS) {
                drawMode = !drawMode;

                if(drawMode)
                    glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                else if (!drawMode)
                    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            }

            if (key == GLFW_KEY_C && action == GLFW_PRESS) {
                inputMode = !inputMode;
                if(inputMode)
                    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                else if(!inputMode)
                    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            }

            if (key == GLFW_KEY_F11 && action == GLFW_PRESS) {
                Game.getWindow().switchFullscreen();
            }

            if (key == GLFW_KEY_Q && action == GLFW_PRESS) {
                Game.setGenerate(!Game.generate());
            }

        });

        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            if(firstMouse) {
                oldX = (float) xpos;
                oldY = (float) ypos;
                firstMouse = false;
            }

            mpos[0] = (float) xpos;
            mpos[1] = (float) ypos;
        });

        glfwSetWindowFocusCallback(window, (window, focus) -> {
           if(!focus) {
               firstMouse = true;
           }
        });

        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
           glViewport(0, 0, width, height);
        });
    }
    static int x=0, y=0, z=0;

    static float sprintSpeed = 1.0f;

    // Instantaneous key updates
    public static void update() {
        offsetUpdate();

        if(glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
            sprintSpeed = 4.0f;
        } else {
            sprintSpeed = 1.0f;
        }

        if(glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            Game.getMainCamera().moveForward(5.0f * sprintSpeed);
        }
        if(glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            Game.getMainCamera().moveBackward(5.0f * sprintSpeed);
        }
        if(glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            Game.getMainCamera().moveLeft(5.0f * sprintSpeed);
        }
        if(glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            Game.getMainCamera().moveRight(5.0f * sprintSpeed);
        }

        if(glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
            Game.getMainCamera().moveUp(5.0f * sprintSpeed);
        }

        if(glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS) {
            Game.getMainCamera().moveDown(5.0f * sprintSpeed);
        }

    }

    public static void offsetUpdate() {
        moffset[0] = sensitivity * (mpos[0] - oldX);
        moffset[1] = sensitivity * (oldY - mpos[1]);

        oldX = mpos[0];
        oldY = mpos[1];
    }

    public static float[] getMousePosReference() {
        return mpos;
    }

    public static float[] getMOffsetReference() {
        return moffset;
    }

}
