package es.urjc.escet.gsyc.config;

/**
 * Esta clase alberga la información de la Configuración del Servidor
 * @author LuisGP
 *
 */
public class ConfiguracionGeneral {
	static int puertoHttp;
	static String directorioDeUsuarios;
	static String hostRmiRegistry;
	static int puertoRmiRegistry;
	static String pathRmiDelRegistrador;
	
	public static int getPuertoHttp(){
		return puertoHttp; 
	}
	public static String getDirectorioDeUsuarios(){
		return directorioDeUsuarios;
	}
	public static String getHostRmiRegistry(){
		return hostRmiRegistry;
	}
	public static int getPuertoRmiRegistry(){
		return puertoRmiRegistry;
	}
	public static String getPathRmiDelRegistrador(){
		return pathRmiDelRegistrador;
	}
}
