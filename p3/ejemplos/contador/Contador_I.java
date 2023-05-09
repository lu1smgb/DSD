// Interfaz remota que implementa nuestra clase

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Contador_I extends Remote {
    int sumar() throws RemoteException;
    void sumar(int valor) throws RemoteException;
    public int incrementar() throws RemoteException;
}
