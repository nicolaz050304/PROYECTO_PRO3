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

        // Revela todo de inmediato (sin observar ni animar). Helper para los casos de salida temprana.
        function revelarTodo() {
            elementos.forEach(function (el) { el.classList.add('is-visible'); });
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

        elementos.forEach(function (el) { observer.observe(el); });
    }
};
