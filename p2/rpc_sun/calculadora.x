typedef float numero;
typedef numero numeros<>;
typedef char operadores<>;

struct operacion_sencilla {
    numero n1; 
    numero n2;
    char opr;
};

struct cadena_operaciones {
    numeros nums;
    operadores oprs;
};

program CALCULADORA {
    version BASICO {
        numero CALCULO_SENCILLO(operacion_sencilla) = 1;
    } = 1;
    version CADENA {
        numero CALCULO_CADENA(cadena_operaciones) = 1;
    } = 2;
} = 0x30042069;