import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.vector.Vector3f;

import shaders.ShaderProgram;

import static org.lwjgl.opengl.GL11.*;

public class Objeto3D {
	float x;
	float y;
	float z;
	
	float velocidade;
	Vector3f velVec = new Vector3f();
	
	float escala;
	

	public void desenhase(ShaderProgram shaderp) {
	}
	
	public void simulase(float timeLapse) {
		//System.out.println(""+x+" "+y+" "+z+" "+timeLapse);
		float oldx = x;
		float oldy = y;
		float oldz = z;
		
		x = x+velocidade*velVec.x*timeLapse;
		y = y+velocidade*velVec.y*timeLapse;
		z = z+velocidade*velVec.z*timeLapse;
		
		if(x > 0.5f||x <-0.5f) {
			x = oldx;
			velVec.set(-velVec.x, -velVec.y, -velVec.z); 
		}
		if(y > 0.5f||y <-0.5f) {
			y = oldy;
			velVec.set(-velVec.x, -velVec.y, -velVec.z); 
		}
		if(z > 0.5f||z <-0.5f) {
			z = oldz;
			velVec.set(-velVec.x, -velVec.y, -velVec.z); 
		}
	}
}
