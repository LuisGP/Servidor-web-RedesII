package es.urjc.escet.gsyc.p2p.tipos;

import java.io.Serializable;

public class Mensaje implements Serializable {
	
	private String emisor;
	private String receptor;
	private String asunto;
	private String mensaje;
	private long instante;
	
	public Mensaje(String emisor, String receptor, String asunto, String mensaje){
		this.emisor = emisor;
		this.receptor = receptor;
		this.asunto = asunto;
		this.mensaje = mensaje;
		this.instante = System.currentTimeMillis();
	}
	
	public String getEmisor(){
		return emisor;
	}
	public String getReceptor(){
		return receptor;
	}
	public String getAsunto(){
		return asunto;
	}
	public String getMensaje(){
		return mensaje;
	}
	public long getInstante(){
		return instante;
	}
}