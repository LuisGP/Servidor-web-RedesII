package es.urjc.escet.gsyc.p2p;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import es.urjc.escet.gsyc.peer.Peer;


public class ServidorP2P extends Thread{

	private Peer peer;
	
	public ServidorP2P(Peer peer){
		this.peer = peer;
	}
		
	public void run(){
		//Declaramos server sobre del bloque try{}catch{} para poder utilizarlo fuera del mismo
		ServerSocket server = null;
		try{
			server = new ServerSocket( peer.getPuertoP2P() );
			server.setSoTimeout(500);
			
			System.out.println("El servidor P2P del peer " + peer.getNick() + 
			" se ha atado al puerto " + peer.getPuertoP2P());
		} catch(Exception e){
			//Esta excepción indica que ha habido algún problema al atarse al puerto especificado
			//Consideramos que esta excepción es grave, por lo que el programa no puede continuar.
			System.out.println("ERROR: No se puede crear un ServerSocket en el puerto " + peer.getPuertoP2P() );
			System.out.println("Los detalles del error son los siguientes:");
			System.out.println(e.getMessage());
			e.printStackTrace();	
			System.exit(-1);
		}
		
		//Aceptamos conexiones y las servimos, cada una en su Thread
		while(!this.isInterrupted()){
			try{
				Socket conn = server.accept();
				System.out.println("Conexión aceptada conn=" + conn.toString());
				GestorDePeticionesP2P gestor = new GestorDePeticionesP2P(conn, peer);
				gestor.start();
			} catch (SocketTimeoutException e){
			} catch (IOException e){
				//Esta excepción indica que algo ha ido mal en el establecimiento de la conexión
				//Quizás otras conexiones puedan funcionar, por lo que dejamos que la aplicación continúe
				//De todos modos, informamos de que algo no ha ido bien al usuario.
				System.out.println("AVISO: Se ha producido un error estableciendo una conexión");
				System.out.println("Los detalles del problema son los siguientes:");
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
		
		try{
			server.close();
		} catch (IOException e){
			System.out.println("AVISO: Se ha producido un error cerrando la conexión");
			System.out.println("Los detalles del problema son los siguientes:");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		System.out.println("El servidor P2P del usuario " + peer.getNick() + 
		" que usa el puerto " + peer.getPuertoP2P() + " se ha cerrado correctamente");
	}
	
	
}