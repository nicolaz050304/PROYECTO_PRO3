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

    // Usado SOLO por DetalleAlojamiento: sin altInput, el input real es el visible
    // y muestra la fecha como d/m/Y. Así las fechas precargadas (incluso pasadas)
    // se ven directamente desde el value del input, sin sincronizar un altInput.
    initDetalle: function (selector, options) {
        if (typeof flatpickr === "undefined") {
            console.error("flatpickr no está cargado.");
            return;
        }

        options = options || {};

        flatpickr(selector, {
            locale: "es",
            dateFormat: "d/m/Y",
            altInput: false,
            minDate: options.minDate || "today",
            disableMobile: true,
            allowInput: false,
            position: "below"
        });
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
