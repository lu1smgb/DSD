/*
    Código del servidor del sistema domótico
    Desarrollo de Sistemas Distribuidos

    Luis Miguel Guirado Bautista
    Curso 2022/23
    Universidad de Granada
*/

// Inicializamos todos los módulos y variables globales
const PUERTO = 8080;

var http = require("http");
var url = require("url");
var fs = require("fs");
var path = require("path");
var socketio = require("socket.io");

var MongoClient = require("mongodb").MongoClient;
var mimeTypes = { "html": "text/html", "jpeg": "image/jpeg", "jpg": "image/jpeg", "png": "image/png", "js": "text/javascript", "css": "text/css", "swf": "application/x-shockwave-flash" };

// Inicializamos el servidor
var httpServer = http.createServer(
    (request, response) => {
        var uri = url.parse(request.url).pathname;
        if (uri == "/") uri = "/interfaz.html";
        var fname = path.join(process.cwd(), uri);
        if (fs.existsSync(fname)) {
            fs.readFile(fname, (err, data) => {
                if (!err) {
                    var extension = path.extname(fname).split(".")[1];
                    var mimeType = mimeTypes[extension];
                    response.writeHead(200, mimeType);
                    response.write(data);
                    response.end();
                }
                else {
                    response.writeHead(404, {"Content-Type": "text/plain"});
                    response.write("Error de lectura del fichero: " + uri);
                    response.end();
                }
            });
        }
        else {
            console.log("Peticion invalida: " + uri);
            response.writeHead(404, {"Content-Type": "text/plain"});
            response.write("404 Not Found");
            response.end();
        }
    }
);

MongoClient.connect("mongodb://localhost:27017/", { useUnifiedTopology: true }, (err, db) => {

    if (!err) {
        httpServer.listen(PUERTO);
        var io = socketio(httpServer);

        var database = db.db("sistemaDomotico");
        var usuarios = database.collection("usuarios");
        var notificaciones = database.collection("notificaciones");
        var sensorTemperatura = database.collection("sensorTemperatura");
        var sensorLuminosidad = database.collection("sensorLuminosidad");
        var aireAcondicionado = database.collection("aireAcondicionado");
        var persianas = database.collection("persianas");

        const UMBRALES = {
            temperatura: {
                min: 10,
                max: 40
            },
            luminosidad: {
                min: 25,
                max: 75
            }
        };

        // Valores iniciales de los actuadores al crear la base de datos
        var registro = {
            encendido: false,
            date: new Date(),
            host: '::ffff:127.0.0.1',
            port: PUERTO
        }

        // Establecemos los valores iniciales de los actuadores, si no los tiene ya
        aireAcondicionado.find().sort({ date: 1 }).toArray((err, result) => {
            if (!err) {
                if (result.length == 0) {
                    aireAcondicionado.insertOne(registro, { safe: true }, (err, result) => {
                        if (!err) {
                            console.log("Aire acondicionado no tenia un estado establecido");
                            console.log("Aire acondicionado a APAGADO");
                        }
                        else {
                            console.error("Error al establecer el aire acondicionado");
                        }
                    });
                }
            }
            else {
                console.error("Error interno");
                throw err;
            }
        });
        persianas.find().sort({ date: 1 }).toArray((err, result) => {
            if (!err) {
                if (result.length == 0) {
                    persianas.insertOne(registro, { safe: true }, (err) => {
                        if (!err) {
                            console.log("Persianas no tenian un estado establecido");
                            console.log("Persianas a CERRADAS");
                        }
                        else {
                            console.error("Error al establecer las persianas");
                        }
                    });
                }
            }
            else {
                console.error("Error interno");
                throw err;
            }
        });

        // ----------------------------------------------------------------------------------------------------------------------
        // var umbrales = database.collection("umbrales");
        // const _dbUMBRALES = [
        //     {
        //         temperatura: {
        //             min: 10,
        //             max: 30
        //         }
        //     },
        //     {
        //         luminosidad: {
        //             min: 25,
        //             max: 75
        //         }
        //     }
        // ];
        // umbrales.find().toArray((err, result) => {
        //     if (result.length == 0) {
        //         umbrales.insertMany(_dbUMBRALES, (err, result) => {
        //             if (!err) {
        //                 console.log("Umbrales establecidos por primera vez");
        //             }
        //             else {
        //                 console.error("No se han podido establecer los umbrales para los actuadores");
        //                 throw err;
        //             }
        //         });
        //     }
        //     else {
        //         console.log("Los umbrales ya estaban establecidos, no se hace nada");
        //     }
        // });
        // Para reestablecer los umbrales, ejecutaremos `db.umbrales.deleteMany({})` antes de volver a iniciar el servidor
        // ----------------------------------------------------------------------------------------------------------------------

        // Cuando un cliente se conecte al servidor:
        io.sockets.on('connection', (client) => {

            // Datos del usuario conectado
            var usuario = {
                host: client.request.connection.remoteAddress,
                port: client.request.connection.remotePort,
                date: new Date()
            }

            // FUNCION que establece por primera vez el color de los botones
            var establecerColorBotones = () => {
                aireAcondicionado.find({}, { projection: { encendido: 1 } }).sort({ date: -1 }).limit(1).toArray((err, result) => {
                    if (!err && result.length > 0) {
                        var estadoAireAcondicionado = result[0].encendido;
                        io.sockets.emit('actualizarEstadoAireAcondicionado', estadoAireAcondicionado);
                    }
                });

                persianas.find({}, { projection: { encendido: 1 } }).sort({ date: -1 }).limit(1).toArray((err, result) => {
                    if (!err && result.length > 0) {
                        var estadoPersianas = result[0].encendido;
                        io.sockets.emit('actualizarEstadoPersianas', estadoPersianas);
                    }
                });
            }

            // FUNCION que muestra por primera vez el valor de los sensores
            var establecerValoresSensores = () => {
                sensorTemperatura.find({}, { projection: { value: 1 }}).sort({ date: -1 }).limit(1).toArray((err, result) => {
                    if (!err && result.length > 0) {
                        var temperatura = result[0].value;
                        io.sockets.emit('actualizarCampoTemperatura', temperatura);
                    }
                });

                sensorLuminosidad.find({}, { projection: { value: 1}}).sort({ date: -1 }).limit(1).toArray((err, result) => {
                    if (!err && result.length > 0) {
                        var luminosidad = result[0].value;
                        io.sockets.emit('actualizarCampoLuminosidad', luminosidad);
                    }
                });
            }

            // REGISTRO DEL USUARIO EN EL SISTEMA
            // Buscamos si el usuario ya se habia registrado antes
            usuarios.find({ host: usuario.host, port: usuario.port }, { projection: { _id: 0 } }).toArray((err, result) => {
                
                if (!err) {

                    // Si no se habia registrado antes
                    if (result.length == 0) {
                        // Lo registramos
                        usuarios.insertOne(usuario, { safe: true }, (err) => {

                            if (!err) {
                                console.log("Usuario " + usuario.host + ":" + usuario.port + " registrado");
                            }
                            else {
                                console.error("No se ha podido registrar a " + usuario.host + ":" + usuario.port + " en la base de datos");
                                throw err;
                            }

                        });
                    }

                    // Actualizamos la lista de usuarios en la interfaz
                    usuarios.find().toArray((err, results) => {

                        if (!err) {
                            // Acciones a realizar cuando el usuario termine de conectarse:
                            io.sockets.emit('actualizarListaUsuarios', results);
                            client.emit('actualizarListaNotificaciones'); // Lista de notificaciones vacía
                            establecerColorBotones();
                            establecerValoresSensores();

                        }
                        else {
                            console.error("No se ha podido actualizar la lista de usuarios");
                        }

                    });

                }
                else {

                    console.error("No se ha podido encontrar al usuario en la base de datos");
                    throw err;

                }

            });

            // FUNCION que envia una notificacion a los usuarios conectados en el sistema
            var notificar = (msg) => {

                console.log("Notificando: " + msg);
                var data = { msg: msg, date: new Date() };
                notificaciones.insertOne(data, {safe: true}, (err) => {
                    if (!err) {
                        io.sockets.emit('actualizarListaNotificaciones', [data]);
                    }
                    else {
                        console.error("No se ha podido registrar la notificación");
                    }
                });

            };

            // FUNCION que cambia el estado del aire acondicionado
            var alternarAireAcondicionado = () => {

                // Con esta llamada a sort ordenamos los resultados obtenidos por su fecha
                aireAcondicionado.find({}, { projection: { encendido: 1 } }).sort({ date: -1 }).limit(1).toArray((err, result) => {

                    var estadoActual = result[0].encendido;
                    if (!err) {
                        var registro = {
                            encendido: (estadoActual) ? false : true,
                            date: new Date(),
                            host: usuario.host,
                            port: usuario.port
                        }
                        aireAcondicionado.insertOne(registro, { safe: true }, (err) => {
                            if (!err) {
                                // Una vez efectuemos el cambio, lo comunicamos a todos los usuarios
                                io.sockets.emit('actualizarEstadoAireAcondicionado', registro.encendido);
                                var msg = registro.encendido ? "Se ha encendido el aire acondicionado" : "Se ha apagado el aire acondicionado";
                                notificar(msg);
                            }
                            else {
                                console.error("Error al actualizar el estado del aire acondicionado");
                            }
                        });
                    }
                    else {
                        console.error("Error al alternar el aire acondicionado");
                    }

                });

            };

            // FUNCION que cambia el estado de las persianas
            var alternarPersianas = () => {

                // Con esta llamada a sort ordenamos los resultados obtenidos por su fecha
                persianas.find({}, { projection: { encendido: 1 } }).sort({ date: -1 }).limit(1).toArray((err, result) => {

                    var estadoActual = result[0].encendido;
                    if (!err) {
                        var registro = {
                            encendido: (estadoActual) ? false : true,
                            date: new Date(),
                            host: usuario.host,
                            port: usuario.port
                        }
                        persianas.insertOne(registro, { safe: true }, (err) => {
                            if (!err) {
                                io.sockets.emit('actualizarEstadoPersianas', registro.encendido);
                                var msg = registro.encendido ? "Se han cerrado las persianas" : "Se han abierto las persianas";
                                notificar(msg);
                            }
                            else {
                                console.error("Error al actualizar el estado de las persianas");
                            }
                        });
                    }
                    else {
                        console.error("Error al alternar la persiana");
                    }

                });

            }

            // LISTENER para cuando se actualice la temperatura
            client.on('actualizarTemperatura', (valor) => {
                
                // Datos del registro que guardaremos en el sensor
                var datosTemperatura = {
                    value: valor,
                    userAddress: usuario.host,
                    userPort: usuario.port,
                    date: new Date()
                }

                // Registramos los cambios
                sensorTemperatura.insertOne(datosTemperatura, {safe: true}, (err) => {

                    if (!err) {

                        // Actualizamos la vista y notificamos a todos los usuarios
                        io.sockets.emit('actualizarCampoTemperatura', datosTemperatura.value);
                        var msg = "La temperatura ha cambiado a " + datosTemperatura.value + "ºC";
                        notificar(msg);

                        // Comprobamos si esta fuera del umbral, para tomar las medidas necesarias
                        if (datosTemperatura.value > UMBRALES.temperatura.max || datosTemperatura.value < UMBRALES.temperatura.min) {

                            // En caso de estar fuera del umbral, enviamos una alerta
                            msg = "Alerta por temperatura";
                            notificar(msg);

                            // Obtenemos el estado mas reciente del aire acondicionado
                            aireAcondicionado.find({}, { projection: { encendido: 1 } }).sort({ date: -1 }).limit(1).toArray((err, result) => {

                                if (!err) {
                                    // Si el aire está apagado lo encendemos
                                    if (result[0].encendido == false) {
                                        console.log("El aire esta en " + result[0].encendido + ", se va a iniciar automaticamente");
                                        alternarAireAcondicionado();
                                    }
                                }
                                else {
                                    console.error("No se ha podido obtener el estado actual del aire acondicionado");
                                }

                            });

                        }

                    }
                    else {
                        console.error("No se ha podido actualizar el sensor de temperatura");
                    }

                });

            });

            client.on('actualizarLuminosidad', (valor) => {

                // Datos del registro que guardaremos en el sensor
                var datosLuminosidad = {
                    value: valor,
                    userAddress: usuario.host,
                    userPort: usuario.port,
                    date: new Date()
                }

                // Registramos los cambios
                sensorLuminosidad.insertOne(datosLuminosidad, {safe: true}, (err) => {

                    if (!err) {

                        // Actualizamos la vista y notificamos a todos los usuarios
                        io.sockets.emit('actualizarCampoLuminosidad', datosLuminosidad.value);
                        var msg = "La luminosidad ha cambiado a " + datosLuminosidad.value + "%";
                        notificar(msg);

                        // Comprobamos si esta fuera del umbral, para tomar las medidas necesarias
                        if (datosLuminosidad.value > UMBRALES.luminosidad.max || datosLuminosidad.value < UMBRALES.luminosidad.min) {

                            // En caso de estar fuera del umbral, enviamos una alerta
                            msg = "Alerta por luminosidad";
                            notificar(msg);

                            // Obtenemos el estado mas reciente del aire acondicionado
                            persianas.find({}, { projection: { encendido: 1 } }).sort({ date: -1 }).limit(1).toArray((err, result) => {

                                if (!err) {
                                    // Si las persianas están abiertas, las cerramos
                                    if (result[0].encendido == false) {
                                        alternarPersianas();
                                    }
                                }
                                else {
                                    console.error("No se ha podido obtener el estado actual de la persiana");
                                }

                            });

                        }

                    }
                    else {
                        console.error("No se ha podido actualizar el sensor de luminosidad");
                    }
                });

            });

            // LISTENERS sencillos para el cambio manual del estado de los actuadores
            client.on('alternarAireAcondicionado', alternarAireAcondicionado);

            client.on('alternarPersianas', alternarPersianas);

        });

        console.log("Servidor iniciado");
    }
    else {
        console.error("No se ha podido acceder a la base de datos");
        httpServer.close();
    }

});