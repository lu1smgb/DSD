# RPC Sun

Programación RPC de un cliente y un servidor usando `rpcgen` y del lenguaje C

Calculadora capaz de soportar una pila de operaciones aritméticas básicas (suma, resta, multiplicación y división)

## Uso

1. Compilar todos los programas mediante el archivo `Makefile.calculadora`

2. Llamar a los ejecutables según el rol

    ### Cliente

    ```
    ./calculadora_client <hostname> <operacion>
    <operacion> := <num_1> <op_1> <num_2> <op_2> <num_3> ... <op_m> <num_n>
    ```

    ### Servidor

    ```
    ./calculadora_server
    ```

    **Consideraciones:**

    1. Si desea ejecutar el programa en un solo equipo, abra dos terminales,
    ejecute el servidor en uno y el cliente en otro

    2. **Primero el servidor, después el cliente**

3. El resultado de la operación se mostrará por el terminal del cliente

---

## Ejemplo

**Servidor**
```
./calculadora_server
calculo_cadena_2_svc
Números (4): 1 2 3 4 Operadores (3): '+' '+' '+' 
+ 2 = 3
+ 3 = 6
+ 4 = 10
Petición finalizada --------------------

```
**Cliente**
```
$ ./calculadora_client ubuntu 1 + 2 + 3 + 4
El resultado es: 10
```