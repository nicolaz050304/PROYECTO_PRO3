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
