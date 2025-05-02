/*

    Desarrollo de Sistemas Distribuidos
    Practica 3 - RMI

    Luis Miguel Guirado Bautista
    Curso 2024/2025
    Universidad de Granada

	Interfaz servidor / cliente

*/

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface ICliente extends Remote {
    public boolean registrar(String idCliente) throws RemoteException;
    public boolean depositar(String idCliente, int cantidad) throws RemoteException;
    public Integer obtenerTotalDonado(String idCliente) throws RemoteException;
    public Map<String, Integer> obtenerDonantes(String idCliente) throws RemoteException;
}