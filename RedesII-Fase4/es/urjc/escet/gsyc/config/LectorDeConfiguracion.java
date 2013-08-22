package es.urjc.escet.gsyc.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.urjc.escet.gsyc.peer.Peer;
import es.urjc.escet.gsyc.rmi.Cliente;

/**
 * Esta clase proporciona métodos estáticos para la lectura de los diferentes ficheros de configuración.
 * @author LuisGP
 * @version En pruebas con los lectores de documentos XML, cuya funcionalidad aun no está implementada.
 */
public class LectorDeConfiguracion {
	
	private static String DIRECTORIO_DE_USUARIOS = "DIRECTORIO_DE_USUARIOS";
	private static String PUERTO_HTTP_KEY = "PUERTO_HTTP";
	private static String HOST_RMI_REGISTRY = "HOST_RMI_REGISTRY";
	private static String PUERTO_RMI_REGISTRY = "PUERTO_RMI_REGISTRY";
	private static String PATH_RMI_REGISTRADOR = "PATH_RMI_REGISTRADOR";
	
	private static String PUERTO_P2P = "PUERTO_P2P";
	private static String DIRECTORIO_EXPORTADO = "DIRECTORIO_EXPORTADO";
	private static String NICK = "NICK";
	private static String NOMBRE_COMPLETO = "NOMBRE_COMPLETO";
	private static String CORREO_ELECTRONICO = "CORREO_ELECTRONICO";
	private static String CLAVE = "CLAVE";
	
	
	
	private static String leeParametro(BufferedReader config, String parametro) throws ConfigException {
		try{
			String line = config.readLine();				  
			String regEx = "^" + parametro + "\\s+\"([^\"]*)\"$";
			
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(line);
			if(!m.matches()){
				ConfigException ce = new ConfigException();
				ce.setMensajeDeError("El parámetro " + parametro + " no está definido");
				throw ce;
			}
			
			return m.group(1);
			
		} catch (IOException e) {
			ConfigException ce = new ConfigException();
			ce.setMensajeDeError("No se puede leer el parámetro " + parametro + " del fichero de configuracion");
			throw ce;
		}	
	}
	
	public synchronized static void LeeConfiguracionGeneral(String filename) throws ConfigException {
		LectorDeConfiguracionXML.LeeConfiguracionGeneral(filename);
		Cliente.location = "//"+ConfiguracionGeneral.getHostRmiRegistry()+":"+
			ConfiguracionGeneral.getPuertoRmiRegistry()+
			"/"+ConfiguracionGeneral.getPathRmiDelRegistrador();
	}
	
	/**
	 * Esta clase lee el fichero de configuración del servidor y lo lanza según esta
	 * información
	 * @param filename Fichero de configuración del servidor
	 * @throws ConfigException Excepción si el fichero no es correcto
	 */
	public static void LeeConfiguracionGeneralOLD(String filename) throws ConfigException {

		FileInputStream fis = null;
		try{
			fis = new FileInputStream(filename);
		} catch(IOException e){
			ConfigException ce = new ConfigException();
			ce.setMensajeDeError("No se puede abrir el fichero de configuración " + filename);
			ce.initCause(e);
			throw ce;
		}
		
		BufferedReader lector = new BufferedReader(new InputStreamReader(fis));

		/* Ejemplo de formato del fichero, hay que respetar el orden indicado
		PUERTO_HTTP 			"3456"
		DIRECTORIO_DE_USUARIOS "/home/llopez/tmp/redes-II"
		HOST_RMI_NAMING 		"localhost"
		PUERTO_RMI_NAMING	"4567"
		PATH_RMI_REGISTRADOR "Registrador"
		*/
		
		String puertoHttp = leeParametro(lector, PUERTO_HTTP_KEY);
		try{
			ConfiguracionGeneral.puertoHttp = Integer.parseInt(puertoHttp);
		} catch (NumberFormatException e) {
			ConfigException ce = new ConfigException();
			ce.setMensajeDeError("En el fichero de configuración " + 
							filename + 
							" el parámetro " + 
							PUERTO_HTTP_KEY + 
							" debe ser un entero");
			throw ce;
		}
		
		ConfiguracionGeneral.directorioDeUsuarios = leeParametro(lector, DIRECTORIO_DE_USUARIOS);
		File dir = new File(ConfiguracionGeneral.directorioDeUsuarios);
		if(!dir.isDirectory() || !dir.canRead()){
			ConfigException ce = new ConfigException();
			ce.setMensajeDeError("En el fichero de configuración " +
							filename + 
							" el valor " + ConfiguracionGeneral.directorioDeUsuarios + 
							" del parámetro " + DIRECTORIO_DE_USUARIOS +
							" no es un directorio válido");
			throw ce;
		}
		
		ConfiguracionGeneral.hostRmiRegistry = leeParametro(lector, HOST_RMI_REGISTRY);

		String puertoRmiNamingStr = leeParametro(lector, PUERTO_RMI_REGISTRY);
		try{
			ConfiguracionGeneral.puertoRmiRegistry = Integer.parseInt(puertoRmiNamingStr);
		} catch (NumberFormatException e) {
			ConfigException ce = new ConfigException();
			ce.setMensajeDeError("En el fichero de configuración " + 
							filename + 
							" el parámetro " + 
							PUERTO_RMI_REGISTRY + 
							" debe ser un entero");
			throw ce;
		}
		
		ConfiguracionGeneral.pathRmiDelRegistrador = leeParametro(lector, PATH_RMI_REGISTRADOR);
			
		try{
			lector.close();
		} catch(IOException e){
			ConfigException ce = new ConfigException();
			ce.setMensajeDeError("No se puede cerrar el fichero de configuración " + filename);
			throw ce;
		}
		
		Cliente.location = "//"+ConfiguracionGeneral.getHostRmiRegistry()+":"+
			ConfiguracionGeneral.getPuertoRmiRegistry()+
			"/"+ConfiguracionGeneral.getPathRmiDelRegistrador();
	}
	
	public synchronized static Peer LeeConfiguracionUsuario(String filename) throws ConfigException {
		return LectorDeConfiguracionXML.LeeConfiguracionPeer(filename);
	}
	
	/**
	 * Lector de configuración para ficheros de texto plano
	 * @param filename Fichero
	 * @return Peer asociado al usuario
	 * @throws ConfigException Excepción lanzada ante un error en la configuración
	 */
	public synchronized static Peer LeeConfiguracionUsuarioOLD(String filename) throws ConfigException {
		
		FileInputStream fis = null;
		try{
			fis = new FileInputStream(filename);
		} catch(IOException e){
			ConfigException ce = new ConfigException();
			ce.setMensajeDeError("No se puede abrir el fichero de configuración " + filename);
			ce.initCause(e);
			throw ce;
		}
		
		BufferedReader lector = new BufferedReader(new InputStreamReader(fis));

		/* Ejemplo de formato del fichero, hay que respetar el orden indicado
		PUERTO_P2P                      "3456"
		DIRECTORIO_EXPORTADO            "E:\COMPARTIDO-REDESII\Luis"
		NICK                            "Luis"
		NOMBRE_COMPLETO                 "Luis Gasco"
		CORREO_ELECTRONICO              "neo@iies.es"
		CLAVE                           "hola"
		*/
		
		Peer peer;
		int puertoP2P;
		
		try{
			puertoP2P = Integer.parseInt(leeParametro(lector, PUERTO_P2P));
			//ConfiguracionPeer.puertoP2P = Integer.parseInt(puertoP2P);
		} catch (NumberFormatException e) {
			ConfigException ce = new ConfigException();
			ce.setMensajeDeError("En el fichero de configuración " + 
							filename + 
							" el parámetro " + 
							PUERTO_P2P + 
							" debe ser un entero");
			throw ce;
		}
		
		String directorioExportados = leeParametro(lector, DIRECTORIO_EXPORTADO);
		//ConfiguracionPeer.directorioExportados = leeParametro(lector, DIRECTORIO_EXPORTADO);
		File dir = new File(directorioExportados);
		if(!dir.isDirectory() || !dir.canRead()){
			ConfigException ce = new ConfigException();
			ce.setMensajeDeError("En el fichero de configuración " +
							filename + 
							" el valor " + directorioExportados + 
							" del parámetro " + DIRECTORIO_EXPORTADO +
							" no es un directorio válido");
			throw ce;
		}
		
		String nick = leeParametro(lector, NICK);
		//ConfiguracionPeer.nick = leeParametro(lector, NICK);

		String nombre = leeParametro(lector, NOMBRE_COMPLETO);
		//ConfiguracionPeer.nombre = leeParametro(lector, NOMBRE_COMPLETO);
		
		String email = leeParametro(lector, CORREO_ELECTRONICO);
		//ConfiguracionPeer.email = leeParametro(lector, CORREO_ELECTRONICO);
		
		String clave = leeParametro(lector, CLAVE);
		//ConfiguracionPeer.clave = leeParametro(lector, CLAVE);
		
		peer = new Peer(nick,directorioExportados,puertoP2P);
		peer.setNombre(nombre);
		peer.setEmail(email);
		peer.setClave(clave);
		
		return peer;
	}
}
