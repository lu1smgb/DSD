typedef double Numero;
typedef TipoOperador Operador;

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

service Calculadora {
    void ping(),
    Numero calcular(1:Operacion op);
}

