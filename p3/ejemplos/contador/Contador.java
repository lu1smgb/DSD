// Implementación de la clase remota

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.net.MalformedURLException;

public class Contador extends UnicastRemoteObject implements Contador_I {
    
    private int suma;

    // Constructor
    public Contador() throws RemoteException {

    }

    // Devuelve el estado del objeto
    public int sumar() throws RemoteException {
        return suma;
    }

    // Establece un valor al objeto
    public void sumar(int valor) throws RemoteException {
        suma = valor;
    }

    // Incrementa en uno el valor del estado y despues lo devuelve
    public int incrementar() throws RemoteException {
        suma++;
        return suma;
    }

}
