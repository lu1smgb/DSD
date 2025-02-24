#include <iostream>

#include <thrift/protocol/TBinaryProtocol.h>
#include <thrift/transport/TSocket.h>
#include <thrift/transport/TTransportUtils.h>

#include "Calculadora.h"

using namespace std;
using namespace apache::thrift;
using namespace apache::thrift::protocol;
using namespace apache::thrift::transport;

int main() {

    shared_ptr<TTransport> socket(new TSocket("localhost", 9090));
    shared_ptr<TTransport> transport(new TBufferedTransport(socket));
    shared_ptr<TProtocol> protocol(new TBinaryProtocol(transport));
    CalculadoraClient client(protocol);

    try {
        transport->open();
        
        cout << "\t--- Ping ---\n" << endl;
        client.ping();

        const double n1 = 4;
        const double n2 = 6;
        cout << "\t--- Suma ---" << endl;
        cout << n1 << " + " << n2 << " = " << client.suma(n1,n2) << endl;
        cout << "\t--- Resta ---" << endl;
        cout << n1 << " - " << n2 << " = " << client.resta(n1,n2) << endl;
        cout << "\t--- Multiplicación ---" << endl;
        cout << n1 << " * " << n2 << " = " << client.multiplica(n1,n2) << endl;
        cout << "\t--- División ---" << endl;
        cout << n1 << " / " << n2 << " = " << client.divide(n1,n2) << endl;
        cout << "\t--- División entre cero ---" << endl;
        cout << n1 << " / " << 0 << " = " << client.divide(n1, 0) << endl;

        const std::string cadena = "5 + 3 * 2 - 2 / 2";
        cout << "\t--- Cálculo en cadena ---" << endl;
        cout << cadena << " -> " << client.calcularCadena(cadena) << endl;

        const Vector v1 = {1,2,3};
        const Vector v2 = {4,5,6};
        Vector resultado;
        cout << "\t--- Suma de vectores ---" << endl;
        client.sumaVectores(resultado, v1, v2);
        cout << vectorToString(v1) << " + " << vectorToString(v2) << " = " << vectorToString(resultado) << endl;
        cout << "\t--- Resta de vectores ---" << endl;
        client.restaVectores(resultado, v1, v2);
        cout << vectorToString(v1) << " + " << vectorToString(v2) << " = " << vectorToString(resultado) << endl;
        cout << "\t--- Producto escalar ---" << endl;
        client.productoVectorEscalar(resultado, v1, 3);
        cout << vectorToString(v1) << " + " << vectorToString(v2) << " = " << vectorToString(resultado) << endl;
        cout << "\t--- Producto vectorial ---" << endl;
        client.productoVectorial(resultado, v1, v2);
        cout << vectorToString(v1) << " + " << vectorToString(v2) << " = " << vectorToString(resultado) << endl;

        transport->close();
    }
    catch (TException& tx) {
        cout << "Error: " << tx.what() << endl;
    }

    return 0;
}