package com.zerologic.mcclone.engine;

import com.zerologic.mcclone.Game;
import com.zerologic.mcclone.components.gui.uitext.UIFontLoader;
import com.zerologic.mcclone.components.gui.uitext.UIText;
import com.zerologic.mcclone.objects.GameObject;
import com.zerologic.mcclone.world.Chunk;
import com.zerologic.mcclone.world.World;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL40.*;

public class Renderer {

    private static Matrix4f transform = new Matrix4f();

    public static void draw(GameObject object) {
        Game.getGameShader().use();

        transform.translation(object.x(), object.y(), object.z());
        Game.getGameShader().setModel(transform);
        Game.getGameShader().update();
        object.draw();
    }

    public static void drawWorld() {
        Game.getGameShader().use();

        for(Chunk c : World.chunks()) {
            transform.translation(c.x() * 16, 0.0f, c.z() * 16);
            Game.getGameShader().setModel(transform);
            Game.getGameShader().update();

            c.draw();
        }

        Shader.use(0);
    }

    public static void draw(UIText text) {
        UIFontLoader.getShader().use();

        transform.translation(text.x(), text.y(), 0f);
        UIFontLoader.getShader().setModel(transform);
        UIFontLoader.getShader().update();

        glDisable(GL_DEPTH_TEST);
        text.draw();
        glEnable(GL_DEPTH_TEST);

        Shader.use(0);
    }
}
