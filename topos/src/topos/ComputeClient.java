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
import interfaces.Compute;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.net.InetAddress;

public class ComputeClient {
    static InetAddress ipCliente;
    static InetAddress ipServer;
    static InetAddress ipServerReal;
    static int puertoServer;
    static int puertoServerReal;
    static String ganador;
    public ComputeClient(){
    }
    public long estresa() throws UnknownHostException{
        long Tiempo =System.currentTimeMillis();
        System.setProperty("java.net.preferIPv4Stack", "true");
        inicializarRMI();
        
        Tiempo=System.currentTimeMillis()-Tiempo;
            
            return Tiempo;
       }
    
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
            System.out.println(topo);
            if(topo<13){
                    vent.cambiarTitulo("El juego ya ha comenzado");
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
                    ganador = ganador.substring(2,ganador.indexOf("#"));
                    if(ganador.equals(ipCliente.getHostAddress()))
                        vent.cambiarTitulo("Ya hay un ganador y eres tú");
                    else
                        vent.cambiarTitulo("Ya hay un ganador y es: "+ganador);        
                }
                else{
                    System.out.println("Esto está pendiente");
                }
                topo=esperaTopo();
            }
        }
        
        
    }
    public static void inicializarRMI(){
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
            String prueba=comp.prueba();
            puertoServer=comp.obtenerPuerto(ipCliente);
            puertoServerReal=comp.obtenerPuertoReal(ipCliente);
            System.out.println(prueba);
        } catch (Exception e) {
            System.err.println("exception");
            e.printStackTrace();
        }
    }
    public static int esperaTopo(){
    //Mientras el juego siga activo espera a un topo.
        int topo=0;
        String rec;
        MulticastSocket s =null;
   	 try {
                InetAddress group = ipServer; // destination multicast group 
	    	s = new MulticastSocket(puertoServer);
	   	s.joinGroup(group); 

	    	byte[] buffer = new byte[1000];

                System.out.println("Waiting for messages");
                DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
                s.receive(messageIn);
                String datos=new String(messageIn.getData());
                
                if(datos.substring(0,2).equals("13")){
                    topo = 13;
                    rec = new String(messageIn.getData());
                    ganador=rec;
                    System.out.println(rec);
                    
                }
                else{
                    topo = Integer.parseInt(datos.substring(0,datos.indexOf("#")));
                    System.out.println(topo);
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
    public static void topoMachucado() {
       Socket s = null;
	    try {
                s = new Socket(ipServerReal, puertoServerReal);  
		DataInputStream in = new DataInputStream( s.getInputStream());
		DataOutputStream out = new DataOutputStream( s.getOutputStream());
		out.writeUTF(ipCliente.getHostAddress());        	// UTF is a string encoding 	          
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
