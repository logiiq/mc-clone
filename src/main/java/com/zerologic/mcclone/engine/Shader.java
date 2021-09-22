package com.zerologic.mcclone.engine;

import com.zerologic.mcclone.Game;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.io.BufferedReader;
import java.io.FileReader;

import static org.lwjgl.opengl.GL40.*;

public class Shader {

	private int ID; // Program handle

	private static int currentID;

	private int vertexShader; // Vertex shader handle
	private final String vertexShaderSource;

	private int fragmentShader; // Fragment shader handle
	private final String fragmentShaderSource;

	private final boolean shaderType;

	// Matrices for vertices drawn
	private Matrix4f projection = new Matrix4f();
	private Matrix4f view       = new Matrix4f();
	private Matrix4f model      = new Matrix4f();
	private Matrix4f finalMat   = new Matrix4f();

	/**
	 * @param vertPath The vertex shader path
	 * @param fragPath The fragment.glsl shader path
	 * @param type {@code true} if perspective transformation, {@code false} if orthographic
	 */
	public Shader(String vertPath, String fragPath, boolean type) {
		vertexShaderSource = readStringFromFile(vertPath);
		fragmentShaderSource = readStringFromFile(fragPath);
		shaderType = type;
	}

	public void init() {
		ID = glCreateProgram(); // Create the shader program
		vertexShader = glCreateShader(GL_VERTEX_SHADER); // Create the vertex shader
		fragmentShader = glCreateShader(GL_FRAGMENT_SHADER); // Create the fragment.glsl shader

		glShaderSource(vertexShader, vertexShaderSource); // Add shader source to shader
		glCompileShader(vertexShader); // Compile shader
		checkShader(vertexShader);

		glShaderSource(fragmentShader, fragmentShaderSource); // Add shader source to shader
		glCompileShader(fragmentShader); // Compile shader
		checkShader(fragmentShader);

		// Attach shaders and link program, then activate it so that we can initialize the matrices
		glAttachShader(ID, vertexShader);
		glAttachShader(ID, fragmentShader);
		glLinkProgram(ID);

		initMatrices();

		// Delete the shaders as we no longer need them
		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
	}

	public void initMatrices() {
		// Set up matrices
		glUseProgram(ID);
		if (shaderType) {
			projection.setPerspective(degToRad(70.0f), (float)Game.getWindow().width()/(float)Game.getWindow().height(), 0.1f, 300.0f);
		} else if (!shaderType){
			projection.setOrtho(0.0f, (float) Game.getWindow().width(), (float) Game.getWindow().height(), 0.0f, -1.0f, 1.0f);
		}

		update();

		glUseProgram(0);
	}

	public void setProjection(Matrix4f p) { p.get(this.projection); }

	public void setModel(Matrix4f m) {
		m.get(this.model);
	}

	public void setView(Matrix4f v) {
		v.get(this.view);
	}

	/**
	 * Premultiply projection, view and model matrices and send them to the GPU
	 */
	public void update() {
		// Premultiply all matrices
		projection.mul(view, finalMat);
		finalMat.mul(model);

		setMatrix4f(finalMat, "pvm");
	}

	public void use() {
		currentID = this.ID;
		glUseProgram(this.ID);
	}

	public static void use(int id) {
		currentID = 0;
		glUseProgram(id);
	}

	public void setMatrix4f(Matrix4f matrix, String query) {
		int loc = glGetUniformLocation(this.ID, query);
		glUniformMatrix4fv(loc, false, matrix.get(new float[16]));
	}

	public void setVector4f(Vector4f vec, String query) {
		int loc = glGetUniformLocation(this.ID, query);
		glUniform4f(loc, vec.x(), vec.y(), vec.z(), vec.w());
	}

	public static int getCurrentID() {
		return currentID;
	}

	private void checkShader(int shader) {
		int status = glGetShaderi(shader, GL_COMPILE_STATUS);
		if (status != 1) {
			System.err.println("Status: " + status + " Info log: " + glGetShaderInfoLog(shader));
		}
	}

	private static String readStringFromFile(String filePath) {

		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			StringBuilder sb = new StringBuilder();

			String store;

			while((store = reader.readLine()) != null) {
				sb.append(store).append("\r\n");
			}
			return sb.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "An error occurred.";
	}

	float degToRad(float f) {
		return f * ((float)Math.PI / 180.0f);
	}
}
