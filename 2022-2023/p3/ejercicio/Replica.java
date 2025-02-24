/*
 * Desarrollo de Sistemas Distribuidos
 * Práctica 3 - Java RMI
 * Fichero: Replica.java
 * 
 * Autor: Luis Miguel Guirado Bautista
 * Universidad de Granada
 * Curso 2022/2023
 */

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Replica implements IServidor, ICliente {

    public static final String _SUFFIX = "Replica_";
    private LocalizacionReplica info;
    private HashMap<String, Double> donaciones;
    private static HashSet<LocalizacionReplica> replicas = null;
    
    // CONSTRUCTOR
    public Replica(String host, int id) throws RemoteException {

        try {
            this.info = new LocalizacionReplica(host, id);
            this.donaciones = new HashMap<String, Double>();
            if (replicas == null) {
                replicas = new HashSet<LocalizacionReplica>();
            }
            replicas.add(this.info);

        }
        catch (Exception e) {
            System.err.println("Error al construir la replica: " + e.toString());
        }

    }

    // INTERFAZ SERVIDOR SERVIDOR
    // Obtiene el conjunto de entidades registradas en la replica local
    public ArrayList<String> GetEntidades() {
        
        return new ArrayList<String>(this.donaciones.keySet());

    }

    // FUNCION PRIVADA
    // Olvida un servidor de forma global
    // Esta funcion se usa cuando una replica ya no esta disponible
    // por que se ha cerrado/destruido
    private void Olvidar(LocalizacionReplica loc) {

        replicas.remove(loc);
        System.out.println("Se ha olvidado la replica " + loc);

    }

    // FUNCION PRIVADA
    // Localiza una entidad en el conjunto de replicas
    private LocalizacionReplica Localizar(String entidad) {

        // Localizacion a devolver cuando termine la funcion
        LocalizacionReplica ret = null;
        // Ultima localizacion visitada para que
        // en caso de que nos topemos con una excepcion podamos tratarla
        LocalizacionReplica ultima_loc = null;

        System.out.println("Localizando entidad " + entidad);
        try {

            // Para cada replica conocida
            for (LocalizacionReplica loc : replicas) {

                String host = loc.GetHost();
                String nombre_replica = _SUFFIX + loc.GetId();
                ultima_loc = loc;
                
                // Comprobamos el registro de su host
                System.out.println("Comprobando rmiregistry de " + host);
                Registry reg = LocateRegistry.getRegistry(host);

                // Comprobamos que la replica exista en el host
                System.out.println(
                    "Comprobando si existe la replica " + host + ":" + nombre_replica);
                IServidor replica = (IServidor) reg.lookup(nombre_replica);

                // Comprobamos si la entidad esta registrada en la replica
                System.out.println(
                    "Comprobando si la entidad se encuentra registrada en " + host + ":" + nombre_replica);
                if (replica.GetEntidades().contains(entidad)) {
                    System.out.println(
                        "Se ha localizado a " + entidad + " en " + host + ":" + nombre_replica);
                    ret = loc;
                    break;
                }

                if (ret == null) {
                    System.out.println("No se ha encontrado a " + entidad + " en el conjunto de replicas");
                    ret = new LocalizacionReplica();
                }

            }

        } catch (RemoteException e) {

            System.err.println("Error remoto:" + e.getMessage());
            return new LocalizacionReplica();

        } catch (NotBoundException e) {

            System.err.println(
                "Se intentó consultar un servidor que actualmente no existe (" + ultima_loc + ")");
            Olvidar(ultima_loc);
            return new LocalizacionReplica();

        } catch (Exception e) {

            System.err.println("Error al localizar " + entidad + " en " + this);
            return new LocalizacionReplica();

        }

        return ret;

    }

    // FUNCION PRIVADA
    // Comprueba que una entidad se encuentra en el conjunto de las replicas
    private boolean EstaRegistrado(String entidad) {

        System.out.println("Comprobando si " + entidad + " esta registrado");
        LocalizacionReplica loc = Localizar(entidad);
        boolean condicion = loc.GetHost() != "null";
        return condicion;

    }

    // INTERFAZ SERVIDOR SERVIDOR
    // Añade una entidad al servidor local
    // siempre que la entidad no se encuentra registrada en el conjunto de replicas
    public void AniadirEntidad(String entidad) {

        if (this.donaciones.containsKey(entidad)) {
            System.out.println(entidad + " ya se encuentra registrada en " + this);
        }
        else {
            this.donaciones.put(entidad, 0.0);
            System.out.println(
                "Se ha añadido la entidad " + entidad + " a la replica " + this);
        }

    }

    // INTERFAZ SERVIDOR SERVIDOR
    // Añade una cantidad donada a la entidad especificada
    // Es necesario que la entidad se encuentre registrada en el servidor local
    public void AniadirCantidad(String entidad, double cantidad) {

        double valor_antiguo = this.donaciones.get(entidad);
        double valor_nuevo = valor_antiguo + cantidad;
        System.out.println("Actualizando valor donado de " + entidad + " a " + valor_nuevo);
        this.donaciones.replace(entidad, valor_nuevo);

    }

    // INTERFAZ CLIENTE SERVIDOR
    // Registra una entidad en el conjunto de replicas
    // Devuelve el id. de replica en el que se ha registrado
    /**
     *! NOTA: Tenia pensado que devolviera una estructura de datos, pero
     *! da error al desempaquetar los datos de salida, asi que solo devolvera
     *! el identificador de la replica donde quedo registrado,
     *! posiblemente limitando toda la practica a una sola maquina
     */
    public int Registrar(String entidad) throws Exception {

        // Localizacion en la que se realizará el registro y
        // ultima localizacion visitada anteriormente
        LocalizacionReplica ret = new LocalizacionReplica();
        LocalizacionReplica ultima_loc = null;

        // Comprobamos que la entidad no se encuentre registrada en el conjunto de replicas
        if (EstaRegistrado(entidad)) {
            System.out.println("La entidad ya se encuentra registrada en el conjunto de replicas, abortando");
            throw new Exception("Ya se encontraba registrado");
        }

        System.out.println("Registrando " + entidad + " en el conjunto de replicas");
        try {

            // --- Seleccionamos la replica con menos entidades registradas
            System.out.println("Escogiendo replica de registro para " + entidad);

            // Stub del servidor en el que registraremos a la entidad
            IServidor objetivo = null;
            // Aqui guardaremos el minimo de entidades
            int min_entidades = Integer.MAX_VALUE;
            // Localizacion del registro
            LocalizacionReplica loc_objetivo = null;

            // Iteramos por las replicas conocidas
            for (LocalizacionReplica loc : replicas) {

                String host = loc.GetHost();
                String nombre_replica = _SUFFIX + loc.GetId();
                ultima_loc = loc;
                // Obtenemos el registro de la maquina donde se aloja
                System.out.println("Comprobando rmiregistry de " + host);
                Registry reg = LocateRegistry.getRegistry(host);
                // Accedemos a la replica
                System.out.println(
                    "Comprobando si existe la replica " + host + ":" + nombre_replica);
                IServidor replica = (IServidor) reg.lookup(nombre_replica);
                // Si es menor que el minimo, actualizamos el stub y la localizacion de registro
                int num_entidades = replica.GetEntidades().size();
                if (num_entidades < min_entidades) {
                    min_entidades = num_entidades;
                    objetivo = replica;
                    loc_objetivo = loc;
                }

            }

            // --- Según si se ha encontrado un servidor idóneo...
            // Añadimos la entidad a la replica
            if (objetivo != null) {
                String nombre_objetivo = loc_objetivo.GetHost() + ":" + _SUFFIX + loc_objetivo.GetId();
                System.out.println("Registrando " + entidad + " en " + nombre_objetivo);
                objetivo.AniadirEntidad(entidad);
                ret = loc_objetivo;
            }
            // Si no se ha encontrado un servidor idóneo, se añade localmente
            else {
                System.err.println(
                    "No se ha encontrado un servidor idóneo, el registro se hará de forma local en " + this);
                this.AniadirEntidad(entidad);
                ret = this.info;
            }

        }
        catch (RemoteException e) {

            // En caso de que falle el obtener el registro o buscar el objeto
            System.err.println("Error remoto: " + e.getMessage());
            throw new RemoteException("Ha ocurrido un error inesperado");

        }
        catch (NotBoundException e) {
            
            // En caso de que el objeto no exista en el registro
            System.err.println(
                "Se intentó consultar un servidor que actualmente no existe (" + ultima_loc + ")");
            Olvidar(ultima_loc);
            throw new Exception("Ha ocurrido un error inesperado");

        }
        catch (Exception e) {

            System.out.println("Error al registrar " + entidad + " en " + this);
            throw new Exception("Ha ocurrido un error inesperado");

        }
        
        return ret.GetId();

    }

    // INTERFAZ CLIENTE SERVIDOR
    // Hace que una entidad done al conjunto de replicas una cantidad
    public void Donar(String entidad, double cantidad) throws Exception {

        // Aproximamos la entrada proporcionada a dos decimales y
        // comprobamos que la misma sea válida
        double cantidad_real = Math.round(cantidad * 100.0) / 100.0;
        System.out.println(entidad + " quiere donar " + cantidad_real + " en " + this);
        if (cantidad_real <= 0) {
            System.err.println(entidad + " introdujo una cantidad inválida (" + cantidad_real + "), abortando");
            throw new Exception("La cantidad introducida no es válida, inténtelo de nuevo");
        }

        // Localizamos la entidad en el conjunto de replicas
        LocalizacionReplica loc_entidad = Localizar(entidad);
        String host = loc_entidad.GetHost();
        String nombre_replica = _SUFFIX + loc_entidad.GetId();
        if (host == "null") {
            System.err.println(
                "No se ha podido localizar a " + entidad + " en el conjunto de replicas, abortando");
            throw new Exception(
                "No se ha podido localizar su entidad en el conjunto de réplicas, inténtelo de nuevo");
        }

        try {
            // Si todo va bien
            // Comprobamos las replicas dentro de la localizacion obtenida
            System.out.println("Comprobando el registro de " + host);
            Registry reg = LocateRegistry.getRegistry(host);
            // Obtenemos la replica donde esta registrado
            System.out.println("Comprobando si existe la replica " + nombre_replica);
            IServidor replica = (IServidor) reg.lookup(nombre_replica);
            // Y añadimos la cantidad en esa replica
            replica.AniadirCantidad(entidad, cantidad_real);

        }
        catch (RemoteException e) {

            // En caso de que falle el obtener el registro o buscar el objeto
            System.err.println("Error remoto: " + e.getMessage());

        }
        catch (NotBoundException e) {

            // En caso de que el objeto no exista en el registro
            System.err.println(
                "Se intentó consultar un servidor que actualmente no existe (" + host + ":" + nombre_replica + ")");
            Olvidar(this.info);

        }
        catch (Exception e) {

            System.err.println("Error al donar " + cantidad_real + " como " + entidad);

        }

    }

    // INTERFAZ SERVIDOR SERVIDOR
    // Obtiene la cantidad acumulada de la replica local
    public double GetAcumulado() {

        double acumulado = 0;

        // Iteramos en el map, acumulando las donaciones de las entidades
        Iterator<String> it = this.donaciones.keySet().iterator();
        while (it.hasNext()) {
            String it_entidad = it.next();
            double donacion = this.donaciones.get(it_entidad);
            acumulado += donacion;
        }

        System.out.println(this.nombreReplica() + " tiene " + acumulado);
        return acumulado;

    }

    // Obtiene la cantidad donada por el conjunto de replicas
    //! La operacion tendra que realizarse en la replica en la que ha quedado registrada la entidad
    public double TotalDonado(String entidad) throws Exception {

        double suma = 0;
        LocalizacionReplica ultima_loc = null;

        // Comprobamos que la entidad se encuentra registrada en la replica que usamos
        // para llamar a esta funcion
        System.out.println("Comprobando que " + entidad + " es apta para ver el total");
        if (!this.donaciones.containsKey(entidad)) {
            System.err.println("No se ha encontrado a " + entidad + " en " + this);
            throw new Exception(
                "No se ha encontrado su entidad en esta replica, por favor, refiérase a la que quedó registrada");
        }
        // Si la entidad esta registrada pero no ha realizado ningún depósito
        else if (this.donaciones.get(entidad) <= 0) {
            throw new Exception("Debe de haber realizado una donación antes de consultar el total");
        }

        try {

            System.out.println("Calculando total...");
            for (LocalizacionReplica loc : replicas) {
                ultima_loc = loc;
                Registry reg = LocateRegistry.getRegistry(loc.GetHost());
                IServidor replica = (IServidor) reg.lookup(_SUFFIX + loc.GetId());
                suma += replica.GetAcumulado();
            }
            System.out.println("Total obtenido: " + suma);

        }
        catch (RemoteException e) {

            System.err.println("Error remoto: " + e.getMessage());

        }
        catch (NotBoundException e) {

            System.err.println(
                "Se intentó consultar un servidor que actualmente no existe (" + ultima_loc + ")");
            Olvidar(ultima_loc);

        }
        catch (Exception e) {
            
            System.err.println(e.getMessage());

        }

        return suma;

    }

    @Override
    public String toString() {
        return this.info.GetHost() + ":" + this.nombreReplica();
    }

    public String nombreReplica() {
        return _SUFFIX + this.info.GetId();
    }

}