package com.zerologic.mcclone.objects;

import java.util.Vector;

public class Mesh {
    Vector<Float> vertices;
    Vector<Integer> indices;

    private int indicesLength = 0;

    public Mesh() {
        vertices = new Vector<>();
        indices = new Vector<>();
    }

    public float[] get() {
        float[] data = new float[vertices.size()];

        for (int i = 0; i < data.length; i++) {
            data[i] = vertices.elementAt(i);
        }

        return data;
    }

    public int[] getIndices() {
        int[] arrIndices = new int[indices.size()];

        for (int i = 0; i < arrIndices.length; i++) {
            arrIndices[i] = indices.elementAt(i);
        }

        return arrIndices;
    }

    public int indicesLength() {
        return indicesLength;
    }

    public void appendVertices(float[] verts, float[] texCoords, int dataAmount, float x, float y, float z) {
        for (int line = 0; line < 4; line++) {

            float tileOffset = 16f / 256f;
            float s0 = (texCoords[0] * 16f) / 256f;
            float s1 = s0 + tileOffset;
            float t1 = (texCoords[1] * 16f) / 256f;
            float t0 = t1 + tileOffset;

            float s = s0, t = t0;

            if ((verts[3 + (line * dataAmount)]) == 1) {
                s = s1;
            }

            if ((verts[4 + (line * dataAmount)]) == 1) {
                t = t1;
            }

            vertices.add(verts[line * dataAmount] + x);
            vertices.add(verts[1 + (line * dataAmount)] + y);
            vertices.add(verts[2 + (line * dataAmount)] + z);
            vertices.add(s); // Texture S
            vertices.add(t); // Texture T
            vertices.add(verts[5 + (line * dataAmount)]); // Light Value
        }
    }

    // block index will tell the program which block it is drawing which will then multiply it by 4,
    public void appendIndices(int[] index, int blockIndex) {
        for (int i : index) {
            indices.add(i + (blockIndex * 4));
            indicesLength++;
        }
    }

    public void deleteVertices() {
        this.vertices.removeAllElements();
    }

    public void deleteIndices() {
        this.indices.removeAllElements();
    }
}
