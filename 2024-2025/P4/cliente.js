const serviceURL = document.URL;
const socket = io(serviceURL);

const seccionSensores = document.querySelector('#sensores');
const actualizadorTemperatura = document.querySelector('#actualizador-temperatura');
const vistaPreviaTemperatura = document.querySelector('#vista-previa-temperatura');
const actualizadorLuminosidad = document.querySelector('#actualizador-luminosidad');
const vistaPreviaLuminosidad = document.querySelector('#vista-previa-luminosidad');
const actuadorAire = document.querySelector('#actuador-aire');
const actuadorPersianas = document.querySelector('#actuador-persianas');
const listaNotificaciones = document.querySelector('#lista-notificaciones');
const listaUsuarios = document.querySelector('#lista-usuarios');

function cambiarTemperatura(valor) {
    console.log("Cambiando temperatura a " + valor + " ºC");
    socket.emit('actualizar-temperatura', valor);
}

function cambiarLuminosidad(valor) {
    console.log("Cambiando luminosidad a " + valor + "%");
    socket.emit('actualizar-luminosidad', valor);
}

function alternarAire() {
    console.log("Activando/desactivando aire");
    socket.emit('alternar-aire');
}

function alternarPersianas() {
    console.log("Alternando persianas");
    socket.emit('alternar-persianas');
}

function actualizarTemperatura(valor) {
    
    console.log(`Actualizando temperatura ${valor} ºC`);
    actualizadorTemperatura.value = valor;
    vistaPreviaTemperatura.textContent = valor + ' ºC';

}

function actualizarLuminosidad(valor) {

    console.log(`Actualizando luminosidad ${valor}%`);
    actualizadorLuminosidad.value = valor;
    vistaPreviaLuminosidad.textContent = valor + ' %';

}

function estadoAireToString(estado) {
    return (estado ? "Encendido" : "Apagado");
}; 

function estadoPersianasToString(estado) {
    return (estado ? "Cerradas" : "Abiertas");
}; 

function actualizarAire(valor) {

    var estado = estadoAireToString(valor);
    console.log(`Actualizando aire a ${valor}`);
    actuadorAire.style.backgroundColor = valor ? 'greenyellow' : 'tomato';

}

function actualizarPersianas(valor) {

    var estado = estadoPersianasToString(valor);
    console.log(`Actualizando persianas a ${valor}`);
    actuadorPersianas.style.backgroundColor = valor ? 'greenyellow' : 'tomato';

}

function actualizarListaUsuarios(usuarios) {

    if (usuarios.length > 0) {
        console.log(`Actualizando lista de usuarios (${usuarios.length})`);
        listaUsuarios.innerHTML = "";
        usuarios.forEach(usuario => {
            var item = document.createElement('li');
            item.textContent = '🧑🏻 ' + usuario.puerto;
            listaUsuarios.appendChild(item);
        });
    }
    
}

function actualizarNotificaciones(notificaciones) {

    console.log(notificaciones);
    if (notificaciones.length > 0) {
        console.log(`Actualizando notificaciones (${notificaciones.length})`);
        listaNotificaciones.innerHTML = "";
        notificaciones.forEach(notificacion => {
            var item = document.createElement('li');
            item.textContent = notificacion.contenido
            listaNotificaciones.appendChild(item);
        });
    }

}

function mostrarErrorCritico(mensaje) {
    
    var error = document.createElement('div');
    error.className = 'mensaje-error';
    error.textContent = mensaje;
    error.style.textAlign = "center";
    error.style.color = "#f00";
    error.style.textDecoration = "bold";

    var body = document.querySelector('#estado');
    body.innerHTML = "";
    body.appendChild(error);

    seccionSensores.style.visibility = "hidden";
    document.querySelector('#notificaciones').style.visibility = "hidden";
    document.querySelector('#usuarios').style.visibility = "hidden";

}

actualizadorTemperatura.addEventListener("click", () => {
    var valor = parseFloat(actualizadorTemperatura.value);
    cambiarTemperatura(valor);
});
actualizadorTemperatura.addEventListener("input", () => {
    var valor = parseFloat(actualizadorTemperatura.value);
    vistaPreviaTemperatura.textContent = valor + ' ºC';
});

actualizadorLuminosidad.addEventListener("click", () => {
    var valor = parseFloat(actualizadorLuminosidad.value);
    cambiarLuminosidad(valor);
});
actualizadorLuminosidad.addEventListener("input", () => {
    var valor = parseFloat(actualizadorLuminosidad.value);
    vistaPreviaLuminosidad.textContent = valor + ' %';
});

actuadorAire.addEventListener('click', alternarAire);
actuadorPersianas.addEventListener('click', alternarPersianas);

socket.on('obtener-usuarios', actualizarListaUsuarios);
socket.on('obtener-notificaciones', actualizarNotificaciones);
socket.on('obtener-temperatura', actualizarTemperatura);
socket.on('obtener-luminosidad', actualizarLuminosidad);
socket.on('obtener-aire', actualizarAire);
socket.on('obtener-persianas', actualizarPersianas);
socket.on('disconnect', () => mostrarErrorCritico('Se ha perdido la conexion con el servidor'));