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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ComputeClient {
    static String ipCliente="direccion";
    static String ipServer;
    static int puertoServer;
    
    
    public static void main(String args[]) {
        inicializarRMI();
        
        int topo=esperaTopo();
        poblarTopo(topo);
       
        //Puebla el campo del topo correspondiente y inicia el mecanismo que envia al topo activado.
        int machucado=esperaMartillazo();
        topoMachucado();
        
        
        
        
        
    }
    private static void inicializarRMI(){
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
    }
    private static int esperaTopo(){
    //Mientras el juego siga activo espera a un topo.
        int topo=0;
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
         return topo;
    }
    private static void topoMachucado() {
       Socket s = null;
	    try {
	    	int serverPort = 7896;
	   	
                s = new Socket("localhost", serverPort);    
             //   s = new Socket("127.0.0.1", serverPort);    
		DataInputStream in = new DataInputStream( s.getInputStream());
		DataOutputStream out =
			new DataOutputStream( s.getOutputStream());
		out.writeUTF("Hello");        	// UTF is a string encoding 
                
		String data = in.readUTF();	      
                System.out.println("Received: "+ data) ;      
       	    } 
            catch (UnknownHostException e) {
		System.out.println("Sock:"+e.getMessage()); 
	    }
            catch (EOFException e) {
                System.out.println("EOF:"+e.getMessage());
    	    } 
            catch (IOException e) {
                System.out.println("IO:"+e.getMessage());
            } finally {
                if(s!=null) 
                    try {
                        s.close();
                    } catch (IOException e){
                    System.out.println("close:"+e.getMessage());}
                    }
    }

    private static void poblarTopo(int topo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static int esperaMartillazo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
