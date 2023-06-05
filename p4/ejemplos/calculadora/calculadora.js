var http = require("http");
var url = require("url");

function calcular(operacion, v1, v2) {
    switch (operacion.toLowerCase()) {
        case "sumar":
            return v1+v2;
        case "restar":
            return v1-v2;
        case "multiplicar":
            return v1*v2;
        case "dividir":
            return v1/v2
        default:
            return "Error: parametros no validos";
    }
}

var httpServer = http.createServer(
    function(request, response) {
        var uri = url.parse(request.url).pathname;
        var output = "";
        while (uri.indexOf('/') == 0) uri = uri.slice(1);
        var params = uri.split("/");
        if (params.length >= 3) {
            var v1 = parseFloat(params[1]);
            var v2 = parseFloat(params[2]);
            var result = calcular(params[0], v1, v2);
            output = result.toString();
        }
        else {
            output = "Error: el numero de parametros no es valido";
        }

        response.writeHead(200, {"Content-Type": "text/html"});
        response.write(output);
        response.end();
    }
);

httpServer.listen(8080);
console.log("Servicio HTTP iniciado");

// http://localhost:8080/sumar/2/3 --> 5