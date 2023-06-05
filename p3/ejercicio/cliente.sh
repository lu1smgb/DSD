# $1: Nombre de la entidad
# $2: Id. de la replica de referencia
java -cp . -Djava.security.policy=server.policy Cliente localhost $1 $2