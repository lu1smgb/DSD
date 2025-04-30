import java.rmi.Naming;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class Servidor {
    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {

            Registry reg = LocateRegistry.createRegistry(1099);
            Contador contador = new Contador();
            Naming.rebind("MiContador", contador);
            System.out.println("Servidor listo");

        } 
        catch (RemoteException | MalformedURLException e) {
            System.err.println("Exception: " + e.getMessage());
        }
    }
}
