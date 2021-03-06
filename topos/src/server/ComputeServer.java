
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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class ComputeServer implements Compute {
    
    
     
    static ArrayList<InetAddress> direccionesIP = new ArrayList();
    static int[] score= new int[30];
    static int jugadoresActivos;
    static InetAddress ipServer;
    static int puertoServer=6789;
    static int puertoServerReal=7896;
    static int n=4;
    public ComputeServer() throws RemoteException
    {
        super();
    }
    //Método que determina si alguien llego a n partidas ganadas.
    private static boolean alguienHaGanao() {
         for (int i = 0; i < jugadoresActivos; i++) {
            if(score[i]==n){
                return true;
            }
        }
         return false;
    }
    
    private static String quiengano(){
        String res = "";
        for (int i = 0; i < jugadoresActivos; i++) {
            if(score[i]==n){
                res = direccionesIP.get(i).getHostAddress();
            }
        }
        return res;
    }
    
    private static void resetScores(){
        for (int i = 0; i < jugadoresActivos; i++){
            score[i] = 0;
        }
    }
    
    //incrementa el score del ganador.
    private static void puntoPara(String jugador) throws UnknownHostException {
        InetAddress dir = InetAddress.getByName(jugador);
        int id = direccionesIP.indexOf(dir);
        score[id]++;
    }
    private static void inicializaRMI(){
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
    }
    
    private static int getRand(int min, int max) {

            if (min >= max) {
                    throw new IllegalArgumentException("max must be greater than min");
            }

            Random r = new Random();
            return r.nextInt((max - min) + 1) + min;
    }
    
    public static Boolean enviarTopo(){
        //Se envía por multicast el topo;
            MulticastSocket s =null;
            Boolean respo = true;
            String ipgan = "";
            int aux;
            String resp;
            try {
                   InetAddress group = InetAddress.getByName("228.5.6.7"); // destination multicast group 
                   s = new MulticastSocket(puertoServer);
                   s.joinGroup(group); 
                   aux = (getRand(1,12));
                   if(aux<10)
                    resp = String.valueOf(aux)+"#";
                   else{
                    resp = String.valueOf(aux)+"#";
                   }
                   if(alguienHaGanao()){
                       ipgan = quiengano()+"#"; //Aquí necesitamos el ip del cliente ganador
                       resp = "13" + ipgan;// +ipgan + "#";                      
                   }

                   String myMessage=resp;
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
           return respo;
    }
    public static String esperaGanador() throws IOException{
        //Se espera la respuesta de la computadora ganadora y se incrementa el score del ganador, posteriormente se reinicia el juego.
        String ganador = "";
        ServerSocket listenSocket = new ServerSocket(puertoServerReal);
        try{
            listenSocket.setSoTimeout(7000);
            while(true) {
                System.out.println("Waiting for messages..."); 
                Socket clientSocket = listenSocket.accept();  // Listens for a connection to be made to this socket and accepts it. The method blocks until a connection is made. 
                Connection c = new Connection(clientSocket);
                c.start();
                ganador = c.in.readUTF();
                listenSocket.close();
                puntoPara(ganador);
                return ganador;  
            }
	}
        catch(IOException e) {
            //System.out.println(e.getMessage().equals("Accept timed out"));
            if(e.getMessage()!=null){
                //listenSocket.close();
                if(e.getMessage().equals("Accept timed out")){
                    listenSocket.close();
                }
            }
            //System.out.println("Listen :"+ e.getMessage());
        }
        return ganador;
    }
    @Override
    public void crearSesion(InetAddress ipCliente) throws RemoteException {
           if(!direccionesIP.contains(ipCliente)){
               direccionesIP.add(ipCliente);
               jugadoresActivos++;
           }
    }

    @Override
    public InetAddress obtenerIP(InetAddress ipCliente) throws RemoteException {
        if(direccionesIP.contains(ipCliente))
            return ipServer;
        return null;
    }
    
    public String prueba(){
        return "El rmi funciona";
    }

    @Override
    public int obtenerPuerto(InetAddress ipCliente) throws RemoteException {
        if(direccionesIP.contains(ipCliente))
            return puertoServer;
        return -1;
    }
    
        @Override
    public int obtenerPuertoReal(InetAddress ipCliente) throws RemoteException {
        if(direccionesIP.contains(ipCliente))
            return puertoServerReal;
        return -1;
    }
    
    public static void main(String[] args) throws UnknownHostException, IOException {
        System.setProperty("java.net.preferIPv4Stack", "true");
        ipServer = InetAddress.getByName("228.5.6.7");
        inicializaRMI();
        
        //Aquí se inicia el envío de topos, es decir el comienzo del juego.
        while(true){
            while(!alguienHaGanao()){
                //System.out.println(alguienHaGanao());
                enviarTopo();
                String ganador=esperaGanador();

            }
            enviarTopo();
            resetScores(); 
            //System.out.println(alguienHaGanao());
        }
        
    }

}
class Connection extends Thread {
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;
        
	public Connection (Socket aClientSocket) {
	    try {
		clientSocket = aClientSocket;
		in = new DataInputStream(clientSocket.getInputStream());
		out =new DataOutputStream(clientSocket.getOutputStream());
	     } catch(IOException e)  {System.out.println("Connection:"+e.getMessage());}
	}
        
        @Override
	public void run(){
	    try {			                 // an echo server
		String data = in.readUTF();	     
                System.out.println("Message received from: " + clientSocket.getRemoteSocketAddress());
		out.writeUTF(data);
	    } 
            catch(EOFException e) {
                System.out.println("EOF:"+e.getMessage());
	    } 
            catch(IOException e) {
                System.out.println("IO:"+e.getMessage());
	    } finally {
                try {
                    clientSocket.close();
                } catch (IOException e){
                    System.out.println(e);
                }
                }
            }
}
