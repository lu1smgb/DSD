# Desarrollo de Sistemas Distribuidos
# Practica 2 - Llamada a procedimiento remoto - Apache Thrift

# Luis Miguel Guirado Bautista
# Curso 2024/2025
# Universidad de Granada

# Codigo del cliente

from calculadora import Calculadora

from thrift import Thrift
from thrift.transport import TSocket, TTransport
from thrift.protocol import TBinaryProtocol

# Funcion auxiliar que convierte un operador caracter en un tipo de
# la enumeracion de operacion definida en el IDL
def stringToTipoOperador(string: str):
    operador = None
    if len(string) == 0: return operador
    match string[0]:
        case '+':
            operador = Calculadora.TipoOperador.SUMA
        case '-':
            operador = Calculadora.TipoOperador.RESTA
        case '*':
            operador = Calculadora.TipoOperador.MULTIPLICACION
        case '/':
            operador = Calculadora.TipoOperador.DIVISION
    return operador

# Funcion a ejecutar cuando el usuario escoge la opcion de operacion basica
def operacionBasica(client: Calculadora.Client):
    resultado = None
    try:
        # Procesamos la entrada del usuario
        entrada = input("Introduzca la expresion: ").strip().split(" ")
        while entrada.count('') > 0: entrada.remove('')
        entrada = entrada[:3]
        operacion = Calculadora.Operacion()
        # Operandos
        operacion.operando1, operacion.operando2 = float(entrada[0]), float(entrada[2])
        # Operador
        operacion.operador = stringToTipoOperador(entrada[1][0])
        if operacion.operador == None:
            raise ValueError("No se reconoce el operador")
        # Enviamos solicitud y esperamos
        resultado = client.calcularOperacion(operacion)
        print(f"Resultado: {resultado}")
    except ValueError as e:
        print(f"[X] Error al leer la expresion: {str(e)}")
    except Exception as e:
        print(str(type(e)))
        print(f"[X] Error inesperado: {str(e)}")

# Funcion a ejecutar cuando el usuario escoge la opcion de factorial
def operacionFactorial(client: Calculadora.Client):
    resultado = None
    try:
        entrada = int(input("Introduzca el numero: "))
        resultado = client.calcularFactorial(entrada)
        print(f"Resultado: {resultado}")
    except ValueError as e:
        print(f"[X] Error al leer el numero: {str(e)}")

# Funcion comun para el MCM y MCD
def inputCuatroNumeros(func):
    resultado = None
    try:
        entrada = input("Introduzca los numeros (max 4): ").strip().split(" ")[:4]
        entrada = [int(numero) for numero in entrada]
        resultado = func(entrada)
        print(f"Resultado: {resultado}")
    except ValueError as e:
        print(f"[X] Error al leer la expresion: {str(e)}")
    except Exception as e:
        print(f"[X] Error inesperado: {str(e)}")

# Funcion a ejecutar cuando el usuario escoge la opcion de operacion compuesta
def operacionCompuesta(client: Calculadora.Client):
    resultado = None
    try:
        entrada = input("Introduzca la expresion: ").strip().split(" ")
        while entrada.count('') > 0: entrada.remove('')
        operandos = [float(entrada[i]) for i in range(0, len(entrada), 2)]
        operadores = [stringToTipoOperador(entrada[i]) for i in range(1, len(entrada), 2)]
        print(operandos)
        print(operadores)
        operacion = Calculadora.OperacionCompuesta(operandos, operadores)
        resultado = client.calcularOperacionCompuesta(operacion)
        print(f"Resultado: {resultado}")
    except ValueError as e:
        print(f"[X] Error al leer la expresion: {str(e)}")
    except Exception as e:
        print(str(type(e)))
        print(f"[X] Error inesperado: {str(e)}")

# Funcion a ejecutar cuando el usuario escoge la opcion de operacion vectorial
def operacionVectorial(client: Calculadora.Client):
    resultado = None
    try:
        entrada = input("Introduzca los numeros del vector 1: ").strip().split(" ")
        v1 = [float(numero) for numero in entrada]
        operador = stringToTipoOperador(input("Introduzca el operador: ")[0])
        entrada = input("Introduzca los numeros del vector 2: ").strip().split(" ")
        v2 = [float(numero) for numero in entrada]
        operacion = Calculadora.OperacionVectorial(v1, operador, v2)
        resultado = client.calcularOperacionVectorial(operacion)
        print(f"Resultado: {resultado}")
    except ValueError as e:
        print(f"[X] Error al leer la expresion: {str(e)}")
    except Exception as e:
        print(str(type(e)))
        print(f"[X] Error inesperado: {str(e)}")

# Funcion a ejecutar cuando el usuario escoge la opcion de vector por escalar
def escalarVectorial(client: Calculadora.Client):
    resultado = None
    try:
        entrada = input("Introduzca los numeros del vector 1: ").strip().split(" ")
        v = [float(numero) for numero in entrada]
        escalar = int(input("Introduzca el escalar: "))
        resultado = client.vectorPorEscalar(v, escalar)
        print(f"Resultado: {resultado}")
    except ValueError as e:
        print(f"[X] Error al leer la expresion: {str(e)}")
    except Exception as e:
        print(str(type(e)))
        print(f"[X] Error inesperado: {str(e)}")

# Muestra ayuda para el usuario
def mostrarAyuda():
    print("\n ------------------------------------ Ayuda ----------------------------------- ")
    print("Los operandos pueden ser numeros enteros o decimales")
    print("Los operadores pueden ser los siguientes caracteres: +, -, * o /")
    print("Imprescindible separar los operadores y los operandos con espacios en operaciones compuestas")
    print("En las operaciones vectoriales no se puede utilizar / como operador")
    print("Prioridad de los operandos en las operaciones compuestas (descendente): * -> / -> + y -")
    print("Los operadores a la izquierda tienen mas prioridad")
    print(" ------------------------------------------------------------------------------")

# Muestra el menu de opciones
def mostrarMenu():
    print("\n ----- Menu -----")
    print("0. Ping")
    print("1. Operacion basica")
    print("2. Factorial")
    print("3. Minimo Comun Multiplo")
    print("4. Maximo Comun Divisor")
    print("5. Operacion compuesta")
    print("6. Operacion vectorial")
    print("7. Vector por escalar")
    print("8. Ayuda")
    print("9. Salir")

# Codigo principal del cliente
def main():
    transport = TSocket.TSocket('localhost', 9090);
    transport = TTransport.TBufferedTransport(transport)
    protocol = TBinaryProtocol.TBinaryProtocol(transport)
    client = Calculadora.Client(protocol)
    try:
        transport.open()
        opc = None
        while opc != 9:
            mostrarMenu()
            opc = int(input("\nSeleccione una opcion: "))
            match opc:
                case 0:
                    client.ping()
                case 1:
                    operacionBasica(client)
                case 2:
                    operacionFactorial(client)
                case 3:
                    inputCuatroNumeros(client.calcularMCM)
                case 4:
                    inputCuatroNumeros(client.calcularMCD)
                case 5:
                    operacionCompuesta(client)
                case 6:
                    operacionVectorial(client)
                case 7:
                    escalarVectorial(client)
                case 8:
                    mostrarAyuda()
                case 9:
                    print("Adios! :)")
                case _:
                    print("No se reconoce la opcion, intentalo de nuevo")
        transport.close()
    except KeyboardInterrupt:
        print("\nAdios! :)")
    transport.close()

if __name__ == '__main__':
    main()