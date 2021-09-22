package com.zerologic.mcclone.components.gui.uitext;

import org.lwjgl.stb.STBTTBakedChar;

import static org.lwjgl.stb.STBTTBakedChar.*;
import java.nio.ByteBuffer;

public class LoadedFont {

    // The LoadedFont class is simply instantiated with a created bitmap, so that any bitmaps
    // that are needed are created and stored in the FontLoader class.

    private final String path;
    private final float fontSize;

    private Buffer cdata = STBTTBakedChar.create(143);

    private int ascent;
    private int descent;
    private int lineGap;
    private float scale;
    private int bmpSize;
    private int textureID;

    private ByteBuffer bitmap;

    /**
     * Creates a new {@code LoadedFont} with {@code UIFontLoader} for use
     * with {@code UIText} objects.
     * @param path The filepath to the font.
     * @param fontSize The font's size.
     */
    public LoadedFont(String path, float fontSize) {
        this.path = path;
        this.fontSize = fontSize;
    }

    public void setData(Buffer cdata, int ascent, int descent, int lineGap, float scale, int bmpSize, ByteBuffer bitmap, int texture) {

        // Foreach loop adds each STBTTBakedChar into the new buffer without any reference to the source buffer
        for(STBTTBakedChar c : cdata) {
            this.cdata.put(c);
        }
        this.cdata.flip();

        this.ascent = ascent;
        this.descent = descent;
        this.lineGap = lineGap;
        this.scale = scale;
        this.bmpSize = bmpSize;
        this.bitmap = bitmap;
        this.textureID = texture;
    }

    public Buffer getCharData() { return this.cdata; }

    public float getFontSize() { return this.fontSize; }

    public int getBmpSize() { return this.bmpSize; }

    public ByteBuffer getBitmap() { return this.bitmap; }

    public int getTextureID() { return this.textureID; }

    public int ascent() { return this.ascent; }

    public int descent() { return this.descent; }

    public int lineGap() { return this.lineGap; }

    public float scale() { return this.scale; }
}
