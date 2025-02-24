// This autogenerated skeleton file illustrates how to build a server.
// You should copy it to another filename to avoid overwriting it.

#include "Calculadora.h"
#include <thrift/protocol/TBinaryProtocol.h>
#include <thrift/server/TSimpleServer.h>
#include <thrift/transport/TServerSocket.h>
#include <thrift/transport/TBufferTransports.h>

#include <iostream>
#include <sstream>

using namespace ::apache::thrift;
using namespace ::apache::thrift::protocol;
using namespace ::apache::thrift::transport;
using namespace ::apache::thrift::server;

class CalculadoraHandler : virtual public CalculadoraIf {
 private:

  /**
   * Separa los elementos de una cadena de operaciones en operandos y operadores para que
   * la funcion calcularCadena pueda calcularlo todo correctamente
  */
  void procesarCadena(const std::string &cadena, std::vector<double> &operandos, std::vector<char> &operadores) {
    operandos.clear();
    operadores.clear();
    std::istringstream flujo(cadena);
    std::string palabra;
    unsigned int i = 0;

    while (flujo >> palabra) {
      if (i % 2 == 0) {
        operandos.push_back(atof(palabra.c_str()));
      }
      else {
        operadores.push_back(palabra[0]);
      }
      i++;
    }

    // for (unsigned int i=0; i < operandos.size(); i++) {
    //   printf("%g\n", operandos[i]);
    // }

    // for (unsigned int i = 0; i < operadores.size(); i++) {
    //   printf("%c\n", operadores[i]);
    // }
  }

 public:
  CalculadoraHandler() {}

  void ping() {
    printf("Ping recibido\n");
  }

  double suma(const double n1, const double n2) {
    printf("Sumando %g y %g\n", n1, n2);
    return n1 + n2;
  }

  double resta(const double n1, const double n2) {
    printf("Restando %g y %g\n", n1, n2);
    return n1 - n2;
  }

  double multiplica(const double n1, const double n2) {
    printf("Multiplicando %g y %g\n", n1, n2);
    return n1 * n2;
  }

  double divide(const double n1, const double n2) {
    printf("Dividiendo %g y %g\n", n1, n2);
    if (n2 == 0) {
      printf("[!] División entre cero\n");
      return 0;
    }
    else {
      return n1 / n2;
    }
  }

  double calcularCadena(const std::string& cadena) {
    printf("Calculando la cadena:\n %s\n", cadena.c_str());
    std::vector<double> operandos;
    std::vector<char> operadores;
    procesarCadena(cadena, operandos, operadores);
    double resultado = operandos[0];
    for (unsigned int i=0; i < operadores.size(); i++) {
    
      switch (operadores[i]) {
        case '+':
          resultado += operandos[i+1];
          break;
        case '-':
          resultado -= operandos[i+1];
          break;
        case '*':
          resultado *= operandos[i+1];
          break;
        case '/':
          resultado /= operandos[i+1];
          break;
        default:
          break;
      }

    }
    return resultado;
  }

  void sumaVectores(Vector& _return, const Vector& v1, const Vector& v2) {
    printf("Sumando %s y %s\n", vectorToString(v1).c_str(), vectorToString(v2).c_str());
    const Vector *otro = nullptr;

    if (v1.size() < v2.size()) {
      _return = v2;
      otro = &v1;
    }
    else {
      _return = v1;
      otro = &v2;
    }

    for (unsigned int i=0; i < otro->size(); i++) {
      _return[i] += (*otro)[i];
    }

  }

  void restaVectores(Vector& _return, const Vector& v1, const Vector& v2) {
    printf("Restando %s y %s\n", vectorToString(v1).c_str(), vectorToString(v2).c_str());
    const Vector *otro = nullptr;

    if (v1.size() < v2.size())
    {
      _return = v2;
      otro = &v1;
    }
    else
    {
      _return = v1;
      otro = &v2;
    }

    for (unsigned int i = 0; i < otro->size(); i++)
    {
      _return[i] -= (*otro)[i];
    }
  }

  void productoVectorEscalar(Vector& _return, const Vector& vector, const double escalar) {
    printf("Multiplicando %s con %g\n", vectorToString(vector).c_str(), escalar);
    _return = vector;
    for (unsigned int i=0; i < _return.size(); i++) {
      _return[i] *= escalar;
    }
  }

  void productoVectorial(Vector& _return, const Vector& v1, const Vector& v2) {
    printf("Multiplicando %s y %s\n", vectorToString(v1).c_str(), vectorToString(v2).c_str());
    const Vector *otro = nullptr;

    if (v1.size() < v2.size())
    {
      _return = v2;
      otro = &v1;
    }
    else
    {
      _return = v1;
      otro = &v2;
    }

    for (unsigned int i = 0; i < otro->size(); i++)
    {
      _return[i] *= (*otro)[i];
    }
  }

};

int main() {
  int port = 9090;
  ::std::shared_ptr<CalculadoraHandler> handler(new CalculadoraHandler());
  ::std::shared_ptr<TProcessor> processor(new CalculadoraProcessor(handler));
  ::std::shared_ptr<TServerTransport> serverTransport(new TServerSocket(port));
  ::std::shared_ptr<TTransportFactory> transportFactory(new TBufferedTransportFactory());
  ::std::shared_ptr<TProtocolFactory> protocolFactory(new TBinaryProtocolFactory());

  TSimpleServer server(processor, serverTransport, transportFactory, protocolFactory);
  ::std::cout << "Iniciando servidor de C++..." << ::std::endl;
  server.serve();
  return 0;
}

