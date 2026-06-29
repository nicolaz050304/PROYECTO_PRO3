// Splash de intro "BUNKI" — se muestra UNA VEZ POR SESIÓN del navegador.
// Usamos sessionStorage (NO localStorage) a propósito: sessionStorage persiste durante la sesión
// (incluido un F5/recarga) pero se reinicia al abrir una pestaña/ventana nueva o reabrir el navegador.
// Resultado: el splash sale cuando el usuario ENTRA al sitio (sesión nueva) y NO en cada F5; tampoco
// queda "visto para siempre" como pasaría con localStorage.
//
// Sigue el patrón de interop del proyecto: expone un objeto global window.bunkiIntro. A diferencia de
// los demás .js (que van al final del body), ESTE se carga en el <head>: necesita ejecutarse ANTES del
// primer paint para evitar parpadeo (ver abajo).
//
// ANTI-PARPADEO: el overlay es HTML estático del <body> (visible por defecto), así aparece antes de que
// Blazor hidrate, sin flash del Home. En cuanto este script corre (en el <head>), si la intro YA se vio
// EN ESTA SESIÓN —o el usuario pide menos movimiento— marca <html> con la clase 'bunki-intro-seen' de
// inmediato; el CSS oculta el overlay con esa clase ANTES de pintar, así en F5 el splash no se ve.
(function () {
    "use strict";

    var CLAVE = "bunki_intro_seen";

    // Lectura defensiva: si sessionStorage no está disponible (modo privado estricto, etc.), tratamos
    // como "ya visto" para NO molestar. Ante cualquier fallo, la app debe verse normal.
    function yaVistoEnSesion() {
        try { return window.sessionStorage.getItem(CLAVE) === "1"; }
        catch (e) { return true; }
    }

    function prefiereMenosMovimiento() {
        try { return window.matchMedia("(prefers-reduced-motion: reduce)").matches; }
        catch (e) { return false; }
    }

    // ¿Hay que saltarse el splash? Sesión nueva Y con movimiento permitido → se muestra; si no, se salta.
    var saltar = yaVistoEnSesion() || prefiereMenosMovimiento();

    // --- FASE 1 (sincrónica, en el <head>): ocultar el overlay antes del primer paint si toca saltarlo ---
    try {
        if (saltar) {
            document.documentElement.classList.add("bunki-intro-seen");
        }
    } catch (e) { /* si falla, el overlay igualmente se auto-descarta por CSS */ }

    // --- FASE 2 (con el DOM listo): sesión nueva → persistir la marca y limpiar el nodo del DOM ---
    function orquestar() {
        try {
            var overlay = document.getElementById("bunki-intro");
            if (!overlay) return;

            if (saltar) {
                // F5 en la misma sesión / reduced-motion: ya estaba oculto por CSS; lo quitamos del DOM.
                overlay.remove();
                return;
            }

            // Sesión nueva: marcamos YA como visto (en sessionStorage), así un F5 a mitad de animación no
            // la repite; al cerrar/abrir pestaña la marca se pierde y el splash vuelve a salir.
            try { window.sessionStorage.setItem(CLAVE, "1"); } catch (e) { }

            // El overlay se anima y se auto-desvanece por CSS (animation ... forwards). Aquí solo lo
            // retiramos del DOM cuando termina toda la secuencia (~3.55s), para no dejar un nodo encima.
            window.setTimeout(function () {
                if (overlay && overlay.parentNode) overlay.remove();
            }, 3900);
        } catch (e) { /* nunca romper la app por el splash */ }
    }

    // Se expone init() (patrón del proyecto) pero también se auto-ejecuta: no depende de que Blazor lo
    // llame, así el splash funciona aunque la hidratación tarde.
    window.bunkiIntro = { init: orquestar };

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", orquestar);
    } else {
        orquestar();
    }
})();
