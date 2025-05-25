/*     
    Practica 4 - Servicio Web
    Codigo del servicio web

    Desarrollo de Sistemas Distribuidos
    Curso 2024/2025
    Luis Miguel Guirado Bautista
    Universidad de Granada 
*/

import http from 'node:http';
import { join } from 'node:path';
import { readFile } from 'node:fs';
import { Server } from 'socket.io';
import { MongoClient } from 'mongodb';
import { config } from 'dotenv';
import { Telegraf } from 'telegraf';

config()

const fechaInicioServidor = new Date();

const httpServer = http.createServer((request, response) => {
    let { url } = request;
    if (url=='/') url = './cliente.html';
    const extension = url.split('.').reverse().at(0);
    readFile(join(process.cwd(), url), (err, data) => {
        if (!err) {
            response.writeHead(200, {'content-type': `text/${extension == 'js' ? 'javascript' : extension}; encoding=utf-8`});
            response.write(data);
        }
        else {
            response.writeHead(500, {'content-type': 'text-plain'});
            response.write(`Error al acceder al sistema`);
        }
        response.end();
    });
});

MongoClient.connect(process.env.MONGODB_SERVER).then((mongoClient) => {

    console.log(fechaInicioServidor.toUTCString());

    const db = mongoClient.db('SistemaDomotico');
    const io = new Server(httpServer);

    const bot = new Telegraf(process.env.TELEGRAM_BOT_TOKEN);
    const botChats = db.collection('BotChats');
    
    const usuarios = db.collection('Usuarios');
    const notificaciones = db.collection('Notificaciones');
    const sensores = {
        temperatura: db.collection('Temperatura'),
        luminosidad: db.collection('Luminosidad'),
        polen: db.collection('Polen')
    };
    const actuadores = {
        aire: db.collection('AireAcondicionado'),
        persianas: db.collection('Persianas'),
        purificador: db.collection('Purificador')
    };
    const limites = {
        temperatura: {
            min: -10,
            max: 50
        },
        luminosidad: {
            min: 0,
            max: 100
        },
        polen: {
            min: 0,
            max: 300
        }
    };
    const umbrales = {
        temperatura: {
            min: 15,
            max: 30
        },
        luminosidad: 50,
        polen: 150
    };

    const mostrarError = (msg = "Error", err) => console.error(`${msg}:\n${err}`); 

    const emitirEventoATodos = (evento, datos) => io.sockets.emit(evento, datos);

    const estadoMasReciente = async (coleccion) => coleccion.findOne({}, {projection: {estado: 1}, sort: {fecha: -1}});

    const establecerEstadosPorDefecto = async () => {
        try {
            const estadosIniciales = db.collection('EstadosIniciales');
            const existenEstadosIniciales = await estadoMasReciente(estadosIniciales);

            if (!existenEstadosIniciales) {
                const datos = [
                    { nombre: "Temperatura", valor: 20.0 },
                    { nombre: "Luminosidad", valor: 50.0 },
                    { nombre: "Polen", valor: 10.0 },
                    { nombre: "AireAcondicionado", valor: false },
                    { nombre: "Persianas", valor: false },
                    { nombre: "Purificador", valor: false }
                ];
                await estadosIniciales.insertMany(datos);
                console.log('Estados iniciales establecidos en la base de datos');
            }

            const estados = await estadosIniciales.find().toArray();
            for (const estado of estados) {
                const coleccion = db.collection(estado.nombre);
                const existeEstado = await estadoMasReciente(coleccion);
                if (!existeEstado) {
                    const registro = {
                        fecha: fechaInicioServidor,
                        estado: estado.valor
                    };
                    await coleccion.insertOne(registro);
                    console.log(`Se ha establecido un estado por defecto para ${estado.nombre}`);
                }
            }
        } catch (err) {
            mostrarError('Error al inicializar los estados', err);
        }
    }

    const actualizarNotificacionesEnTodos = () => {
        notificaciones.find({fecha: {$gte: fechaInicioServidor}})
            .project({contenido: 1})
            .sort({fecha: -1})
            .limit(10)
            .toArray()
            .then(query => emitirEventoATodos('obtener-notificaciones', query))
            .catch(err => mostrarError('Error al obtener las notificaciones para todos', err));
    }

    const botDifundeMensaje = (mensaje) => {
        botChats.find({}, {projection: {id: 1}}).toArray()
            .then(chats => chats.forEach(chat => {
                const chatId = chat.id;
                bot.telegram.sendMessage(chatId, mensaje);
            }))
            .catch(err => mostrarError('Error al intentar difundir la notificacion', err));
    }

    const notificar = (mensaje) => {
        var notificacion = {
            fecha: new Date(),
            contenido: mensaje
        };
        notificaciones.insertOne(notificacion, {safe: true})
            .then(_ => {
                actualizarNotificacionesEnTodos();
                botDifundeMensaje(mensaje);
            })
            .catch(err => mostrarError('Error al notificar', err));
    }

    const actualizarSensor = (coleccion, evento, manejador) => {
        estadoMasReciente(coleccion)
            .then(query => manejador(evento, query.estado))
            .catch(err => mostrarError('Error al obtener el valor mas reciente de un sensor', err));
    }
    const actualizarTemperatura = (manejador) => actualizarSensor(sensores.temperatura, 'obtener-temperatura', manejador);
    const actualizarLuminosidad = (manejador) => actualizarSensor(sensores.luminosidad, 'obtener-luminosidad', manejador);
    const actualizarPolen = (manejador) => actualizarSensor(sensores.polen, 'obtener-polen', manejador);
    const actualizarAire = (manejador) => actualizarSensor(actuadores.aire, 'obtener-aire', manejador);
    const actualizarPersianas = (manejador) => actualizarSensor(actuadores.persianas, 'obtener-persianas', manejador);
    const actualizarPurificador = (manejador) => actualizarSensor(actuadores.purificador, 'obtener-purificador', manejador);
    const actualizarSensores = (manejador) => {
        const actualizadores = [
            actualizarTemperatura, 
            actualizarLuminosidad,
            actualizarPolen,
            actualizarAire, 
            actualizarPersianas,
            actualizarPurificador
        ];
        actualizadores.forEach(act => act(manejador));
    }

    const registrarConexion = (datosUsuario) => {
        usuarios.insertOne(datosUsuario, {safe: true})
            .then((_) => {
                notificar(`🙋🏻 Nueva conexion de usuario ${datosUsuario.puerto}`);
                actualizarUsuariosEnTodos();
            })
            .catch((err) => mostrarError('Error al insertar al usuario', err));
    }

    const actualizarUsuariosEnTodos = () => {
        usuarios.find({fecha: {$gte: fechaInicioServidor}})
            .project({puerto: 1})
            .sort({fecha: 1})
            .toArray()
            .then((query) => emitirEventoATodos('obtener-usuarios', query))
            .catch(err => mostrarError('Error al obtener los usuarios para todos', err));
    }

    function alternarActuador(coleccion, actualizador, prefijoNotificacion) {
        estadoMasReciente(coleccion).then(query => {
            const estado = query.estado || false;
            const registro = {
                fecha: new Date(),
                estado: !estado
            }
            coleccion.insertOne(registro, {safe: true})
                .then(_ => actualizador(emitirEventoATodos))
                .then(() => notificar(`${prefijoNotificacion} ${registro.estado ? "activado" : "desactivado"}`));
        })
        .catch(err => mostrarError(`Error al alternar el ${prefijoNotificaciond}`, err));
    }
    const alternarAire = () => alternarActuador(actuadores.aire, actualizarAire, '💨 Actuador de aire acondicionado');
    const alternarPersianas = () => alternarActuador(actuadores.persianas, actualizarPersianas, '🪟 Actuador de persianas');
    const alternarPurificador = () => alternarActuador(actuadores.purificador, actualizarPurificador, '⚗️ Actuador de purificador');

    io.sockets.on('connection', (client) => {

        const emitirEventoACliente = (evento, datos) => client.emit(evento, datos);

        const actualizarSensoresEnCliente = () => actualizarSensores(emitirEventoACliente);

        const cambiarTemperatura = (valor) => {
            const registro = {
                fecha: new Date(),
                estado: valor
            };
            sensores.temperatura.insertOne(registro, {safe: true})
                .then(_ => actualizarTemperatura(emitirEventoATodos))
                .catch(err => mostrarError('Error al cambiar la temperatura del sistema', err))
                .then(() => {
                    estadoMasReciente(actuadores.aire)
                        .then((query) => {
                            const estado = query.estado || false;
                            const { min, max } = umbrales.temperatura;
                            if (estado && valor < min) {
                                notificar(`🕵🏻 El agente ha detectado temperatura muy baja (${valor} ºC) mientras que el aire acondicionado estaba encendido`);
                                alternarAire();
                            }
                            else if (!estado && valor > max) {
                                notificar(`🕵🏻 El agente ha detectado temperatura muy alta (${valor} ºC) mientras que el aire acondicionado estaba apagado`);
                                alternarAire();
                            }
                        });
                })
                .catch(err => mostrarError('Error durante la evaluacion del agente sobre la temperatura', err));
        }

        const cambiarLuminosidad = (valor) => {
            const registro = {
                fecha: new Date(),
                estado: valor
            };
            sensores.luminosidad.insertOne(registro, {safe: true})
                .then(_ => actualizarLuminosidad(emitirEventoATodos))
                .catch(err => mostrarError('Error al cambiar la luminosidad del sistema', err))
                .then(() => {
                    estadoMasReciente(actuadores.persianas)
                        .then((query) => {
                            const estado = query.estado || false;
                            const umbral = umbrales.luminosidad;
                            if (!estado && valor > umbral) {
                                notificar(`🕵🏻 El agente ha detectado luminosidad excesiva (${valor}%) mientras que las persianas estaban abiertas`);
                                alternarPersianas();
                            }
                        });
                })
                .catch(err => mostrarError('Error durante la evaluacion del agente sobre la luminosidad', err));
        }

        const cambiarPolen = (valor) => {
            const registro = {
                fecha: new Date(),
                estado: valor
            };
            sensores.polen.insertOne(registro, {safe: true})
                .then(_ => actualizarPolen(emitirEventoATodos))
                .catch(err => mostrarError('Error al cambiar el polen del sistema', err))
                .then(() => {
                    estadoMasReciente(actuadores.purificador)
                        .then((query) => {
                            const estado = query.estado || false;
                            const limite = umbrales.polen;
                            if (!estado && valor > limite) {
                                notificar(`🕵🏻 El agente ha detectado polen excesivo (${valor} gm3) mientras que el purificador estaba desactivado`);
                                alternarPurificador();
                            }
                        });
                })
                .catch(err => mostrarError('Error durante la evaluacion del agente sobre el polen', err));
        }

        const desconectar = () => {
            const puerto = client.request.socket.remotePort;
            usuarios.deleteOne({puerto: puerto})
                .then(result => {
                    if (result.deletedCount > 0) {
                        notificar(`🚪 Desconexion del usuario ${puerto}`);
                    }
                    else {
                        console.error(`El usuario desconectado no existe?`);
                    }
                })
                .then(actualizarUsuariosEnTodos)
                .catch((err) => console.error(`Error al eliminar al usuario\n:${err}`));
        }

        const usuario = {
            fecha: new Date(),
            direccion: client.request.socket.remoteAddress,
            puerto: client.request.socket.remotePort
        };
        registrarConexion(usuario);
        actualizarSensoresEnCliente();
        client.on('actualizar-temperatura', cambiarTemperatura);
        client.on('actualizar-luminosidad', cambiarLuminosidad);
        client.on('actualizar-polen', cambiarPolen);
        client.on('alternar-aire', alternarAire);
        client.on('alternar-persianas', alternarPersianas);
        client.on('alternar-purificador', alternarPurificador);
        client.on('disconnect', desconectar);

    });

    bot.command('start', async ctx => {
        const chatId = ctx.chat.id;
        const registro = {
            fecha: new Date(),
            id: chatId
        };
        ctx.react('👍');
        botChats.findOne({id: chatId})
            .then(result => {
                if (result) {
                    ctx.reply(`⚠️ El bot ya reconoce este chat ➡️ /help`)
                }
                else {
                    botChats.insertOne(registro, {safe: true})
                        .then(_ => ctx.reply(`✅ El bot se ha iniciado en este chat ➡️ /help`))
                        .catch(err => mostrarError('Error al iniciar el bot en un chat', err));
                }
            });
    });

    bot.command('estado', async ctx => {
        const temperatura = await estadoMasReciente(sensores.temperatura);
        const luminosidad = await estadoMasReciente(sensores.luminosidad);
        const polen = await estadoMasReciente(sensores.polen);
        const aire = await estadoMasReciente(actuadores.aire);
        const persianas = await estadoMasReciente(actuadores.persianas);
        const purificador = await estadoMasReciente(actuadores.purificador);
        const lineas = [
            {nombre: "🌡️ Temperatura", valor: `${temperatura.estado} ºC`},
            {nombre: "🔅 Luminosidad", valor: `${luminosidad.estado} %`},
            {nombre: "🐝 Polen", valor: `${polen.estado} gm3`},
            {nombre: "💨 Aire acondicionado", valor: (aire.estado ? "Encendido ✅" : "Apagado ❌")},
            {nombre: "🪟 Persianas", valor: (persianas.estado ? "Cerradas ✅" : "Abiertas ❌")},
            {nombre: "⚗️ Purificador", valor: (purificador.estado ? "Activado ✅" : "Desactivado ❌")}
        ];
        var msg = `🔍 <b>Estado: </b>\n`;
        lineas.forEach(linea => msg += `\n<b>${linea.nombre}:</b> ${linea.valor}`);
        ctx.replyWithHTML(msg);
    });

    bot.command('aire', async ctx => alternarAire());
    bot.command('persianas', async ctx => alternarPersianas());
    bot.command('purificador', async ctx => alternarPurificador());

    bot.command('help', ctx => {
        const lineas = [
            {comando: 'start', descripcion: 'Inicia el bot'},
            {comando: 'help', descripcion: 'Muestra este mensaje'},
            {comando: 'estado', descripcion: 'Muestra el estado de los sensores y actuadores'},
            {comando: 'aire', descripcion: 'Enciende o apaga el aire acondicionado'},
            {comando: 'persianas', descripcion: 'Abre o cierra las persianas'},
            {comando: 'purificador', descripcion: 'Activa o desactiva el purificador de aire'}
        ];
        var msg = `💻 <b>Comandos: </b>\n`;
        lineas.forEach(linea => msg += `\n<b>/${linea.comando}:</b> ${linea.descripcion}`);
        ctx.replyWithHTML(msg);
    })

    httpServer.listen(process.env.PORT);
    bot.launch();
    establecerEstadosPorDefecto();
    process.once('SIGINT', () => bot.stop('SIGINT'));
    process.once('SIGTERM', () => bot.stop('SIGTERM'));

}).catch((err) => {
    console.error("Error al iniciar el sistema: \n" + err);
});

console.log("Sistema domotico en marcha");