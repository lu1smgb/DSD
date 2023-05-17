/*
 * Desarrollo de Sistemas Distribuidos
 * Práctica 3 - Java RMI
 * Fichero: Cliente.java
 * 
 * Autor: Luis Miguel Guirado Bautista
 * Universidad de Granada
 * Curso 2022/2023
 */

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Cliente {

    public static void main(String[] args) {
        
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {

            Scanner input = new Scanner(System.in);
            // Host de referencia del cliente
            String host = args[0];
            // Entidad del cliente
            String entidad = args[1];
            // Identificador de la replica al que hace referencia para el resto de consultas
            int id_replica = Integer.parseInt(args[2]);
            // Opcion escogida en el menú
            int opcion = 0;

            System.out.println("Conectando con el servidor...");
            Registry reg = LocateRegistry.getRegistry(host);

            System.out.print("Es una entidad nueva / desea registrarse? 0 -> [NO] / 1 -> SI: ");
            opcion = Integer.parseInt(input.nextLine());

            // Primero hacemos referencia a la replica que especifico el cliente
            ICliente stub = (ICliente) reg.lookup(Replica._SUFFIX + id_replica);

            // Si desea registrarse...
            if (opcion > 0) {
                // Registramos la entidad y cambiamos el id de referencia
                try {
                    System.out.println("Registrando...");
                    id_replica = stub.Registrar(entidad);
                    System.out.println(entidad + " ha quedado registrado en " + Replica._SUFFIX + id_replica);
                    System.out.println(
                        "Deberá referirse a esta réplica en futuras conexiones para consultar el total donado");
                    stub = (ICliente) reg.lookup(Replica._SUFFIX + id_replica);
                }
                catch (Exception e) {
                    System.err.println("Error al hacer el registro:");
                    System.err.println(e.getMessage());
                    System.exit(-1);
                }
            }

            System.out.println("Bienvenid@ " + entidad + "!");

            while (true) {
                // Menú principal
                System.out.println("\t--- Menu principal ---");
                System.out.println("\t1. Donar");
                System.out.println("\t2. Consultar total donado");
                System.out.println("\t3. Cambiar replica de referencia");
                System.out.println("\t4. Salir");
                System.out.print("Elige una opcion: ");
                opcion = Integer.parseInt(input.nextLine());

                switch (opcion) {
                    case 1:
                        // El cliente dona una cantidad especificada a la replica de referencia
                        try {
                            System.out.print("Cantidad a donar: ");
                            double donativo = Double.parseDouble(input.nextLine());
                            stub.Donar(entidad, donativo);
                            System.out.println("Has donado " + donativo + " correctamente");
                        }
                        catch (InputMismatchException e) {
                            System.out.println("Entrada inválida, inténtelo de nuevo");
                        }
                        catch (Exception e) {
                            System.err.println(e.getMessage());
                        }
                        break;

                    case 2:
                        
                        // El cliente consulta el total donado
                        try {
                            double cantidad = stub.TotalDonado(entidad);
                            System.out.println("Cantidad acumulada total: " + cantidad);
                        }
                        catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        break;

                    case 3:

                        // Cambiamos la replica de referencia del cliente
                        LocalizacionReplica nueva_localizacion = new LocalizacionReplica();

                        // Actualizamos el host y posteriormente el registro rmi
                        try {
                            System.out.print("Host de la nueva replica: ");
                            nueva_localizacion.SetHost(input.nextLine());
                            reg = LocateRegistry.getRegistry(nueva_localizacion.GetHost());
                        }
                        catch (InputMismatchException e) {
                            System.err.println("Entrada no válida, inténtelo de nuevo");
                            break;
                        }
                        catch (Exception e) {
                            System.err.println("No se ha podido localizar el nuevo host, intentelo de nuevo");
                            break;
                        }

                        // Actualizamos el identificador y posteriormente el stub
                        try {
                            System.out.print("Identificador de la nueva replica: ");
                            nueva_localizacion.SetId(Integer.parseInt(input.nextLine()));
                            stub = (ICliente) reg.lookup(Replica._SUFFIX + nueva_localizacion.GetId());
                            System.out.println(
                                "Replica actualizada correctamente: " + nueva_localizacion.GetHost() + ":" +
                                    Replica._SUFFIX + nueva_localizacion.GetId());
                        }
                        catch (InputMismatchException e) {
                            System.err.println("Entrada no válida, intentelo de nuevo");
                        }
                        catch (Exception e) {
                            System.err.println("No se ha podido encontrar la replica, intentelo de nuevo");
                        }
                        break;

                    case 4:

                        // Cerramos el programa
                        System.out.println("Hasta pronto!");
                        input.close();
                        System.exit(0);

                    default:

                        // Si la opción es inválida, no se hace nada
                        System.out.println("Opción no válida, intentelo de nuevo");
                        break;
                    
                }
            }
        }
        catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            System.exit(-1);
        }
        System.exit(0);
    }
}
