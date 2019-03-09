
//
//
// indicate the location of security policies.
//



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;


import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import interfaces.Compute;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class ComputeServer implements Compute {
    //Método que determina si alguien llego a 4 partidas ganadas.
    private static boolean nadieHaGanao() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    //incrementa el score del ganador.
    private static void puntoPara() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    ArrayList<String> direccionesIP = new ArrayList();
    int[] score= new int[10];
    int jugadoresActivos;
    String ipServer="";
    int puertoServer=0;
    
    public ComputeServer() throws RemoteException
    {
        super();
    }

    
  

    public static void main(String[] args) {

       //Aquí se inicia el RMI
        
        System.setProperty("java.security.policy","/Users/jeroaranda/Documents/ITAM/DECIMO/SISTEMAS DISTR./proyecto01/whack-a-mole/topos/src/server/server.policy");

        if (System.getSecurityManager() == null) {

            System.setSecurityManager(new SecurityManager());
        }
        try {

            // start the rmiregistry 
            LocateRegistry.createRegistry(1099);   /// default port
            
            // if the rmiregistry is not started by using java code then
                // 1) Start it as follows: rmiregistry -J-classpath -J"c:/Users/jgutierrgarc/Documents/NetBeansProjects/JavaRMI/dist/javaRMI.jar" or 
                // 2) Add this to the classpath C:\Users\jgutierrgarc\Documents\NetBeansProjects\JavaRMI\dist\javaRMI.jar and then start the rmiregistry 
                        
            String name = "Compute";
            ComputeServer engine = new ComputeServer();
            Compute stub =
                (Compute) UnicastRemoteObject.exportObject(engine, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            
            System.out.println("ComputeEngine bound");
        } catch (Exception e) {
            System.err.println("ComputeEngine exception:");
            e.printStackTrace();
        }
        //Aquí se inicia el envío de topos, es decir el comienzo del juego.
        while(nadieHaGanao()){
            //Se envía por multicast el topo;
            MulticastSocket s =null;
            try {
                   InetAddress group = InetAddress.getByName("228.5.6.7"); // destination multicast group 
                   s = new MulticastSocket(6789);
                   s.joinGroup(group); 
                   //s.setTimeToLive(10);
                   System.out.println("Messages' TTL (Time-To-Live): "+ s.getTimeToLive());
                   String myMessage="Hello";
                   byte [] m = myMessage.getBytes();
                   DatagramPacket messageOut = 
                           new DatagramPacket(m, m.length, group, 6789);
                   s.send(messageOut);

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
           //Se espera la respuesta de la computadora ganadora y se incrementa el score del ganador, posteriormente se reinicia el juego.
            
            puntoPara();
            
            
            
            
            
            
        }     
    }
    
    
   @Override
    public void crearSesion(String ipCliente) throws RemoteException {
           if(!direccionesIP.contains(ipCliente)){
               direccionesIP.add(ipCliente);
               jugadoresActivos++;
           }
    }

    @Override
    public String obtenerIP(String ipCliente) throws RemoteException {
        if(direccionesIP.contains(ipCliente))
            return ipServer;
        return null;
    }

    @Override
    public int obtenerPuerto(String ipCliente) throws RemoteException {
        if(direccionesIP.contains(ipCliente))
            return puertoServer;
        return -1;
    }


}