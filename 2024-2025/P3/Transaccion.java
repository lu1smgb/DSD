/*

    Desarrollo de Sistemas Distribuidos
    Practica 3 - RMI

    Luis Miguel Guirado Bautista
    Curso 2024/2025
    Universidad de Granada

	Clase Transaccion

*/

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Transaccion implements Serializable {
    
    static private DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
    static private int transactionCounter = 0;
    private int id;
    private String entidad;
    private int cantidad;
    private LocalDateTime fecha;

    public Transaccion(String entidad, int cantidad) {
        this.id = transactionCounter++;
        this.entidad = entidad;
        this.cantidad = cantidad > 0 ? cantidad : 0;
        this.fecha = LocalDateTime.now();
    }

    public String getEntidad() {
        return entidad;
    }

    public int getCantidad() {
        return cantidad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    @Override
    public String toString() {
        return id + " - " + entidad + "@" + fecha.format(formatoFecha) + " -> $" + cantidad;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Transaccion)) return false;
        return Objects.equals(id, ((Transaccion) obj).id);
    }

    public static void main(String[] args) {
        Transaccion t = new Transaccion("Luis", 50);
        System.out.println(t);
    }

}
