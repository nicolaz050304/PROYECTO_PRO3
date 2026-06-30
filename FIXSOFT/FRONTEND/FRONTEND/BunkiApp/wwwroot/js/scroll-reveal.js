// Scroll reveal de Bunki.
// Mismo patrón de interop que bunki-flatpickr.js / carrusel.js: expone un objeto global
// (window.bunkiReveal) y Blazor lo invoca con JS.InvokeVoidAsync("bunkiReveal.init") en
// OnAfterRenderAsync(firstRender), DESPUÉS del datepicker y del carrusel.
//
// Usa IntersectionObserver para detectar cuándo las secciones/tarjetas marcadas con [data-reveal]
// o [data-reveal-group] entran al viewport y les añade la clase .is-visible (que dispara el
// fade + translateY definido en styles.css). Una sola vez por elemento (unobserve tras revelar).
window.bunkiReveal = {
    init: function () {
        // Elementos a revelar: secciones sueltas y grupos con escalonado interno.
        var elementos = document.querySelectorAll('[data-reveal], [data-reveal-group]');
        if (!elementos.length) return; // robustez: si no hay nada que revelar, no hace nada

        // init() puede llamarse más de una vez: en firstRender y de nuevo cuando llega contenido
        // async (p.ej. los testimonios de la Home se renderizan tras cargar las reseñas, DESPUÉS
        // del primer render). Solo enganchamos los elementos que todavía no procesamos —marcados
        // con data-reveal-bound— para no crear observers duplicados sobre los ya revelados.
        var pendientes = Array.prototype.filter.call(elementos, function (el) {
            return !el.hasAttribute('data-reveal-bound');
        });
        if (!pendientes.length) return;
        pendientes.forEach(function (el) { el.setAttribute('data-reveal-bound', ''); });

        // Revela todo de inmediato (sin observar ni animar). Helper para los casos de salida temprana.
        function revelarTodo() {
            pendientes.forEach(function (el) { el.classList.add('is-visible'); });
        }

        // prefers-reduced-motion: respetamos la preferencia mostrando todo de una, sin movimiento.
        var reducir = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
        if (reducir) { revelarTodo(); return; }

        // Degradación elegante: si el navegador no soporta IntersectionObserver, mostramos todo.
        if (!('IntersectionObserver' in window)) { revelarTodo(); return; }

        var observer = new IntersectionObserver(function (entries, obs) {
            entries.forEach(function (entry) {
                if (entry.isIntersecting) {
                    entry.target.classList.add('is-visible');
                    obs.unobserve(entry.target); // una sola vez: no se vuelve a ocultar al re-scrollear
                }
            });
        }, {
            threshold: 0.12,                 // revela cuando ~12% del elemento es visible
            rootMargin: '0px 0px -5% 0px'    // dispara un poco antes de llegar al borde inferior
        });

        pendientes.forEach(function (el) { observer.observe(el); });
    }
};
