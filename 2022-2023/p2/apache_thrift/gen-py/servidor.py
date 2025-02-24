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
        print("Ping recibido")
        
    def suma(self, n1, n2):
        print(f"Sumando {n1} y {n2}")
        return n1 + n2
    
    def resta(self, n1, n2):
        print(f"Restando {n1} y {n2}")
        return n1 - n2

    def multiplica(self, n1, n2):
        print(f"Multiplicando {n1} y {n2}")
        return n1 * n2

    def divide(self, n1, n2):
        try:
            print(f"Dividiendo {n1} y {n2}")
            return n1 / n2
        except ZeroDivisionError:
            print("[!] División entre cero")
            return 0
    
    def procesarCadena(self, cadena: str):
        return [float(x) if idx % 2 == 0 else x
                for idx, x in enumerate(cadena.split())]
    
    def calcularCadena(self, cadena: str):
        print(f"Calculando la cadena:\n {cadena}")
        cadena: list = self.procesarCadena(cadena)

        while len(cadena) > 1:
            num1, op, num2 = cadena[0:3]
            match op:
                case '+':
                    num1 += num2
                case '-':
                    num1 -= num2
                case '*':
                    num1 *= num2
                case '/':
                    num1 /= num2
            cadena = cadena[2::]
            cadena[0] = num1

        return cadena[0]
                
    def sumaVectores(self, v1, v2):
        print(f"Sumando {v1} y {v2}")
        minlen = min(len(v1), len(v2))
        for idx in range(minlen):
            v1[idx] += v2[idx]
        return v1
            
    def restaVectores(self, v1, v2):
        print(f"Restando {v1} y {v2}")
        minlen = min(len(v1), len(v2))
        for idx in range(minlen):
            v1[idx] -= v2[idx]
        return v1
    
    def productoVectorEscalar(self, vector, escalar):
        print(f"Multiplicando {vector} con {escalar}")
        return [val * escalar for val in vector]
    
    def productoVectorial(self, v1, v2):
        print(f"Multiplicando {v1} y {v2}")
        minlen = min(len(v1), len(v2))
        for idx in range(minlen):
            v1[idx] *= v2[idx]
        return v1

def main():
    handler = CalculadoraHandler()
    processor = Calculadora.Processor(handler)
    transport = TSocket.TServerSocket(host='127.0.0.1', port=9090)
    tfractory = TTransport.TBufferedTransportFactory()
    pfactory = TBinaryProtocol.TBinaryProtocolFactory()
    server = TServer.TSimpleServer(processor, transport, tfractory, pfactory)
    
    try:
        print('Iniciando servidor de Python...')
        server.serve()
    except KeyboardInterrupt:
        pass
    except Exception as e:
        print(f'Error inesperado: {e}')
    finally:
        print('\nCerrando servidor...')
    
if __name__ == "__main__":
    main()