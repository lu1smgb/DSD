# Ejemplo con una sola hebra/hilo

## Pasos para compilar y ejecutar los programas

1. Iniciar el lanzador RMI
   `rmiregistry &` o `rmiregistry <puerto> &`

2. **Usar una versión de Java no superior a 16** debido a que el código usa `SecurityManager`s
   ```
   update-alternatives --config java
   ```

2. Compilar código
   ```
   javac *.java
   ```

3. Iniciar servidor
   ```
   java -cp . -Djava.rmi.server.codebase=file:.. -Djava.rmi.server.hostname=localhost -Djava.security.policy=server.policy Ejemplo
   ```

4. Iniciar cliente
   ```
   java -cp . -Djava.security.policy=server.policy Cliente_Ejemplo localhost 0
   ```
   ```
   java -cp . -Djava.rmi.server.codebase=file:.. -Djava.security.policy=server.policy Cliente_Ejemplo localhost 1
   ```
   0 y 1 son solo parámetros