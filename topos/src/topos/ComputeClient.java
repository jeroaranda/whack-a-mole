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
import java.net.InetAddress;
import java.util.Arrays;

public class ComputeClient {
    static InetAddress ipCliente;
    static InetAddress ipServer;
    static InetAddress ipServerReal;
    static int puertoServer;
    static int puertoServerReal;
    
    
    public static void main(String args[]) throws UnknownHostException {
        System.setProperty("java.net.preferIPv4Stack", "true");
        int c = 5;
        ipCliente = InetAddress.getLocalHost();
        inicializarRMI();
        ventanaTopos vent = new ventanaTopos();
        vent.setVisible(true);
        int topo = esperaTopo();
        
        while(true){
            vent.cambiarTitulo("Bienvenido al juego");
            if(topo<13){
                vent.poblarTopoInd(topo);
            while(!vent.flag){
                System.out.print("");
            }
            vent.resetFlag();
            topoMachucado();
            topo=esperaTopo();
            }
            else{
                if(topo==13){
                    vent.cambiarTitulo("Ya hay un ganador");
                }
                else{
                    System.out.println("Esto está pendiente");
                }
                topo=esperaTopo();
            }
        }
        
        
    }
    private static void inicializarRMI(){
        //El cliente inicia una sesión por medio de RMI y obtiene los puertos y la ip por la que recibirá al topo.
        System.setProperty("java.security.policy","/Users/diego/whack-a-mole/topos/src/server/server.policy");
        
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "Compute";
            
            Registry registry = LocateRegistry.getRegistry("10.6.75.115"); // server's ip address args[0]
            Compute comp = (Compute) registry.lookup(name);
            comp.crearSesion(ipCliente);
            ipServer=comp.obtenerIP(ipCliente);
            String prueba=comp.prueba();
            puertoServer=comp.obtenerPuerto(ipCliente);
            puertoServerReal=comp.obtenerPuertoReal(ipCliente);
            System.out.println(prueba);
          
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
                InetAddress group = ipServer; // destination multicast group 
	    	s = new MulticastSocket(puertoServer);
	   	s.joinGroup(group); 

	    	byte[] buffer = new byte[1000];

                System.out.println("Waiting for messages");
                DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
                s.receive(messageIn);
                if(messageIn.getData()[1]==0)
                    topo = messageIn.getData()[0] - 48;
                else{
                    topo = messageIn.getData()[1] - 38;
                }
                System.out.println("Se recibió el topo: " + topo + " desde la direccion" + messageIn.getAddress() + "lo mando el servidor y lo recibió el cliente");
  	     	ipServerReal = messageIn.getAddress();
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
                s = new Socket(ipServerReal, puertoServerReal);  
		DataInputStream in = new DataInputStream( s.getInputStream());
		DataOutputStream out = new DataOutputStream( s.getOutputStream());
		out.writeUTF(ipCliente.getHostAddress());        	// UTF is a string encoding 	      
                System.out.println("Envía mensaje de pegarle al topo") ;      
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
}
