import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

@SuppressWarnings({"removal", "deprecation"})
public class Ejemplo implements Ejemplo_I {
    
    public void escribir(String mensaje) {
        System.out.println("Entra hebra " + mensaje);
        if (mensaje.endsWith("0")) {
            try {
                System.out.println("Dormimos");
                Thread.sleep(5000);
                System.out.println("Despertamos");
            }
            catch (Exception e) {
                System.err.println("Error");
                e.printStackTrace();
            }
        }
        System.out.println("Sale hebra " + mensaje);
    }

    public static void main(String args[]) {
        // Instalacion del gestor de seguridad
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {

            // Instancia
            Ejemplo_I prueba = new Ejemplo();

            // Puerto anonimo para atender peticiones
            Ejemplo_I stub = (Ejemplo_I) UnicastRemoteObject.exportObject(prueba, 0);

            // Registro de objetos remotos
            Registry registry = LocateRegistry.getRegistry();

            // Nombre del objeto remoto
            String nombre_objeto_remoto = "MiObjeto";

            // Registramos el nombre para registrar a la clase en el registro
            registry.rebind(nombre_objeto_remoto, stub);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}