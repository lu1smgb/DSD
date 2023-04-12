# [Apache Thrift](https://thrift.apache.org/)

Programación RPC de un sistema distribuido cliente-servidor usando Apache Thrift. 
Puede generar código para muchos idiomas (Python, Java, C++, PHP, JavaScript...)

**He desarrollado la práctica en Python y en C++**

Calculadora que realiza operaciones aritméticas sencillas, operaciones en cadena y operaciones con vectores

## Dependencias

Si queréis compilar los programas, tendréis que instalar las librerías de Apache Thrift y las dependencias para Python y C++

- [Linux](https://thrift.apache.org/docs/install/debian.html)
- [Windows](https://thrift.apache.org/docs/install/windows.html)

## Uso

### Python

1. Ve a la carpeta `gen-py`
2. Abre dos terminales en ese directorio
    1. Ejecuta en una terminal el servidor con:
        ```
        $ python3 servidor.py
        ```
    2. Y el cliente en otra con:
        ```
        $ python3 cliente.py
        ```
3. Salidas esperadas:
    #### Cliente
    ```
            --- Ping ---

            --- Suma ---
    4 + 6 = 10.0
            --- Resta ---
    4 - 6 = -2.0
            --- Multiplicación ---
    4 * 6 = 24.0
            --- División ---
    4 / 6 = 0.6666666666666666

            --- Cálculo en cadena ---
    5 + 3 * 2 - 2 / 2 -> 7.0

            --- Suma de vectores ---
    [1, 2, 3] + [4, 5, 6] = [5.0, 7.0, 9.0]
            --- Resta de vectores ---
    [1, 2, 3] + [4, 5, 6] = [-3.0, -3.0, -3.0]
            --- Producto escalar ---
    [1, 2, 3] * 3 = [3.0, 6.0, 9.0]
            --- Producto vectorial ---
    [1, 2, 3] * [4, 5, 6] = [4.0, 10.0, 18.0]
    ```
    #### Servidor
    ```
    Iniciando servidor...
    Ping recibido
    Sumando 4.0 y 6.0
    Restando 4.0 y 6.0
    Multiplicando 4.0 y 6.0
    Dividiendo 4.0 y 6.0
    Calculando la cadena:
    5 + 3 * 2 - 2 / 2
    Sumando [1.0, 2.0, 3.0] y [4.0, 5.0, 6.0]
    Restando [1.0, 2.0, 3.0] y [4.0, 5.0, 6.0]
    Multiplicando [1.0, 2.0, 3.0] con 3.0
    Multiplicando [1.0, 2.0, 3.0] y [4.0, 5.0, 6.0]
    _
    ```
### C++

1. Ve a la carpeta `gen-cpp`
2. Ejecuta `make (cliente | servidor)` o simplemente `make` para compilar ambos
3. Abre dos terminales en ese directorio
    1. Ejecuta en una terminal el servidor con:
        ```
        $ ./servidor
        ```
    2. Y el cliente en otra con:
        ```
        $ ./cliente
        ```
4. Salidas esperadas: Parecidas a las de Python (los números en los vectores se imprimen siempre con los 6 primeros decimales)