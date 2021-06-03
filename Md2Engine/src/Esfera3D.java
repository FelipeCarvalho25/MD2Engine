import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import Model.Model;
import shaders.ShaderProgram;


public class Esfera3D extends Objeto3D {
	Sphere sphere = new Sphere();
	Vector3f cor = new Vector3f();
	
	Model model;
	
	FloatBuffer matrixBuffer = MemoryUtil.memAllocFloat(16);
	
	public Esfera3D(Model model) {
		this.model = model;
	}
	
	public void desenhase(ShaderProgram shaderp) {
		int modellocation = glGetUniformLocation(shaderp.programID, "model");
		
		Matrix4f modelmatrix = new Matrix4f();
		modelmatrix.translate(new Vector3f(x, y, z));
		
		
		//System.out.println(""+x+" "+y+" "+z );
		Matrix4f scalematrix = new Matrix4f();
		scalematrix.scale(new Vector3f(0.001f, 0.001f, 0.001f));
		
		Matrix4f finalmatix = modelmatrix.mul(modelmatrix, scalematrix, null);
		//Matrix4f scalematrix = new Matrix4f();
		//scalematrix.scale(new Vector3f(0.1f, 0.1f, 0.1f));
		 
		 
		
		matrixBuffer.clear();
		finalmatix.storeTranspose(matrixBuffer);
		matrixBuffer.flip();
		glUniformMatrix4fv(modellocation, false, matrixBuffer);
		
		//glPushMatrix();
		
	    //glDisable(GL_TEXTURE_2D);
	    //glColor3f(cor.x, cor.y, cor.z);
		
	    //glEnable(GL_TEXTURE_2D);
	    //glColor3f(1.0f,1.0f,1.0f);
	    
		//glTranslatef(x,y,z);
		
		//glScalef(0.01f, 0.01f, 0.01f);
		
		
		
		//sphere.draw(0.01F, 16, 16);
		model.draw();
		
		//glPopMatrix();
	}
}
