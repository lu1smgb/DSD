# $1: Numero de replicas a crear
java -cp . -Djava.rmi.server.codebase=file:.. -Djava.rmi.server.hostname=localhost -Djava.security.policy=server.policy Servidor localhost $1