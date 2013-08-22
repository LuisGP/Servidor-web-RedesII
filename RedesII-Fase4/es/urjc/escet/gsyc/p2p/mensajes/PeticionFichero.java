package es.urjc.escet.gsyc.p2p.mensajes;

public class PeticionFichero extends PeticionP2P {

	private String nombreFichero;
	private int puertoReceptor;
	
	public PeticionFichero(String emisor, String nombreFichero, int puertoReceptor){
		super(emisor);
		this.nombreFichero = nombreFichero;
		this.puertoReceptor = puertoReceptor;
	}
	public String getNombreFichero(){
		return nombreFichero;
	}
	public int getPuertoReceptor(){
		return puertoReceptor;
	}
}