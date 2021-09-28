package com.zerologic.mcclone.world;

import com.zerologic.mcclone.Game;
import org.joml.Matrix4f;

import java.util.Random;
import java.util.Vector;

import static org.lwjgl.opengl.GL40.*;

public class World {
    private static final int chunkWidth = 16;
    private static final int chunkLength = 16;
    private static final int chunkHeight = 256;

    private static final Vector<Chunk> chunks = new Vector<>();

    // maxChunks defines the amount of chunks to be rendered, will be n*n units large
    private static int maxChunks = 6; // Can be any number however odd numbers will be casted to even
    private static int halfChunks = maxChunks / 2;


    public static void init(int numChunks) {
        maxChunks = numChunks;
        halfChunks = maxChunks/2;
    }

    /* -- Infinite world generation --
     * We need to be able to always have a 3x3 chunk underneath the player constantly
     * A way to implement would be, get players position, loop through each chunk and get their position,
     * if the player's distance changes by 16 blocks in any direction, add a chunk in that direction
     */

    public static void update() {
        // Get cam coords in chunk coords
        int camX = (int) (Game.getMainCamera().x() / 16);
        int camZ = (int) (Game.getMainCamera().z() / 16);

        for (int z = -halfChunks; z < halfChunks; z++) {
            for (int x = -halfChunks; x < halfChunks; x++) {
                if (!chunkExistsAtPos(camX + x, camZ + z)) {
                    chunks.add(new Chunk(chunkWidth, chunkHeight, chunkLength, camX + x, camZ + z));
                    chunks.lastElement().init();
                }
            }
        }

        /* -- CHUNK CHECKING AND REMOVAL --
         * the reason we do halfchunks + 1 is because chunks vertices are generated in such a way that
         * they are not centered (i.e. they start from one corner and end in the other) therefore we must account
         * for an extra chunk
         */

        for (int i = -halfChunks; i < halfChunks; i++) {
            chunks.remove(getChunkByPos(camX + i, camZ - (halfChunks + 1)));

            chunks.remove(getChunkByPos(camX + i, camZ + halfChunks));

            chunks.remove(getChunkByPos(camX - (halfChunks + 1), camZ + i));

            chunks.remove(getChunkByPos(camX + halfChunks, camZ + i));
        }
    }

    static Matrix4f transform = new Matrix4f();


    private static Chunk getChunkByPos(int x, int z) {
        for (Chunk c : chunks) {
            if (c.x() == x && c.z() == z) {
                return c;
            }
        }
        return null;
    }

    private static boolean chunkExistsAtPos(float x, float z) {
        for (Chunk c : chunks) {
            if (c.x() == x && c.z() == z) {
                return true;
            }
        }
        return false;
    }

    public static Vector<Chunk> chunks() {
        return chunks;
    }

    public static int chunksSize() {
        return chunks.size();
    }
}
