/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;
import obj.HeaderMd2;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

/**
 *
 * @author JF
 */
public class Frame {
    public Vector3f scale;
    public Vector3f translate;
    public ArrayList<Vector4f>  vertices;
    public String name;

    
    public Frame(HeaderMd2 h, int index) {
        DataStream ds = h.getDataStream();
        
        ds.setPosition(h.getOfs_frames() + h.getFramesize() * index);
        
        scale = new Vector3f();
        scale.x = ds.getNextFloat();
        scale.y = ds.getNextFloat();
        scale.z = ds.getNextFloat();

        translate =  new Vector3f();
        translate.x = ds.getNextFloat();
        translate.y = ds.getNextFloat();
        translate.z = ds.getNextFloat();
        
        name = ds.getString(16);

        // fetch all vertices
        vertices = new ArrayList<>();
        
        for (int i=0; i<h.getNum_xyz(); i++) {
            Vector4f v = new Vector4f();
            v.x = ds.getNextByte();
            v.y = ds.getNextByte();
            v.z = ds.getNextByte();
            v.w = ds.getNextByte();
            vertices.add(v);
        }
    }
    public Vector3f getScale() {
        return scale;
    }

    public Vector3f getTranslate() {
        return translate;
    }

    public String getName() {
        return name;
    }
    
    public String getBaseName() {
        String baseName = "";
        for (int c=0; c<name.length(); c++) {
            if (Character.isLetter(name.charAt(c))) {
                baseName += name.charAt(c);
            }
            else {
                break;
            }
        }
        return baseName;
    }

    public ArrayList<Vector4f> getVertices() {
        return vertices;
    }
    
    @Override
    public String toString() {
        return "Frame{" + "scale=" + scale + ", translate=" + translate + ", name=" + name + ", vertices=" + vertices.toString() + '}';
    }
}
