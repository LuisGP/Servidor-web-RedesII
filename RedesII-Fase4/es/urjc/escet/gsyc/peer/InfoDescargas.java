package es.urjc.escet.gsyc.peer;

import java.util.HashMap;

public class InfoDescargas {
	private HashMap<String, Float> info;
	
	public InfoDescargas(){
		info = new HashMap<String,Float>();
	}
	
	public void nueva(String file){
		info.put(file,new Float(0));
	}
	
	public void update(String file, float comp){
		info.put(file,new Float(comp));
	}
	
	public float get(String file){
		return info.get(file).floatValue();
	}
}
