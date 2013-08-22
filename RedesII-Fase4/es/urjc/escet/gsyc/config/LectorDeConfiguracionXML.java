package es.urjc.escet.gsyc.config;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import es.urjc.escet.gsyc.peer.Peer;

public class LectorDeConfiguracionXML {

	/* Constantes para los ficheros XML */
	private static String DIRECTORIO_DE_USUARIOS = "DIRECTORIO_USUARIOS";
	private static String PUERTO_HTTP = "PUERTO_HTTP";
	private static String HOST_RMI_REGISTRY = "HOST_RMI_REGISTRY";
	private static String PUERTO_RMI_REGISTRY = "PUERTO_RMI_REGISTRY";
	private static String PATH_RMI_REGISTRADOR = "PATH_RMI_REGISTRY";
	
	private static String PUERTO_P2P = "PUERTO_P2P";
	private static String DIRECTORIO_EXPORTADO = "DIRECTORIO_EXPORTADO";
	private static String NICK = "NICK";
	private static String NOMBRE_COMPLETO = "NOMBRE_COMPLETO";
	private static String CORREO_ELECTRONICO = "CORREO_ELECTRONICO";
	private static String CLAVE = "CLAVE";

	/**
	 * Esta clase lee el fichero de configuración del servidor y lo lanza según esta
	 * información
	 * @param filename Fichero de configuración del servidor
	 * @throws ConfigException Excepción si el fichero no es correcto
	 */
	public synchronized static void LeeConfiguracionGeneral(String filename) throws ConfigException {
		Element raiz;
		File fichero = null;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			ConfigException ce = new ConfigException();
			ce.setMensajeDeError("Error al crear el DocumentBuilder");
			ce.initCause(e);
			throw ce;
		}
		
		try {
			fichero = new File(filename);
			File schema = new File("p2p.xsd");
			Schema xsd = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(schema);
			StreamSource xml = new StreamSource(fichero);
			xsd.newValidator().validate(xml);
		} catch (Exception e) {
			ConfigException ce = new ConfigException();
			e.printStackTrace();
			ce.setMensajeDeError("Archivo XML "+filename+" no es válido");
			ce.initCause(e);
			throw ce;
		}
		
		try {
			raiz = db.parse(fichero).getDocumentElement();
		} catch (Exception e) {
			ConfigException ce = new ConfigException();
			ce.setMensajeDeError("Error al analizar el fichero " + filename);
			ce.initCause(e);
			throw ce;
		}
		
		if (!raiz.getNodeName().equalsIgnoreCase("CONFIGURACION_P2P")) {
			ConfigException ce = new ConfigException();
			ce.setMensajeDeError("Error en " + filename + ": No sigue el formato");
			throw ce;
		}
		
		/* Hasta aqui todo bien, ahora recuperamos datos */
		
		NodeList nodos = raiz.getChildNodes();
		
		for (int i = 0; i < nodos.getLength(); i++) {
			Node nodo = nodos.item(i);
			String nombre = nodo.getNodeName();
			String value = null;

			if (nodo.getFirstChild() != null)
				value = nodo.getFirstChild().getNodeValue();
			if (nombre.equalsIgnoreCase(PUERTO_HTTP))
				ConfiguracionGeneral.puertoHttp = Integer.parseInt(value);
			if (nombre.equalsIgnoreCase(DIRECTORIO_DE_USUARIOS))
				ConfiguracionGeneral.directorioDeUsuarios = value;
			if (nombre.equalsIgnoreCase(PUERTO_RMI_REGISTRY))
				ConfiguracionGeneral.puertoRmiRegistry = Integer.parseInt(value);
			if (nombre.equalsIgnoreCase(HOST_RMI_REGISTRY))
				ConfiguracionGeneral.hostRmiRegistry = value;
			if (nombre.equalsIgnoreCase(PATH_RMI_REGISTRADOR))
				ConfiguracionGeneral.pathRmiDelRegistrador = value;
		}
	}

	/**
	 * Lector de configuración para ficheros de xml
	 * @param filename Fichero
	 * @return Peer asociado al usuario
	 * @throws ConfigException Excepción lanzada ante un error en la configuración
	 */
	public synchronized static Peer LeeConfiguracionPeer(String filename) throws ConfigException {
		Peer peer;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Element raiz;
		File fichero = null;
		
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			ConfigException ce = new ConfigException();
			ce.setMensajeDeError("Error al crear el DocumentBuilder");
			ce.initCause(e);
			throw ce;
		}
		
		try {
			fichero = new File(filename);
			File schema = new File(ConfiguracionGeneral.directorioDeUsuarios +
					File.separator +"usuario.xsd");
			Schema xsd = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(schema);
			StreamSource xml = new StreamSource(fichero);
			xsd.newValidator().validate(xml);
		} catch (Exception e) {
			ConfigException ce = new ConfigException();
			e.printStackTrace();
			ce.setMensajeDeError("Archivo XML "+filename+" no es válido");
			ce.initCause(e);
			throw ce;
		}
		
		try {
			raiz = db.parse(fichero).getDocumentElement();
		} catch (Exception e) {
			ConfigException ce = new ConfigException();
			ce.setMensajeDeError("Error al analizar el fichero " + filename);
			ce.initCause(e);
			throw ce;
		}
		
		if (!raiz.getNodeName().equalsIgnoreCase("USUARIO")) {
			ConfigException ce = new ConfigException();
			ce.setMensajeDeError("Error en " + filename + ": No sigue el formato");
			throw ce;
		}
		
		/* Hasta aqui todo bien, ahora recuperamos datos */
		
		NodeList nodos = raiz.getChildNodes();
		int puertoP2P = 0;
		String directorioExportados = null;
		String nombre = null;
		String clave = null;
		String email = null;
		String nick = null;

		for (int i = 0; i < nodos.getLength(); i++) {
			Node nodo = nodos.item(i);
			String nombre_nodo = nodo.getNodeName();
			String value = null;

			if (nodo.getFirstChild() != null) {
				value = nodo.getFirstChild().getNodeValue();

				if (nombre_nodo.equalsIgnoreCase(PUERTO_P2P))
					puertoP2P = Integer.parseInt(value);
				if (nombre_nodo.equalsIgnoreCase(DIRECTORIO_EXPORTADO))
					directorioExportados = value;
				if (nombre_nodo.equalsIgnoreCase(NICK))
					nick = value;
				if (nombre_nodo.equalsIgnoreCase(NOMBRE_COMPLETO))
					nombre = value;
				if (nombre_nodo.equalsIgnoreCase(CORREO_ELECTRONICO))
					email = value;
				if (nombre_nodo.equalsIgnoreCase(CLAVE))
					clave = value;
			}
		}
		
		peer = new Peer(nick,directorioExportados,puertoP2P);
		peer.setNombre(nombre);
		peer.setEmail(email);
		peer.setClave(clave);
		
		return peer;
	}
}

