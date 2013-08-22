package es.urjc.escet.gsyc.rmi;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import es.urjc.escet.gsyc.p2p.tipos.ListaUsuarios;
import es.urjc.escet.gsyc.p2p.tipos.Usuario;

public class RegistradorImpl extends UnicastRemoteObject implements Registrador {
	
	private HashMap<String, Usuario> usuarios;
	private HashMap<String, String> claves;
	private ArrayList<String> uris = new ArrayList<String>();
	
	public RegistradorImpl() throws RemoteException {
		usuarios = new HashMap<String, Usuario>();
		claves = new HashMap<String, String>();
	}
	
	/**
	 * Los métodos devuelven null si no hay problema
	 * Devuelven un String con una descripción de error si hay algún problema
	 * */
	public synchronized String registrar(Usuario usuario, String clave) throws RemoteException {
		if(usuarios.containsKey(usuario.getNick())){
			String claveGuardada = claves.get(usuario.getNick());
			if(claveGuardada != null && claveGuardada.contentEquals(clave))
				return null;
			else
				return "Ya existe un usuario registrado con el nick: " + usuario.getNick();
		}
			
		usuarios.put(usuario.getNick(),usuario);
		claves.put(usuario.getNick(),clave);
	    informar();
		
		return null;
	}
	
	public synchronized String darDeBaja(String nick, String clave) throws RemoteException {
		if(usuarios.get(nick) == null)
			return "No existe un usuario registrado con el nick: "  + nick;
		
		if(claves.get(nick).contentEquals(clave)){		   
		    usuarios.remove(nick);
		    claves.remove(nick);
		    informar();
		    return null;

		} else {
			return "La clave especificada no es válida";
		}
	}
	
	public synchronized ListaUsuarios getTodos() throws RemoteException {
			ArrayList<Usuario> lista = new ArrayList<Usuario>();
			Collection<Usuario> coleccion = usuarios.values();
			
			lista.addAll(coleccion);
			
	        return new ListaUsuarios(lista);

	}
	
	public synchronized Usuario getUsuario(String nick) throws RemoteException {
		return usuarios.get(nick);
	}
	
	public synchronized void asociarse(String uri) throws RemoteException{
		if(!uris.contains(uri))
			uris.add(uri);
		System.out.println("Hay "+uris.size()+" clientes asociados");
	}
	
	private void informar() throws RemoteException{
		ClienteRetro tmp = null;
		String uri;
		
		for(int i = 0; i < uris.size(); i++){
			uri = uris.get(i);
			
			try{
				tmp = (ClienteRetro)Naming.lookup(uri);
				tmp.obsoleto();
			} catch(Exception e){
				uris.remove(i);
				System.out.println("No ha sido posible recuperar el objeto dado por " + uri);
				e.printStackTrace();
				System.out.println("Hay "+uris.size()+" clientes asociados");
			} 
		}
		
	}
	
	public synchronized ListaUsuarios buscarArchivos(String cadena) throws RemoteException{
		ArrayList<Usuario> lista = new ArrayList<Usuario>();
		Collection<Usuario> coleccion = usuarios.values();
		
		Iterator<Usuario> usuarios = coleccion.iterator();
		
		Usuario actual;
		
		while(usuarios.hasNext()){
			actual = usuarios.next();
			if(actual.buscarFichero(cadena)){
				lista.add(actual);
			}
		}
		
        return new ListaUsuarios(lista);
	}

	/*
	public synchronized String enviarMensaje(Mensaje msj, String nick) throws RemoteException {
		Usuario user = usuarios.get(nick);
		
		if(user == null){
			return "Usuario inválido";
		}
		
		user.addMensaje(msj);
		
		return null;
	}

	public synchronized ArrayList<Mensaje> getMensajes(String nick) throws RemoteException {
		Usuario user = usuarios.get(nick);
		
		if(user == null){
			return null;
		}
		
		return user.getMensajes();		
	}*/
}