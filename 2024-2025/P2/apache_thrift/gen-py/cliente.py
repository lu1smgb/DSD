from calculadora import Calculadora

from thrift import Thrift
from thrift.transport import TSocket, TTransport
from thrift.protocol import TBinaryProtocol

def main():
    transport = TSocket.TSocket('localhost', 9090);
    transport = TTransport.TBufferedTransport(transport)
    protocol = TBinaryProtocol.TBinaryProtocol(transport)
    client = Calculadora.Client(protocol)
    transport.open()

    n1 = 5
    n2 = 4
    operador = Calculadora.TipoOperador.SUMA
    operacion = Calculadora.Operacion(n1, operador, n2)
    resultado = client.calcularOperacion(operacion)
    print(f"Resultado: {resultado}")

    print(f"Factoriales:")
    for i in range(0, 6):
        print(f"{i}! = {client.calcularFactorial(i)}")

    n3 = 36
    n4 = 81
    nums = Calculadora.ColeccionCuatroNumeros(n3, n4)
    print(f"mcd({n3}, {n4}) = {client.calcularMCD(nums)}")

    n3 = 150
    n4 = 670
    nums = Calculadora.ColeccionCuatroNumeros(n3, n4)
    print(f"mcm({n3}, {n4}) = {client.calcularMCM(nums)}")


    transport.close()

if __name__ == '__main__':
    main()