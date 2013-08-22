package es.urjc.escet.gsyc.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClienteRetro  extends Remote {
	public String getURI() throws RemoteException;
	public void obsoleto() throws RemoteException;
}
