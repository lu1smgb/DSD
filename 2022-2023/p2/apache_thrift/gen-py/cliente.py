from calculadora import Calculadora

from thrift import Thrift
from thrift.transport import TSocket, TTransport
from thrift.protocol import TBinaryProtocol

def main():
    transport = TSocket.TSocket('localhost', 9090)
    transport = TTransport.TBufferedTransport(transport)
    protocol = TBinaryProtocol.TBinaryProtocol(transport)
    client = Calculadora.Client(protocol)
    transport.open()
    
    try:
        print("\t--- Ping ---\n")
        client.ping()
        
        n1 = 4
        n2 = 6
        print("\t--- Suma ---")
        print(f"{n1} + {n2} = {client.suma(n1, n2)}")
        print("\t--- Resta ---")
        print(f"{n1} - {n2} = {client.resta(n1, n2)}")
        print("\t--- Multiplicación ---")
        print(f"{n1} * {n2} = {client.multiplica(n1, n2)}")
        print("\t--- División ---")
        print(f"{n1} / {n2} = {client.divide(n1, n2)}\n")
        print("\t--- División entre cero ---")
        print(f"{n1} / 0 = {client.divide(n1, 0)}\n")
        
        cadena = "5 + 3 * 2 - 2 / 2"
        print("\t--- Cálculo en cadena ---")
        print(f"{cadena} -> {client.calcularCadena(cadena)}\n")
        
        v1 = [1,2,3]
        v2 = [4,5,6]
        escalar = 3
        print("\t--- Suma de vectores ---")
        print(f"{v1} + {v2} = {client.sumaVectores(v1, v2)}")
        print("\t--- Resta de vectores ---")
        print(f"{v1} + {v2} = {client.restaVectores(v1, v2)}")
        print("\t--- Producto escalar ---")
        print(f"{v1} * {escalar} = {client.productoVectorEscalar(v1, escalar)}")
        print("\t--- Producto vectorial ---")
        print(f"{v1} * {v2} = {client.productoVectorial(v1, v2)}")
    except Thrift.TApplicationException:
        print("Ha ocurrido un error en el servidor")
    finally:
        transport.close()
    
if __name__ == "__main__":
    main()