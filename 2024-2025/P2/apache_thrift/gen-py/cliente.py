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

    print('Hacemos ping')
    client.ping()

    transport.close()

if __name__ == '__main__':
    main()