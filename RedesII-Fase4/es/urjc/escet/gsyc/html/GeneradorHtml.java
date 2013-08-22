package es.urjc.escet.gsyc.html;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;

import es.urjc.escet.gsyc.html.internal.Html;
import es.urjc.escet.gsyc.html.internal.HtmlForm;
import es.urjc.escet.gsyc.html.internal.HtmlP;
import es.urjc.escet.gsyc.html.internal.HtmlTable;
import es.urjc.escet.gsyc.html.internal.HtmlTableTd;
import es.urjc.escet.gsyc.html.internal.HtmlTableTr;
import es.urjc.escet.gsyc.http.Constantes;
import es.urjc.escet.gsyc.p2p.mensajes.PeticionEntregaMensaje;
import es.urjc.escet.gsyc.p2p.mensajes.PeticionListaFicheros;
import es.urjc.escet.gsyc.p2p.mensajes.PeticionP2P;
import es.urjc.escet.gsyc.p2p.mensajes.RespuestaEntregaMensaje;
import es.urjc.escet.gsyc.p2p.mensajes.RespuestaListaFicheros;
import es.urjc.escet.gsyc.p2p.tipos.DescriptorFichero;
import es.urjc.escet.gsyc.p2p.tipos.ListaFicheros;
import es.urjc.escet.gsyc.p2p.tipos.ListaUsuarios;
import es.urjc.escet.gsyc.p2p.tipos.Mensaje;
import es.urjc.escet.gsyc.p2p.tipos.Usuario;
import es.urjc.escet.gsyc.peer.Peer;
import es.urjc.escet.gsyc.peer.PeerException;
import es.urjc.escet.gsyc.peer.PeerHash;
import es.urjc.escet.gsyc.rmi.Cliente;

/**
 * Esta clase contiene los métodos estáticos que nos generan las distintas páginas que nos solicite el usuario,
 * apoyándose en el paquete es.urjc.escet.gsyc.html.internal
 * @author LuisGP
 *
 */
public class GeneradorHtml {
	/**
	 * Genera la página raiz (la pantalla de registro y entrada a la aplicación)
	 * @param string Mensaje adicional que se muestra en la pantalla
	 * @return Página HTML generada
	 */
	public static StringBuilder generaRaiz(String string){
		Html page = new Html("black","white");
		
		page.setTile("PÁGINA DE REGISTRO DE LA APLICACIÓN P2P DE REDES-II");
		page.addBr();
		
		HtmlP p = page.addP();
		p.addText("<basefont face=\"Comic Sans MS\" size=\"24\">");
		//p.addText("Bienvenido a la aplicación P2P de Redes-II");
		p.addText("<center><img src=/media/welcome.bmp alt=\"Bienvenido\"></center>");
		p.setAlign(HtmlP.AlignType.CENTER);
		page.addHr();
		
		//Creamos una tabla de 3x3
		HtmlTable table = page.addTable();
		HtmlTableTr[] tr = new HtmlTableTr[3]; 
		HtmlTableTd[][] td = new HtmlTableTd[3][3];
		
		//Creamos los trs
		for(int i = 0; i < 3; i++)
			tr[i] = table.addTr();
		
		//Creamos los tds
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 3; j++)
				td[i][j] = tr[i].addTd();
		
		//Damos formato
		//table.setBorder(1);
		tr[0].setHeight(100);
		tr[1].setHeight(100);
		tr[1].setValign(HtmlTableTr.ValignStyle.BOTTOM);
		tr[2].setHeight(50);
		td[0][0].setWidth(35);
		td[0][1].setWidth(25);
		td[0][2].setWidth(40);
		
		//Creamos el formulario en el td central
		HtmlForm f = td[1][1].addForm(Constantes.MENU_PATH,"POST");
	
		//Creamos la tabla interna
		HtmlTable itable = f.addTable();
		HtmlTableTr[] itr = new HtmlTableTr[3];
		for(int i = 0; i < 3; i++)
			itr[i] = itable.addTr();
		
		HtmlTableTd[][] itd = new HtmlTableTd[3][2];
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 2; j++)
				itd[i][j] = itr[i].addTd();
		
		//Damos formato a la tabla internat
		//itable.setBorder(1);
		
		itd[0][0].setWidth(25);
		//Añadimos los campos de formulario
		itd[0][0].addText("<img src=/media/nick.bmp alt=\"Nick P2P\">");
		itd[0][1].addInputText(Constantes.NICK_VAR);
		itd[1][0].addText("<img src=/media/clave.bmp alt=\"Contraseña\">");
		itd[1][1].addInputPassword(Constantes.CLAVE_VAR);
		
		itr[2].setHeight(40);
		itr[2].setValign(HtmlTableTr.ValignStyle.BOTTOM);
		itd[2][0].addInputSubmit("  Enviar  ");
		itd[2][0].setAlign(HtmlTableTd.AlignStyle.CENTER);
		itd[2][1].addInputReset("  Borrar  ");
		itd[2][1].setAlign(HtmlTableTd.AlignStyle.CENTER);
		
		page.addHr();
		
		if(string != null){
			p.addBr();
			p.setAlign(HtmlP.AlignType.CENTER);
			p.addText(string);
		}
		
		return page.getPage();
	}

	/**
	 * Genera el listado del Directorio Local del usuario actual
	 * @param cred Credencial del usuario que formuló la petición
	 * @return La página HTML así generada
	 */
	public static StringBuilder generarDirLocal(String cred) {
		Html page = new Html("black","white");
		
		page.setTile("PÁGINA DE DIRECTORIO LOCAL");
		HtmlP head = page.addP();
			head.setAlign(HtmlP.AlignType.CENTER);
			//head.addText("Listado del Directorio Compartido");
			head.addText("<center><img src=/media/compartido_local.bmp alt=\"Listado del Directorio Compartido\"></center>");
		page.addHr();
		HtmlP p = page.addP();

		p.setAlign(HtmlP.AlignType.CENTER);
		
		long credencial = Long.parseLong(cred);
		Peer peer = PeerHash.getPeer(credencial);
		ListaFicheros content = peer.getExportados();
		
		p.addText("<FONT FACE=\"Comic Sans MS\" SIZE=4>");
		for(int i=0; i < content.getNumFicheros(); i++){
			p.addA("file://"+((DescriptorFichero)content.getFichero(i)).getNombreFichero()+"?credencial="+
					cred,((DescriptorFichero)content.getFichero(i)).getNombreFichero());
			p.addBr();
		}
		
		p.addBr();
		p.addA("/menu"+"?credencial="+cred,"<img src=/media/link_menu.bmp alt=\"Menu\" border=0>");
		p.addBr();
		p.addA("/baja"+"?credencial="+cred,"<img src=/media/link_logout.bmp alt=\"Logout\" border=0>");
		p.addText("</FONT>");
		
		page.addHr();
		
		return page.getPage();
	}

	/**
	 * Genera el menú para el usuario actual
	 * @param cred Credencial del cliente que hizo la petición
	 * @return La página HTML de respuesta
	 */
	public static StringBuilder generaMenu(String cred, Peer peer) {
		Html page = new Html("black","white");
		
		page.setTile("PÁGINA DE MENU");
		HtmlP head = page.addP();
			head.setAlign(HtmlP.AlignType.CENTER);
			//head.addText("MENU");
			head.addText("<center><img src=/media/menu.bmp alt=\"MENU\"></center>");
		page.addHr();
		page.addText("<basefont face=\"Comic Sans MS\" size=5>");
		
		/* Formulario BUSCAR */
		{
			
			HtmlTable table = page.addTable();
			HtmlTableTr[] tr = new HtmlTableTr[1]; 
			HtmlTableTd[][] td = new HtmlTableTd[1][1];
			
			//Creamos los trs
			tr[0] = table.addTr();
			
			//Creamos los tds
			td[0][0] = tr[0].addTd();
			
			//Damos formato
			//table.setBorder(1);
			tr[0].setHeight(20);
			tr[0].setValign(HtmlTableTr.ValignStyle.BOTTOM);
			td[0][0].setWidth(35);
			
			//Creamos el formulario en el td central
			HtmlForm f = td[0][0].addForm(Constantes.BUSCAR);
			
			//Creamos la tabla interna
			HtmlTable itable = f.addTable();
			HtmlTableTr[] itr = new HtmlTableTr[1];
			itr[0] = itable.addTr();
			
			HtmlTableTd[][] itd = new HtmlTableTd[3][1];
			itd[0][0] = itr[0].addTd();
			itd[1][0] = itr[0].addTd();
			itd[2][0] = itr[0].addTd();
			
			//Damos formato a la tabla internat
			//itable.setBorder(1);
			
			itd[0][0].setWidth(30);
			//Añadimos los campos de formulario
			// Provoca, cosas raras con la codificacion...
			String str = "credencial="+cred+"&"+Constantes.FIND;
			itd[0][0].setAlign(HtmlTableTd.AlignStyle.RIGHT);
			itd[0][0].addText("<FONT FACE=\"Comic Sans MS\" SIZE=4>");
			itd[0][0].addText("Cadena: ");
			itd[0][0].addText("</FONT>");
			itd[1][0].setWidth(10);
			itd[1][0].addInputText(str);
			//itd[0][1].addInputText(Constantes.FIND);
			
			itd[2][0].setWidth(30);
			itd[2][0].setAlign(HtmlTableTd.AlignStyle.LEFT);
			itd[2][0].addInputSubmit("  BUSCAR  ");
			//itd[2][0].setAlign(HtmlTableTd.AlignStyle.CENTER);
			
		}
		/* FIN FORMULARIO */
		
		page.addHr();
		HtmlP p = page.addP();
		
		p.setAlign(HtmlP.AlignType.CENTER);
		page.addBr();
		
		if(Constantes.info != null){
			page.addText("<center><FONT FACE=\"Comic Sans MS\" SIZE=5 COLOR=RED>"+ Constantes.info +"</FONT><br></center>");
			Constantes.info = null;
		}
		
		HtmlTable table = page.addTable();
		HtmlTableTr[] tr = new HtmlTableTr[1];
		HtmlTableTd[][] td1 = new HtmlTableTd[1][1];
		HtmlTableTd[][] td2 = new HtmlTableTd[1][1];
		//		Creamos los trs
		tr[0] = table.addTr();
		td1[0][0] = tr[0].addTd();
		td2[0][0] = tr[0].addTd();
		
		td1[0][0].setAlign(HtmlTableTd.AlignStyle.LEFT);
		td1[0][0].addText("<FONT FACE=\"Comic Sans MS\" SIZE=5>");
		td1[0][0].addText("<img src=/media/datos_personales.bmp alt=\"Datos Personales\" border=0>");
		td1[0][0].addText("</FONT>");
		td1[0][0].addBr();
		td1[0][0].addText("<FONT FACE=\"Comic Sans MS\" SIZE=4>");
		td1[0][0].addText("\t\tNombre Completo:\t\t"+peer.getNombre());
		td1[0][0].addBr();
		td1[0][0].addText("\t\tNick:\t\t"+peer.getNick());
		td1[0][0].addBr();
		td1[0][0].addText("\t\tPuerto P2P:\t\t"+peer.getPuertoP2P());
		td1[0][0].addBr();
		td1[0][0].addText("\t\tDirectorio Compartido:\t\t"+peer.getDirectorio());
		td1[0][0].addBr();
		td1[0][0].addText("\t\tEmail:\t\t" +peer.getEmail());
		td1[0][0].addText("</FONT>");

		
		td2[0][0].setAlign(HtmlTableTd.AlignStyle.CENTER);
		td2[0][0].addA("/ver_descargas"+"?credencial="+cred,"<img src=/media/ver_descargas.bmp alt=\"Ver Descargas\" border=0>");
		td2[0][0].addBr();
		td2[0][0].addA("/ver_mensajes"+"?credencial="+cred,"<img src=/media/ver_mensajes.bmp alt=\"Ver Mensajes\" border=0>");
		td2[0][0].addA("/ver_mensajes"+"?credencial="+cred,"("+peer.getMensajes().size()+")");
		td2[0][0].addBr();
		td2[0][0].addA("/lista_todos"+"?credencial="+cred,"<img src=/media/link_usuarios.bmp alt=\"Lista Usuarios\" border=0>");
		td2[0][0].addBr();
		td2[0][0].addA("/dir_local"+"?credencial="+cred,"<img src=/media/link_compartido.bmp alt=\"Compartido\" border=0>");
		td2[0][0].addBr();
		td2[0][0].addA("/baja"+"?credencial="+cred,"<img src=/media/link_logout.bmp alt=\"Logout\" border=0>");
				
		page.addHr();
		page.addBr();
		//page.addText("<center><img src=/media/hola.bmp></center>");
		page.addBr();
		
		return page.getPage();
	}

	public static StringBuilder generarUser(String cred, String user) {
		Html page = new Html("black","white");
		
		page.setTile("PÁGINA DE "+user);
		HtmlP head = page.addP();
			head.setAlign(HtmlP.AlignType.CENTER);
			//head.addText("DATOS DE "+ user);
			head.addText("<center><b><FONT FACE=\"Comic Sans MS\" SIZE=6>"+
					"<img src=/media/datos_personales.bmp alt=\"Pagina personal de \" ALIGN=MIDDLE>"+
					" "+user+"</FONT></b></center>");
			
		page.addHr();
		HtmlP p = page.addP();
		
		p.setAlign(HtmlP.AlignType.CENTER);
		
		Usuario usr;
		
		try{
			if(user != null){
				usr = Cliente.getUsuario(user);
				HtmlP pcl = p.addP();
				pcl.setAlign(HtmlP.AlignType.LEFT);
				pcl.addText("<FONT FACE=\"Comic Sans MS\" SIZE=4>");
				pcl.addText("\t\tNombre Completo:\t\t"+usr.getNombreCompleto());
				pcl.addBr();
				pcl.addText("\t\tEmail:\t\t" +usr.getCorreoElectronico());
				pcl.addText("</FONT>");
				pcl.addBr();
				pcl.addBr();
				//p.addA("/dir_local"+"?credencial="+cred,user);
			}
		}catch(RemoteException re){
			re.printStackTrace();
			Constantes.info = "No fue posible contactar con el servidor remoto";
		}
		
		
		HtmlP pn = p.addP();
		pn.setAlign(HtmlP.AlignType.CENTER);
		pn.addA("/lista_todos"+"?credencial="+cred,"<img src=/media/link_usuarios.bmp alt=\"Lista Usuarios\" border=0>");
		pn.addBr();
		pn.addA("/menu"+"?credencial="+cred,"<img src=/media/link_menu.bmp alt=\"Menu\" border=0>");
		pn.addBr();
				
		page.addHr();
		
		return page.getPage();
	}

	public static StringBuilder generarListaTodos(String cred) {
		Html page = new Html("black","white");
		
		page.setTile("LISTADO DE USUARIOS ACTIVOS");
		page.addBr();
		HtmlP head = page.addP();
			head.setAlign(HtmlP.AlignType.CENTER);
			//head.addText("USUARIOS ACTIVOS:");
			head.addText("<center><img src=/media/activos.bmp alt=\"USUARIOS ACTIVOS\"></center>");
		page.addHr();
		HtmlP p = page.addP();
		
		p.setAlign(HtmlP.AlignType.CENTER);
		page.addHr();
		
		ListaUsuarios lista;
		
		try{
			lista = Cliente.getListaUsuarios();
			Usuario usr;
			
			p.addText("<FONT FACE=\"Comic Sans MS\" SIZE=4>");
			for(int i = 0; i < lista.size(); i++){
				usr = lista.getUsuario(i);
				p.addA("/user"+"?credencial="+cred+"&usuario="+usr.getNick(),usr.getNick());
				p.addText("&nbsp;&nbsp;&nbsp;");
				p.addA("/dir_remoto"+"?credencial="+cred+"&usuario="+usr.getNick(),
						"<img src=/media/ficheros.bmp alt=\"Listar ficheros de este peer\" border=0>");
				p.addText("&nbsp;&nbsp;&nbsp;");
				p.addA("/escribir_mensaje"+"?credencial="+cred+"&usuario="+usr.getNick(),
						"<img src=/media/carta.bmp alt=\"Enviar un mensaje a este peer\" border=0>");
				p.addBr();
			}
			p.addText("</FONT>");
			
			
		}catch(RemoteException re){
			re.printStackTrace();
			Constantes.info = "No fue posible contactar con el servidor remoto";
		}
		
		p.addBr();
		p.addA("/menu"+"?credencial="+cred,"<img src=/media/link_menu.bmp alt=\"Menu\" border=0>");
		p.addBr();
		
		return page.getPage();
	}

	public static StringBuilder generaBuscar(String cred, String cad) {
		Html page = new Html("black","white");
		int nfich = 0;
		
		page.setTile("LISTADO DE USUARIOS");
		page.addBr();
		HtmlP head = page.addP();
			head.setAlign(HtmlP.AlignType.CENTER);
			//head.addText("USUARIOS CON LA BUSQUEDA:");
			head.addText("<center><img src=/media/busqueda.bmp alt=\"USUARIOS CON LA BUSQUEDA:\"></center>");
		page.addHr();
		HtmlP p = page.addP();
		
		p.setAlign(HtmlP.AlignType.CENTER);
		page.addHr();
		
		ListaUsuarios lista;
		
		try{
			lista = Cliente.buscarFicheros(cad);
			Usuario usr;
			
			p.addText("<FONT FACE=\"Comic Sans MS\" SIZE=4>");
			for(int i = 0; i < lista.size(); i++){
				usr = lista.getUsuario(i);
				p.addA("/user"+"?credencial="+cred+"&usuario="+usr.getNick(),usr.getNick());
				p.addText("&nbsp;&nbsp;&nbsp;");
				p.addA("/dir_remoto"+"?credencial="+cred+"&usuario="+usr.getNick(),
						"<img src=/media/ficheros.bmp alt=\"Listar ficheros de este peer\" border=0>");
				p.addText("&nbsp;&nbsp;&nbsp;");
				p.addA("/escribir_mensaje"+"?credencial="+cred+"&usuario="+usr.getNick(),
						"<img src=/media/carta.bmp alt=\"Enviar un mensaje a este peer\" border=0>");
				p.addBr();
				nfich++;
			}
			p.addText("</FONT>");
			
		}catch(RemoteException re){
			re.printStackTrace();
			Constantes.info = "No fue posible contactar con el servidor remoto";
		}
		
		p.addBr();
		p.addText("<FONT FACE=\"Comic Sans MS\" SIZE=3>");
		p.addText("Se han encontrado "+nfich+" usuarios con ficheros con esa cadena");
		p.addText("</FONT>");
		
		p.addBr();
		p.addA("/menu"+"?credencial="+cred,"<img src=/media/link_menu.bmp alt=\"Menu\" border=0>");
		p.addBr();
		
		return page.getPage();
	}

	public static StringBuilder generaDirRemoto(Peer mipeer, String cred, String user) {
		Html page = new Html("black","white");
		
		page.setTile("PÁGINA DE DIRECTORIO REMOTO");
		HtmlP head = page.addP();
			head.setAlign(HtmlP.AlignType.CENTER);
			//head.addText("Listado del Directorio Compartido de " + user);
			head.addText("<center><b><FONT FACE=\"Comic Sans MS\" SIZE=6>"+
					"<img src=/media/compartido_remoto.bmp alt=\"Listado del Directorio remoto de\" ALIGN=MIDDLE>"+
					" "+user+"</FONT></b></center>");
		page.addHr();
		HtmlP p = page.addP();
		p.setAlign(HtmlP.AlignType.CENTER);
		
		try{
			Usuario peer = Cliente.getUsuario(user);
			
			PeticionP2P peticion = new PeticionListaFicheros(mipeer.getNick());
			
			RespuestaListaFicheros respuesta = (RespuestaListaFicheros)mipeer.p2pEnviaPeticionP2P(peer,peticion);
			//ListaFicheros content = peer.getCompartidos();
			
			ListaFicheros content = respuesta.getListaFicheros();
			
			p.addText("<FONT FACE=\"Comic Sans MS\" SIZE=4>");
			for(int i=0; i < content.getNumFicheros(); i++){
				p.addA("/descargar?file="+content.getFichero(i).getNombreFichero()+
						"&credencial="+cred+
						"&owner="+user,
						content.getFichero(i).getNombreFichero());
				p.addBr();
			}
			p.addText("</FONT>");
		}catch(RemoteException re){
			re.printStackTrace();
			Constantes.info = "No fue posible contactar con el servidor remoto";
		}catch(PeerException pe){
			pe.printStackTrace();
			Constantes.info = "No fue posible contactar con el usuario remoto "+user;
		}catch(Exception e){
			e.printStackTrace();
			Constantes.info = "No fue posible realizar la operación";
		}
		
		p.addBr();
		p.addA("/menu"+"?credencial="+cred,"<img src=/media/link_menu.bmp alt=\"Menu\" border=0>");
		p.addBr();
		p.addA("/baja"+"?credencial="+cred,"<img src=/media/link_logout.bmp alt=\"Logout\" border=0>");
		
		page.addHr();
		
		return page.getPage();
	}

	public static StringBuilder generaEscribirMensaje(String cred, String user) {
		Html page = new Html("black","white");
		
		page.setTile("PÁGINA DE ESCRITURA DE MENSAJES");
		HtmlP head = page.addP();
			head.setAlign(HtmlP.AlignType.CENTER);
			//head.addText("Mensaje para " + user);
			head.addText("<center><b><FONT FACE=\"Comic Sans MS\" SIZE=6>"+
					"<img src=/media/escribir_mensaje.bmp alt=\"Mensaje para\" ALIGN=MIDDLE>"+
					" "+user+"</FONT></b></center>");
		page.addHr();
		
		/* PRUEBA 
		HtmlP prueba = page.addP();
		prueba.addText("<form name=\"datos\" method=\"POST\">" +
				"Escribe tu nombre: <input type=\"text\" name=\"nombre\"><br>" +
				"<input type=\"submit\" value=\"enviar formulario\"><br>" +
				"<input type=\"reset\" value=\"borrar\">" +
		 		"</form>");
		/* FIN PRUEBA */
		
		
		//		Creamos el formulario en el td central
		HtmlForm f = page.addForm(Constantes.ENVIAR_MENSAJE,"POST");
		
		/* 
		 	<form name="datos" action="ejemplos/procesar.asp" method="POST" target="_blank">
		 	Escribe tu nombre: <input type="text" name="nombre"><br>
		 	<input type="submit" value="enviar formulario"><br>
		 	<input type="reset" value="borrar">
		 	</form>
		 */
		
		/* TABLA PARA MENSAJES */
		HtmlTable table = f.addTable();
		HtmlTableTr[] tr = new HtmlTableTr[3];
		HtmlTableTd[][] td1 = new HtmlTableTd[2][2];
		//		Creamos los trs
		tr[0] = table.addTr();
		td1[0][0] = tr[0].addTd();
		td1[1][0] = tr[0].addTd();
		tr[1] = table.addTr();
		td1[0][1] = tr[1].addTd();
		tr[2] = table.addTr();
		td1[1][1] = tr[2].addTd();
		
		td1[0][0].setAlign(HtmlTableTd.AlignStyle.CENTER);
		td1[0][0].addText("<img src=/media/asunto.bmp alt=\"Asunto\" border=0>");
		td1[0][0].addInputText(Constantes.CREDENCIAL+"="+cred+"&"+Constantes.USUARIO+"="+user+"&"+Constantes.ASUNTO);
		
		/* Cuerpo deberia ser TextArea y usar POST :S */
		tr[1].setValign(HtmlTableTr.ValignStyle.TOP);
		td1[0][1].setAlign(HtmlTableTd.AlignStyle.CENTER);
		td1[0][1].addText("<img src=/media/cuerpo.bmp alt=\"Cuerpo\" border=0>");
		//HtmlForm form = td1[0][1].addForm("");
		td1[0][1].addTextArea(Constantes.CUERPO);
		//td1[0][1].addInputText(Constantes.CUERPO);
		
		td1[1][1].setAlign(HtmlTableTd.AlignStyle.CENTER);
		td1[1][1].addInputReset("   Borrar   ");
		td1[1][1].addInputSubmit("   Enviar   ");
		
		HtmlP p = page.addP();
		p.setAlign(HtmlP.AlignType.CENTER);
		
		p.addBr();
		p.addA("/menu"+"?credencial="+cred,"<img src=/media/link_menu.bmp alt=\"Menu\" border=0>");
		p.addBr();
		p.addA("/baja"+"?credencial="+cred,"<img src=/media/link_logout.bmp alt=\"Logout\" border=0>");
		
		page.addHr();
		
		return page.getPage();
	}

	public static StringBuilder generaEnviarMensaje(String cred, Peer peer, String user, String asunto, String cuerpo) {
		Html page = new Html("black","white");
		
		page.setTile("PÁGINA DE ENVIO DE MENSAJES");
		HtmlP head = page.addP();
			head.setAlign(HtmlP.AlignType.CENTER);
			head.addText("<center><b><FONT FACE=\"Comic Sans MS\" SIZE=6>"+
					"<img src=/media/escribir_mensaje.bmp alt=\"Mensaje para\" ALIGN=MIDDLE>"+
					" "+user+"</FONT></b></center>");
		page.addHr();
		
		/* GENERAR MENSAJE Y COMPROBAR */
		Mensaje msg = new Mensaje(peer.getNick(),user,asunto,cuerpo);
		String resp = null;
		Usuario dest;
		
		try{
			dest = Cliente.getUsuario(user);
			
			PeticionEntregaMensaje peticion = new PeticionEntregaMensaje(peer.getNick(),msg);

			RespuestaEntregaMensaje respuesta = (RespuestaEntregaMensaje)peer.p2pEnviaMensaje(dest,peticion);
			
			if(respuesta.getDescription() != null){
				resp = respuesta.getDescription();
			}
			
		}catch(RemoteException re){
			Constantes.info = "No fue posible contactar con el servidor remoto";
		}catch(Exception re){
			re.printStackTrace();
			resp = resp +". "+ "No se pudo conectar con el Peer destino";
		}
		 
		
		
		HtmlP p = page.addP();
		p.setAlign(HtmlP.AlignType.CENTER);
		p.addText("<FONT FACE=\"Comic Sans MS\" SIZE=4>");
		if(resp != null)
			p.addText(resp);
		else
			p.addText("Mensaje enviado correctamente");
		
		p.addBr();
		p.addA("/menu"+"?credencial="+cred,"<img src=/media/link_menu.bmp alt=\"Menu\" border=0>");
		p.addBr();
		p.addA("/baja"+"?credencial="+cred,"<img src=/media/link_logout.bmp alt=\"Logout\" border=0>");
		p.addText("</FONT>");
		page.addHr();
		
		return page.getPage();
	}

	public static StringBuilder generaVerMensajes(String cred, Peer peer) {
		Html page = new Html("black","white");
		
		page.setTile("PÁGINA DE VISUALIZACION DE MENSAJES");
		HtmlP head = page.addP();
			head.setAlign(HtmlP.AlignType.CENTER);
			//head.addText("Mensajes personales");
			head.addText("<center><img src=/media/mensajes.bmp alt=\"Mensajes Personales\"></center>");
		page.addHr();
		
		/* GENERAR MENSAJE Y COMPROBAR */
		ArrayList<Mensaje> msg = peer.getMensajes();
		
		/*try{
			msg = Cliente.getMensajes(peer.getNick());
		}catch(RemoteException re){
			re.printStackTrace();
		}*/
		
		
		HtmlP p = page.addP();
		p.setAlign(HtmlP.AlignType.CENTER);
		
		p.addText("<FONT FACE=\"Comic Sans MS\" SIZE=4>");
		for(int i = 0; i < msg.size(); i++){
			p.addA("/borrar_mensaje?credencial="+cred+"&mensaje="+i,"<img src=/media/borrar.bmp alt=\"Borrar\"  border=0>");
			p.addA("/leer_mensaje?credencial="+cred+"&mensaje="+i,msg.get(i).getAsunto());
			p.addText(" de ");
			p.addA(Constantes.USER+"?"+Constantes.CREDENCIAL+"="
					+cred+"&"+Constantes.USUARIO+"="+msg.get(i).getEmisor(),msg.get(i).getEmisor());
			p.addBr();
		}
		
		p.addBr();
		p.addA("/menu"+"?credencial="+cred,"<img src=/media/link_menu.bmp alt=\"Menu\" border=0>");
		p.addBr();
		p.addA("/baja"+"?credencial="+cred,"<img src=/media/link_logout.bmp alt=\"Logout\" border=0>");
		p.addText("</FONT>");
		
		page.addHr();
		
		return page.getPage();
	}

	public static StringBuilder generaImagen(String ruta) {
		InputStream fis = null;
		StringBuilder cb = null;
		
		try{
			fis = new FileInputStream(ruta);
			File fichero = new File(ruta);
			BufferedInputStream lector = new BufferedInputStream(fis);
			
			byte[] img = new byte[(int)fichero.length()];
			
			lector.read(img);
			
			String tmp = new String(img);
			cb = new StringBuilder(tmp);
			
		}catch(IOException e){
			e.printStackTrace();
			//Constantes.info = "No fue posible contactar con el servidor remoto";
		}
		
		return cb;
	}

	public static StringBuilder generaLeerMensaje(String cred, Peer peer, String indice) {
		Html page = new Html("black","white");
		
		page.setTile("PÁGINA DE LECTURA DE MENSAJES");
		HtmlP head = page.addP();
			head.setAlign(HtmlP.AlignType.CENTER);
			//head.addText("Mensajes personales");
			head.addText("<center><img src=/media/mensajes.bmp alt=\"Mensajes Personales\"></center>");
		page.addHr();
		
		/* GENERAR MENSAJE Y COMPROBAR */
		ArrayList<Mensaje> msgs = peer.getMensajes();
		Mensaje msg = null;
		String text = null;
		int index = -1;
		
		try{
			index = Integer.parseInt(indice);

			if(index < 0 || index >= msgs.size()){
				text = "Mensaje no existente";
			}
			
			msg = msgs.get(index);
			
		}catch(Exception e){
			text = "Mensaje no existente";
			Constantes.info = "No fue posible contactar con el servidor remoto";
		}
		
		HtmlP p = page.addP();
		p.setAlign(HtmlP.AlignType.CENTER);
		
		p.addText("<FONT FACE=\"Comic Sans MS\" SIZE=4>");
		if(text == null){
			text = "Asunto: " + msg.getAsunto() + "<br>" + "Emisor: " + msg.getEmisor() +
				"<br>" + "Momento: " + new Date(msg.getInstante()) + "<br>" + "Para: " +
				msg.getReceptor() + "<br><br>" + "Cuerpo: " + "<br>" + msg.getMensaje();
		}
		p.addA("/escribir_mensaje"+"?credencial="+cred+"&usuario="+msg.getEmisor(),
			"<img src=/media/reply.bmp alt=\"Responder al mensaje a este peer\" border=0>");
		p.addA("/borrar_mensaje?credencial="+cred+"&mensaje="+index,"<img src=/media/borrar.bmp alt=\"Borrar\"  border=0>");
		p.addBr();
		p.addText(text);
		p.addBr();
		p.addBr();
		p.addA("/escribir_mensaje"+"?credencial="+cred+"&usuario="+msg.getEmisor(),
			"<img src=/media/reply.bmp alt=\"Responder al mensaje a este peer\" border=0>");
		p.addA("/borrar_mensaje?credencial="+cred+"&mensaje="+index,"<img src=/media/borrar.bmp alt=\"Borrar\"  border=0>");
		p.addBr();
		p.addA("/menu"+"?credencial="+cred,"<img src=/media/link_menu.bmp alt=\"Menu\" border=0>");
		p.addBr();
		p.addA("/baja"+"?credencial="+cred,"<img src=/media/link_logout.bmp alt=\"Logout\" border=0>");
		p.addText("</FONT>");
		
		page.addHr();
		
		return page.getPage();
	}

	public static StringBuilder generaBorrarMensaje(String cred, Peer peer, String indice) {
		Html page = new Html("black","white");
		
		page.setTile("PÁGINA DE BORRADO DE MENSAJES");
		HtmlP head = page.addP();
			head.setAlign(HtmlP.AlignType.CENTER);
			//head.addText("Mensajes personales");
			head.addText("<center>Mensaje Borrado</center>");
		page.addHr();
		
		/* GENERAR MENSAJE Y COMPROBAR */
		ArrayList<Mensaje> msgs = peer.getMensajes();
		Mensaje msg = null;
		String text = null;
		
		try{
			int index = Integer.parseInt(indice);

			if(index < 0 || index >= msgs.size()){
				text = "Mensaje no existente";
			}
			
			msg = msgs.get(index);
			text = "Borrado mensaje "+msg.getAsunto() + " de " + msg.getEmisor();
			
			peer.delMensaje(msg);
			
		}catch(Exception e){
			text = "Mensaje no existente";
			Constantes.info = "No fue posible contactar con el servidor remoto";
		}
		
		HtmlP p = page.addP();
		p.setAlign(HtmlP.AlignType.CENTER);
		
		p.addText("<FONT FACE=\"Comic Sans MS\" SIZE=4>");
		
		p.addText(text);
		p.addBr();
		p.addBr();
		p.addA("/menu"+"?credencial="+cred,"<img src=/media/link_menu.bmp alt=\"Menu\" border=0>");
		p.addBr();
		p.addA("/baja"+"?credencial="+cred,"<img src=/media/link_logout.bmp alt=\"Logout\" border=0>");
		p.addText("</FONT>");
		
		page.addHr();
		
		return page.getPage();
	}

	public static StringBuilder generaDescargaFichero(String cred, Peer peer, String usuarioRemoto, String fichero) {
		Html page = new Html("black","white");
		
		page.setTile("DESCARGANDO "+fichero+ " DE "+usuarioRemoto);
		HtmlP head = page.addP();
			head.setAlign(HtmlP.AlignType.CENTER);
			//head.addText("Mensajes personales");
			head.addText("<center>"+fichero+ " DE "+usuarioRemoto+"</center>");
		page.addHr();
		
		Usuario ur;
		String info = new String();
		int size = 0;
		
		try{
			ur = Cliente.getUsuario(usuarioRemoto);
			info = peer.descargarFicheroRemoto(ur,fichero);
			
			info = "Descargando fichero "+fichero+" de "+info+" bytes";
			
		}catch(RemoteException re){
			re.printStackTrace();
			info = "Error: "+info;
		}catch(IOException ioe){
			ioe.printStackTrace();
			info = "Error: "+info;
		}catch(PeerException pe){
			pe.printStackTrace();
			info = "Error: No fue posible contactar con el usuario remoto";
		}
		
		
		HtmlP p = page.addP();
		p.setAlign(HtmlP.AlignType.CENTER);
		
		p.addText("<FONT FACE=\"Comic Sans MS\" SIZE=4>");
		
		p.addBr();
		p.addText(info);
		p.addBr();
		p.addBr();
		p.addA("/menu"+"?credencial="+cred,"<img src=/media/link_menu.bmp alt=\"Menu\" border=0>");
		p.addBr();
		p.addA("/baja"+"?credencial="+cred,"<img src=/media/link_logout.bmp alt=\"Logout\" border=0>");
		p.addText("</FONT>");
		
		page.addHr();
		
		return page.getPage();
	}

	public static StringBuilder generaVerDescargas(String cred, Peer peer) {
		Html page = new Html("black","white");
		
		page.setTile("DESCARGAS");
		HtmlP head = page.addP();
			head.setAlign(HtmlP.AlignType.CENTER);
			//head.addText("Mensajes personales");
			head.addText("<center>DESCARGAS</center>");
		page.addHr();
		
		
		ArrayList<String> lista = peer.getListaDescargas();
		
		HtmlP p = page.addP();
		p.setAlign(HtmlP.AlignType.CENTER);
		p.addText("<meta http-equiv=\"refresh\" content=\"5;URL=/ver_descargas?credencial="+cred+"\" >");
		
		p.addText("<FONT FACE=\"Comic Sans MS\" SIZE=4>");

		for(int i=0; i < lista.size(); i++){
			p.addText(lista.get(i)+" @ "+peer.getInfo().get(lista.get(i))*100+"%");
			p.addBr();
		}
		
		p.addBr();
		p.addBr();
		p.addA("/menu"+"?credencial="+cred,"<img src=/media/link_menu.bmp alt=\"Menu\" border=0>");
		p.addBr();
		p.addA("/baja"+"?credencial="+cred,"<img src=/media/link_logout.bmp alt=\"Logout\" border=0>");
		p.addText("</FONT>");
		
		page.addHr();
		
		return page.getPage();
	}
}
