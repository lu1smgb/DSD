/*

    Desarrollo de Sistemas Distribuidos
    Practica 3 - RMI

    Luis Miguel Guirado Bautista
    Curso 2024/2025
    Universidad de Granada

	Implementacion del servidor y las interfaces

*/

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Servidor implements ICliente, IServidor, Runnable {

    String host = "localhost";
    String id;
    Set<IServidor> vecinos = new HashSet<>();
    Map<String, Integer> registro = new HashMap<>();
    Set<Transaccion> transacciones = new HashSet<>();

    public Servidor(String id) {
        this.id = id;
    }

    public Servidor(String host, String id) {
        this(id);
        this.host = host;
    }

    // Obtiene el identificador del servidor
    @Override
    public String obtenerId() throws RemoteException {
        return id;
    }

    // Añade un servidor vecino al conjunto de vecinos de un servidor
    private void aniadirVecino(IServidor vecino) {
        vecinos.add(vecino);
    }

    // Obtiene el registro de un servidor
    @Override
    public Map<String, Integer> obtenerRegistro() throws RemoteException {
        return registro;
    }

    // Obtiene el conjunto local de transacciones de un servidor
    @Override
    public Set<Transaccion> obtenerTransacciones() throws RemoteException {
        return transacciones;
    }

    // Genera un mapa como clave el servidor y como valor la longitud del registro, 
    // o lo que es igual, el número de entidades registradas
    private Map<IServidor, Integer> obtenerLongitudesRegistros() throws RemoteException {
        Map<IServidor, Integer> longitudesRegistros = new HashMap<>();
        longitudesRegistros.put(this, this.registro.size());
        for (IServidor vecino : this.vecinos) {
            longitudesRegistros.put(vecino, vecino.obtenerRegistro().size());
        }
        return longitudesRegistros;
    }

    // Estima el mejor servidor donde realizar el registro de una entidad, 
    // es decir, devuelve el servidor con menos entidades registradas
    private IServidor estimarMejorServidor() throws Exception {
        return obtenerLongitudesRegistros().entrySet()
                .stream().min(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey).get();
    }

    // Genera un registro global a partir de los registros de cada uno de los servidores
    private Map<String, Integer> obtenerRegistroGlobal() throws RemoteException {
        Map<String, Integer> resultado = new HashMap<>();
        resultado.putAll(this.obtenerRegistro());
        for (IServidor vecino : this.vecinos)
            resultado.putAll(vecino.obtenerRegistro());
        return resultado;
    }

    // Registra la entidad con nombre idCliente en el servidor
    @Override
    public void grabarEntidad(String idCliente) throws RemoteException {
        System.out.println("Registrada entidad '" + idCliente + "' en '" + this.obtenerId() + "'");
        this.registro.putIfAbsent(idCliente, 0);
    }

    // Comprueba si una entidad ha realizado una donacion en el sistema
    private boolean haDonado(String idCliente) throws RemoteException {
        Integer cantidad = obtenerDonadoPorEntidad(idCliente);
        return cantidad != null && cantidad > 0;
    }

    // Intenta localizar una entidad en el conjunto de servidores y devuelve el servidor en el que se encuentra o null en caso contrario
    private IServidor localizarEntidad(String idCliente) throws RemoteException {
        IServidor resultado = null;
        if (this.registro.keySet().contains(idCliente))
            resultado = this;
        for (IServidor vecino : this.vecinos) {
            Map<String, Integer> registroVecino = vecino.obtenerRegistro();
            Set<String> entidadesVecino = registroVecino.keySet();
            if (entidadesVecino.contains(idCliente))
                resultado = vecino;
        }
        if (resultado != null)
            System.out.println("Desde " + this.obtenerId() + ", '" + idCliente + "' localizado en " + resultado.obtenerId());
        else
            System.out.println("Desde " + this.obtenerId() + ", no se ha podido localizar '" + idCliente + "'");
        return resultado;
    }

    // Registra una donacion en el servidor junto con su correspondiente transaccion
    @Override
    public void grabarDeposito(String idCliente, int cantidad) throws Exception, RemoteException {
        if (!this.registro.containsKey(idCliente))
            throw new Exception("La entidad '" + idCliente + "' no se encuentra registrada en '" + id + "'");
        int valorActualizado = this.registro.get(idCliente) + cantidad;
        this.registro.put(idCliente, valorActualizado);
        Transaccion transaccion = new Transaccion(idCliente, cantidad);
        this.transacciones.add(transaccion);
        System.out.println("Deposito de " + cantidad + " por entidad '" + idCliente + "' en '" + this.obtenerId() + "'");
    }

    // Obtiene la cantidad total que ha donado una entidad
    private Integer obtenerDonadoPorEntidad(String idCliente) throws RemoteException {
        return localizarEntidad(idCliente).obtenerRegistro().get(idCliente);
    }

    // Realiza el registro de una entidad en el sistema
    @Override
    public synchronized boolean registrar(String idCliente) throws RemoteException {
        // El cliente solicita el registro a una de las replicas a las que se dirija
        // El servidor realiza el registro del cliente en coordinacion con el resto de servidores
        // - Tiene que comprobar que no este registrado en ninguna de las replicas
        // El servidor delega al registro al servidor con menos entidades registradas
        boolean estado = false;
        try {
            if (localizarEntidad(idCliente) != null) {
                System.out.println("La entidad '" + idCliente + "' ya esta registrada");
                return estado;
            }
            IServidor mejorServidor = estimarMejorServidor();
            if (mejorServidor == null) {
                System.out.println("No se ha podido estimar el mejor servidor para el registro");
                return estado;
            }
            mejorServidor.grabarEntidad(idCliente);
            estado = true;
        }
        catch (Exception e) {
            System.err.println("Error en 'registrar': ");
            e.printStackTrace();
        }
        return estado;
    }

    // Realiza el depósito especificado en cantidad como idCliente
    @Override
    public synchronized boolean depositar(String idCliente, int cantidad) throws RemoteException {
        // El cliente solicita la donacion a una de las replicas a las que se dirija
        // El servidor recibe la solicitud
        // - Si contiene la entidad en la lista de entidades, realiza el deposito
        // - Si no, busca en el resto de servidores y delega la operacion al mismo
        // - Si no existe la entidad en los servidores, la operacion termina sin efecto
        boolean estado = false;
        try {
            if (cantidad <= 0) {
                System.err.println("Cantidad no valida (" + cantidad + ")");
                return estado;
            }
            IServidor localizacion = localizarEntidad(idCliente);
            if (localizacion == null) {
                System.out.println("La entidad '" + idCliente + "' no esta registrada");
                return estado;
            }
            localizacion.grabarDeposito(idCliente, cantidad);
            System.out.println("'" + idCliente + "' ha depositado " + cantidad + " en '" +
                                localizacion.obtenerId() + "' haciendo un total de " +
                                obtenerDonadoPorEntidad(idCliente));
            estado = true;
        }
        catch (Exception e) {
            System.err.println("Error en 'depositar': ");
            e.printStackTrace();
        }
        return estado;
    }

    // Obtiene el total donado por todas las entidades de todo el sistema
    @Override
    public Integer obtenerTotalDonado(String idCliente) throws RemoteException {
        // El cliente solicita obtener el dato
        // El servidor comprueba que este registrado y que ha realizado un deposito
        // Si no, busca en el resto de servidores
        // Si no, levanta una excepcion
        if (!haDonado(idCliente)) {
            System.out.println("La entidad '" + idCliente + 
                                "' ha intentado obtener la suma sin donar");
            return null;
        }
        return obtenerRegistroGlobal().values().stream().mapToInt(x->x).sum();
    }

    // Obtiene la lista de donantes de todo el sistema, junto con las cantidades totales que ha donado cada entidad
    @Override
    public Map<String, Integer> obtenerDonantes(String idCliente) throws RemoteException {
        // El cliente solicita obtener el dato
        // El servidor comprueba que este registrado y que ha realizado un deposito
        // Si no, busca en el resto de servidores
        // Si no, levanta una excepcion
        if (!haDonado(idCliente)) {
            System.out.println("La entidad '" + idCliente + 
                                "' ha intentado obtener la lista de donantes sin donar");
            return null;
        }
        return obtenerRegistroGlobal();
    }

    // Obtiene el historial de transacciones de la entidad idCliente
    @Override
    public Set<Transaccion> obtenerHistorial(String idCliente) throws RemoteException {
        if (!haDonado(idCliente)) {
            System.out.println("La entidad '" + idCliente + 
                                "' ha intentado obtener el historial sin donar");
            return null;
        }
        return localizarEntidad(idCliente).obtenerTransacciones();
    }

    // Funcion encargada de iniciar una replica
    public void run() {
        System.out.println("Iniciando " + id);
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            IServidor stub = (IServidor) UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(id, stub);
        }
        catch (Exception e) {
            System.err.println("Error " + id);
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void main(String[] args) {

        final String HOST = args[0];
        final int N_SERVIDORES = Integer.parseInt(args[1]);

        try {

            Servidor[] servidores = new Servidor[N_SERVIDORES];
            Thread[] hilos = new Thread[N_SERVIDORES];
            for (int i = 0; i < N_SERVIDORES; i++) {
                String idServidor = "SERVIDOR " + (i+1);
                servidores[i] = new Servidor(HOST, idServidor);
            }
            for (int i = 0; i < N_SERVIDORES; i++) {
                for (int j = 0; j < N_SERVIDORES; j++) {
                    if (i != j) {
                        servidores[i].aniadirVecino(servidores[j]);
                    }
                }
                hilos[i] = new Thread(servidores[i]);
                hilos[i].start();
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
    
}
