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

    public void appendVertices(float[] verts, int dataAmount, float x, float y, float z) {
        for (int line = 0; line < 4; line++) {
            vertices.add(verts[line * 6] + x);
            vertices.add(verts[1 + (line * dataAmount)] + y);
            vertices.add(verts[2 + (line * dataAmount)] + z);
            vertices.add(verts[3 + (line * dataAmount)]);
            vertices.add(verts[4 + (line * dataAmount)]);
            vertices.add(verts[5 + (line * dataAmount)]);
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
