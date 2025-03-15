/*

    Desarrollo de Sistemas Distribuidos
    Practica 2 - Llamada a procedimiento remoto - RPC Sun
    Definicion de programa

    - Operaciones sencillas
    - Pila de operaciones
    - Operaciones vectoriales

    Luis Miguel Guirado Bautista
    Curso 2024/2025
    Universidad de Granada

*/

/* ********************************* PARTE BASICA ********************************* */

/* Definicion de tipo para los operandos o datos numericos */
typedef double Numero;

/* Definicion de tipo para los operadores */
typedef char Operador;

/* Definicion de estructura para definir operaciones simples */
struct Operacion {
    Numero operando1;
    Operador operador;
    Numero operando2;
};

/* Definicion de resultado de una operacion basica */
union ResultadoNumerico switch (int errnum) {
    case 0:
        Numero resultado;
    default:
        void;
};

/* ********************************* PARTE ADICIONAL ****************************** */

/* ----------------- VECTORES ---------------- */

/* Definicion de valores del vector */
const MAX_VECTOR = 10;
typedef Numero ValoresVector<MAX_VECTOR>;

/* Definicion del vector, especificamos dimension y valores */
struct Vector {
    int dim;
    ValoresVector valores;
};

/* Definicion de operacion vectorial */
struct OperacionVectorial {
    Vector vector1;
    Operador operador;
    Vector vector2;
};

/* Definicion de resultado de una operacion vectorial */
union ResultadoVectorial switch (int errnum) {
    case 0:
        Vector resultado;
    default:
        void;
};

/* ---------- OPERACIONES COMPUESTAS ---------- */

/* Definicion de operacion compuesta por operandos y operadores */
struct OperacionCompuesta {
    Numero operandos<>;
    Operador operadores<>;
};

/* *********************************** PROGRAMA *********************************** */
program CALCULADORA {
    version BASICA {
        ResultadoNumerico CALCULAR(Operacion) = 1;
    } = 1;
    version VECTORIAL {
        ResultadoVectorial CALCULAR(OperacionVectorial) = 1;
    } = 2;
    version COMPUESTA {
        ResultadoNumerico CALCULAR(OperacionCompuesta) = 1;
    } = 3;
} = 0x30005000;