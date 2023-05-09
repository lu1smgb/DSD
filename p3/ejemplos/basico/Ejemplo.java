import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Ejemplo implements Ejemplo_I {

    // Implementación de la función especificada por la interfaz
    public void escribir_mensaje(int id_proceso) {
        System.out.println("Recibida petición de proceso: " + id_proceso);
        if (id_proceso == 0) {
            try {
                System.out.println("Empezamos a dormir");
                Thread.sleep(5000);
                System.out.println("Terminamos de dormir");
            }
            catch (Exception e) {
                System.err.println("Error: ");
                e.printStackTrace();
            }
        }
    }

    // Función principal de la clase (actúa como servidor)
    public static void main(String[] args) {

        // Inicializamos el security manager (Java <16)
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            // Creamos un nuevo objeto remoto y le abrimos un puerto (anónimo = 0)
            Ejemplo_I prueba = new Ejemplo();
            Ejemplo_I stub = (Ejemplo_I) UnicastRemoteObject.exportObject(prueba, 0);

            // Creamos un registro de objetos remotos
            Registry registry = LocateRegistry.getRegistry();

            // Asignamos un nombre a nuestro objeto
            String nombre_obj_remoto = "manolo";
            registry.rebind(nombre_obj_remoto, stub);

            System.out.println("Ejemplo bound");
        }
        // Es fundamental manejar los errores
        catch (Exception e) {
            System.err.println("Error: ");
            e.printStackTrace();
        }
    }
}
