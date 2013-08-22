package es.urjc.escet.gsyc.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClienteRetroImpl extends UnicastRemoteObject implements ClienteRetro {

	private String uri;
	
	public ClienteRetroImpl(String uri) throws RemoteException{
		this.uri = uri;
	}
	
	public synchronized void obsoleto() throws RemoteException {
		// TODO Auto-generated method stub
		Cliente.regObsoleto();
		System.out.println("La copia está obsoleta!!");
	}

	public String getURI() throws RemoteException {
		// TODO Auto-generated method stub
		return uri;
	}

	
}
