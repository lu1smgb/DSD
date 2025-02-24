import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Cliente_Ejemplo {
    public static void main(String[] args) {

        // Inicializamos el security manager (Java <16)
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            // Localizamos el registro de objetos remotos del servidor especificado
            Registry registry = LocateRegistry.getRegistry(args[0]);

            // Busca el objeto en el registro por su nombre y lo almacenamos en una variable
            System.out.println("Buscando el objeto remoto");
            String nombre_objeto_remoto = "manolo";
            Ejemplo_I instancia_local = (Ejemplo_I) registry.lookup(nombre_objeto_remoto);

            // Finalmente podemos llamar a sus funciones
            System.out.println("Invocando el objeto remoto");
            instancia_local.escribir_mensaje(Integer.parseInt(args[1]));
        }
        // Debemos manejar los errores que puedan producirse
        catch (Exception e) {
            System.err.println("Error: ");
            e.printStackTrace();
        }
    }
}
