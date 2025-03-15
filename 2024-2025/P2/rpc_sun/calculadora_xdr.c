/*
 * Please do not edit this file.
 * It was generated using rpcgen.
 */

#include "calculadora.h"

bool_t
xdr_Numero (XDR *xdrs, Numero *objp)
{
	register int32_t *buf;

	 if (!xdr_double (xdrs, objp))
		 return FALSE;
	return TRUE;
}

bool_t
xdr_Operador (XDR *xdrs, Operador *objp)
{
	register int32_t *buf;

	 if (!xdr_char (xdrs, objp))
		 return FALSE;
	return TRUE;
}

bool_t
xdr_Operacion (XDR *xdrs, Operacion *objp)
{
	register int32_t *buf;

	 if (!xdr_Numero (xdrs, &objp->operando1))
		 return FALSE;
	 if (!xdr_Operador (xdrs, &objp->operador))
		 return FALSE;
	 if (!xdr_Numero (xdrs, &objp->operando2))
		 return FALSE;
	return TRUE;
}

bool_t
xdr_ResultadoNumerico (XDR *xdrs, ResultadoNumerico *objp)
{
	register int32_t *buf;

	 if (!xdr_int (xdrs, &objp->errnum))
		 return FALSE;
	switch (objp->errnum) {
	case 0:
		 if (!xdr_Numero (xdrs, &objp->ResultadoNumerico_u.resultado))
			 return FALSE;
		break;
	default:
		break;
	}
	return TRUE;
}

bool_t
xdr_ValoresVector (XDR *xdrs, ValoresVector *objp)
{
	register int32_t *buf;

	 if (!xdr_array (xdrs, (char **)&objp->ValoresVector_val, (u_int *) &objp->ValoresVector_len, MAX_VECTOR,
		sizeof (Numero), (xdrproc_t) xdr_Numero))
		 return FALSE;
	return TRUE;
}

bool_t
xdr_Vector (XDR *xdrs, Vector *objp)
{
	register int32_t *buf;

	 if (!xdr_int (xdrs, &objp->dim))
		 return FALSE;
	 if (!xdr_ValoresVector (xdrs, &objp->valores))
		 return FALSE;
	return TRUE;
}

bool_t
xdr_OperacionVectorial (XDR *xdrs, OperacionVectorial *objp)
{
	register int32_t *buf;

	 if (!xdr_Vector (xdrs, &objp->vector1))
		 return FALSE;
	 if (!xdr_Operador (xdrs, &objp->operador))
		 return FALSE;
	 if (!xdr_Vector (xdrs, &objp->vector2))
		 return FALSE;
	return TRUE;
}

bool_t
xdr_ResultadoVectorial (XDR *xdrs, ResultadoVectorial *objp)
{
	register int32_t *buf;

	 if (!xdr_int (xdrs, &objp->errnum))
		 return FALSE;
	switch (objp->errnum) {
	case 0:
		 if (!xdr_Vector (xdrs, &objp->ResultadoVectorial_u.resultado))
			 return FALSE;
		break;
	default:
		break;
	}
	return TRUE;
}

bool_t
xdr_OperacionCompuesta (XDR *xdrs, OperacionCompuesta *objp)
{
	register int32_t *buf;

	 if (!xdr_array (xdrs, (char **)&objp->operandos.operandos_val, (u_int *) &objp->operandos.operandos_len, ~0,
		sizeof (Numero), (xdrproc_t) xdr_Numero))
		 return FALSE;
	 if (!xdr_array (xdrs, (char **)&objp->operadores.operadores_val, (u_int *) &objp->operadores.operadores_len, ~0,
		sizeof (Operador), (xdrproc_t) xdr_Operador))
		 return FALSE;
	return TRUE;
}
