package com.zerologic.mcclone.world.block;

public interface Block {
    int id();

    float[] topFace();
    float[] bottomFace();
    float[] leftFace();
    float[] rightFace();
    float[] frontFace();
    float[] backFace();
}
