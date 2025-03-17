/*

    Desarrollo de Sistemas Distribuidos
    Practica 2 - Llamada a procedimiento remoto - RPC Sun

    Luis Miguel Guirado Bautista
    Curso 2024/2025
    Universidad de Granada

	Codigo del cliente

*/

#include "calculadora.h"

void imprimirVector(Vector *v, int newLine) {
	printf("[ ");
	for (int i=0; i < v->dim; i++) {
		printf("%.2f ", v->valores.ValoresVector_val[i]);
	}
	printf("]");
	if (newLine) printf("\n");
	return;
}

void calculadora_1(char *host, Operacion op)
{
	CLIENT *clnt;
	ResultadoNumerico *result_1;
	Operacion calcular_1_arg1 = op;

#ifndef DEBUG
	// printf("Crea cliente\n");
	clnt = clnt_create(host, CALCULADORA, BASICA, "udp");
	if (clnt == NULL)
	{
		clnt_pcreateerror(host);
		exit(1);
	}
	// printf("Fin crea cliente\n");
#endif /* DEBUG */

	// printf("Llama servidor\n");
	result_1 = calcular_1(&calcular_1_arg1, clnt);
	if (result_1 == (ResultadoNumerico *)NULL)
	{
		clnt_perror(clnt, "No se ha podido procesar la operacion");
		return;
	}

	if (result_1->errnum == 1) {
		fprintf(stderr, "Division entre cero\n");
	}

	if (result_1->errnum == 2) {
		fprintf(stderr, "No se reconoce el operador\n");
	}

	if (result_1->errnum == 0)
		printf("\nResultado: %.2f\n", result_1->ResultadoNumerico_u.resultado);

#ifndef DEBUG
	clnt_destroy(clnt);
#endif /* DEBUG */
}

void calculadora_2(char *host, OperacionVectorial op)
{
	CLIENT *clnt;
	ResultadoVectorial *result_1;
	OperacionVectorial calcular_2_arg1 = op;

#ifndef DEBUG
	// printf("Crea cliente\n");
	clnt = clnt_create(host, CALCULADORA, VECTORIAL, "udp");
	if (clnt == NULL)
	{
		clnt_pcreateerror(host);
		exit(1);
	}
	// printf("Fin crea cliente\n");
#endif /* DEBUG */

	// printf("Llama servidor\n");
	result_1 = calcular_2(&calcular_2_arg1, clnt);
	if (result_1 == (ResultadoVectorial *)NULL)
	{
		clnt_perror(clnt, "No se ha podido procesar la operacion");
	}

	if (result_1->errnum == 1) {
		fprintf(stderr, "Division entre cero\n");
	}

	if (result_1->errnum == 2) {
		fprintf(stderr, "No se reconoce el operador\n");
	}

	if (result_1->errnum == 0) {
		printf("\nResultado: ");
		imprimirVector(&result_1->ResultadoVectorial_u.resultado, 1);
	}

#ifndef DEBUG
	clnt_destroy(clnt);
#endif /* DEBUG */
}

void calculadora_3(char *host, OperacionCompuesta op)
{
	CLIENT *clnt;
	ResultadoNumerico *result_1;
	OperacionCompuesta calcular_3_arg1 = op;

#ifndef DEBUG
	// printf("Crea cliente\n");
	clnt = clnt_create(host, CALCULADORA, COMPUESTA, "udp");
	if (clnt == NULL)
	{
		clnt_pcreateerror(host);
		exit(1);
	}
	// printf("Fin crea cliente\n");
#endif /* DEBUG */

	// printf("Llama servidor\n");
	result_1 = calcular_3(&calcular_3_arg1, clnt);
	if (result_1 == (ResultadoNumerico *)NULL)
	{
		clnt_perror(clnt, "No se ha podido procesar la operacion");
	}

	if (result_1->errnum == 1) {
		fprintf(stderr, "Division entre cero\n");
	}

	if (result_1->errnum == 2) {
		fprintf(stderr, "No se reconoce el operador\n");
	}

	if (result_1->errnum == 3) {
		fprintf(stderr, "Error durante el procesamiento de operadores");
	}

	if (result_1->errnum == 0)
		printf("\nResultado: %.2f\n", result_1->ResultadoNumerico_u.resultado);

#ifndef DEBUG
	clnt_destroy(clnt);
#endif /* DEBUG */
}

void leerOperacionBasica(Operacion *op, int ayuda) {

	if (ayuda) {
		printf("\nEscriba la operacion basica con la sintaxis: <operando1> <operador> <operando2>\n");
		printf("Los operandos pueden ser numeros enteros o decimales\n");
		printf("Los operadores pueden ser los siguientes caracteres: +, -, * o /\n\n");
	}
	printf("Escriba aqui la expresion: ");
	scanf("%lf %c %lf", &op->operando1, &op->operador, &op->operando2);
	// printf("Lectura de usuario finalizada\n");
	// printf("[DEBUG] %.2f %c %.2f\n", op->operando1, op->operador, op->operando2);
	return;

}

void leerOperacionVectorial(OperacionVectorial *op, int ayuda) {

	printf("\n");
	// Le pedimos al usuario el tamaño de los vectores
	int dim = 0;
	while (1) {
		printf("Tamaño de los vectores (min 2, max %d): ", MAX_VECTOR);
		if (!scanf("%d", &dim)) {
			fprintf(stderr, "Error al leer la entrada del usuario, intentelo de nuevo\n");
			continue;
		}
		if (dim < 2 || dim > MAX_VECTOR) {
			fprintf(stderr, "Tamaño no valido, intentelo de nuevo\n");
			continue;
		}
		op->vector1.dim = op->vector2.dim = dim;
		op->vector1.valores.ValoresVector_len = op->vector2.valores.ValoresVector_len = dim;
		op->vector1.valores.ValoresVector_val = malloc(dim * sizeof(Numero));
		op->vector2.valores.ValoresVector_val = malloc(dim * sizeof(Numero));
		break;
	}
	printf("\n");

	// Valores del primer vector
	if (ayuda) {
		printf("\nEscribe los %d operandos del primer vector \n", dim);
		printf("Los operandos pueden ser numeros enteros o decimales\n\n");
	}
	for (int i=0; i < dim; i++) {
		while (1) {
			printf("Operando %d de %d del vector 1: ", i+1, dim);
			if (!scanf("%lf", &op->vector1.valores.ValoresVector_val[i])) {
				fprintf(stderr, "Error al leer la entrada del usuario, intentelo de nuevo\n");
				continue;
			}
			break;
		}
		// printf("[DEBUG] %d/%d de v1 -> %.2f\n", i+1, dim, op->vector1.valores.ValoresVector_val[i]);
	}
	printf("\n");

	// Operador
	if (ayuda) {
		printf("\nEscribe el operador\n");
		printf("Los operadores pueden ser los siguientes caracteres: +, - o *\n");
		printf("En las operaciones vectoriales no se puede utilizar / como operador\n\n");
	}
	while (1) {
		// Por algun motivo, al escribir el ultimo operando, el programa se salta el input del operador 1 vez
		getchar();

		printf("Operador: ");
		if (!scanf("%c", &op->operador)) {
			fprintf(stderr, "Error al leer la entrada del usuario, intentelo de nuevo\n");
			continue;
		}
		break;

	}
	printf("\n");

	// Valores del segundo vector
	if (ayuda) {
		printf("\nEscribe los %d operandos del segundo vector \n", dim);
		printf("Los operandos pueden ser numeros enteros o decimales\n");
	}
	for (int i=0; i < dim; i++) {
		while (1) {
			printf("Operando %d de %d del vector 2: ", i+1, dim);
			if (!scanf("%lf", &op->vector2.valores.ValoresVector_val[i])) {
				fprintf(stderr, "Error al leer la entrada del usuario, intentelo de nuevo\n");
				continue;
			}
			break;
		}
		// printf("[DEBUG] %d/%d de v2 -> %.2f\n", i+1, dim, op->vector2.valores.ValoresVector_val[i]);
	}

	if (ayuda) {
		printf("\nLectura de usuario finalizada\n");
		imprimirVector(&op->vector1, 0);
		printf(" %c ", op->operador);
		imprimirVector(&op->vector2, 1);
	}
	return;

}

void leerOperacionCompuesta(OperacionCompuesta *op, int ayuda) {

	if (ayuda) {
		printf("\nEscriba la operacion compuesta con la sintaxis:\n");
		printf("\n<operando1> <operador1> <operando2> <operador2> <operando3> ... <operadorN> <operandoM>\n");
		printf("\nLos operandos pueden ser numeros enteros o decimales\n");
		printf("Los operadores pueden ser los siguientes caracteres: +, -, * o /\n");
		printf("Imprescindible separar los operadores y los operandos con espacios en operaciones compuestas\n");
		printf("Prioridad de los operandos en las operaciones compuestas (descendente): * -> / -> + y -\n");
		printf("Los operadores a la izquierda tienen mas prioridad\n\n");
	}

	char expresion[256];
	op->operandos.operandos_len = op->operadores.operadores_len = 0;
	op->operandos.operandos_val = malloc(256 * sizeof(Numero));
	op->operadores.operadores_val = malloc(256 * sizeof(Operador));

	printf("Expresion: ");
	getchar();
	fgets(expresion, sizeof(expresion), stdin);

	char *buffer;
	for (buffer = strtok(expresion, " "); buffer; buffer = strtok(NULL, " ")) {
		Numero numero;
		Operador operador;
		// printf("%s\n", buffer);
		if (sscanf(buffer, "%lf", &numero)) {
			// printf("%lf\n", numero);
			op->operandos.operandos_val[op->operandos.operandos_len] = numero;
			op->operandos.operandos_len++;
		}
		else if (sscanf(buffer, "%c", &operador)) {
			// printf("%c\n", operador);
			op->operadores.operadores_val[op->operadores.operadores_len] = operador;
			op->operadores.operadores_len++;
		}
		else break;
	}

	if (ayuda) {
		printf("\nOperandos: %d\n", op->operandos.operandos_len);
		for (int i=0; i < op->operandos.operandos_len; i++) {
			printf("(%.2lf) ", op->operandos.operandos_val[i]);
		}
		printf("\nOperadores: %d\n", op->operadores.operadores_len);
		for (int i=0; i < op->operadores.operadores_len; i++) {
			printf("(%c) ", op->operadores.operadores_val[i]);
		}
	}

	printf("\n");
	return;

}

void mostrarMenu() {
	printf("\n ----- Menu ----- \n");
	printf("1. Operacion basica\n");
	printf("2. Operacion vectorial (dos vectores)\n");
	printf("3. Operacion compuesta\n");
	printf("4. Ayuda\n");
	printf("5. Salir\n");
	return;
}

void mostrarAyuda() {
	printf("\n ------------------------------------ Ayuda ----------------------------------- \n");
	printf("Los operandos pueden ser numeros enteros o decimales\n");
	printf("Los operadores pueden ser los siguientes caracteres: +, -, * o /\n");
	printf("Imprescindible separar los operadores y los operandos con espacios en operaciones compuestas\n");
	printf("En las operaciones vectoriales no se puede utilizar / como operador\n");
	printf("Prioridad de los operandos en las operaciones compuestas (descendente): * -> / -> + y -\n");
	printf("Los operadores a la izquierda tienen mas prioridad\n");
	printf(" ------------------------------------------------------------------------------\n");
	return;
}

int main(int argc, char *argv[])
{
	char *host = argv[1];
	int opcion = 0;

	if (argc < 2) {
		printf("Uso: %s <host>\n", argv[0]);
		exit(1);
	}

	printf(" ----- CALCULADORA DISTRIBUIDA USANDO RPC SUN ----- \n");
	printf("Nombre de host: %s\n", host);

	while (1) {
		mostrarMenu();
		printf("Seleccione una opcion: ");
		scanf("%d", &opcion);
		switch (opcion) {
			case 1:
				Operacion op;
				leerOperacionBasica(&op, 1);
				calculadora_1(host, op);
				break;
			case 2:
				OperacionVectorial op_vec;
				leerOperacionVectorial(&op_vec, 1);
				calculadora_2(host, op_vec);
				break;
			case 3:
				OperacionCompuesta op_comp;
				leerOperacionCompuesta(&op_comp, 1);
				calculadora_3(host, op_comp);
				break;
			case 4:
				mostrarAyuda();
				break;
			case 5:
				printf("Adios! :)\n");
				exit(0);
			default:
				printf("\nOpcion no valida, intentelo de nuevo\n");
				break;
		}
	}

	exit(0);
}
