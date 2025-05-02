/*

    Desarrollo de Sistemas Distribuidos
    Practica 3 - RMI

    Luis Miguel Guirado Bautista
    Curso 2024/2025
    Universidad de Granada

	Codigo del cliente

*/

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Cliente {

    static Map<Integer, String> opciones = new HashMap<>(); static {
        opciones.put(1, "Registrarse");
        opciones.put(2, "Donar");
        opciones.put(3, "Obtener total donado");
        opciones.put(4, "Obtener lista de donantes");
        opciones.put(5, "Obtener historial");
        opciones.put(6, "Salir");
    }

    public static void imprimirOpciones(Map<Integer, String> opciones) {
        System.out.println("--------------- Menu ---------------");
        opciones.entrySet().stream()
            .forEach(e -> System.out.println(e.getKey() + ". " + e.getValue()));
        System.out.println("------------------------------------");
    }

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {

            Registry registry = LocateRegistry.getRegistry(args[0]);
            Scanner scanner = new Scanner(System.in);
            System.out.print("Indica el numero de servidor al que quieres conectarte: ");
            int numeroServidor = scanner.nextInt();
            scanner.nextLine();
            ICliente servidor = (ICliente) registry.lookup("SERVIDOR " + numeroServidor);
            System.out.println("Conectado al servidor");
            System.out.print("Indica tu nombre de entidad: ");
            String nombreEntidad = scanner.nextLine();

            
            int opcion = -1;
            while (opcion != 6) {
                imprimirOpciones(opciones);
                System.out.print("Selecciona una opcion: ");
                opcion = scanner.nextInt();
                switch (opcion) {
                    case 1:
                        if (servidor.registrar(nombreEntidad)) {
                            System.out.println("El registro se ha realizado correctamente");
                            System.out.println("Ya puedes donar, despues podras consultar el total donado y la lista de donantes");
                        }
                        else {
                            System.out.println("No se ha podido realizar el registro, puede ser por que ya este registrado, si no lo esta, intentelo de nuevo");
                        }
                        break;
                    case 2:
                        System.out.print("Cantidad: ");
                        int cantidad = scanner.nextInt();
                        if (servidor.depositar(nombreEntidad, cantidad)) {
                            System.out.println("La donacion se ha realizado correctamente");
                            System.out.println("Ya puedes consultar el total donado y la lista de donantes");
                        }
                        else {
                            System.out.println("Ha ocurrido un error, puede ser que no este registrado, si lo esta, intentelo de nuevo");
                        }
                        break;
                    case 3:
                        Integer total = servidor.obtenerTotalDonado(nombreEntidad);
                        if (total == null) {
                            System.err.println("No se ha podido obtener el total donado, prueba a realizar una primera donacion o a registrarse si no lo esta");
                            break;
                        }
                        System.out.println("Total donado: $" + total);
                        break;
                    case 4:
                        Map<String, Integer> donantes = servidor.obtenerDonantes(nombreEntidad);
                        if (donantes == null) {
                            System.err.println("No se ha podido obtener el total donado, prueba a realizar una primera donacion o a registrarse si no lo esta");
                            break;
                        }
                        System.out.println("----- Lista de donantes -----");
                        donantes.entrySet().stream()
                            .filter( e -> e.getValue() > 0 )
                            .sorted( Comparator.comparing(Map.Entry<String, Integer>::getValue).reversed() )
                            .forEach( e -> System.out.println(e.getKey() + " -> $" + e.getValue()) );
                        System.out.println("-----------------------------");
                        break;
                    case 5:
                        Set<Transaccion> historial = servidor.obtenerHistorial(nombreEntidad);
                        if (historial == null || historial.isEmpty()) {
                            System.err.println("No se ha podido obtener el historial, registrese, haga una donacion o intentelo de nuevo");
                            break;
                        }
                        System.out.println("----- Historial -----");
                        historial.stream()
                            .sorted(Comparator.comparing(Transaccion::getFecha))
                            .forEach(System.out::println);
                        System.out.println("---------------------");
                        break;
                    case 6:
                        System.out.println("Adios! :)");
                        break;
                    default:
                        System.out.println("Opcion no valida");
                        break;
                }
            }

            scanner.close();
        }
        catch (NotBoundException e) {
            System.err.println("No se ha podido localizar el servidor");
            e.printStackTrace();
            System.exit(-1);
        }
        catch (RemoteException e) {
            System.err.println("No se ha podido conectar al conjunto de servidores");
            e.printStackTrace();
            System.exit(-1);
        }
        catch (Exception e) {
            System.err.println("Exception: ");
            e.printStackTrace();
            System.exit(-1);
        }
        System.exit(0);
    }

}
