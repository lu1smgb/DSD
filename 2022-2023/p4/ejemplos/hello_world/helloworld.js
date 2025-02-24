// Puerto de escucha
const puerto = 8080;

// Importamos el modulo HTTP de Nodejs
var http = require("http");

// Creamos nuestro servicio
var httpServer = http.createServer(
    function(request, response) {

        // Imprimimos las cabeceras HTTP de las peticiones recibidas por pantalla
        console.log(request.headers);

        // En la respuesta mandamos el codigo 200 (éxito) y el tipo de contenido del cuerpo de la respuesta
        response.writeHead(200, {"Content-Type": "text/plain"});

        // Escribimos en el cuerpo de la respuesta
        response.write("Hola mundo");

        // Terminamos de escribir en la respuesta
        response.end();
        
    }
)

// Inicializamos el servidor y lo ponemos en escucha en el puerto especificado
httpServer.listen(puerto);
console.log("Servicio HTTP iniciado");

// A partir de aqui el programa no termina hasta que se cierre el puerto
// Por cada petición que se reciba, se imprimirán por pantalla la request y la response