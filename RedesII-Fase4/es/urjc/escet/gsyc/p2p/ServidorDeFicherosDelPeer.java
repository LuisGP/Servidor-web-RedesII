package es.urjc.escet.gsyc.p2p;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import es.urjc.escet.gsyc.peer.InfoDescargas;
import es.urjc.escet.gsyc.peer.Peer;

public class ServidorDeFicherosDelPeer extends Thread {

	private ServerSocket serverSocket;
	private String fileNameFullPath;
	private int size;
	private int completed = 0;
	private String fileName;
	private String directorio;
	private InfoDescargas info;
	
	public void setSize(int s){
		size = s;
	}
	
	public ServidorDeFicherosDelPeer(Peer peer, ServerSocket serverSocket, String fileName){
		this.serverSocket = serverSocket;
		this.directorio = peer.getDirectorio();
		this.fileName = fileName;
		this.info = peer.getInfo();
		this.fileNameFullPath = directorio + File.separator + fileName;
	}
	
	public void run(){		
		
		//Aceptamos una sola conexión y, a través de ella, el fichero
		try{
			Socket conn = serverSocket.accept();

			File file = new File(this.fileNameFullPath);
			if(file.exists()){
				this.fileNameFullPath = directorio + File.separator + "Nuevo_" + fileName;
			}
			
			FileOutputStream os = new FileOutputStream(this.fileNameFullPath);
			
			DataInputStream is = new DataInputStream(conn.getInputStream());
			
			byte[]  buffer = new byte[1048];
			int num;
			while( (num = is.read(buffer)) > 0 ){
				os.write(buffer, 0, num);
				completed += num;
				//System.err.println(completed/size);
				this.info.update(fileName,((float)completed)/(float)size);
			}
			
			os.close();
			is.close();
			serverSocket.close();
			
		} catch (IOException e){
			//Esta excepción indica que algo ha ido mal en la establecimiento de la conexión
			//Quizás otras conexiones puedan funcionar, por lo que dejamos que la aplicación continúe
			//De todos modos, informamos de que algo no ha ido bien al usuario.
			System.out.println("AVISO: Se ha producido un error en la transmisión del fichero");
			System.out.println("Los detalles del problema son los siguientes:");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}			
	}
}
