import java.rmi.Naming;

import es.urjc.escet.gsyc.config.ConfigException;
import es.urjc.escet.gsyc.config.ConfiguracionGeneral;
import es.urjc.escet.gsyc.config.LectorDeConfiguracion;
import es.urjc.escet.gsyc.http.Constantes;
import es.urjc.escet.gsyc.http.ServidorHttpDelTerminal;
import es.urjc.escet.gsyc.rmi.Cliente;
import es.urjc.escet.gsyc.rmi.ClienteRetro;
import es.urjc.escet.gsyc.rmi.ClienteRetroImpl;

/**
 * 
 * @author Luis Gasco Poderoso
 * Esta clase es el Terminal, encargada únicamente de leer el fichero de configuración
 * del servidor y lanzarlo.
 */
public class Terminal {
		
	public static void main(String[] args) {

		if (args.length != 3){
			System.out.println("Modo de uso:");
			System.out.println("> java Terminal <fichero.xml> <hostRMI> <puertoRMI>");
			System.exit(-1);
		}
		
		try{
			LectorDeConfiguracion.LeeConfiguracionGeneral(args[0]);
		} catch (ConfigException e){
			System.out.println(e.getMensajeDeError());
			System.exit(-1);
		}
		
		/* Creamos el rmi Cliente para las retrollamadas */
		String rmiHost = args[1];
		short rmiPort;
		try{
			rmiPort = (short)Integer.parseInt(args[2]);
		}catch(Exception e){
			System.err.println("Error leyendo puerto, utilizando por defecto \"1099\"");
			rmiPort = 1099;
		}
		
		//if(System.getSecurityManager()==null){
		//	System.setSecurityManager(new RMISecurityManager());
		//}
		
		System.out.println("Lanzando el cliente de retrollamadas ...");
		
		String name = "//"+rmiHost+":"+rmiPort+"/Cliente";
		
		try{
			ClienteRetro cr = new ClienteRetroImpl(name);
			Naming.rebind(name, cr);
			Cliente.asociarse(cr);
			
		} catch(Exception e){
			e.printStackTrace();
			System.err.println("No ha sido posible asociarse al servidor remoto");
			Constantes.info = "No ha sido posible asociarse al servidor remoto";
		}
		
		/* Servidor */
		
		ServidorHttpDelTerminal httpServer = 
			new ServidorHttpDelTerminal(ConfiguracionGeneral.getPuertoHttp());
		httpServer.start();
		
		while(true){
			try{
				Thread.sleep(1000);
			} catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}
}
