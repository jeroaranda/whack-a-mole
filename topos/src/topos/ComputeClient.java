//
// indicate the location of security policies.
//

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topos;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.math.BigDecimal;
import interfaces.Compute;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

public class ComputeClient {
    static String ipCliente="direccion";
    static String ipServer;
    static int puertoServer;
    
    
    public static void main(String args[]) {
        //El cliente inicia una sesión por medio de RMI y obtiene los puertos y la ip por la que recibirá al topo.
        System.setProperty("java.security.policy","/Users/jeroaranda/Documents/ITAM/DECIMO/SISTEMAS DISTR./proyecto01/whack-a-mole/topos/src/topos/client.policy");
        
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "Compute";
            Registry registry = LocateRegistry.getRegistry("localhost"); // server's ip address args[0]
            Compute comp = (Compute) registry.lookup(name);
            comp.crearSesion(ipCliente);
            ipServer=comp.obtenerIP(ipCliente);
            puertoServer=comp.obtenerPuerto(ipCliente);
//System.out.println("3^2 = "+comp.square(3, new Credential("Jose", "Jalisco", 1987, "123")));
            
        } catch (Exception e) {
            System.err.println("exception");
            e.printStackTrace();
        }
        //Mientras el juego siga activo espera a un topo.
        MulticastSocket s =null;
   	 try {
                InetAddress group = InetAddress.getByName(ipServer); // destination multicast group 
	    	s = new MulticastSocket(puertoServer);
	   	s.joinGroup(group); 

	    	byte[] buffer = new byte[1000];
 	   	for(int i=0; i< 3; i++) {
                    System.out.println("Waiting for messages");
                    DatagramPacket messageIn = 
			new DatagramPacket(buffer, buffer.length);
 		    s.receive(messageIn);
 		    System.out.println("Message: " + (new String(messageIn.getData())).trim()+ " from: "+ messageIn.getAddress());
  	     	}
	    	s.leaveGroup(group);		
 	    }
         catch (SocketException e){
             System.out.println("Socket: " + e.getMessage());
	 }
         catch (IOException e){
             System.out.println("IO: " + e.getMessage());
         }
	 finally {
            if(s != null) s.close();
        }
        //Puebla el campo del topo correspondiente y inicia el mecanismo que envia al topo activado.
        
        
        
        
        
        
        
    }    
}
