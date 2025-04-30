import java.rmi.registry.LocateRegistry;
import java.rmi.*;
import java.rmi.registry.Registry;

public class Cliente {
    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            
            Registry reg = LocateRegistry.getRegistry("127.0.0.1", 1099);
            IContador contador = (IContador) reg.lookup("MiContador");
            System.out.println("Contador a 0");
            contador.sumar(0);
            long horaComienzo = System.currentTimeMillis();
            System.out.println("Incremento...");
            for (int i=0; i < 1000; i++) {
                contador.incrementar();
            }
            long horaFin = System.currentTimeMillis();
            float media = (horaFin - horaComienzo) / 1000f;
            System.out.println("Media de RMI realizadas = " + media + " ms");
            System.out.println("RMI realizadas = " + contador.sumar());
        } catch (NotBoundException | RemoteException e) {
            System.err.println("Exception e: " + e);
        }
        System.exit(0);
    }
}
