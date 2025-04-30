import java.rmi.Remote;
import java.rmi.RemoteException;

@SuppressWarnings({"removal", "deprecation"})
public interface Ejemplo_I extends Remote {
    public void escribir(int id_proceso) throws RemoteException;
}