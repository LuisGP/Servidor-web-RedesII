package es.urjc.escet.gsyc.p2p.mensajes;

import es.urjc.escet.gsyc.p2p.tipos.Mensaje;

public class PeticionEntregaMensaje extends PeticionP2P {

	private Mensaje mensaje;
	
	public PeticionEntregaMensaje(String emisor, Mensaje mensaje){
		super(emisor);
		this.mensaje = mensaje;
	}
	public Mensaje getMensaje(){
		return mensaje;
	}
}