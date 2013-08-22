import java.rmi.Naming;
import java.rmi.RMISecurityManager;

import es.urjc.escet.gsyc.rmi.Registrador;
import es.urjc.escet.gsyc.rmi.RegistradorImpl;

public class ServidorCentral {

	public static void main(String[] args) {
		if(args.length != 2){
			System.out.println("Debe especificar el host y el puerto del servidor de nombres RMI");
			System.exit(-1);
		}

		String rmiHost = args[0];
		short rmiPort;
		try{
			rmiPort = (short)Integer.parseInt(args[1]);
		}catch(Exception e){
			System.err.println("Error leyendo puerto, utilizando por defecto \"1099\"");
			rmiPort = 1099;
		}
		
		if(System.getSecurityManager()==null){
			System.setSecurityManager(new RMISecurityManager());
		}
	
		System.out.println("Lanzando el servidor central ...");
		
		String name = "//"+rmiHost+":"+rmiPort+"/Registrador";
		
		try{
			Registrador rm = new RegistradorImpl();
			Naming.rebind(name, rm);
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}