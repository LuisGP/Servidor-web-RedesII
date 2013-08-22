package es.urjc.escet.gsyc.config;

/**
 * Antigua clase que contenia la información del usuario.
 * No permite varios usuarios a la vez de forma concurrente.
 * @author LuisGP
 * @deprecated Usar es.urjc.escet.gsyc.peer.Peer
 */
public class ConfiguracionPeer {
	static int puertoP2P;
	static String directorioExportados;
	static String nick;
	static String nombre;
	static String clave;
	static String email;
	
	static long credencial;
	private static boolean setCredencial = false;
	
	public static int getPuertoP2P(){
		return puertoP2P; 
	}
	public static String getDirectorioExportados(){
		return directorioExportados;
	}
	public static String getNick(){
		return nick;
	}
	public static String getNombre(){
		return nombre;
	}
	public static String getClave(){
		return clave;
	}
	public static String getEMail(){
		return email;
	}
	public static long getCredencial(){
		return credencial;
	}
	public static boolean isLoged(){
		return setCredencial;
	}
	public static void setCredencial(long cred){
		setCredencial = true;
		credencial = cred;
	}
}
