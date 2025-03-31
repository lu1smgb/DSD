import glob, sys

from calculadora import Calculadora

from thrift.transport import TSocket, TTransport
from thrift.protocol import TBinaryProtocol
from thrift.server import TServer

import logging
logging.basicConfig(level=logging.DEBUG)

class CalculadoraHandler:
    def __init__(self):
        self.log = {}

    # Metodo auxiliar que devuelve la lista con los valores unicos de una lista de elementos
    def eliminarRepetidos(self, nums):
        return list(set(nums[:4]))

    def ping(self):
        print('Pingeado')
    
    def calcularOperacion(self, op: Calculadora.Operacion):
        n1, n2 = op.operando1, op.operando2
        operador = op.operador
        resultado = None
        print(f"Operando 1: {n1}")
        print(f"Operando 2: {n2}")
        print(f"Operador: {operador}")

        match operador:
            case Calculadora.TipoOperador.SUMA:
                resultado = n1 + n2
            case Calculadora.TipoOperador.RESTA:
                resultado = n1 - n2
            case Calculadora.TipoOperador.MULTIPLICACION:
                resultado = n1 * n2
            case Calculadora.TipoOperador.DIVISION:
                if n2 == 0:
                    # Deberia ser ZeroDivisionError pero da problemas
                    raise ValueError("Division por cero")
                resultado = n1 / n2
            case _:
                raise ValueError("No se reconoce el operando")
        
        print(f"Resultado: {resultado}")
        return resultado
    
    def calcularFactorial(self, num: int):
        # Rango de casos en los que el numero equivale al resultado
        if num >= -2 and num <= 2:
            return num
        resultado = 1
        rango = range(num, 2, -1) if num > 0 else range(num, -2, 1)
        for valor in rango: resultado *= valor
        print(f"Resultado: {resultado}")
        return resultado
    
    def calcularMCD(self, nums):
        resultado = 1
        valores = self.eliminarRepetidos(nums)
        print(f"Maximo Comun divisor de {valores}")

        # Puede ser como maximo entre 1 y el maximo de valores, iteramos al reves para encontrarlo antes
        for divisor in range(int(min(valores)), 1, -1):
            # Tabla de verdad para comprobar que el divisor divide a todos los valores
            subresultados = [valor % divisor == 0 for valor in valores]
            # Si todos los valores de la tabla de verdad son ciertos, se devuelve el divisor como MCD
            if all(subresultados): 
                resultado = divisor
                break
        
        print(f"Resultado: {resultado}")
        return resultado
    
    def calcularMCM(self, nums):
        valores = self.eliminarRepetidos(nums)
        print(f"Minimo Comun Multiplo de {valores}")
        resultado = valores[0]
        for num in valores[1:]: resultado = int(abs(resultado * num))
        resultado = resultado // self.calcularMCD(nums)
        print(f"Resultado: {resultado}")
        return resultado
    
    def calcularOperacionCompuesta(self, op: Calculadora.OperacionCompuesta):
        operandos, operadores = op.operandos, op.operadores
        operadores_por_prioridad = [
            Calculadora.TipoOperador.MULTIPLICACION,
            Calculadora.TipoOperador.DIVISION,
            Calculadora.TipoOperador.SUMA,
            Calculadora.TipoOperador.RESTA
        ]
        # Mientras haya mas de un operando
        while len(operandos) > 1 and len(operadores) > 0:
            # Tipo de operador buscado en la expresion
            indice_operador_buscado = 0
            operador_buscado = operadores_por_prioridad[indice_operador_buscado]
            # Operador encontrado en la expresion
            indice_operador_encontrado = None
            operador_encontrado = None
            # Buscamos el siguiente operador
            while operador_encontrado == None:
                try:
                    # Guardamos el operador encontrado correspondiente al tipo buscado
                    print(f"Buscando operador {Calculadora.TipoOperador._VALUES_TO_NAMES[operador_buscado]}")
                    indice_operador_encontrado = operadores.index(operador_buscado)
                    operador_encontrado = operadores[indice_operador_encontrado]
                    print(f"Operador {Calculadora.TipoOperador._VALUES_TO_NAMES[operador_buscado]} encontrado en indice {indice_operador_encontrado}")
                except ValueError as e:
                    # Si no se encuentra el operador (excepcion lanzada por llamada a index)
                    # Cambiamos el tipo de operador a buscar al siguiente en la lista de prioridad
                    indice_operador_buscado += 1
                    if indice_operador_buscado > len(operadores_por_prioridad)-1:
                        raise ValueError("No se ha podido realizar la operacion debido a la falta de operadores")
                    operador_buscado = operadores_por_prioridad[indice_operador_buscado]
                    continue
            # Obtenemos los operandos adyacentes y realizamos la suboperacion
            operando1 = operandos[indice_operador_encontrado]
            operando2 = operandos[indice_operador_encontrado+1]
            print(operando1, operando2, Calculadora.TipoOperador._VALUES_TO_NAMES[operador_encontrado])
            nuevo_operando = self.calcularOperacion(Calculadora.Operacion(operando1, operador_encontrado, operando2))
            print(operandos)
            print([Calculadora.TipoOperador._VALUES_TO_NAMES[operador] for operador in operadores])
            print(indice_operador_encontrado)
            # Actualizamos la lista de operadores y operandos
            operandos = operandos[:indice_operador_encontrado] + [nuevo_operando] + operandos[indice_operador_encontrado+2:]
            operadores = operadores[:indice_operador_encontrado] + operadores[indice_operador_encontrado+1:]
            print(operandos)
            print([Calculadora.TipoOperador._VALUES_TO_NAMES[operador] for operador in operadores])
        return operandos[0]
    
    def calcularOperacionVectorial(self, op: Calculadora.OperacionVectorial):
        if op.operador == Calculadora.TipoOperador.DIVISION:
            raise ValueError("No se puede realizar division entre vectores")
        resultado = op.vector1
        dimension = len(op.vector1)
        # Obtenemos los vectores con la dimension acotada del primero
        v1, v2 = op.vector1[:dimension], op.vector2[:dimension]
        # Si el segundo vector solo tiene un valor, se considera como operacion escalar
        if len(v2) == 1 and op.operador == Calculadora.TipoOperador.MULTIPLICACION:
            resultado = self.vectorPorEscalar(v1, v2[0])
        else:
            match op.operador:
                case Calculadora.TipoOperador.SUMA:
                    resultado = [n1 + n2 for n1, n2 in zip(v1, v2)]
                case Calculadora.TipoOperador.RESTA:
                    resultado = [n1 - n2 for n1, n2 in zip(v1, v2)]
                case Calculadora.TipoOperador.MULTIPLICACION:
                    resultado = [n1 * n2 for n1, n2 in zip(v1, v2)]
                case _:
                    raise ValueError("No se reconoce el operando")
        print(f"Resultado: {resultado}")
        return resultado
    
    def vectorPorEscalar(self, vector, escalar):
        resultado = [n*escalar for n in vector]
        print(f"Resultado: {resultado}")
        return resultado

# Inicializador del servidor ------------------------------------------------------
def main():
    handler = CalculadoraHandler()
    processor = Calculadora.Processor(handler)
    transport = TSocket.TServerSocket(host='127.0.0.1', port=9090)
    tfactory = TTransport.TBufferedTransportFactory()
    pfactory = TBinaryProtocol.TBinaryProtocolFactory()

    server = TServer.TSimpleServer(processor, transport, tfactory, pfactory)
    print('Iniciando servidor...')
    try:
        server.serve()
    except KeyboardInterrupt:
        print("Servidor detenido")

if __name__ == '__main__':
    main()