import java.rmi.Remote;
import java.rmi.RemoteException;

// Interfaz que especifica las funciones del objeto a las que tendremos acceso desde fuera
// Especificaremos que es una interfaz que da lugar a clases remotas
public interface Ejemplo_I extends Remote {

    // Es obligatorio que especifiquemos que los metodos puedan lanzar una RemoteException
    public void escribir_mensaje(int id_proceso) throws RemoteException;

}