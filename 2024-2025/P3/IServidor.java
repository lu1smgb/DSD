/*

    Desarrollo de Sistemas Distribuidos
    Practica 3 - RMI

    Luis Miguel Guirado Bautista
    Curso 2024/2025
    Universidad de Granada

	Interfaz servidor / servidor

*/

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface IServidor extends Remote {
    public Map<String, Integer> obtenerRegistro() throws RemoteException;
    public String obtenerId() throws RemoteException;
    public void grabarEntidad(String idCliente) throws RemoteException;
    public void grabarDeposito(String idCliente, int cantidad) throws Exception, RemoteException;
}
