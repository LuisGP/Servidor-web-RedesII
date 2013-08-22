package es.urjc.escet.gsyc.rmi;

import java.rmi.Naming;
import java.rmi.RemoteException;

import es.urjc.escet.gsyc.p2p.tipos.ListaFicheros;
import es.urjc.escet.gsyc.p2p.tipos.ListaUsuarios;
import es.urjc.escet.gsyc.p2p.tipos.Usuario;
import es.urjc.escet.gsyc.peer.Peer;

public class Cliente {
	public static String location;
	private static Registrador ro = null; 
	
	
	public synchronized static void regObsoleto(){
		ro = null;
	}
	
	public static String registrar(Peer peer) throws Exception{
		
		if(ro == null){
			try{
				ro = (Registrador)Naming.lookup(location);
			} catch(Exception e){
				System.out.println("No ha sido posible recuperar el objeto dado por " + location);
				e.printStackTrace();
				//System.exit(-1);
				throw new RemoteException();
			}
		}
		//ArrayList<String> lista = new ArrayList<String>();
		ListaFicheros fich;
		
		fich = peer.getExportados();
		
		/*if(fich != null)
			for(int i = 0; i < fich.getNumFicheros(); i++){
				lista.add(fich.getFichero(i).getNombreFichero());
			}*/
		
		Usuario user = new Usuario(peer.getNick(),peer.getEmail(),peer.getNombre(),
				peer.getHost(),peer.getPuertoP2P(),fich);
		
		return ro.registrar(user,peer.getClave());
	}
	
	public static String darDeBaja(Peer peer) throws RemoteException{
		
		if(ro == null){
			try{
				ro = (Registrador)Naming.lookup(location);
			} catch(Exception e){
				System.out.println("No ha sido posible recuperar el objeto dado por " + location);
				e.printStackTrace();
				//System.exit(-1);
				throw new RemoteException();
			}
		}
		return ro.darDeBaja(peer.getNick(),peer.getClave());
	}
	
	public static Usuario getUsuario(String nick) throws RemoteException{
		
		if(ro == null){
			try{
				ro = (Registrador)Naming.lookup(location);
			} catch(Exception e){
				System.out.println("No ha sido posible recuperar el objeto dado por " + location);
				e.printStackTrace();
				//System.exit(-1);
				throw new RemoteException();
			}
		}
		return ro.getUsuario(nick);
	}
	
	public static ListaUsuarios getListaUsuarios() throws RemoteException{
		
		if(ro == null){
			try{
				ro = (Registrador)Naming.lookup(location);
			} catch(Exception e){
				System.out.println("No ha sido posible recuperar el objeto dado por " + location);
				e.printStackTrace();
				//System.exit(-1);
				throw new RemoteException();
			}
		}
		return ro.getTodos();
	}
	
	public static ListaUsuarios buscarFicheros(String cadena) throws RemoteException{
		if(ro == null){
			try{
				ro = (Registrador)Naming.lookup(location);
			} catch(Exception e){
				System.out.println("No ha sido posible recuperar el objeto dado por " + location);
				e.printStackTrace();
				//System.exit(-1);
				throw new RemoteException();
			}
		}
		return ro.buscarArchivos(cadena);
	}
	/*
	public static String enviarMensaje(Mensaje msg, String nick) throws RemoteException{
		if(ro == null){
			try{
				ro = (Registrador)Naming.lookup(location);
			} catch(Exception e){
				System.out.println("No ha sido posible recuperar el objeto dado por " + location);
				e.printStackTrace();
				//System.exit(-1);
			}
		}
		return ro.enviarMensaje(msg,nick);
	}
	
	public static ArrayList<Mensaje> getMensajes(String nick) throws RemoteException{
		if(ro == null){
			try{
				ro = (Registrador)Naming.lookup(location);
			} catch(Exception e){
				System.out.println("No ha sido posible recuperar el objeto dado por " + location);
				e.printStackTrace();
				//System.exit(-1);
			}
		}
		return ro.getMensajes(nick);
	}*/
	
	public static void asociarse(ClienteRetro cl) throws RemoteException{
		if(ro == null){
			try{
				ro = (Registrador)Naming.lookup(location);
			} catch(Exception e){
				System.out.println("No ha sido posible recuperar el objeto dado por " + location);
				e.printStackTrace();
				//System.exit(-1);
				throw new RemoteException();
			}
		}
		ro.asociarse(cl.getURI());
	}
}
