package es.urjc.escet.gsyc.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;

//import es.urjc.escet.gsyc.terminal.GestorPeticionesHTTP;

/**
 * Esta clase acepta las peticiones http y lanza el GestorDePeticiones para atenderlas
 */
public class ServidorHttpDelTerminal extends Thread {

	private int portNumber;
	private final int NUM_GESTORES = 50;
	private GestorDePeticionesHttp[] pull = new GestorDePeticionesHttp[NUM_GESTORES];
	private int actual = 0;

	public ServidorHttpDelTerminal(int portNumber){
		this.portNumber = portNumber;
	}
	
	private void ManejarPeticion(Socket conn){
		System.out.println("Manejador: "+actual);
		//pull[actual] = new GestorDePeticionesHttp(conn);
		
		//pull[actual].stop();
		pull[actual].aceptarPeticion(conn);
		
		actual = (actual+1)%NUM_GESTORES;
	}
	
	public void run() {

		// Declaramos server sobre del bloque try{}catch{} para poder utilizarlo
		// fuera del mismo
		//ServerSocket server = null;  // Este era inseguro!!
		// Modificamos para tener conexión segura ;)
		ServerSocketFactory ssocketFactory = SSLServerSocketFactory.getDefault();
        ServerSocket server = null;
		try {
			//server = new ServerSocket(this.portNumber);
			server = ssocketFactory.createServerSocket(portNumber);
			System.out
					.println("El servidor HTTP del Terminal se ha atado al puerto "
							+ this.portNumber);
		} catch (IOException e) {
			// Esta excepción indica que, seguramente, ha habido algún problema
			// al atarse al puerto especificado
			// Consideramos que esta excepción es grave, por lo que el programa
			// no puede continuar.
			System.out
					.println("ERROR: No se puede crear un ServerSocket en el puerto "
							+ this.portNumber);
			System.out.println("Los detalles del error son los siguientes:");
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
		
		// Inicializamos todos los gestores de peticiones
		for(int i = 0; i < NUM_GESTORES; i++){
			this.pull[i] = new GestorDePeticionesHttp();
			this.pull[i].start();
		}
		
		// Aceptamos conexiones y las servimos, cada una en su Thread
		while (true) {
			try {
				Socket conn = server.accept();
				/*GestorDePeticionesHttp gestor = new GestorDePeticionesHttp(conn);
				 gestor.start();*/
				ManejarPeticion(conn);
			} catch (IOException e) {
				// Esta excepción indica que algo ha ido mal en el
				// establecimiento de la conexión
				// Quizás otras conexiones puedan funcionar, por lo que dejamos
				// que la aplicación continúe
				// De todos modos, informamos de que algo no ha ido bien al
				// usuario.
				System.out.println("AVISO: Se ha producido un error estableciendo una conexión");
				System.out.println("Los detalles del problema son los siguientes:");
				System.out.println(e.getMessage());
				e.printStackTrace();
			}// try
		}// while
	}// run
}// class
