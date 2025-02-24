# Ejemplo de contador

Es un ejemplo similar al básico, pero con ciertas diferencias

- Separación del servidor y de la implementación del objeto remoto
- El objeto remoto ahora tiene más funciones
- El cliente realiza muchas llamadas al objeto (1002)

## Pasos para compilar y ejecutar los programas

1. Iniciar el lanzador RMI
   `rmiregistry &` o `rmiregistry <puerto> &`

2. **Usar una versión de Java no superior a 16** debido a que el código usa `SecurityManager`
   ```
   update-alternatives --config java`
   ```

3. Compilar código
   ```
   javac *.java
   ```

4. Iniciar servidor
   ```
   java -cp . -Djava.rmi.server.codebase=file:.. -Djava.rmi.server.hostname=localhost -Djava.security.policy=server.policy Servidor
   ```

5. Iniciar cliente
   ```
   java -cp . -Djava.security.policy=server.policy Cliente
   ```