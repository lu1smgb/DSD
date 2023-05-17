/*
 * Desarrollo de Sistemas Distribuidos
 * Práctica 3 - Java RMI
 * Fichero: Servidor.java
 * 
 * Autor: Luis Miguel Guirado Bautista
 * Universidad de Granada
 * Curso 2022/2023
 */

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Servidor {
    public static void main(String[] args) {
        
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {

            // Verificamos el numero de replica
            String host = args[0];
            Integer num_replicas = Integer.parseInt(args[1]);
            if (num_replicas < 2 || args.length != 2) {
                num_replicas = 2;
            }

            // Inicializamos las replicas
            System.out.println("Inicializando " + num_replicas + " replicas");
            Registry reg = LocateRegistry.getRegistry(host);
            for (int i=0; i < num_replicas; i++) {
                
                Replica replica = new Replica(host, i+1);
                ICliente stub = (ICliente) UnicastRemoteObject.exportObject(replica, 0);
                reg.rebind(replica.nombreReplica(), stub);
                System.out.println(replica.nombreReplica() + " inicializado");

            }

            System.out.println("Listo para escuchar");

        }
        catch (Exception e) {
            System.err.println("Excepcion: " + e);
            System.exit(-1);
        }

    }
}
