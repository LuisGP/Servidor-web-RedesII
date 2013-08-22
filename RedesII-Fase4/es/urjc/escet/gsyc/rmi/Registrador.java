package es.urjc.escet.gsyc.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import es.urjc.escet.gsyc.p2p.tipos.ListaUsuarios;
import es.urjc.escet.gsyc.p2p.tipos.Usuario;

public interface Registrador extends Remote {
	
	public String registrar(Usuario usuario, String clave) throws RemoteException;
	public String darDeBaja(String nick, String clave) throws RemoteException;
	public ListaUsuarios getTodos() throws RemoteException;
	public Usuario getUsuario(String nick) throws RemoteException;
	
	public ListaUsuarios buscarArchivos(String cadena) throws RemoteException;
	
	public void asociarse(String uri) throws RemoteException;
	/*
	public String enviarMensaje(Mensaje msj, String nick) throws RemoteException;
	public ArrayList getMensajes(String nick) throws RemoteException;
	*/
	
}