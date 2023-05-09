import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class Cliente {
    public static void main(String[] args) {
        // Establece un SecurityManager si no hay uno ya establecido
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            // Localizamos el registro del servidor y obtenemos el objeto remoto
            Registry reg = LocateRegistry.getRegistry("localhost");
            Contador_I mi_contador = (Contador_I) reg.lookup("mi_contador");

            // Establecemos el valor del contador a 0
            System.out.println("Poniendo contador a 0");
            mi_contador.sumar(0);

            // Empezamos a medir el tiempo
            long hora_comienzo = System.currentTimeMillis();

            // Enviamos 1000 solicitudes de incremento
            System.out.println("Incrementando...");
            for (int i=0; i < 1000; i++) {
                mi_contador.incrementar();
            }

            // Terminamos de medir
            long hora_fin = System.currentTimeMillis();
            System.out.println("Media de las RMI realizadas = " 
                               + ((hora_fin - hora_comienzo) / 1000f)
                               + " ms");
            System.out.println("RMI realizadas = " + mi_contador.sumar());
        }
        catch (NotBoundException | RemoteException e) {
            System.err.println("Exception del sistema: " + e);
        }
        System.exit(0);
    }
}
