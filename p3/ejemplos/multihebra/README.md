# Ejemplo multihebra/hilo

## Pasos para compilar y ejecutar los programas

1. Iniciar el lanzador RMI
   ```
   rmiregistry &
   ```
   o
   ```
   rmiregistry <puerto> &
   ```

2. **Usar una versión de Java no superior a 16** debido a que el código usa `SecurityManager`
   ```
   update-alternatives --config java
   ```

3. Compilar código
   ```
   javac *.java
   ```

4. Iniciar servidor
   ```
   java -cp . -Djava.rmi.server.codebase=file:.. -Djava.rmi.server.     hostname=localhost -Djava.security.policy=server.policy Ejemplo
   ```

5. Iniciar cliente
   ```
   java -cp . -Djava.security.policy=server.policy Cliente_Ejemplo_Multi localhost 11
   ```
   Ahora especificaremos el número de hebras del cliente

## Keyword `synchronized`

Si en el fichero `Ejemplo.java` ponemos la *keyword* `synchronized` en la cabecera de la función que implementa para la interfaz tal que así:
```java
//     vvvvvvvvvvvv
public synchronized void escribir_mensaje(String mensaje) { ... }
```
Entonces las hebras que ejecuten esa función tendrán un comportamiento síncrono, como si estuvieran haciendo cola para ejecutar la función, por ejemplo, si hay una hebra $A$ ejecutando la función $f$ y hay otra hebra $B$ que va a ejecutar esa función, $B$ esperará bloqueada hasta que $A$ termine de ejecutar $f$.

Si omitimos esta *keyword*, entonces todas las hebras ejecutarán la función al mismo tiempo, lo que se conoce como comportamiento asíncrono.