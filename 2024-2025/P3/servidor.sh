# Desarrollo de Sistemas Distribuidos
# Practica 3 - RMI

# Luis Miguel Guirado Bautista
# Curso 2024/2025
# Universidad de Granada

# Script para inicializar el servidor

java -cp . -Djava.rmi.server.codebase=file:./ \
           -Djava.rmi.server.hostname=localhost \
           -Djava.security.policy=server.policy \
           Servidor localhost 4