import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Ejemplo implements Ejemplo_I {
    //* Opcionalmente podemos incluir la keyword 'synchronized'
    public void escribir_mensaje(String mensaje) {
        //! Ponemos a dormir los mensajes (id_proceso) que terminen en "0"
        if (mensaje.endsWith("0")) {
            try {
                System.out.println("[" + mensaje + "]" + "Empezamos a dormir");
                Thread.sleep(1000);
                System.out.println("[" + mensaje + "]" + "Terminamos de dormir");
            }
            catch (Exception e) {
                System.err.println("Error: ");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            Ejemplo_I prueba = new Ejemplo();
            Ejemplo_I stub = (Ejemplo_I) UnicastRemoteObject.exportObject(prueba, 0);
            Registry registry = LocateRegistry.getRegistry();
            String nombre_obj_remoto = "manolo";
            registry.rebind(nombre_obj_remoto, stub);
            System.out.println("Ejemplo bound");
        }
        catch (Exception e) {
            System.err.println("Error: ");
            e.printStackTrace();
        }
    }
}
