package com.zerologic.mcclone.world.block;

public class Stone implements Block {

    private static final int id = 4;

    private static final float[] face = {4f, 0f};

    @Override
    public int id() {
        return id;
    }

    @Override
    public float[] topFace() {
        return face;
    }

    @Override
    public float[] bottomFace() {
        return topFace();
    }

    @Override
    public float[] leftFace() {
        return topFace();
    }

    @Override
    public float[] rightFace() {
        return topFace();
    }

    @Override
    public float[] frontFace() {
        return topFace();
    }

    @Override
    public float[] backFace() {
        return topFace();
    }
}
