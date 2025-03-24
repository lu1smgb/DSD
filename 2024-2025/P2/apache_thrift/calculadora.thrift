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

struct ColeccionCuatroNumeros {
    1: required Numero n1;
    2: required Numero n2;
    3: optional Numero n3;
    4: optional Numero n4;
}

service Calculadora {
    void ping(),
    Numero calcularOperacion(1:Operacion op);
    i64 calcularFactorial(1:i64 num);
    i64 calcularMCD(1:ColeccionCuatroNumeros nums);
    i64 calcularMCM(1:ColeccionCuatroNumeros nums);
}

