# Makefile para compilar el programa Thrift

# Información del compilador
CXX = g++
CXXFLAGS = -std=c++11 -Wall -Wextra

# Nombres de ficheros de código
CLI_CPP = cliente.cpp
SVC_CPP = servidor.cpp
SRCS = Calculadora.cpp
CLI_SRC = $(CLI_CPP) $(SRCS)
SVC_SRC = $(SVC_CPP) $(SRCS)
LIBS = -lthrift
CLI_NAME = cliente
SVC_NAME = servidor

all: $(CLI_NAME) $(SVC_NAME)

# Compilación del cliente
$(CLI_NAME): $(CLI_SRC)
	$(CXX) $(CXXFLAGS) $(CLI_SRC) $(LIBS) -o $(CLI_NAME)

# Compilación del servidor
$(SVC_NAME): $(SVC_SRC)
	$(CXX) $(CXXFLAGS) $(SVC_SRC) $(LIBS) -o $(SVC_NAME)

# Limpieza de archivos
clean:
	rm *.o $(CLI_NAME) $(SVC_NAME)