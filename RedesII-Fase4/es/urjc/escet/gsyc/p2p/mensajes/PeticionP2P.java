package es.urjc.escet.gsyc.p2p.mensajes;


public abstract class PeticionP2P extends MensajeP2P {

	private String emisor;
	
	public PeticionP2P(String emisor){
		this.emisor = emisor;
	}
	public String getEmisor(){
		return emisor;
	}
}