package com.zerologic.mcclone.world.block;

import com.zerologic.mcclone.world.Chunk;

public class Grass implements Block {

    private static final int id = 1;

    // Each face will have the x and y position of the texture face
    private static final float[] topFace = {1f, 0f};
    private static final float[] sideFace = {0f, 0f};
    private static final float[] bottomFace = {2f, 0f};

    @Override
    public int id() {
        return id;
    }

    @Override
    public float[] topFace() {
        return topFace;
    }

    @Override
    public float[] bottomFace() {
        return bottomFace;
    }

    @Override
    public float[] leftFace() {
        return sideFace;
    }

    @Override
    public float[] rightFace() {
        return leftFace();
    }

    @Override
    public float[] frontFace() {
        return leftFace();
    }

    @Override
    public float[] backFace() {
        return leftFace();
    }
}
