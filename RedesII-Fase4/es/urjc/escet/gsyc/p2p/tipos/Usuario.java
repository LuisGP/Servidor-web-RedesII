package es.urjc.escet.gsyc.p2p.tipos;

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;

public class Usuario implements Serializable {

	private String nick;
	private String correoElectronico;
	private String nombreCompleto;
	private String host;
	private ListaFicheros compartidos;
	private int puertoP2P;
	
	//private Socket socket;
	
	//private ArrayList<Mensaje> mensajes = new ArrayList<Mensaje>();
	
	public Usuario(String nick, String correoElectronico, String nombreCompleto,
			String host, int puertoP2P, ListaFicheros lista) throws UnknownHostException, IOException{
		this.nick = nick;
		this.correoElectronico = correoElectronico;
		this.nombreCompleto = nombreCompleto;
		this.host = host;
		this.puertoP2P = puertoP2P;
		this.compartidos = lista;
		
		//this.socket = socket;
	}
	
	public String getNick(){
		return nick;
	}
	public String getCorreoElectronico(){
		return correoElectronico;
	}
	public String getNombreCompleto(){
		return nombreCompleto;
	}
	public String getHost(){
		return host;
	}
	public int getPuertoP2P(){
		return puertoP2P;
	}
	/*
	public ListaFicheros getCompartidos() throws IOException, ClassNotFoundException{
		
		PeticionP2P peticion = new PeticionListaFicheros(nick);
		Socket socket = new Socket(this.getHost(),this.getPuertoP2P());
		
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		
		out.writeObject(peticion);
		
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
		
		RespuestaListaFicheros respuesta = (RespuestaListaFicheros)in.readObject();
		
		this.compartidos = respuesta.getListaFicheros();
		
		return compartidos;
	}*/
	
	public boolean buscarFichero(String cadena){
		if(compartidos != null){
			for(int i = 0; i < compartidos.getNumFicheros(); i++){
				if(compartidos.getFichero(i).getNombreFichero().contains(cadena)){
					return true;
				}
			}
		}
		return false;
	}

	/*public ArrayList<Mensaje> getMensajes() {
		return mensajes;
	}

	public void addMensaje(Mensaje mensaje) {
		this.mensajes.add(mensaje);
	}
	
	public void delMensaje(Mensaje mensaje){
		this.mensajes.remove(mensaje);
	}*/
}