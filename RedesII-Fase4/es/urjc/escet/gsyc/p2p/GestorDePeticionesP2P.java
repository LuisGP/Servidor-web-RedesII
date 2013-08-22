package es.urjc.escet.gsyc.p2p;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import es.urjc.escet.gsyc.p2p.mensajes.PeticionEntregaMensaje;
import es.urjc.escet.gsyc.p2p.mensajes.PeticionFichero;
import es.urjc.escet.gsyc.p2p.mensajes.PeticionListaFicheros;
import es.urjc.escet.gsyc.p2p.mensajes.RespuestaEntregaMensaje;
import es.urjc.escet.gsyc.p2p.mensajes.RespuestaFichero;
import es.urjc.escet.gsyc.p2p.mensajes.RespuestaListaFicheros;
import es.urjc.escet.gsyc.p2p.mensajes.RespuestaP2P;
import es.urjc.escet.gsyc.p2p.tipos.ListaFicheros;
import es.urjc.escet.gsyc.p2p.tipos.Usuario;
import es.urjc.escet.gsyc.peer.Peer;
import es.urjc.escet.gsyc.rmi.Cliente;

public class GestorDePeticionesP2P extends Thread {

	private Socket socket;
	private Peer peer;
	
	public GestorDePeticionesP2P(Socket socket, Peer peer){
		this.socket = socket;
		this.peer = peer;
	}
	
	private void enviaRespuesta(RespuestaP2P respuesta) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		out.writeObject(respuesta);
		out.flush();
		out.close();
	}
	
	private void procesaPeticionFichero(PeticionFichero peticion) throws IOException {
		//1- Recupera el fichero solicitado
		//2- Chequea que el fichero existe
		//3- 	Si el fichero no existe, manda un mensaje de respuesta con error
		//4-		Si el fichero existe, 
		//				1- Envía un mensaje de respuesta sin error
		//				2- abre el socket al puerto especificado y lo envía

		String file = peticion.getNombreFichero();
		
		try{
			FileInputStream is = new FileInputStream(peer.getDirectorio()+File.separator+file);
			
			Usuario ul = Cliente.getUsuario(peticion.getEmisor());
			Socket datos = new Socket(ul.getHost(), peticion.getPuertoReceptor());
			
			//		No hay error
			RespuestaFichero respuesta = new RespuestaFichero(RespuestaP2P.OK, Integer.toString(is.available()));
			enviaRespuesta(respuesta);
			
			try{
				DataOutputStream os = new DataOutputStream(datos.getOutputStream());
				byte[] buffer = new byte[1048];
				int num;	
				while((num = is.read(buffer)) > 0){
					os.write(buffer, 0, num);}
				os.close();
				is.close();
			}catch(IOException e) {
				System.out.println("Se ha producido un error de entrada/salida en la transferencia del fichero");
				e.printStackTrace();
				return;
			}
			
		}catch(FileNotFoundException fnfe){
			RespuestaFichero respuesta = 
				new RespuestaFichero(RespuestaP2P.ERROR_DE_PARAMETRO, "Fichero no existe");
			enviaRespuesta(respuesta);
		}

	}
	
	private void procesaPeticionListaFicheros (PeticionListaFicheros peticion) throws IOException {
		//1- Recupera la lista de ficheros
		//2- Crea el mensaje de respuesta
		//3- Lo envía por la conexión abierta
		ListaFicheros lista = null;

		lista = this.peer.getExportados();
		
		RespuestaListaFicheros respuesta = 
			new RespuestaListaFicheros(RespuestaP2P.OK, "OK", lista);
		enviaRespuesta(respuesta);
	}
	
	private void procesaPeticionEntregaMensaje(PeticionEntregaMensaje peticion) throws IOException {
		//1- Recupera la información del mensaje
		//2- La almacena el mensaje en una lista de mensajes del peer
		//3- Crea el mensaje de respuesta

		this.peer.addMensaje(peticion.getMensaje());

		RespuestaEntregaMensaje respuesta = new RespuestaEntregaMensaje(RespuestaP2P.OK, "OK");
		enviaRespuesta(respuesta);
	}
	
	public void run(){
		try{
			ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(this.socket.getInputStream()));
			
			Object msg = null;
			try{
				msg = in.readObject();
				if(msg.getClass() == PeticionFichero.class){
					procesaPeticionFichero((PeticionFichero)msg);
					
				} else if (msg.getClass() == PeticionListaFicheros.class){
					procesaPeticionListaFicheros((PeticionListaFicheros)msg);
					
				} else if (msg.getClass() == PeticionEntregaMensaje.class){
					procesaPeticionEntregaMensaje((PeticionEntregaMensaje)msg);
					
				} else {
					//Error de protocolo
					RespuestaP2P response = new RespuestaP2P(-1,"Mensaje de tipo desconocido");
					ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
					out.writeObject(response);
					out.flush();
					out.close();
				}							
			} catch (ClassNotFoundException e) {
				//Se ha producido un error recibiendo el mensaje de petición
				System.out.print("Error recibiendo un mensaje de petición");
				System.out.println("Los detalles del problema son los siguientes:");
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
			
			in.close();		
			
		} catch (IOException e) {
			System.out.println("AVISO: ha habido un problema leyendo o escribiendo en el socket especificado");
			System.out.println("Los detalles del problema son los siguientes");
			System.out.println(e.getMessage());
			e.printStackTrace();
			return;
		}
	}
	
}