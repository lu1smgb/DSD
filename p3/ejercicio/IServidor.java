/*
 * Desarrollo de Sistemas Distribuidos
 * Práctica 3 - Java RMI
 * Fichero: IServidor.java
 * 
 * Autor: Luis Miguel Guirado Bautista
 * Universidad de Granada
 * Curso 2022/2023
 */

// Interfaz servidor - servidor
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

// Esta interfaz solamente se utilizaran en la implementacion de los objetos
// remotos para que puedan comunicarse entre si, no los podran usar los clientes
// Por ejemplo, obtener la cantidad de entidades registradas del otro servidor
//              obtener la cantidad acumulada del otro servidor
//              etc.
public interface IServidor extends Remote {
    
    ArrayList<String> GetEntidades() throws RemoteException;
    void AniadirEntidad(String entidad) throws RemoteException;
    void AniadirCantidad(String entidad, double cantidad) throws RemoteException;
    double GetAcumulado() throws RemoteException;

}
