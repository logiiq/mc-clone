package com.zerologic.mcclone.components;

import com.zerologic.mcclone.Game;
import com.zerologic.mcclone.engine.Input;
import com.zerologic.mcclone.engine.Shader;
import com.zerologic.mcclone.engine.Time;
import com.zerologic.mcclone.objects.GameObject;
import org.joml.*;
import java.lang.Math;

public class Camera extends GameObject {
    private Matrix4f view = new Matrix4f();

    private Vector3f position;
    private Vector3f direction = new Vector3f();

    private Vector3f worldUp = new Vector3f(0.0f, 1.0f, 0.0f);
    private Vector3f camUp = new Vector3f();
    private Vector3f camRight = new Vector3f();

    // Mouse look
    private float[] mouseoffset;

    float pitch; // default 0f
    float yaw; // default -90.0f

    public Camera(float x, float y, float z, float pitch, float yaw) {
        super(x, y, z);
        position = new Vector3f(x(), y(), z());

        this.pitch = pitch;
        this.yaw = yaw;
    }

    public void init() {
        mouseoffset = Input.getMOffsetReference();
    }

    public void update() {
        updateDirection();

        yaw += mouseoffset[0];
        pitch += mouseoffset[1];

        setPos(position.x(), position.y(), position.z());

        view.lookAt(position, direction.add(position, new Vector3f()), camUp);
        Game.getGameShader().use();
        Game.getGameShader().setView(view);
        Game.getGameShader().update();
        view.identity();
        Shader.use(0);
    }

    void updateDirection() {
        if(pitch >= 90.0f) {
            pitch = 89.0f;
        } else if (pitch <= -90.0f) {
            pitch = -89.0f;
        }

        direction.x = (float) Math.cos(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        direction.z = (float) Math.sin(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        direction.y = (float) Math.sin(Math.toRadians(pitch));
        direction = direction.normalize();

        // get camera right (cross the direction of camera and world up)
        worldUp.cross(direction, camRight);
        camRight.normalize();

        // get camera up (cross the direction of camera and camera right)
        direction.cross(camRight, camUp);
        camUp.normalize();
    }

    public void moveForward(float moveSpeed) {
        position.add(direction.mul(moveSpeed * Time.deltaTimef(), new Vector3f()));
    }

    public void moveBackward(float moveSpeed) {
        position.sub(direction.mul(moveSpeed * Time.deltaTimef(), new Vector3f()));
    }

    public void moveRight(float moveSpeed) {
        position.sub(camRight.mul(moveSpeed * Time.deltaTimef(), new Vector3f()));
    }

    public void moveLeft(float moveSpeed) {
        position.add(camRight.mul(moveSpeed * Time.deltaTimef(), new Vector3f()));
    }

    public void moveUp(float moveSpeed) {
        position.add(worldUp.mul(moveSpeed * Time.deltaTimef(), new Vector3f()));
    }

    public void moveDown(float moveSpeed) {
        position.sub(worldUp.mul(moveSpeed * Time.deltaTimef(), new Vector3f()));
    }

    private void printVec3(Vector3f vec) {
        System.out.println("x: " + vec.x() + " y: " + vec.y() + " z: " + vec.z());
    }

    public void draw() {}
}
