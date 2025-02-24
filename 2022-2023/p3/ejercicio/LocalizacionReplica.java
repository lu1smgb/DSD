/*
 * Desarrollo de Sistemas Distribuidos
 * Práctica 3 - Java RMI
 * Fichero: LocalizacionReplica.java
 * 
 * Autor: Luis Miguel Guirado Bautista
 * Universidad de Granada
 * Curso 2022/2023
 */

// Estructura de datos utilizada para guardar la ubicación de una réplica
// tal como la dirección de la máquina en la que se aloja como un número entero
// positivo que la identifica
public class LocalizacionReplica {

    private String host = "null";
    private int id;

    public LocalizacionReplica() {
        this.id = 0;
    }

    public LocalizacionReplica(int id) {
        this.id = Math.abs(id);
    }

    public LocalizacionReplica(String host, int id) {
        this(id);
        if (host == null) {
            host = "null";
        }
        this.host = host;
    }

    public String GetHost() {
        return this.host;
    }

    public int GetId() {
        return this.id;
    }

    public void SetHost(String host) {
        this.host = host;
    }

    public void SetId(int id) {
        this.id = id;
    }

}
