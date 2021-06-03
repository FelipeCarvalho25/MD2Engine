/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obj;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import util.DataStream;
import util.Frame;

/**
 *
 * @author JF
 */
public class ObjMd2 extends ObjModel{
    public ArrayList<Vector3f> v;
	public ArrayList<Vector3f> vn;
        public ArrayList<Vector3f> vst;
	public ArrayList<Vector2f> vt;
        public ArrayList<Frame>  frame;
	public ArrayList<Face3D> f;
	public ArrayList<GrupoFaces> g;
	public ConcurrentHashMap<String, GrupoFaces> gname;
        public String conteudo;
        public HeaderMd2 header;
        public String[] NomesSkins;
        public int startFrame;
        public int endFrame;
        public int currentFrame;
        public float tweening;
        private float scaleFactor;
        public Map<String, AnimationFramesInfo> animationFramesInfoMap = new HashMap<String, AnimationFramesInfo>();
    
    private class AnimationFramesInfo {
        public String name;
        public int startFrame;
        public int endFrame;
    }
    
    public ObjMd2() {
		// TODO Auto-generated constructor stubn
		v = new ArrayList<>();
		vn = new ArrayList<>();
		vt = new ArrayList<>();
                vst = new ArrayList<>();
		f = new ArrayList<>();
		g = new ArrayList<>();
		gname = new ConcurrentHashMap<>();
                frame = new ArrayList<>() ;
    }
    public void loadObj(String file, float fatorEscala) {
		// System.out.println(" "+file);
		// InputStream in = this.getClass().getResourceAsStream(file);
		InputStream in;
		try {
			in = new FileInputStream(file);
                    try {
                        this.scaleFactor = fatorEscala;
                        DataStream stream = new DataStream(in, file);
                        header = new HeaderMd2(stream);
                        decodeVertice();
                        decodeTextureMapping();
                        decodeNameSkins();
                        decodeFrames();
                        startFrame = 0;
                        endFrame = header.getNum_frames() - 1;
                        stream.dispose();
                    } catch (Exception ex) {
                        System.out.println("Erro na criação do stream. Erro:" + ex.toString());
                        ex.printStackTrace();
                    }

		} catch (FileNotFoundException e1) {
                    // TODO Auto-generated catch block
                    System.out.println("Arquivo não encontrado.");
		}
                System.out.println(header.toString());
	}
    
    public void decodeVertice() {
        //triangles = new Triangle[];
       
		 
        DataStream ds = header.getDataStream();
              
	for (int t = 0; t < header.getNum_tris(); t++) {
            ds.setPosition(header.getOfs_tris() + 12 * t);  
            //triangles[t] = new Triangle(header, t);
            Vector3f vec = new Vector3f();
            Vector3f vecst = new Vector3f();
            try {

                    vec.x = ds.getNextShort(); 		
                    vec.y = ds.getNextShort();		
                    vec.z = ds.getNextShort();                    
                    
                    vecst.x = ds.getNextShort(); 		
                    vecst.y = ds.getNextShort();		
                    vecst.z = ds.getNextShort();    

            } catch (Exception e) {
		System.err.println("Erro no vertice"+e.toString());
            }
            System.out.print("V " + vec.toString());
             v.add(vec);
             vst.add(vecst);
	}
           
    }
    public void decodeTextureMapping() {
       
        DataStream ds = header.getDataStream();
              
	for (int t = 0; t < header.getNum_st(); t++) {
            Vector2f vec = new Vector2f();
            try {

                    vec.x = ds.getNextShort() / (float) header.getSkinwidth();
		
                    vec.y = ds.getNextShort() / (float) header.getSkinheight();

            } catch (Exception e) {
		System.err.println("Erro na textura"+e.toString());
            }
            vt.add(vec);
	}
           
    }
    
    public void decodeNameSkins(){
        NomesSkins = new String[header.getNum_skins()];
        DataStream ds = header.getDataStream();
        ds.setPosition(header.getOfs_skins());
        for (int s = 0; s < NomesSkins.length; s++) {
            NomesSkins[s] = ds.getString(64);
        }
    }
    
    public void decodeFrames(){
        AnimationFramesInfo animationFramesInfo = null;
        for (int f=0; f<header.getNum_frames(); f++) {
            frame.add(new Frame(header, f));
            //System.out.println(f + " frame=" + frames[f]);
            if (animationFramesInfo == null) {
                animationFramesInfo = new AnimationFramesInfo();
                animationFramesInfo.name = frame.get(f).getBaseName();
            }
            else if (!frame.get(f).getBaseName().equals(animationFramesInfo.name) || f == frame.size() - 1) {
                animationFramesInfo.endFrame = f == frame.size() - 1 ? f : f - 1;
                animationFramesInfoMap.put(animationFramesInfo.name, animationFramesInfo);
                animationFramesInfo = new AnimationFramesInfo();
                animationFramesInfo.name = frame.get(f).getBaseName();
                animationFramesInfo.startFrame = f;
            }
        }
    }
    public ArrayList<Frame> getFrames() {
        return frame;
    }

    public Set<String> getAnimationNames() {
        return animationFramesInfoMap.keySet();
    }

    public int getStartFrame() {
        return startFrame;
    }

    public void setStartFrame(int startFrame) {
        this.startFrame = startFrame;
    }

    public int getEndFrame() {
        return endFrame;
    }

    public void setEndFrame(int endFrame) {
        this.endFrame = endFrame;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    public void nextFrame() {
        currentFrame++;
        if (currentFrame > endFrame) {
            currentFrame = startFrame;
        }
    }
    
    public void nextFrame(float t) {
        tweening += t;
        if (tweening >= 1) {
            nextFrame();
            tweening = 0;
        }
    }
     
    public void setAnimation(String name) {
        AnimationFramesInfo animationFramesInfo = animationFramesInfoMap.get(name);
        if (animationFramesInfo == null) {
            throw new RuntimeException("Invalid animation name !");
        }
        startFrame = animationFramesInfo.startFrame;
        endFrame = animationFramesInfo.endFrame;
        currentFrame = startFrame;
    }
    
    private Frame getNextFrameObj() {
        int cf = currentFrame + 1;
        if (cf > endFrame) {
            cf = startFrame;
        }
        return frame.get(cf);
    } 
    
    private float[] vtmp = new float[8];
    private float[] vtmp2 = new float[8];
    
    // p = indice do vertice do triangulo. pode ser 0, 1 ou 2
    public float[] getTriangleVertex(int triangleIndex, int p) {
        Vector3f vec = v.get(triangleIndex);
         Vector3f vecst = vst.get(triangleIndex);
        float vertexIndex = 0;
        float stIndex = 0;
        switch (p) {
            case 0:
                vertexIndex = vec.x;
                stIndex  = vecst.x;
                break;
            case 1:
                vertexIndex = vec.y;
                stIndex  = vecst.y;
                break;
            default:
                vertexIndex = vec.z;
                stIndex  = vecst.z;
                break;
        }
        Vector2f st = vt.get((int)(stIndex));
        setVertexInfo((int)vertexIndex,  vtmp, frame.get(currentFrame), st);
        if (tweening > 0) {
            setVertexInfo((int)vertexIndex, vtmp2, getNextFrameObj(), st);
            for (int l=0; l<6; l++) {
                vtmp[l] = lerp (vtmp[l], vtmp2[l], tweening);
            }
        }
        return vtmp;
    }
    
    private void setVertexInfo(int vertexIndex, float[] v, Frame frame, Vector2f st) {
        v[0] = (frame.scale.x * frame.vertices.get(vertexIndex).x + frame.translate.x) * scaleFactor;
        v[1] = (frame.scale.y * frame.vertices.get(vertexIndex).y + frame.translate.y) * scaleFactor;
        v[2] = (frame.scale.z * frame.vertices.get(vertexIndex).z + frame.translate.z) * scaleFactor;
        
        float normalIndex = frame.vertices.get(vertexIndex).z;
        double[] normal = NormalMd2.MD2_NORMAL_TABLE[(int)normalIndex];
        v[3] = (float) normal[0];
        v[4] = (float) normal[1];
        v[5] = (float) normal[2];
        
        v[6] = st.x;
        v[7] = st.y;
    }
    
    private float lerp(float start, float end, float p) {
        return start + (end - start) * p;
    }
}
