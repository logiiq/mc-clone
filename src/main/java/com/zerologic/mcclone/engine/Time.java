package com.zerologic.mcclone.engine;

import static org.lwjgl.glfw.GLFW.*;

public class Time {

    private static double oldTime = 0;
    private static double currentTime = 0;
    private static float deltaTime;

    public static float deltaTimef() {
        return deltaTime;
    }

    public static void calcTime() {
        oldTime = currentTime;
        currentTime = glfwGetTime();

        deltaTime = (float) (currentTime - oldTime);
    }
}