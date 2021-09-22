package com.zerologic.mcclone.world;

import com.zerologic.mcclone.engine.Texture;
import com.zerologic.mcclone.objects.Mesh;
import org.joml.SimplexNoise;

import static org.lwjgl.opengl.GL40.*;

public class Chunk {

    int VAO;
    int VBO;
    int EBO;

    static float frontLightFactor = 0.8f;
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
            -0.5f, -0.5f, 0.5f,  0.0f, 0.0f, frontLightFactor, // Bottom front left
             0.5f, -0.5f, 0.5f,  1.0f, 0.0f, frontLightFactor, // Bottom front right
             0.5f,  0.5f, 0.5f,  1.0f, 1.0f, frontLightFactor, // Top front right
            -0.5f,  0.5f, 0.5f,  0.0f, 1.0f, frontLightFactor // Top front left
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

    private int width;
    private int length;
    private int height;

    private int[][][] blocks;
    Texture woodTex;
    Texture stoneTex;

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

    OpenSimplex2F simplexNoise = new OpenSimplex2F(413763287631L);
    public void init() {

        stoneTex = new Texture("src/main/resources/textures/stone.png", true, GL_RGBA);

        float maxPercentage = width * length * height;
        float currentProgress = 0f;

        //System.out.println("Generating world");\

        float frequency = 0.007f;
        float[][] map = generateHeightmap(frequency);

        for (int y = 0; y < height; y++) {
            for (int z = 0; z < length; z++) {
                for (int x = 0; x < width; x++) {
                    float yVal = (map[x][z] + 12) * 10;

                    float height = map[x][z] * this.height;

                    if (y < yVal) {
                        blocks[x][y][z] = 1;
                    }

                    blocks[x][(int)yVal][z] = 1;
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
                map[x][y] = (float)simplexNoise.noise2((x + this.x * 16) * frequency, (y + this.z * 16) * frequency);
            }
        }

        return map;
    }

    private float sumOctave(int num_iterations, float x, float y, float z, float persistence, float scale, float low, float high) {
        float maxAmp = 0;
        float amp = 1;
        float freq = scale;
        float noise = 0;


        for (int i = 0; i < num_iterations; i++) {
            noise += SimplexNoise.noise(x * freq, y * freq, z * freq) * amp;
            maxAmp += amp;
            amp *= persistence;
            freq *= 2;
        }

        noise /= maxAmp;

        noise = noise * (high - low) / 2f + (high + low) / 2f;

        return noise;
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

       // System.out.println("Updating mesh...");
        float max = width * length * height;
        float progress = 0;


        int blockIndex = 0;
        for (int y = 0; y < height; y++) {
            for (int z = 0; z < length; z++) {
                for (int x = 0; x < width; x++) {
                    //System.out.println("Updating: " + progress / max * 100 + "%")"

                    if(blocks[x][y][z] == 0) {
                        faces = new boolean[]{false, false, false, false, false, false};
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
                            chunkMesh.appendVertices(leftFace, 6, x, y, z);
                            chunkMesh.appendIndices(ccwIndex, blockIndex);
                            blockIndex++;
                        }

                        if (faces[1]) {
                            chunkMesh.appendVertices(rightFace, 6, x, y, z);
                            chunkMesh.appendIndices(cwIndex, blockIndex);
                            blockIndex++;
                        }

                        if (faces[2]) {
                            chunkMesh.appendVertices(topFace, 6, x, y, z);
                            chunkMesh.appendIndices(ccwIndex, blockIndex);
                            blockIndex++;
                        }

                        if (faces[3]) {
                            chunkMesh.appendVertices(bottomFace, 6, x, y, z);
                            chunkMesh.appendIndices(cwIndex, blockIndex);
                            blockIndex++;
                        }

                        if (faces[4]) {
                            chunkMesh.appendVertices(frontFace, 6, x, y, z);
                            chunkMesh.appendIndices(ccwIndex, blockIndex);
                            blockIndex++;
                        }

                        if (faces[5]) {
                            chunkMesh.appendVertices(backFace, 6, x, y, z);
                            chunkMesh.appendIndices(cwIndex, blockIndex);
                            blockIndex++;
                        }
                    }
                    progress++;
                }
            }
        }
        //System.out.println("Updating: " + progress / max * 100 + "%");
    }

    public void draw() {
        stoneTex.use();
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