/*
    Código del cliente del sistema domótico
    Desarrollo de Sistemas Distribuidos

    Luis Miguel Guirado Bautista
    Curso 2022/23
    Universidad de Granada
*/

var serviceURL = document.URL;
var socket = io.connect(serviceURL);

// Elimina todas las secciones de la interfaz y deja un mensaje
function limpiarTodo() {

    var cuerpo = document.getElementById('body');
    cuerpo.innerHTML = "El sistema ha dejado de estar disponible";

}

// Actualiza la lista de usuarios del sistema completamente
function actualizarListaUsuarios(data) {

    var listaUsuarios = document.getElementById("listaUsuarios");
    if (data.length > 0) {
        listaUsuarios.innerHTML = "";
        for (var i = 0; i < data.length; i++) {
            var itemLista = document.createElement('li');
            var usuario = data[i];
            itemLista.innerHTML = usuario.host + ':' + usuario.port;
            listaUsuarios.appendChild(itemLista);
        }
    }
    else {
        listaUsuarios.innerHTML = "No hay usuarios conectados";
    }

}

// Manda al servidor una actualización de la temperatura en el sistema
function actualizarTemperatura() {

    var value = document.getElementById("valorTemperatura").value;
    socket.emit('actualizarTemperatura', value);

}

// Manda al servidor una actualización de la luminosidad en el sistema
function actualizarLuminosidad() {

    var value = document.getElementById("valorLuminosidad").value;
    socket.emit('actualizarLuminosidad', value);

}

// Añade una nueva notificación a la lista de notificaciones
function actualizarListaNotificaciones(data) {

    var listaNotificaciones = document.getElementById("listaNotificaciones");
    if (data && data.length > 0) {
        for (var i=0; i < data.length; i++) {
            var itemLista = document.createElement('li');
            var notificacion = data[i].msg;
            itemLista.innerHTML = notificacion;
            listaNotificaciones.appendChild(itemLista);
        }
    }
    else {
        listaNotificaciones.innerHTML = "";
    }

}

// Actualiza el campo de la temperatura con el valor establecido
function actualizarCampoTemperatura(valor) {

    console.log("Obtenida temperatura actual: " + valor);
    var campoTemperatura = document.getElementById("temperaturaActual");
    campoTemperatura.innerHTML = valor;

}

// Actualiza el campo de la luminosidad con el valor establecido
function actualizarCampoLuminosidad(valor) {

    console.log("Obtenida luminosidad actual: " + valor);
    var campoLuminosidad = document.getElementById("luminosidadActual");
    campoLuminosidad.innerHTML = valor;

}

// Funcion encargada de cambiar el estado del aire acondicionado al servidor
function alternarAireAcondicionado() {

    console.log("Alternando aire acondicionado");
    socket.emit('alternarAireAcondicionado');

}

// Funcion encargada de cambiar el estado de las persianas al servidor
function alternarPersianas() {

    console.log("Alternando persianas");
    socket.emit('alternarPersianas');

}

// Funcion que cambia el boton del aire acondicionado segun su estado (bool)
function actualizarEstadoAireAcondicionado(estado) {

    console.log("Cambiando boton del aire al " + ((estado) ? "verde" : "rojo"));
    var boton = document.getElementById("botonAC");
    boton.style.background = estado ? '#0f0' : '#f00';

}

// Funcion que cambia el boton de las persianas segun su estado (bool)
function actualizarEstadoPersianas(estado) {

    console.log("Cambiando boton de las persianas al " + ((estado) ? "verde" : "rojo"));
    var boton = document.getElementById("botonPersiana");
    boton.style.background = estado ? '#0f0' : '#f00';

}

// Listeners
socket.on('actualizarListaUsuarios', (data) => {
    actualizarListaUsuarios(data);
});

socket.on('actualizarListaNotificaciones', (data) => {
    actualizarListaNotificaciones(data);
});

socket.on('actualizarCampoTemperatura', (data) => {
    actualizarCampoTemperatura(data);
});

socket.on('actualizarCampoLuminosidad', (data) => {
    actualizarCampoLuminosidad(data);
});

socket.on('actualizarEstadoAireAcondicionado', (data) => {
    actualizarEstadoAireAcondicionado(data);
});

socket.on('actualizarEstadoPersianas', (data) => {
    actualizarEstadoPersianas(data);
});

socket.on('disconnect', () => {
    limpiarTodo();
});