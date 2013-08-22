package es.urjc.escet.gsyc.peer;

import java.rmi.RemoteException;
import java.util.HashMap;

import es.urjc.escet.gsyc.rmi.Cliente;

/**
 * Estructura de datos para almecenar los Peers (clientes).
 * Se trata de una tabla Hash indexada por el credencial y permite
 * abstraerse de la representación en forma de tabla Hash.
 * @author LuisGP
 *
 */
public class PeerHash {
	private static HashMap<Long,Peer> peers = new HashMap<Long,Peer>();
	private static HashMap<String,Peer> nicks = new HashMap<String,Peer>();
	
	public synchronized static String add(Peer peer){
		String error = null;
		
		if(!nicks.containsValue(peer.getNick())){
			peers.put(peer.getCredencial(),peer);
			nicks.put(peer.getNick(),peer);
			
			try{
				error = Cliente.registrar(peer);
				if(error != null){
					System.err.println(error);
				}
			}catch(Exception re){
				error = "No fue posible dar de alta el Peer: "+re.getMessage();
			}
		}
		return error;
	}
	
	public synchronized static Peer getPeer(long cred){
		return peers.get(Long.valueOf(cred));
	}
	
	public synchronized static Peer getPeer(String nick){
		return nicks.get(nick);
	}
	
	public synchronized static void delPeer(long cred){
		Peer tmp = peers.get(Long.valueOf(cred));
		peers.remove(cred);
		nicks.remove(tmp.getNick());
		
		String error;
		
		try{
			error = Cliente.darDeBaja(tmp);
			if(error != null){
				System.err.println(error);
			}
		}catch(RemoteException re){
			System.err.println("No fue posible dar de alta el Peer: "+re.getMessage());
		}
	}
}
