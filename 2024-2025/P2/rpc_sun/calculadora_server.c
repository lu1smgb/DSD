/*

    Desarrollo de Sistemas Distribuidos
    Practica 2 - Llamada a procedimiento remoto - RPC Sun

    Luis Miguel Guirado Bautista
    Curso 2024/2025
    Universidad de Granada

	Codigo del servidor

*/

#include "calculadora.h"

ResultadoNumerico calcularOperacion(Operacion op) {
	ResultadoNumerico result;
	result.errnum = 0;
	Numero *v1 = &op.operando1;
	Numero *v2 = &op.operando2;
	Numero *vR = &result.ResultadoNumerico_u.resultado;
	printf("\nCalculando %.2lf %c %.2lf...\n", *v1, op.operador, *v2);
	switch (op.operador) {
		case '+':
			*vR = *v1 + *v2;
			break;
		case '-':
			*vR = *v1 - *v2;
			break;
		case '*':
			*vR = *v1 * *v2;
			break;
		case '/':
			if (*v2 == 0) {
				result.errnum = 1;
				break;
			}
			*vR = *v1 / *v2;
			break;
		default:
			result.errnum = 2;
			break;
	}
	if (result.errnum == 0)
		printf("Resultado = %.2lf\n", *vR);

	return result;
}

ResultadoNumerico *
calcular_1_svc(Operacion *op, struct svc_req *rqstp)
{
	printf("\n--- Se ha recibido una operacion SIMPLE ---\n");
	static ResultadoNumerico result;
	result = calcularOperacion(*op);
	printf("\n--- FIN. Enviando resultado a cliente ---\n");
	return &result;
}

ResultadoVectorial *
calcular_2_svc(OperacionVectorial *op, struct svc_req *rqstp)
{
	printf("\n--- Se ha recibido una operacion VECTORIAL ---\n");
	static ResultadoVectorial result;
	result.errnum = 0;

	Vector *vectorResultado = &result.ResultadoVectorial_u.resultado;
	vectorResultado->dim = op->vector1.dim;
	vectorResultado->valores.ValoresVector_len = op->vector1.dim;
	vectorResultado->valores.ValoresVector_val = malloc(vectorResultado->dim * sizeof(Numero));

	for (int i=0; i < vectorResultado->dim; i++) {
		Numero valor1 = op->vector1.valores.ValoresVector_val[i];
		Numero valor2 = op->vector2.valores.ValoresVector_val[i];
		Numero *resultado = &result.ResultadoVectorial_u.resultado.valores.ValoresVector_val[i];
		printf("\nComponente %d\n", i+1);
		printf("Calculando %.2lf %c %.2lf...\n", valor1, op->operador, valor2);
		switch (op->operador) {
			case '+':
				*resultado = valor1 + valor2;
				break;
			case '-':
				*resultado = valor1 - valor2;
				break;
			case '*':
				*resultado = valor1 * valor2;
				break;
			default:
				result.errnum = 2;
				break;
		}
		printf("Resultado = %.2lf\n", *resultado);
	}
	printf("\n--- FIN. Enviando resultado a cliente ---\n");
	return &result;
}

// Funcion secundaria para el calculo compuesto que permite encontrar un operador
// en una operacion compuesta
int encontrarOperador(Operador *operadores, u_int size, Operador op) {
	printf("Buscando %c...\n", op);
	int indice = -1;
	for (int i=0; operadores[i] != '\0' && i < size; i++) {
		if (operadores[i] == op) {
			printf("Encontrado operador %c en posicion %d\n", op, i);
			indice = i;
			break;
		}
	}
	return indice;
}

int encontrarOperadorSumaResta(Operador *operadores, u_int size) {
	printf("Buscando %c o %c...\n", '+', '-');
	int indice = -1;
	for (int i=0; operadores[i] != '\0' && i < size; i++) {
		if (operadores[i] == '+') {
			printf("Encontrado operador %c en posicion %d\n", '+', i);
			indice = i;
			break;
		}
		if (operadores[i] == '-') {
			printf("Encontrado operador %c en posicion %d\n", '-', i);
			indice = i;
			break;
		}
	}
	return indice;
}

// Funcion secundaria para el calculo compuesto que permite encontrar el
// operador con mayor prioridad ('*' -> '/' -> '+' o '-')
int encontrarProximoOperador(Operador *operadores, u_int size) {
	int ret = encontrarOperador(operadores, size, '*');
	if (ret < 0 || ret >= size) {
		ret = encontrarOperador(operadores, size, '/');
	}
	if (ret < 0 || ret >= size) {
		ret = encontrarOperadorSumaResta(operadores, size);
	}
	return ret;
}

ResultadoNumerico *
calcular_3_svc(OperacionCompuesta *op, struct svc_req *rqstp)
{
	printf("\n--- Se ha recibido una operacion COMPUESTA ---\n");
	static ResultadoNumerico result;
	result.errnum = 0;
	result.ResultadoNumerico_u.resultado = 0;

	Numero *operandos = op->operandos.operandos_val;
	Operador *operadores = op->operadores.operadores_val;

	// Mostramos contenido de la operacion compuesta
	printf("\nOperandos: %d\n", op->operandos.operandos_len);
	for (int i=0; i < op->operandos.operandos_len; i++) {
		printf("(%.2lf) ", operandos[i]);
	}
	printf("\nOperadores: %d\n", op->operadores.operadores_len);
	for (int i=0; i < op->operadores.operadores_len; i++) {
		printf("(%c) ", operadores[i]);
	}
	printf("\n");

	while (op->operandos.operandos_len > 1 && op->operadores.operadores_len > 0) {

		Operacion subOp;
		int indiceOperador;
		ResultadoNumerico resultadoPaso;
		Numero *resultadoValor = &resultadoPaso.ResultadoNumerico_u.resultado;
		
		// Escoge el operador
		indiceOperador = encontrarProximoOperador(op->operadores.operadores_val, op->operadores.operadores_len);
		if (indiceOperador >= 0 && indiceOperador < op->operadores.operadores_len) {
			subOp.operador = operadores[indiceOperador];
			printf("Siguiente operador: %c\n", subOp.operador);
		}
		if (indiceOperador < 0) {
			fprintf(stderr, "Error inesperado, no se ha encontrado el siguiente operador\n");
			result.errnum = 3;
			break;
		}

		// Escoge los operandos
		subOp.operando1 = operandos[indiceOperador];
		subOp.operando2 = operandos[indiceOperador+1];

		// Calcula la operacion de dos operandos
		resultadoPaso = calcularOperacion(subOp);
		if (resultadoPaso.errnum != 0) {
			fprintf(stderr, "Se ha encontrado un error mientras se procesaba el calculo\n");
			break;
		}

		// Guardamos el resultado
		operandos[indiceOperador] = resultadoPaso.ResultadoNumerico_u.resultado;

		// Modifica la coleccion de operandos y operadores para la siguiente iteracion
		if (indiceOperador < op->operandos.operandos_len-2) {
			for (int i=indiceOperador+2; i < op->operandos.operandos_len; i++) {
				operandos[i-1] = operandos[i];
			}
		}
		op->operandos.operandos_len--;
		for (int i=indiceOperador; i < op->operadores.operadores_len; i++) {
			operadores[i] = operadores[i+1];
		}
		op->operadores.operadores_len--;

		// Mostramos contenido de la operacion compuesta
		printf("\nOperandos: %d\n", op->operandos.operandos_len);
		for (int i=0; i < op->operandos.operandos_len; i++) {
			printf("(%.2lf) ", operandos[i]);
		}
		printf("\nOperadores: %d\n", op->operadores.operadores_len);
		for (int i=0; i < op->operadores.operadores_len; i++) {
			printf("(%c) ", operadores[i]);
		}
		printf("\n");	

	}

	printf("\n--- FIN. Enviando resultado a cliente ---\n");
	result.ResultadoNumerico_u.resultado = operandos[0];

	return &result;
}
