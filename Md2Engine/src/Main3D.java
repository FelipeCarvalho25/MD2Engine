import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import Model.VboCube;
import obj.ObjModel;
import shaders.ShaderProgram;
import shaders.StaticShader;
import util.TextureLoader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.*;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import obj.ObjMd2;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;



public class Main3D {

	// The window handle
	private long window;
	
	ObjModel obj = new ObjModel();
	ObjModel objSpace = new ObjModel();
        ObjMd2 md2  = new ObjMd2();
	
	int idTextura1;
	int idTextura2;
	int idTextura3;
    BufferedImage imageUV;
    
    boolean firstrun = true;
    
    Esfera3D esferaTeste;
    
    int elapseTime = 0;
    long lastframeTime = 0;
    
    ArrayList<Objeto3D> listaDeObjetos = new ArrayList<Objeto3D>();
    
    Random rnd = new Random();
    
    int camAngleX = 0;
    int camAngleY = 0;
    
    VboCube modelCube;

	public void run() {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		init();
		loop();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void init() {
		
		//obj.loadObj("tank.obj");
		//objSpace.loadObj("x-35_obj.obj");
		
		
		//objSpace.loadObj("Intergalactic_Spaceship-(Wavefront).obj");
		
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(800, 600, "Hello World!", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ) {
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
			}
			
			if(action == GLFW_PRESS) {
				if(key == GLFW_KEY_A) {
					esferaTeste.velocidade = 0.25f;
					esferaTeste.velVec.set(-1,0,0);
				}
				if(key == GLFW_KEY_D) {
					esferaTeste.velocidade = 0.25f;
					esferaTeste.velVec.set(1,0,0);
				}
				if(key == GLFW_KEY_W) {
					esferaTeste.velocidade = 0.25f;
					esferaTeste.velVec.set(0,1,0);
				}
				if(key == GLFW_KEY_S) {
					esferaTeste.velocidade = 0.25f;
					esferaTeste.velVec.set(0,-1,0);
				}
				if(key == GLFW_KEY_RIGHT) {
					camAngleY+=10;
				}
				if(key == GLFW_KEY_LEFT) {
					camAngleY-=10;
				}
				if(key == GLFW_KEY_UP) {
					camAngleX+=10;
				}
				if(key == GLFW_KEY_DOWN) {
					camAngleX-=10;
				}
			}
			if(action == GLFW_RELEASE) {
				if(key == GLFW_KEY_A || key == GLFW_KEY_D) {
					esferaTeste.velocidade = 0.0f;
					esferaTeste.velVec.set(0,0,0);
				}
				if(key == GLFW_KEY_W || key == GLFW_KEY_S) {
					esferaTeste.velocidade = 0.0f;
					esferaTeste.velVec.set(0,0,0);
				}
			}
			
		});

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically
		
		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
	}

	private void loop() {

		GL.createCapabilities();
		
		ShaderProgram shader = new StaticShader(); 
		
		idTextura1 = TextureLoader.loadTexture(TextureLoader.loadImage("gato-american-wirehair-cuidados.jpg"));
		//idTextura2 = TextureLoader.loadTexture(TextureLoader.loadImage("Intergalactic Spaceship_color_4.jpg"));
		//idTextura2 = TextureLoader.loadTexture(TextureLoader.loadImage("x35_b.jpg"));
		idTextura3 = TextureLoader.loadTexture(TextureLoader.loadImage("texturaGlass.jpg"));
                idTextura2 = TextureLoader.loadTexture(TextureLoader.loadImage("models/car.png"));
		
		modelCube = new VboCube();
		modelCube.load();
		
		//objSpace.loadObj("x-35_obj.obj");
                md2.loadObj("models/car.md2", 0.5f);
		//objSpace.load();
                md2.load();
		
		esferaTeste = new Esfera3D(md2);
		esferaTeste.velocidade = 0.25f;
		esferaTeste.velVec.set(0,0,0);
		esferaTeste.cor = new Vector3f(1,0,1);
		
		listaDeObjetos.add(esferaTeste); 
		
		
		
		
		
		imageUV = new BufferedImage(4096, 4096, BufferedImage.TYPE_INT_BGR);
		
		// Set the clear color
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(45, 800f/600f,0.0f,1000);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		
		   glEnable(GL_LIGHTING);
		   glShadeModel(GL_SMOOTH);
		   


		   float[] lightAmbient = {0.0f, 0.0f, 0.0f, 1.0f}; 
	       float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};    
	       float[] lightPosition = {5f, 5f, 1f, 1.0f};
	       
	       glLightfv(GL_LIGHT0, GL_AMBIENT, lightAmbient);              
	       glLightfv(GL_LIGHT0, GL_DIFFUSE, lightDiffuse);            
	       glLightfv(GL_LIGHT0, GL_POSITION,lightPosition);
	       
		   float[] lightAmbient2 = {0.0f, 0.0f, 0.0f, 1.0f}; 
	       float[] lightDiffuse2 = {1.0f, 1.0f, 1.0f, 1.0f};    
	       float[] lightPosition2 = {-5f, -5f, 0f, 1.0f};
	       
	       glLightfv(GL_LIGHT1, GL_AMBIENT, lightAmbient2);              
	       glLightfv(GL_LIGHT1, GL_DIFFUSE, lightDiffuse2);            
	       glLightfv(GL_LIGHT1, GL_POSITION,lightPosition2);
	       
	       glEnable(GL_LIGHT0);		
	       //glEnable(GL_LIGHT1);	
		   glEnable(GL_COLOR_MATERIAL);
	       glColorMaterial(GL_FRONT, GL_DIFFUSE);
		

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		
		int frame = 0;
		long lasttime  = System.currentTimeMillis();
		lastframeTime = System.currentTimeMillis();
		
		int angle = 0;
		
		VboCube cube = new VboCube();
		cube.load();
		
		FloatBuffer matrixBuffer = MemoryUtil.memAllocFloat(16);
		while ( !glfwWindowShouldClose(window) ) {		
			//UPDATE
			
			//esferaTeste.simulase(elapseTime/1000.0f);
			
			float elapsefloat = elapseTime/1000.0f;
			for(int i = 0; i < listaDeObjetos.size();i++) {
				Objeto3D obj = listaDeObjetos.get(i);
				
				obj.simulase(elapsefloat);
			}
			
			//DESENHO
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			glEnable(GL_DEPTH_TEST);
			/*
			angle++;
			
			glEnable(GL_DEPTH_TEST);
			glLoadIdentity();
			//glRotatef(angle, 0.0f, 0.0f, 1.0f);
			
			
		   //desenhaGato(angle);
		   
			glEnable(GL_TEXTURE_2D);
			glBindTexture(GL_TEXTURE_2D, idTextura2);
			glColor3f(1.0f, 1.0f, 1.0f);
			
			glLoadIdentity();
		    glTranslatef(0,0,0);
			//glRotatef(angle, 0.5f, 0.5f, 0.0f);
		    //glScalef(0.15f, 0.15f, 0.15f);
			glScalef(0.015f, 0.015f, 0.015f);
			
		    //objSpace.desenhaseFaces(objSpace.f.size()/2);
		    //objSpace.desenhaseFaces(objSpace.f.size());
		    objSpace.desenhaseGrupo("body");
		    glBindTexture(GL_TEXTURE_2D, idTextura3);
		    objSpace.desenhaseGrupo("glass");
			
		    if(firstrun) {
		    	firstrun = false;
		    	objSpace.desenhaUvImage(imageUV);
		    	try {
					ImageIO.write(imageUV, "JPG", new File("UVimage.jpg"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		    
		    */
		    
		    
			//desenhaTanke(angle);
			

			
			//glPushMatrix();
			
			//glRotated(camAngleX,1, 0, 0);
			//glRotated(camAngleY,0, 1, 0);
		    
		    
			shader.start();
			
	        glActiveTexture(GL_TEXTURE0);
	        glBindTexture(GL_TEXTURE_2D, idTextura2);
	        int loctexture = glGetUniformLocation(shader.programID, "tex");
	        glUniform1i(loctexture, 0);
	          
			//Matrix4f projection = new Matrix4f();
			//projection.;
			
			//int projectionlocation = glGetUniformLocation(shader.programID, "view");
			//projection.storeTranspose(matrixBuffer);
			//matrixBuffer.flip();
			//glUniformMatrix4fv(projectionlocation, false, matrixBuffer);
			
			
			Matrix4f view = new Matrix4f();
			//view.setIdentity();
			view.rotate((float)Math.toRadians(camAngleX), new Vector3f(1,0,0));
			view.rotate((float)Math.toRadians(camAngleY), new Vector3f(0,1,0));
			
			int viewlocation = glGetUniformLocation(shader.programID, "view");
			
			
			view.storeTranspose(matrixBuffer);
			matrixBuffer.flip();
			glUniformMatrix4fv(viewlocation, false, matrixBuffer);
			
			//cube.draw();

			glEnable(GL_TEXTURE_2D);
			glBindTexture(GL_TEXTURE_2D, idTextura2);
			for(int i = 0; i < listaDeObjetos.size();i++) {
				Objeto3D obj = listaDeObjetos.get(i);
				
				obj.desenhase(shader);
			}


			
			glfwSwapBuffers(window); // swap the color buffers
			glfwPollEvents();
			frame++;
			
			elapseTime = (int)(System.currentTimeMillis() -lastframeTime);
			lastframeTime = System.currentTimeMillis();
			if((lasttime/1000)!=(lastframeTime/1000)) {
				System.out.println("FPS "+frame);
				frame=0;
				lasttime = lastframeTime;
			}
			
		}
	}

	private void desenhaTanke(int angle) {
		glBindTexture(GL_TEXTURE_2D, 0);
		
		glColor3f(0.0f, 1.0f, 0.0f);
		
		glLoadIdentity();
		glTranslatef(0,0.5f,0);
		glRotatef(angle, 0.0f, 1.0f, 0.0f);
		glScalef(0.002f, 0.002f, 0.002f);
		
		obj.draw();
	}

	private void desenhaGato(int angle) {
		float senoang = (float)Math.sin(Math.toRadians(angle));
		   float cosang = (float)Math.cos(Math.toRadians(angle));
			float triangleSize = 0.4f;
			
			glEnable(GL_TEXTURE_2D);
			glBindTexture(GL_TEXTURE_2D, idTextura1);
			glBegin(GL_TRIANGLES);
			
			  glColor3f(1.0f, 1.0f, 1.0f);
			  glNormal3f(cosang, senoang, 0);
			
			  glTexCoord2f(1.0f, 1.0f);
			  //glColor3f(1.0f, 0.0f, 0.0f);
			  glVertex3f(0.8f*cosang-triangleSize,0.8f*senoang-triangleSize,0.1f);
			  
			  glTexCoord2f(0.0f, 1.0f);
			  //glColor3f(0.0f, 1.0f, 0.0f);
		      glVertex3f(0.8f*cosang+triangleSize,0.8f*senoang-triangleSize,0.1f);
		      
		      glTexCoord2f(1.0f, 0.0f);
		      //glColor3f(0.0f, 0.0f, 1.0f);
		      glVertex3f(0.8f*cosang-triangleSize,0.8f*senoang+triangleSize,0.1f);
		      
		      
		      
		      glTexCoord2f(0.0f, 1.0f);
			  //glColor3f(1.0f, 0.0f, 0.0f);
			  glVertex3f(0.8f*cosang+triangleSize,0.8f*senoang-triangleSize,0.1f);
			  
			  glTexCoord2f(0.0f, 0.0f);
			  //glColor3f(0.0f, 1.0f, 0.0f);
		      glVertex3f(0.8f*cosang+triangleSize,0.8f*senoang+triangleSize,0.1f);
		      
		      glTexCoord2f(1.0f, 0.0f);
		      //glColor3f(0.0f, 0.0f, 1.0f);
		      glVertex3f(0.8f*cosang-triangleSize,0.8f*senoang+triangleSize,0.1f);
		   glEnd();
		   
		   glBindTexture(GL_TEXTURE_2D, 0);
	}

	public static void main(String[] args) {
		new Main3D().run();
	}
	
	public static void gluPerspective(float fovy, float aspect, float near, float far) {
	    float bottom = -near * (float) Math.tan(fovy / 2);
	    float top = -bottom;
	    float left = aspect * bottom;
	    float right = -left;
	    glFrustum(left, right, bottom, top, near, far);
	}
}

