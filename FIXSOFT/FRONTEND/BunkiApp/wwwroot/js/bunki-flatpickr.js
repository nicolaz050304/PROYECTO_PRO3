// Convierte "yyyy-MM-dd" a un Date en hora LOCAL (medianoche local), evitando el corrimiento
// de día que produce new Date("yyyy-MM-dd") al interpretarlo como UTC. Usado por las fechas
// ocupadas que se pasan a la opción "disable" de flatpickr.
function parseFechaLocal(iso) {
    var p = String(iso).split("-");
    return new Date(parseInt(p[0], 10), parseInt(p[1], 10) - 1, parseInt(p[2], 10));
}

// Formatea un Date a ISO "yyyy-MM-dd" usando sus componentes LOCALES (no toISOString, que pasa a UTC
// y correría el día por zona horaria). Es el formato con el que SetRango alimenta a Blazor.
function formatearISO(d) {
    var y = d.getFullYear();
    var m = String(d.getMonth() + 1).padStart(2, "0");
    var day = String(d.getDate()).padStart(2, "0");
    return y + "-" + m + "-" + day;
}

window.bunkiDatePicker = {
    // Usado por Home y Explorar: input oculto + altInput visible (d/m/Y).
    // options es OPCIONAL y retrocompatible (minDate "today" por defecto).
    init: function (selector, options) {
        if (typeof flatpickr === "undefined") {
            console.error("flatpickr no está cargado.");
            return;
        }

        options = options || {};

        flatpickr(selector, {
            locale: "es",
            dateFormat: "Y-m-d",
            altInput: true,
            altFormat: "d/m/Y",
            minDate: options.minDate || "today",
            disableMobile: true,
            allowInput: false,
            position: "below"
        });
    },

    // Usado por Explorar cuando llega con fechas en la URL: inicializa Flatpickr con las
    // fechas pasadas explícitamente desde C# (no depende del value del DOM, que aún puede
    // estar vacío) y las refleja en el altInput vía setDate.
    initConFechas: function (selectorEntrada, selectorSalida, fechaEntrada, fechaSalida) {
        if (typeof flatpickr === "undefined") { console.error("flatpickr no está cargado."); return; }
        var opts = {
            locale: "es", dateFormat: "Y-m-d", altInput: true, altFormat: "d/m/Y",
            minDate: "today", disableMobile: true, allowInput: false, position: "below"
        };
        var fpE = flatpickr(selectorEntrada, opts);
        if (fechaEntrada) fpE.setDate(fechaEntrada, false);  // false: no dispara onChange
        var fpS = flatpickr(selectorSalida, opts);
        if (fechaSalida) fpS.setDate(fechaSalida, false);
    },

    // Usado SOLO por DetalleAlojamiento (reserva tipo Airbnb): UN solo calendario en modo RANGO
    // con el rangePlugin de flatpickr, que mantiene los DOS campos (entrada/salida) y conserva el
    // rango resaltado al cambiar de mes. SetRango (en Blazor, vía dotNetRef) es la ÚNICA fuente de
    // verdad de las fechas: no dependemos del change nativo de los inputs.
    initDetalleRango: function (selectorEntrada, selectorSalida, options, dotNetRef) {
        if (typeof flatpickr === "undefined") {
            console.error("flatpickr no está cargado.");
            return;
        }
        if (typeof rangePlugin === "undefined") {
            console.error("rangePlugin no está cargado.");
            return;
        }

        options = options || {};

        var config = {
            mode: "range",
            // rangePlugin conecta el segundo input (salida): un calendario alimenta ambos campos.
            plugins: [ new rangePlugin({ input: selectorSalida }) ],
            locale: "es",
            dateFormat: "d/m/Y",
            minDate: options.minDate || "today",
            disableMobile: true,
            allowInput: false,
            position: "below",
            onChange: function (selectedDates) {
                // selectedDates es la fuente de verdad: avisamos a Blazor con ISO yyyy-MM-dd.
                // 2 fechas = rango completo; 1 = solo inicio (fin null); 0 = sin selección.
                var iso = function (d) { return d ? formatearISO(d) : null; };
                if (selectedDates.length >= 2) {
                    dotNetRef.invokeMethodAsync("SetRango", iso(selectedDates[0]), iso(selectedDates[1]));
                } else if (selectedDates.length === 1) {
                    dotNetRef.invokeMethodAsync("SetRango", iso(selectedDates[0]), null);
                } else {
                    dotNetRef.invokeMethodAsync("SetRango", null, null);
                }
            }
        };

        // Fechas YA OCUPADAS (reservas CONFIRMADA + PENDIENTE) que llegan desde C# como
        // rangos { from: "yyyy-MM-dd", to: "yyyy-MM-dd" }: se deshabilitan igual que antes para que
        // no se puedan elegir NI incluir dentro del rango. Si no vienen, no se bloquea nada.
        if (Array.isArray(options.disable) && options.disable.length > 0) {
            // Date LOCAL (new Date(y, m-1, d)) en vez de new Date("yyyy-MM-dd"), que se interpretaría
            // como UTC y podría correr el día por zona horaria (Perú UTC-5).
            config.disable = options.disable.map(function (r) {
                return { from: parseFechaLocal(r.from), to: parseFechaLocal(r.to) };
            });
        }

        var fp = flatpickr(selectorEntrada, config);

        // Precarga (Explorar / Mis Reservas): mostramos el rango ya seleccionado sin disparar onChange
        // (false), para que las fechas que vienen por la URL aparezcan resaltadas al abrir el detalle.
        if (options.fechaEntrada && options.fechaSalida) {
            fp.setDate([options.fechaEntrada, options.fechaSalida], false);
        }
    },

    // Usado por páginas de anfitrión (filtros / rangos de reporte): mismo calendario
    // dark, formato d/m/Y, input real visible y SIN minDate (permite fechas pasadas).
    initHostDate: function (selector) {
        if (typeof flatpickr === "undefined") {
            console.error("flatpickr no está cargado.");
            return;
        }

        flatpickr(selector, {
            locale: "es",
            dateFormat: "d/m/Y",
            altInput: false,
            disableMobile: true,
            allowInput: false,
            position: "below"
        });
    }
};
