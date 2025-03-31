/**
 * Desarrollo de Sistemas Distribuidos
 * Practica 2 - Llamada a procedimiento remoto - Apache Thrift
 *
 * Luis Miguel Guirado Bautista
 * Curso 2024-25
 * Universidad de Granada
 *
 * Definicion de interfaz y tipos
*/

typedef double Numero;
typedef TipoOperador Operador;
typedef list<i64> ListaEnteros;

typedef list<Numero> Numeros;
typedef list<Operador> Operadores;
typedef list<Numero> Vector;

enum TipoOperador {
    SUMA = 1, 
    RESTA = 2, 
    MULTIPLICACION = 3, 
    DIVISION = 4
}

struct Operacion {
    1: required Numero operando1;
    2: required Operador operador;
    3: required Numero operando2;
}

struct OperacionCompuesta {
    1: required Numeros operandos;
    2: required Operadores operadores;
}

struct OperacionVectorial {
    1: required Vector vector1;
    2: required Operador operador;
    3: required Vector vector2;
}

service Calculadora {
    void ping(),
    Numero calcularOperacion(1:Operacion op);
    i64 calcularFactorial(1:i64 num);
    i64 calcularMCD(1:ListaEnteros nums);
    i64 calcularMCM(1:ListaEnteros nums);
    Numero calcularOperacionCompuesta(1:OperacionCompuesta op);
    Vector calcularOperacionVectorial(1:OperacionVectorial op);
    Vector vectorPorEscalar(1:Vector vector, 2:i64 escalar);
}

