typedef list<double> Vector;

service Calculadora {
    void ping(),
    double suma(1:double n1, 2:double n2),
    double resta(1:double n1, 2:double n2),
    double multiplica(1:double n1, 2:double n2),
    double divide(1:double n1, 2:double n2),
    double calcularCadena(1:string cadena),
    Vector sumaVectores(1:Vector v1, 2:Vector v2),
    Vector restaVectores(1:Vector v1, 2:Vector v2),
    Vector productoVectorEscalar(1:Vector vector, 2:double escalar),
    Vector productoVectorial(1:Vector v1, 2:Vector v2)
}