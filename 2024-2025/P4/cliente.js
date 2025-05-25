/*     
    Practica 4 - Servicio Web
    JS del cliente

    Desarrollo de Sistemas Distribuidos
    Curso 2024/2025
    Luis Miguel Guirado Bautista
    Universidad de Granada 
*/

const serviceURL = document.URL;
const socket = io(serviceURL);

const seccionSensores = document.querySelector('#sensores');

const actualizadorTemperatura = document.querySelector('#actualizador-temperatura');
const vistaPreviaTemperatura = document.querySelector('#vista-previa-temperatura');

const actualizadorLuminosidad = document.querySelector('#actualizador-luminosidad');
const vistaPreviaLuminosidad = document.querySelector('#vista-previa-luminosidad');

const actualizadorPolen = document.querySelector('#actualizador-polen');
const vistaPreviaPolen = document.querySelector('#vista-previa-polen');

const actuadorAire = document.querySelector('#actuador-aire');
const actuadorPersianas = document.querySelector('#actuador-persianas');
const actuadorPurificador = document.querySelector('#actuador-purificador');

const listaNotificaciones = document.querySelector('#lista-notificaciones');
const listaUsuarios = document.querySelector('#lista-usuarios');

const error = document.querySelector('#mensaje-error');

function cambiarTemperatura(valor) {
    console.log("Cambiando temperatura a " + valor + " ºC");
    socket.emit('actualizar-temperatura', valor);
}

function cambiarLuminosidad(valor) {
    console.log("Cambiando luminosidad a " + valor + "%");
    socket.emit('actualizar-luminosidad', valor);
}

function cambiarPolen(valor) {
    console.log("Cambiando polen a " + valor + " gm3");
    socket.emit('actualizar-polen', valor);
}

function alternarAire() {
    console.log("Alternando aire");
    socket.emit('alternar-aire');
}

function alternarPersianas() {
    console.log("Alternando persianas");
    socket.emit('alternar-persianas');
}

function alternarPurificador() {
    console.log("Alternando purificador");
    socket.emit('alternar-purificador');
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

function actualizarPolen(valor) {

    console.log(`Actualizando polen ${valor} gm3`);
    actualizadorPolen.value = valor;
    vistaPreviaPolen.textContent = valor + ' gm3';

}

function actualizarAire(valor) {

    console.log(`Actualizando aire a ${valor ? "Encendido" : "Apagado"}`);
    actuadorAire.style.backgroundColor = valor ? 'greenyellow' : 'tomato';

}

function actualizarPersianas(valor) {

    console.log(`Actualizando persianas a ${valor ? "Cerradas" : "Abiertas"}`);
    actuadorPersianas.style.backgroundColor = valor ? 'greenyellow' : 'tomato';

}

function actualizarPurificador(valor) {

    console.log(`Actualizando purificador a ${valor ? "Activado" : "Desactivado"}`);
    actuadorPurificador.style.backgroundColor = valor ? 'greenyellow' : 'tomato';

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
    
    error.childNodes.item(0).textContent = mensaje;
    error.style.display = 'block';

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

actualizadorPolen.addEventListener("click", () => {
    var valor = parseFloat(actualizadorPolen.value);
    cambiarPolen(valor);
});
actualizadorPolen.addEventListener("input", () => {
    var valor = parseFloat(actualizadorPolen.value);
    vistaPreviaPolen.textContent = valor + ' gm3';
});

actuadorAire.addEventListener('click', alternarAire);
actuadorPersianas.addEventListener('click', alternarPersianas);
actuadorPurificador.addEventListener('click', alternarPurificador);

socket.on('obtener-usuarios', actualizarListaUsuarios);
socket.on('obtener-notificaciones', actualizarNotificaciones);
socket.on('obtener-temperatura', actualizarTemperatura);
socket.on('obtener-luminosidad', actualizarLuminosidad);
socket.on('obtener-polen', actualizarPolen);
socket.on('obtener-aire', actualizarAire);
socket.on('obtener-persianas', actualizarPersianas);
socket.on('obtener-purificador', actualizarPurificador);
socket.on('disconnect', () => mostrarErrorCritico('Se ha perdido la conexion con el servidor'));