var http = require("http");
var url = require("url");
var fs = require("fs");
var path = require("path");

var mimeTypes = {
    "html": "text/html",
    "jpeg": "image/jpeg",
    "jpg": "image/jpg",
    "png": "image/png",
    "js": "text/javascript",
    "css": "text/css",
    "swf": "application/x-shockwave-flash"
};

function calcular(operacion, v1, v2) {
    switch (operacion.toLowerCase()) {
        case "sumar":
            return v1 + v2;
        case "restar":
            return v1 - v2;
        case "multiplicar":
            return v1 * v2;
        case "dividir":
            return v1 / v2
        default:
            return "Error: parametros no validos";
    }
}

var httpServer = http.createServer(
    function(request, response) {
        var uri = url.parse(request.url).pathname;
        if (uri=="/") uri = "/calc.html";
        var fname = path.join(process.cwd(), uri);
        fs.exists(fname, function(exists) {
            if (exists) {
                fs.readFile(fname, function(err, data) {
                    if (!err) {
                        var extension = path.extname(fname).split(".")[1];
                        var mimeType = mimeTypes[extension];
                        response.writeHead(200, mimeType);
                        response.write(data);
                        response.end();
                    }
                    else {
                        response.writeHead(200, {"Content-Type": "text/plain"});
                        response.write("Error de lectura en el fichero: " + uri);
                        response.end();
                    }
                });
            }
            else {
                while (uri.indexOf('/') == 0) uri = uri.slice(1);
                var params = uri.split("/");
                if (params.length >= 3) {
                    console.log("Peticion REST: " + uri);
                    var v1 = parseFloat(params[1]);
                    var v2 = parseFloat(params[2]);
                    var result = calcular(params[0], v1, v2);
                    response.writeHead(200, {"Content-Type": "text/plain"});
                    response.write(result.toString());
                    response.end();
                }
                else {
                    console.log("Peticion invalida: " + uri);
                    response.writeHead(200, {"Content-Type": "text/plain"});
                    response.write('404 Not Found');
                    response.end();
                }
            }
        });
    }
);

httpServer.listen(8080);
console.log("Servicio HTTP iniciado");