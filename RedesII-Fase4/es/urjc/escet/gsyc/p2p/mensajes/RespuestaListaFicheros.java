package es.urjc.escet.gsyc.p2p.mensajes;

import es.urjc.escet.gsyc.p2p.tipos.ListaFicheros;

public final class RespuestaListaFicheros extends RespuestaP2P {
	private ListaFicheros lista;
	
	public RespuestaListaFicheros(int codigoError, String descripcion, ListaFicheros lista){
		super(codigoError, descripcion);
		this.lista = lista;
	}
	
	public ListaFicheros getListaFicheros(){
		return lista;
	}
}