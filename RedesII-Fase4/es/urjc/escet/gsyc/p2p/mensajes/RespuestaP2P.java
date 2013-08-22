package es.urjc.escet.gsyc.p2p.mensajes;


public class RespuestaP2P extends MensajeP2P {
	
	public static final int OK = 0;
	public static final int MENSAJE_DESCONOCIDO = -1;
	public static final int ERROR_DE_PARAMETRO = -2;
	public static final int ERROR_DE_COMUNICACION = -3;
	
	protected int codigoError;
	protected String descripcion;
	
	public RespuestaP2P(int codigoError, String descripcion){
		this.codigoError = codigoError;
		this.descripcion = descripcion;
	}
	
	public int getCodigoError(){
		return this.codigoError;
	}
	
	public String getDescription(){
		return this.descripcion;
	}
}