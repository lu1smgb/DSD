var serviceURL = document.URL;
var socket = io.connect(serviceURL);

function limpiarTodo() {
    var cuerpo = document.getElementById('body');
    cuerpo.innerHTML = "El sistema ha dejado de estar disponible";
}

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

function actualizarTemperatura() {

    var value = document.getElementById("valorTemperatura").value;
    socket.emit('actualizarTemperatura', value);

}

function actualizarLuminosidad() {

    var value = document.getElementById("valorLuminosidad").value;
    socket.emit('actualizarLuminosidad', value);

}

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

function actualizarCampoTemperatura(valor) {
    console.log("Obtenida temperatura actual: " + valor);
    var campoTemperatura = document.getElementById("temperaturaActual");
    campoTemperatura.innerHTML = valor;
}

function actualizarCampoLuminosidad(valor) {
    console.log("Obtenida luminosidad actual: " + valor);
    var campoLuminosidad = document.getElementById("luminosidadActual");
    campoLuminosidad.innerHTML = valor;
}

// Funcion encargada de enviar el evento 'alternarAireAcondicionado' al servidor
function alternarAireAcondicionado() {
    console.log("Alternando aire acondicionado");
    socket.emit('alternarAireAcondicionado');
}

function alternarPersianas() {
    console.log("Alternando persianas");
    socket.emit('alternarPersianas');
}

function actualizarEstadoAireAcondicionado(estado) {

    console.log("Cambiando boton del aire al " + ((estado) ? "verde" : "rojo"));
    var boton = document.getElementById("botonAC");
    boton.style.background = estado ? '#0f0' : '#f00';

}

function actualizarEstadoPersianas(estado) {

    console.log("Cambiando boton de las persianas al " + ((estado) ? "verde" : "rojo"));
    var boton = document.getElementById("botonPersiana");
    boton.style.background = estado ? '#0f0' : '#f00';

}

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