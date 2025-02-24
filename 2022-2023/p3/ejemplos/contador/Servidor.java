import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;

public class Servidor {
    public static void main(String[] args) {
        // Establece un SecurityManager si no hay uno establecido
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            // Crea un registro de objetos remotos
            Registry reg = LocateRegistry.createRegistry(1100);

            // Creamos el objeto remoto
            Contador mi_contador = new Contador();
            Naming.rebind("mi_contador", mi_contador);

            System.out.println("Servidor RemoteException | MalformedURLException preparado");
        }
        catch (RemoteException | MalformedURLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
