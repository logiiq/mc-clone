package com.zerologic.mcclone.world;

import com.zerologic.mcclone.Game;
import java.util.ArrayList;
import java.util.HashSet;

public class World {
    private static final int chunkWidth = 16;
    private static final int chunkLength = 16;
    private static final int chunkHeight = 256;

    private static final ArrayList<Chunk> chunks = new ArrayList<>();

    // maxChunks defines the amount of chunks to be rendered, will be n*n units large
    private static int maxChunks = 6; // Can be any number however odd numbers will be cast to even
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

    private static HashSet<Point> loadedChunkPositions = new HashSet<>();

    /*
    public static void update() {
        // Get cam coords in chunk coords
        int camX = (int) (Game.getMainCamera().x() / 16);
        int camZ = (int) (Game.getMainCamera().z() / 16);

        for (int z = -halfChunks; z < halfChunks; z++) {
            for (int x = -halfChunks; x < halfChunks; x++) {
                if (!chunkExistsAtPos(camX + x, camZ + z)) {
                    chunks.add(new Chunk(chunkWidth, chunkHeight, chunkLength, camX + x, camZ + z));
                    chunks.get(chunks.size() - 1).init();
                }
            }
        }

        /* -- CHUNK CHECKING AND REMOVAL --
         * the reason we do halfchunks + 1 is because chunks vertices are generated in such a way that
         * they are not centered (i.e. they start from one corner and end in the other) therefore we must account
         * for an extra chunk


        for (int i = -halfChunks; i < halfChunks; i++) {
            chunks.remove(getChunkByPos(camX + i, camZ - (halfChunks + 1)));

            chunks.remove(getChunkByPos(camX + i, camZ + halfChunks));

            chunks.remove(getChunkByPos(camX - (halfChunks + 1), camZ + i));

            chunks.remove(getChunkByPos(camX + halfChunks, camZ + i));
        }
    }
    */
    public static void update() {
        // Get cam coords in chunk coords
        int camX = (int) (Game.getMainCamera().x() / 16);
        int camZ = (int) (Game.getMainCamera().z() / 16);

        /*
        // Add new chunks and update loadedChunkPositions set
        for (int z = -halfChunks; z < halfChunks; z++) {
            for (int x = -halfChunks; x < halfChunks; x++) {
                Point pos = new Point(camX + x, camZ + z);
                if (!loadedChunkPositions.contains(pos)) {
                    chunks.add(new Chunk(chunkWidth, chunkHeight, chunkLength, camX + x, camZ + z));
                    chunks.get(chunks.size() - 1).init();
                    loadedChunkPositions.add(pos);
                }
            }
        }

         */

        for (int z = -halfChunks; z < halfChunks; z++) {
            for (int x = -halfChunks; x < halfChunks; x++) {
                if (!chunkExistsAtPos(camX + x, camZ + z)) {
                    chunks.add(new Chunk(chunkWidth, chunkHeight, chunkLength, camX + x, camZ + z));
                    chunks.get(chunks.size() - 1).init();
                    loadedChunkPositions.add(new Point(camX + x, camZ + z)); // Add position to the set
                }
            }
        }

        // Remove chunks and update loadedChunkPositions set
        for (int i = -halfChunks; i < halfChunks; i++) {
            Point pos1 = new Point(camX + i, camZ - (halfChunks + 1));
            Point pos2 = new Point(camX + i, camZ + halfChunks);
            Point pos3 = new Point(camX - (halfChunks + 1), camZ + i);
            Point pos4 = new Point(camX + halfChunks, camZ + i);

            if (loadedChunkPositions.contains(pos1)) {
                chunks.remove(getChunkByPos(camX + i, camZ - (halfChunks + 1)));
                loadedChunkPositions.remove(pos1);
            }

            if (loadedChunkPositions.contains(pos2)) {
                chunks.remove(getChunkByPos(camX + i, camZ + halfChunks));
                loadedChunkPositions.remove(pos2);
            }

            if (loadedChunkPositions.contains(pos3)) {
                chunks.remove(getChunkByPos(camX - (halfChunks + 1), camZ + i));
                loadedChunkPositions.remove(pos3);
            }

            if (loadedChunkPositions.contains(pos4)) {
                chunks.remove(getChunkByPos(camX + halfChunks, camZ + i));
                loadedChunkPositions.remove(pos4);
            }
        }
    }


    /*
    private static Chunk getChunkByPos(int x, int z) {
        for (int i = 0; i < chunks.size(); i++) {
            if (chunks.get(i).x() == x && chunks.get(i).z() == z) {
                return chunks().get(i);
            }
        }
        return null;
    }

     */
    private static Chunk getChunkByPos(int x, int z) {
        Point pos = new Point(x, z);
        if (loadedChunkPositions.contains(pos)) {
            for (int i = 0; i < chunks.size(); i++) {
                if (chunks.get(i).x() == x && chunks.get(i).z() == z) {
                    return chunks.get(i);
                }
            }
        }
        return null;
    }

    private static boolean chunkExistsAtPos(float x, float z) {
        Point pos = new Point(x, z);
        return loadedChunkPositions.contains(pos);
    }

    /*
    private static boolean chunkExistsAtPos(float x, float z) {
        for (Chunk c : chunks) {
            if (c.x() == x && c.z() == z) {
                return true;
            }
        }
        return false;
    }

     */

    public static ArrayList<Chunk> chunks() {
        return chunks;
    }

    public static int chunksSize() {
        return chunks.size();
    }
}
