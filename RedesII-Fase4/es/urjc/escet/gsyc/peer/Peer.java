package es.urjc.escet.gsyc.peer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import sun.misc.BASE64Encoder;
import es.urjc.escet.gsyc.p2p.ServidorDeFicherosDelPeer;
import es.urjc.escet.gsyc.p2p.ServidorP2P;
import es.urjc.escet.gsyc.p2p.mensajes.PeticionEntregaMensaje;
import es.urjc.escet.gsyc.p2p.mensajes.PeticionFichero;
import es.urjc.escet.gsyc.p2p.mensajes.PeticionP2P;
import es.urjc.escet.gsyc.p2p.mensajes.RespuestaP2P;
import es.urjc.escet.gsyc.p2p.tipos.DescriptorFichero;
import es.urjc.escet.gsyc.p2p.tipos.ListaFicheros;
import es.urjc.escet.gsyc.p2p.tipos.Mensaje;
import es.urjc.escet.gsyc.p2p.tipos.Usuario;

/**
 * Clase que alberga la información sobre un usuario logeado en el sistema
 * Al crear un nuevo Peer (autentificarlo), se general su Credencial
 * @author LuisGP
 * 
 */
public class Peer {
	private int puertoP2P;
	private String nombre;
	private long credencial;
	private String nick;
	private ListaFicheros exportado;
	private File dir_export;
	private String email;
	private String clave;
	private String host;
	
	private ArrayList<Mensaje> mensajes = new ArrayList<Mensaje>();
	private ServidorP2P server;
	
	// Descargas Activas
	private HashMap<String, ServidorDeFicherosDelPeer> descargas;
	private ArrayList<String> listaDescargas;
	private InfoDescargas info;
	
	
	public Peer(String nick, String direxp, int port){
		Random rnd = new Random();
		File dir = new File(direxp);
		
		credencial = rnd.nextLong();
		dir_export = dir;
		this.nick = nick;
		puertoP2P = port;
		this.listaDescargas = new ArrayList<String>();
		this.descargas = new HashMap<String,ServidorDeFicherosDelPeer>();
		this.info = new InfoDescargas();

		this.exportado = this.getExportados();
		
		server = new ServidorP2P(this);
	}
	
	public void lanzarServerP2P(){
		server.start();
	}
	
	public void liberarServerP2P(){
		server.interrupt();
	}
	
	public int getPuertoP2P(){
		return this.puertoP2P;
	}
	
	public String getNombre(){
		return this.nombre;
	}
	
	public void setNombre(String name){
		this.nombre = name;
	}
	
	public void setEmail(String mail){
		this.email = mail;
	}
	
	public String getEmail(){
		return this.email;
	}
	
	public String getDirectorio(){
		return this.dir_export.getAbsolutePath();
	}
	
	public long getCredencial(){
		return credencial;
	}
	
	public String getNick(){
		return this.nick;
	}
	
	public ListaFicheros getExportados(){
		File content[] = dir_export.listFiles();
		ArrayList<DescriptorFichero> alist = new ArrayList<DescriptorFichero>();
		
		//this.exportado.clear();
		DescriptorFichero df;
		
		for(int i = 0; i < content.length; i++){
			//exportado.add(content[i]);
			df = new DescriptorFichero(content[i].getName(),content[i].length(),content[i].lastModified());
			alist.add(df);
		}
		
		this.exportado = new ListaFicheros(alist);
		
		return this.exportado;
	}
	
	/**
	 * La clave se comprueba utilizando SHA sobre la clave que nos pasan por atributo en GET
	 * y comparándola con la clave (cifrada) del fichero de configuración
	 * @param cl Clave a comprobar
	 * @return True si la contraseña es correcta y false en caso contrario
	 */
	public boolean checkClave(String cl){
		String strSha = new String();
		
		try{
			MessageDigest md = MessageDigest.getInstance("SHA");
			
			byte[] sha = md.digest(cl.getBytes());
			
			strSha = new BASE64Encoder().encode(sha);
			System.out.println("Base64: "+strSha);

		}catch(Exception e){
			e.printStackTrace();
		}
		
		return clave.contentEquals(strSha);
	}
	
	public void setClave(String cl){
		this.clave = cl;
	}
	
	public String getClave(){
		return this.clave;
	}
	
	public String toString(){
		String peer = new String("Nick: "+nick);
		
		peer = peer +"\n Credencial: "+credencial+"\n email: "+email+"\n";
		
		return peer;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
	
	public RespuestaP2P p2pEnviaMensaje(Usuario usuarioRemoto, PeticionEntregaMensaje peticion) throws PeerException {
		if(usuarioRemoto == null || peticion == null){
			PeerException pe = new PeerException();
			pe.setMensajeDeError("Se ha especificado un usuario o un mensaje nulos");
			throw pe;
		}
				
		//Lanzamos la peticion
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		try{
			Socket socket = new Socket(usuarioRemoto.getHost(), usuarioRemoto.getPuertoP2P());			
			out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			out.writeObject(peticion);
			out.flush();
			in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
			RespuestaP2P respuesta = (RespuestaP2P) in.readObject();
			out.close();
			in.close();
			return respuesta;
			
		} catch (Exception e){
			PeerException pe = new PeerException();
			pe.setMensajeDeError(e.getMessage());
			pe.initCause(e);
			throw pe;
		} 
	}
	
	public RespuestaP2P p2pEnviaPeticionP2P(Usuario usuarioRemoto, PeticionP2P peticion) throws PeerException{
		if(usuarioRemoto == null || peticion == null){
			PeerException pe = new PeerException();
			pe.setMensajeDeError("Se ha especificado un usuario o un mensaje nulos");
			throw pe;
		}
				
		//Lanzamos la peticion
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		try{
			Socket socket = new Socket(usuarioRemoto.getHost(), usuarioRemoto.getPuertoP2P());				
			out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			out.writeObject(peticion);
			out.flush();
			in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
			RespuestaP2P respuesta = (RespuestaP2P) in.readObject();
			out.close();
			in.close();
			return respuesta;
			
		} catch (Exception e){
			PeerException pe = new PeerException();
			pe.setMensajeDeError(e.getMessage());
			pe.initCause(e);
			throw pe;
		} 
	}
	
	public ArrayList<Mensaje> getMensajes() {
		return mensajes;
	}

	public void addMensaje(Mensaje mensaje) {
		this.mensajes.add(mensaje);
	}
	
	public void delMensaje(Mensaje mensaje){
		this.mensajes.remove(mensaje);
	}
	
	public String descargarFicheroRemoto(Usuario ur, String fileName) throws IOException, PeerException{
		// Creamos el servidor local 
		ServerSocket serverSocket = new ServerSocket(0);
		int puertoServidor = serverSocket.getLocalPort();
		
		ServidorDeFicherosDelPeer servidor = new ServidorDeFicherosDelPeer(this, serverSocket, fileName);
		
		try{
			servidor.start();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		// Enviamos la peticion de envio del fichero
		PeticionFichero peticion = new PeticionFichero(this.nick, fileName, puertoServidor);
		RespuestaP2P respuesta;
		respuesta = p2pEnviaPeticionP2P(ur,peticion);
		
		if(respuesta.getCodigoError() == RespuestaP2P.OK){
			System.out.println("Recibida Respuesta Afirmativa!");
			servidor.setSize(Integer.parseInt(respuesta.getDescription()));
			this.descargas.put(fileName,servidor);
			this.listaDescargas.add(fileName);
			this.info.nueva(fileName);
		}
		
		return respuesta.getDescription();
	}
	
	public ServidorDeFicherosDelPeer getInfoDescarga(String file){
		return descargas.get(file);
	}
	
	public ArrayList<String> getListaDescargas(){
		return this.listaDescargas;
	}
	
	public InfoDescargas getInfo(){
		return info;
	}
}
