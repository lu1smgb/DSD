/*
 * Desarrollo de Sistemas Distribuidos
 * Práctica 3 - Java RMI
 * Fichero: ICliente.java
 * 
 * Autor: Luis Miguel Guirado Bautista
 * Universidad de Granada
 * Curso 2022/2023
 */

// Interfaz cliente - servidor
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICliente extends Remote {

    int Registrar(String entidad_registro) throws Exception, RemoteException;
    void Donar(String entidad, double cantidad) throws Exception, RemoteException;
    double TotalDonado(String entidad) throws Exception, RemoteException;

}
