package com.zerologic.mcclone.engine;

import com.zerologic.mcclone.objects.GameObject;
import org.lwjgl.BufferUtils;

import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.*;

/**
 * The {@code Texture} class that handles all loading, creation and binding of
 * textures to be used.
 * 
 * @author Dilan Shabani
 */

public class Texture {
	private IntBuffer wbuffer   = BufferUtils.createIntBuffer(1);
	private IntBuffer hbuffer   = BufferUtils.createIntBuffer(1);
	public final IntBuffer comp = BufferUtils.createIntBuffer(1);
	public final ByteBuffer data;

	public int width;
	public int height;

	int textureID;

	/**
	 * Create a texture with the supplied parameters, this class takes care of any 
	 * loading and usage of textures and puts it all in one easy to use class.
	 * 
	 * @param filepath The file path of the texture to be used.
	 * @param flipImageOnLoad Whether or not to flip the image on load.
	 * @param type The type of color channel the texture will use (RGB or RGBA).
	 * 
	 * @author Dilan Shabani
	 */
	
	public Texture(String filepath, boolean flipImageOnLoad, int type) {
		textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureID);

		stbi_set_flip_vertically_on_load(flipImageOnLoad);
		data = stbi_load(filepath, this.wbuffer, this.hbuffer, this.comp, 0);

		width = wbuffer.get(0);
		height = hbuffer.get(0);

		wbuffer.position(0);
		hbuffer.position(0);

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		glTexImage2D(GL_TEXTURE_2D, 0, type, wbuffer.get(), hbuffer.get(), 0, type, GL_UNSIGNED_BYTE, data);
		glGenerateMipmap(GL_TEXTURE_2D);

		stbi_image_free(data);
	}

	/**
	 * This method 'uses' the texture, it is called so that the GPU will draw the correct for each {@code glDrawArrays()} call.
	 * As such, it must be called before the object it is mapped to, is drawn.
	 */
	
	public void use() {
		glBindTexture(GL_TEXTURE_2D, textureID);
	}
}
