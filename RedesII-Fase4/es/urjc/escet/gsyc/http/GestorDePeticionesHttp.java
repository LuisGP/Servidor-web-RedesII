package es.urjc.escet.gsyc.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Map;

import es.urjc.escet.gsyc.config.ConfiguracionGeneral;
import es.urjc.escet.gsyc.config.LectorDeConfiguracion;
import es.urjc.escet.gsyc.html.GeneradorHtml;
import es.urjc.escet.gsyc.peer.Peer;
import es.urjc.escet.gsyc.peer.PeerHash;

/**
 * 
 * @author LuisGP
 * Esta clase se encarga de atender las peticiones que se le hacen a nuestro servidor.
 * Identifica si el cliente ya está logeado, si no lo está se le muestra la ventana de login
 * y lo autentifica, creando su Peer asociado e insertándolo en la tabla hash de peers.
 * Una vez el cliente está identificado, se procesan sus peticiones del modo adecuado.
 */
public class GestorDePeticionesHttp extends Thread {
	Peer peer;
	private Socket socket;
	
	public GestorDePeticionesHttp(Socket socket){
		this.socket = socket;
	}
	
	public GestorDePeticionesHttp(){
		this.socket = null;
	}
	
	public void setSocket(Socket socket){
		this.socket = socket;
	}
	
	private StringBuilder procesaPeticionPaginaDesconocida(){
		StringBuilder page = new StringBuilder("");
		page.append("<html><body>El recurso solicitado no se encuentra en el servidor </body></html>");
		return page;
	}
	
	private StringBuilder procesaPeticionPaginaRaiz(String string){
		return GeneradorHtml.generaRaiz(string);
	}
		
	private StringBuilder generaMensaje200 (StringBuilder htmlPage) {
		StringBuilder respuesta = new StringBuilder("");
		respuesta.append("HTTP/1.1 200 OK\r\n");
		respuesta.append("Connection: close\r\n");
		respuesta.append("Content-Type: text/html\r\n");
		respuesta.append("Content-Length: " + htmlPage.length() + "\r\n");
		respuesta.append("\r\n");
		respuesta.append(htmlPage);
		return respuesta;
	}
	
	private StringBuilder generaMensaje200Object (StringBuilder object, String tipo) {
		StringBuilder respuesta = new StringBuilder("");
		respuesta.append("HTTP/1.1 200 OK\r\n");
		respuesta.append("Connection: close\r\n");
		respuesta.append("Content-Type: "+tipo+"\r\n");
		respuesta.append("Content-Length: " + object.length() + "\r\n");
		respuesta.append("\r\n");
		respuesta.append(object);
		return respuesta;
	}

	private StringBuilder generaMensaje404(StringBuilder htmlPage) {
		StringBuilder respuesta = new StringBuilder("");
		respuesta.append("HTTP/1.1 404 Not Found\r\n");
		respuesta.append("Connection: close\r\n");
		respuesta.append("Content-Type: text/html\r\n");
		respuesta.append("Content-Length: " + htmlPage.length() + "\r\n");
		respuesta.append("\r\n");
		respuesta.append(htmlPage);
		return respuesta;
	}
	
	
	/**
	 * Genera la respuesta en función de la peticion y de si el usuario está o no logeado
	 * @param path El recurso que solicita
	 * @param vars Variables de la peticion
	 * @return El mensaje de respuesta HTML
	 */
	private StringBuilder generaRespuesta(String path, Map<String, String> vars){
		try{
			String valida = validarUsuario(vars);
			if(path.contentEquals(Constantes.RAIZ_PATH)){
				return generaMensaje200(procesaPeticionPaginaRaiz(null));
			}else if(valida == null){ //Usuario válido			
				if(path.contentEquals(Constantes.DIR_LOCAL_PATH)){
					return generaMensaje200(procesaPeticionDirLocal(Long.toString(peer.getCredencial())));
				}
				if(path.contentEquals(Constantes.MENU_PATH)){
					return generaMensaje200(procesaPeticionPaginaMenu(Long.toString(peer.getCredencial())));
				}
				if(path.contentEquals(Constantes.LISTA_TODOS)){
					return generaMensaje200(procesaPeticionListaTodos(Long.toString(peer.getCredencial())));
				}
				if(path.contentEquals(Constantes.USER)){
					String user = vars.get(Constantes.USUARIO);
					return generaMensaje200(procesaPeticionUser(Long.toString(peer.getCredencial()),
							user));
				}
				if(path.contentEquals(Constantes.LOGOUT)){
					String cred = vars.get(Constantes.CREDENCIAL);
					this.logout(cred);
					return generaMensaje200(procesaPeticionPaginaRaiz(null));
				}
				if(path.contentEquals(Constantes.BUSCAR)){
					String cad = vars.get(Constantes.FIND);
					return generaMensaje200(procesaPeticionBuscar(Long.toString(peer.getCredencial()),
							cad));
				}
				if(path.contentEquals(Constantes.DIR_REMOTO)){
					String user = vars.get(Constantes.USUARIO);
					return generaMensaje200(procesaPeticionDirRemoto(Long.toString(peer.getCredencial()),user));
				}
				if(path.contentEquals(Constantes.ESCRIBIR_MENSAJE)){
					String user = vars.get(Constantes.USUARIO);
					return generaMensaje200(procesaPeticionEsctibirMensaje(Long.toString(peer.getCredencial()),user));
				}
				if(path.contentEquals(Constantes.ENVIAR_MENSAJE)){
					String user = vars.get(Constantes.USUARIO);
					String asunto = vars.get(Constantes.ASUNTO);
					String cuerpo = vars.get(Constantes.CUERPO);
					
					return generaMensaje200(procesaPeticionEnviarMensaje(Long.toString(peer.getCredencial()),
							peer,user,asunto,cuerpo));
				}
				if(path.contentEquals(Constantes.VER_MENSAJES)){
					return generaMensaje200(procesaPeticionVerMensajes(Long.toString(peer.getCredencial()),peer));
				}
				if(path.contentEquals(Constantes.LEER_MENSAJE)){
					String indice = vars.get(Constantes.MENSAJE);
					return generaMensaje200(procesaPeticionLeerMensaje(Long.toString(peer.getCredencial()),peer,indice));
				}
				if(path.contentEquals(Constantes.BORRAR_MENSAJE)){
					String indice = vars.get(Constantes.MENSAJE);
					return generaMensaje200(procesaPeticionBorrarMensaje(Long.toString(peer.getCredencial()),peer,indice));
				}
				if(path.contentEquals(Constantes.DESCARGAR)){
					String usuarioRemoto = vars.get(Constantes.PROPIETARIO);
					String fichero = vars.get(Constantes.FICHERO);
					return generaMensaje200(procesaPeticionDescargarFichero(Long.toString(peer.getCredencial()),peer,
						usuarioRemoto,fichero));
				}if(path.contentEquals(Constantes.VER_DESCARGAS)){
					return generaMensaje200(procesaPeticionVerDescargas(Long.toString(peer.getCredencial()),peer));
				}
			}else{ // Usuario no válido
				if(path.contentEquals(Constantes.MENU_PATH)) // La peticion inicial es al MENU
					return generaMensaje200(procesaPeticionPaginaRaiz(valida));
				if(path.contentEquals(Constantes.ICONO)){
					return generaMensaje200Object(procesaPeticionImagen("./media/favicon.ico"),"image/x-icon");
				}
				if(path.contains(Constantes.MEDIA)){
					return generaMensaje200Object(procesaPeticionImagen("."+path),"image/bmp");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return generaMensaje404(procesaPeticionPaginaDesconocida());
		}
		return generaMensaje404(procesaPeticionPaginaDesconocida());
}
	
	private StringBuilder procesaPeticionVerDescargas(String cred, Peer peer2) {
		// TODO Auto-generated method stub
		return GeneradorHtml.generaVerDescargas(cred,peer);
	}

	private StringBuilder procesaPeticionDescargarFichero(String cred, Peer peer2, String usuarioRemoto, String fichero) {
		// TODO Auto-generated method stub
		return GeneradorHtml.generaDescargaFichero(cred,peer,usuarioRemoto,fichero);
	}

	private StringBuilder procesaPeticionBorrarMensaje(String cred, Peer peer, String indice) {
		// TODO Auto-generated method stub
		return GeneradorHtml.generaBorrarMensaje(cred,peer,indice);
	}

	private StringBuilder procesaPeticionLeerMensaje(String cred, Peer peer, String indice) {
		// TODO Auto-generated method stub
		return GeneradorHtml.generaLeerMensaje(cred,peer,indice);
	}

	private StringBuilder procesaPeticionImagen(String ruta) {
		// TODO Auto-generated method stub
		return GeneradorHtml.generaImagen(ruta);
	}

	private StringBuilder procesaPeticionVerMensajes(String cred, Peer peer) {
		// TODO Auto-generated method stub
		return GeneradorHtml.generaVerMensajes(cred,peer);
	}

	private StringBuilder procesaPeticionEnviarMensaje(String cred, Peer peer, String user, String asunto, String cuerpo) {
		// TODO Auto-generated method stub
		return GeneradorHtml.generaEnviarMensaje(cred,peer,user,asunto,cuerpo);
	}

	private StringBuilder procesaPeticionEsctibirMensaje(String cred, String user) {
		// TODO Auto-generated method stub
		return GeneradorHtml.generaEscribirMensaje(cred,user);
	}

	private StringBuilder procesaPeticionDirRemoto(String cred, String user) {
		// TODO Auto-generated method stub
		return GeneradorHtml.generaDirRemoto(peer, cred, user);
	}

	private StringBuilder procesaPeticionBuscar(String cred, String cad) {
		// TODO Auto-generated method stub
		return GeneradorHtml.generaBuscar(cred,cad);
	}

	private StringBuilder procesaPeticionUser(String cred, String usr) {
		// TODO Auto-generated method stub
		return GeneradorHtml.generarUser(cred,usr);
	}

	private StringBuilder procesaPeticionListaTodos(String cred) {
		// TODO Auto-generated method stub
		return GeneradorHtml.generarListaTodos(cred);
	}

	/**
	 * Este método, permite el "logout" (Salir de la aplicación) de forma controlada
	 *
	 */
	private void logout(String cred) {
		if(cred != null){
			//PeerHash.delete(peer);
			try{
				long cre = Long.parseLong(cred);
				PeerHash.delPeer(cre);
				this.peer.liberarServerP2P();
				this.peer = null;
			}catch(Exception e){
				;
			}
		}
	}

	/** 
	 * Este método es el encargado de autentificar al usuario y crear su Peer
	 * @param vars Variables de la peticion GET: usuario y passwd, credencial o nada
	 * @return True si se logró autentificar y false en caso contrario
	 */
	private String validarUsuario(Map<String, String> vars) {
		Peer peer;
		
		if(vars != null){
			String user = vars.get(Constantes.NICK_VAR);
			String passwd = vars.get(Constantes.CLAVE_VAR);
			String credencial = vars.get(Constantes.CREDENCIAL);
			String fichero = ConfiguracionGeneral.getDirectorioDeUsuarios()+
				File.separator+user;
			
			// Estaba logeado?
			peer = PeerHash.getPeer(user);
			if(peer != null){
				this.peer = peer;
				return null;
			}
			
			if(credencial != null){
				try{
					peer = PeerHash.getPeer(Long.parseLong(credencial));
				}catch(Exception e){
					System.err.println("Credencial mal formado: "+credencial);
					return "Credencial mal formado"+credencial;
				}
				if(peer != null){
					this.peer = peer;
					if(credencial.contentEquals(Long.toString(peer.getCredencial())))
						return null;
					return "Credencial no válido para "+peer.getNick();
				}
			}
			
			try{
				//peer = LectorDeConfiguracion.LeeConfiguracionUsuarioOLD(fichero+".cfg");
				peer = LectorDeConfiguracion.LeeConfiguracionUsuario(fichero+".xml");
				
				
				if(user.contentEquals(peer.getNick())){
					if(peer.checkClave(passwd)){
						//Random rnd = new Random();
						Constantes.info = null;
						peer.setHost(this.socket.getInetAddress().getHostAddress());
						
						String str = PeerHash.add(peer);
						
						this.peer = peer;
						System.out.println(peer);
						//ConfiguracionPeer.setCredencial(rnd.nextLong());
						if(Constantes.info != null){
							Constantes.info += "<br>"+str;
							str = null;
						}
						
						peer.lanzarServerP2P();
						
						return str;
					}
					// Contraseña incorrecta
				}
				
				return "Usuario o contraseña incorrectos";
				// Usuario no existe
			}catch(Exception e){
				System.err.println("Error leyendo "+fichero+".xml");
				return "Usuario o contraseña incorrectos";
			}
		}
		
		return "";
	}

	private StringBuilder procesaPeticionPaginaMenu(String cred) {
		// TODO Auto-generated method stub
		return GeneradorHtml.generaMenu(cred,peer);
	}

	private StringBuilder procesaPeticionDirLocal(String cred) {
		// TODO Auto-generated method stub
		return GeneradorHtml.generarDirLocal(cred);
	}

	public void run(){
		while(true){
			try{
				this.parar();
			}catch(Exception e){
				e.printStackTrace();
			}
			if(this.socket != null)
				try{
					//Recuperamos la petición del cliente
					BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
					
					//Leemos la primera línea (línea de petición HTTP) que es obligatoria
					String firstLine = in.readLine();
					System.out.println(firstLine);
					
					String path = null;
					Map<String, String> vars = null;
					Map<String, String> variables = null;
					
					StringBuilder respuesta = new StringBuilder();
					
					if(firstLine.contains("GET")){
						
						//Leemos resto de cabeceras, por si nos interesan. Sólo trabajamos con GET, por lo que no hay cuerpo
						String line = null;
						while( (line=in.readLine()) != null){
							//Si la línea está en blanco, entonces se han terminado las cabeceras
							if(line.contentEquals(""))
								break;
						}
						
						//Recuperamos el recuros solicitado y las variables GET presentes
						
						firstLine = URLDecoder.decode(firstLine,"ISO-8859-1");
						path = AnalizadorHttpGet.getPath(firstLine);
						vars = AnalizadorHttpGet.getVars(firstLine);
						

						//Procedemos a construir la respuesta
						respuesta = generaRespuesta(path, vars);
					}
					if(firstLine.contains("POST")){
						
						//Recuperamos el recuros solicitado y las variables GET presentes
						
						firstLine = URLDecoder.decode(firstLine,"ISO-8859-1");
						path = AnalizadorHttpPost.getPath(firstLine);
						//vars = AnalizadorHttpPost.getVars(firstLine);
						variables = AnalizadorHttpPost.getVariables(in); 

						//Procedemos a construir la respuesta
						respuesta = generaRespuesta(path, variables);
					}
					
					//Procedemos a construir la respuesta
					//StringBuilder respuesta = generaRespuesta(path, vars, variables);
					
					PrintWriter out = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()));
					out.print(respuesta.toString());
					out.close();
					in.close();
					
				} catch (Exception e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					return;
				}
		}
	}

	/**
	 * Estos métodos son los utilizados para proporcionar la funcionalidad de POOL de Gestores,
	 * para así mejorar el rendimiento al tratar peticiones.
	 * 
	 * Este acepta la peticion (despierta al thread)
	 * 
	 * @param socket Conexión a atender
	 */
	public synchronized void aceptarPeticion(Socket socket){
		this.notify();
		this.setSocket(socket);
	}
	
	/**
	 * Estos métodos son los utilizados para proporcionar la funcionalidad de POOL de Gestores,
	 * para así mejorar el rendimiento al tratar peticiones.
	 * 
	 * Este "duerme" el thread cuando ya se ha tratado la petición
	 * 
	 * @throws InterruptedException Excepción lanzada cuando nos despiertan 
	 */
	public synchronized void parar() throws InterruptedException{
		this.wait();
	}
	
}
