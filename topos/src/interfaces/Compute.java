package interfaces;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author JGUTIERRGARC
 */
import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Compute extends Remote {
	// Calculate the square of a number
    public void crearSesion (InetAddress ipCliente) throws RemoteException;
    public InetAddress obtenerIP (InetAddress ipCliente) throws RemoteException;
    public int obtenerPuerto (InetAddress ipCliente) throws RemoteException;
    public String prueba () throws RemoteException;
    public int obtenerPuertoReal(InetAddress ip) throws RemoteException;
}

