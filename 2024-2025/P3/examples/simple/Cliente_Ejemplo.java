import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@SuppressWarnings({"removal", "deprecation"})
public class Cliente_Ejemplo {
    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            Registry registry = LocateRegistry.getRegistry(args[0]);
            System.out.println("Buscando el objeto remoto");
            String nombre_objeto_remoto = "MiObjeto";
            Ejemplo_I instancia_local = (Ejemplo_I) registry.lookup(nombre_objeto_remoto);
            System.err.println("Invocando objeto remoto");
            instancia_local.escribir(Integer.parseInt(args[1]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
