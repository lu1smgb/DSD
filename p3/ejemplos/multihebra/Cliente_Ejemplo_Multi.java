import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Cliente_Ejemplo_Multi implements Runnable {

    public String nombre_objeto = "manolo";
    public String server;

    // Constructor del cliente
    public Cliente_Ejemplo_Multi(String server) {
        this.server = server;
    }

    public void run() {
        System.out.println("Buscando el objeto remoto");
        try {
            Registry registry = LocateRegistry.getRegistry(server);
            Ejemplo_I instancia_local = (Ejemplo_I) registry.lookup(nombre_objeto);
            System.out.println("Invocando el objeto remoto");
            instancia_local.escribir_mensaje(Thread.currentThread().getName());
        } catch (Exception e) {
            System.err.println("Error: ");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        // Leemos el numero de hebras a crear en los argumentos
        int n_hebras = Integer.parseInt(args[1]);

        // Creamos un array de referencias al objeto y un array de hebras
        Cliente_Ejemplo_Multi[] v_clientes = new Cliente_Ejemplo_Multi[n_hebras];
        Thread[] v_hebras = new Thread[n_hebras];

        for (int i=0; i < n_hebras; i++) {
            // Inicializamos cada una de las referencias
            v_clientes[i] = new Cliente_Ejemplo_Multi(args[0]);
            // Inicializamos cada una de las hebras y las iniciamos
            v_hebras[i] = new Thread(v_clientes[i], "Cliente " + i);
            v_hebras[i].start();
        }
    }
}
