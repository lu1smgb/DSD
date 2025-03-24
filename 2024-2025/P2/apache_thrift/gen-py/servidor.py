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

    def ping(self):
        print('Pingeado')
    
    def calcularOperacion(self, op: Calculadora.Operacion):
        n1, n2 = op.operando1, op.operando2
        operador = op.operador
        resultado = None
        print(f"Operando 1: {n1}")
        print(f"Operando 2: {n2}")
        print(f"Operador: {operador}")

        match op.operador:
            case Calculadora.TipoOperador.SUMA:
                resultado = n1 + n2
            case Calculadora.TipoOperador.RESTA:
                resultado = n1 - n2
            case Calculadora.TipoOperador.MULTIPLICACION:
                resultado = n1 * n2
            case Calculadora.TipoOperador.DIVISION:
                resultado = n1 / n2
            case _:
                raise ValueError("No se reconoce el operando")
        
        print(f"Resultado: {resultado}")
        return resultado
    
    def calcularFactorial(self, num: int):
        resultado = 2
        if num >= -2 and num <= 2:
            resultado = num
        else:
            rango = range(num, 2, -1) if num > 0 else range(num, -2, 1)
            for valor in rango:
                print(f"{resultado} <= {resultado} * {valor} = {resultado * valor}")
                resultado *= valor
        print(f"Resultado: {resultado}")
        return resultado
    
    def calcularMCD(self, nums: Calculadora.ColeccionCuatroNumeros):
        resultado = 1
        valores = [nums.n1, nums.n2, nums.n3 or nums.n1, nums.n4 or nums.n1]
        valor_maximo = int(max(valores))
        rango = range(valor_maximo, 1, -1)
        for divisor in rango:
            if all([valor % divisor == 0 for valor in valores]): 
                resultado = divisor
                break
        return resultado
    
    def calcularMCM(self, nums: Calculadora.ColeccionCuatroNumeros):
        THRESHOLD = 100_000_000_000
        valores = [nums.n1, nums.n2, nums.n3 or nums.n1, nums.n4 or nums.n1]
        valor_maximo = int(max(valores))
        resultado = valor_maximo
        rango = range(resultado, THRESHOLD+1)
        for candidato in rango:
            subresultados = [candidato % valor == 0 for valor in valores]
            if len(set(subresultados)) == 1 and subresultados[0]:
                resultado = candidato
                break
        return resultado

def main():
    handler = CalculadoraHandler()
    processor = Calculadora.Processor(handler)
    transport = TSocket.TServerSocket(host='127.0.0.1', port=9090)
    tfactory = TTransport.TBufferedTransportFactory()
    pfactory = TBinaryProtocol.TBinaryProtocolFactory()

    server = TServer.TSimpleServer(processor, transport, tfactory, pfactory)
    print('Iniciando servidor...')
    server.serve()

if __name__ == '__main__':
    main()