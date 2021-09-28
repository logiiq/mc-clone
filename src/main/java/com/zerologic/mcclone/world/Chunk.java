package com.zerologic.mcclone.world;

import com.zerologic.mcclone.engine.Texture;
import com.zerologic.mcclone.objects.Mesh;
import com.zerologic.mcclone.world.block.*;

import static org.lwjgl.opengl.GL40.*;

public class Chunk {
    int VAO;
    int VBO;
    int EBO;

    public static float frontLightFactor = 0.8f;
    static float backLightFactor = 0.6f;
    static float leftLightFactor = 0.55f;
    static float rightLightFactor = 0.7f;
    static float topLightFactor = 1.0f;
    static float bottomLightFactor = 0.4f;

    static int[] ccwIndex = {
            0, 1, 2,
            0, 2, 3
    };

    static int[] cwIndex = {
            3, 2, 1,
            3, 1, 0
    };

    static float[] frontFace = {
            // Front face
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, frontLightFactor, // Bottom front left
             0.5f, -0.5f, 0.5f, 1.0f, 0.0f, frontLightFactor, // Bottom front right
             0.5f,  0.5f, 0.5f, 1.0f, 1.0f, frontLightFactor, // Top front right
            -0.5f,  0.5f, 0.5f, 0.0f, 1.0f, frontLightFactor// Top front left
    };

    static float[] backFace = {
            // Back face
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, backLightFactor, // Bottom back left
             0.5f, -0.5f, -0.5f, 1.0f, 0.0f, backLightFactor, // Bottom back right
             0.5f,  0.5f, -0.5f, 1.0f, 1.0f, backLightFactor, // Top back right
            -0.5f,  0.5f, -0.5f, 0.0f, 1.0f, backLightFactor  // Top back left
    };

    static float[] leftFace = {
            // Left face
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, leftLightFactor, // Bottom back left
            -0.5f, -0.5f,  0.5f, 1.0f, 0.0f, leftLightFactor, // Bottom front left
            -0.5f,  0.5f,  0.5f, 1.0f, 1.0f, leftLightFactor, // Top front left
            -0.5f,  0.5f, -0.5f, 0.0f, 1.0f, leftLightFactor  // Top back left
    };

    static float[] rightFace = {
            // Right face
            0.5f, -0.5f, -0.5f, 0.0f, 0.0f, rightLightFactor, // Bottom back left
            0.5f, -0.5f,  0.5f, 1.0f, 0.0f, rightLightFactor, // Bottom front left
            0.5f,  0.5f,  0.5f, 1.0f, 1.0f, rightLightFactor, // Top front left
            0.5f,  0.5f, -0.5f, 0.0f, 1.0f, rightLightFactor  // Top back left
    };

    static float[] topFace = {
            // Top face
            -0.5f, 0.5f,  0.5f, 0.0f, 0.0f, topLightFactor, // Top front left
             0.5f, 0.5f,  0.5f, 1.0f, 0.0f, topLightFactor, // Top front right
             0.5f, 0.5f, -0.5f, 1.0f, 1.0f, topLightFactor, // Top back right
            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, topLightFactor  // Top back left
    };

    static float[] bottomFace = {
            // Bottom face
            -0.5f, -0.5f,  0.5f, 0.0f, 0.0f, bottomLightFactor, // Bottom front left
             0.5f, -0.5f,  0.5f, 1.0f, 0.0f, bottomLightFactor, // Bottom front right
             0.5f, -0.5f, -0.5f, 1.0f, 1.0f, bottomLightFactor, // Bottom back right
            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f, bottomLightFactor  // Bottom back left
    };

    private Mesh chunkMesh = new Mesh();

    private final int width;
    private final int length;
    private final int height;

    private int[][][] blocks;
    Texture atlas;

    private static final int GRASS = 1;
    private static final int DIRT = 2;
    private static final int WOOD = 3;
    private static final int STONE = 4;

    // Chunk actual position
    private float x;
    private float z;

    public Chunk(int width, int height, int length, float x, float z) {
        this.width = width;
        this.height = height;
        this.length = length;
        this.x = x;
        this.z = z;

        this.blocks = new int[width][height][length];
    }

    OpenSimplex2F simplexNoise = new OpenSimplex2F(-11L);

    float frequency = 0.06f;
    public void init() {

        atlas = new Texture("src/main/resources/textures/atlas.png", false, GL_RGBA);

        float maxPercentage = width * length * height;
        float currentProgress = 0f;

        //System.out.println("Generating world");\

        float[][] map = generateHeightmap(frequency);

        for (int y = 0; y < height; y++) {
            for (int z = 0; z < length; z++) {
                for (int x = 0; x < width; x++) {
                    float normalized = map[x][z] * (1f - 0f) / 2f + (1f + 0f) / 2f; // 0 to 1

                    float yVal = (normalized) * height;

                    if(yVal >= height)
                        yVal = height - 1f;

                    if(yVal < 0f)
                        yVal = 0f;

                    if (y < yVal) {
                        blocks[x][y][z] = DIRT;
                    }

                    if (yVal > 140f) {
                        blocks[x][(int) yVal][z] = STONE;
                    } else {
                        blocks[x][(int) yVal][z] = 1;
                    }
                }
            }
        }
        //System.out.println("Loading: " + currentProgress / maxPercentage * 100 + "%");

        updateChunkMesh(); // Generate faces for all the blocks

        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, chunkMesh.get(), GL_DYNAMIC_DRAW);

        EBO = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, chunkMesh.getIndices(), GL_DYNAMIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 24, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 2, GL_FLOAT, false, 24, 12);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 1, GL_FLOAT, false, 24, 20);
        glEnableVertexAttribArray(2);

        // Cleanup
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    private float[][] generateHeightmap(float frequency) {
        float[][] map = new float[width][length];

        for (int y = 0; y < length; y++) {
            for (int x = 0; x < width; x++) {
                double nx = (x + this.x * 16) / width - 0.5;
                double ny = (y + this.z * 16) / length - 0.5;

                double e;

                e = (float)simplexNoise.noise2_XBeforeY(nx * frequency, ny * frequency);
                e += 0.2f * (float)simplexNoise.noise2_XBeforeY(nx * (frequency * 2f), ny * (frequency * 2f));
                e += 0.3f * (float)simplexNoise.noise2_XBeforeY(nx * (frequency * 6f), ny * (frequency * 6f));
                e = e / (1f + 0.2f + 0.3f);

                map[x][y] = (float)Math.pow(e * 0.8, 3.00);
            }
        }

        return map;
    }

    int deleteY = 0;

    public void removeBlock(int x, int z) {
        blocks[x][deleteY][z] = 0;
        chunkMesh.deleteVertices();
        chunkMesh.deleteIndices();
        updateChunkMesh();

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, chunkMesh.get(), GL_DYNAMIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, chunkMesh.getIndices(), GL_DYNAMIC_DRAW);

        deleteY++;
    }

    public int[][][] getBlocks() {
        return blocks;
    }

    public void updateChunkMesh() {
        // left, right, top, bottom, front, back
        boolean[] faces = {true, true, true, true, true, true};

        int blockType = 0;

        int blockIndex = 0;
        for (int y = 0; y < height; y++) {
            for (int z = 0; z < length; z++) {
                for (int x = 0; x < width; x++) {

                    Block grass = new Grass();
                    Block dirt = new Dirt();
                    Block stone = new Stone();

                    Block currentBlock = grass;
                    blockType = blocks[x][y][z];

                    if (blockType == GRASS) {
                        currentBlock = grass;
                    } else if (blockType == DIRT) {
                        currentBlock = dirt;
                    } else if (blockType == STONE) {
                        currentBlock = stone;
                    }

                    if(blocks[x][y][z] == 0) {
                        continue;
                    } else {

                        // Check left
                        try {
                            if (blocks[x - 1][y][z] != 0) {
                                faces[0] = false;
                            } else if (blocks[x - 1][y][z] == 0) {
                                faces[0] = true;
                            }
                        } catch (Exception e) {
                            faces[0] = true;
                        }

                        // Check right
                        try {
                            if (blocks[x + 1][y][z] != 0) {
                                faces[1] = false;
                            } else if (blocks[x + 1][y][z] == 0) {
                                faces[1] = true;
                            }
                        } catch (Exception e) {
                            faces[1] = true;
                        }

                        // Check top
                        try {
                            if (blocks[x][y + 1][z] != 0) {
                                faces[2] = false;
                            } else if (blocks[x][y + 1][z] == 0) {
                                faces[2] = true;
                            }
                        } catch (Exception e) {
                            faces[2] = true;
                        }

                        // Check bottom
                        try {
                            if (blocks[x][y - 1][z] != 0) {
                                faces[3] = false;
                            } else if (blocks[x][y - 1][z] == 0) {
                                faces[3] = true;
                            }
                        } catch (Exception e) {
                            faces[3] = true;
                        }

                        // Check front
                        try {
                            if (blocks[x][y][z + 1] != 0) {
                                faces[4] = false;
                            } else if (blocks[x][y][z + 1] == 0) {
                                faces[4] = true;
                            }
                        } catch (Exception e) {
                            faces[4] = true;
                        }

                        // Check back
                        try {
                            if (blocks[x][y][z - 1] != 0) {
                                faces[5] = false;
                            } else if (blocks[x][y][z - 1] == 0) {
                                faces[5] = true;
                            }
                        } catch (Exception e) {
                            faces[5] = true;
                        }

                        if (faces[0]) {
                            chunkMesh.appendVertices(leftFace, currentBlock.leftFace(), 6, x, y, z);
                            chunkMesh.appendIndices(ccwIndex, blockIndex);
                            blockIndex++;
                        }

                        if (faces[1]) {
                            chunkMesh.appendVertices(rightFace, currentBlock.rightFace(), 6, x, y, z);
                            chunkMesh.appendIndices(cwIndex, blockIndex);
                            blockIndex++;
                        }

                        if (faces[2]) {
                            chunkMesh.appendVertices(topFace, currentBlock.topFace(), 6, x, y, z);
                            chunkMesh.appendIndices(ccwIndex, blockIndex);
                            blockIndex++;
                        }

                        if (faces[3]) {
                            chunkMesh.appendVertices(bottomFace, currentBlock.bottomFace(), 6, x, y, z);
                            chunkMesh.appendIndices(cwIndex, blockIndex);
                            blockIndex++;
                        }

                        if (faces[4]) {
                            chunkMesh.appendVertices(frontFace, currentBlock.frontFace(), 6, x, y, z);
                            chunkMesh.appendIndices(ccwIndex, blockIndex);
                            blockIndex++;
                        }

                        if (faces[5]) {
                            chunkMesh.appendVertices(backFace, currentBlock.backFace(), 6, x, y, z);
                            chunkMesh.appendIndices(cwIndex, blockIndex);
                            blockIndex++;
                        }
                    }
                }
            }
        }
    }

    public void draw() {
        atlas.use();
        glBindVertexArray(VAO);
        glDrawElements(GL_TRIANGLES, chunkMesh.indicesLength(), GL_UNSIGNED_INT, 0);
    }

    public float x() {
        return this.x;
    }

    public float z() {
        return this.z;
    }
}