package interfaces;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author JGUTIERRGARC
 */
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Compute extends Remote {
	// Calculate the square of a number
    public void crearSesion (String ipCliente) throws RemoteException;
    public String obtenerIP (String ipCliente) throws RemoteException;
    public int obtenerPuerto (String ipCliente) throws RemoteException;
}

