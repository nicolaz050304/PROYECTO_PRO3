// Carrusel de alojamientos destacados del Home.
// Sigue el MISMO patrón de JS interop que bunki-flatpickr.js: expone un objeto global
// (window.bunkiCarrusel) e Blazor lo invoca con JS.InvokeVoidAsync en OnAfterRenderAsync(firstRender).
// Solo controla el desplazamiento, las flechas, los puntos y el autoplay; las tarjetas las renderiza
// Blazor con <AlojamientoCard>, así se ven idénticas a las del resto de la app.
window.bunkiCarrusel = {
    init: function (selectorContenedor) {
        var contenedor = document.querySelector(selectorContenedor);
        if (!contenedor) return; // robustez: si el contenedor no existe, no rompe

        var track = contenedor.querySelector('.carrusel-track');
        if (!track) return;

        var prev = contenedor.querySelector('.carrusel-flecha--prev');
        var next = contenedor.querySelector('.carrusel-flecha--next');
        var dotsCont = contenedor.querySelector('.carrusel-dots');

        // Si se re-inicializa, limpia el estado anterior (timers/listeners) para no duplicarlos.
        if (contenedor._bunkiCarrusel) {
            contenedor._bunkiCarrusel.destroy();
        }

        // prefers-reduced-motion: sin autoplay y sin scroll animado para quien pide menos movimiento.
        var reducir = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
        var AUTOPLAY_MS = 5000;
        var timer = null;

        function clientW() { return track.clientWidth; }
        function paginas() { return Math.max(1, Math.ceil(track.scrollWidth / clientW())); }
        function paginaActual() { return Math.round(track.scrollLeft / clientW()); }
        function hayOverflow() { return track.scrollWidth - track.clientWidth > 2; }

        function irAPagina(i) {
            var total = paginas();
            if (i < 0) i = total - 1;     // al inicio: vuelve al final
            if (i >= total) i = 0;        // al final: vuelve al inicio (loop)
            track.scrollTo({ left: i * clientW(), behavior: reducir ? 'auto' : 'smooth' });
        }

        function construirDots() {
            if (!dotsCont) return;
            dotsCont.innerHTML = '';
            var total = paginas();
            for (var i = 0; i < total; i++) {
                var d = document.createElement('button');
                d.type = 'button';
                d.className = 'carrusel-dot';
                d.setAttribute('aria-label', 'Ir a la página ' + (i + 1));
                (function (idx) {
                    d.addEventListener('click', function () { irAPagina(idx); reiniciarAutoplay(); });
                })(i);
                dotsCont.appendChild(d);
            }
            actualizarDots();
        }

        function actualizarDots() {
            if (!dotsCont) return;
            var activo = paginaActual();
            var dots = dotsCont.querySelectorAll('.carrusel-dot');
            for (var i = 0; i < dots.length; i++) {
                dots[i].classList.toggle('carrusel-dot--activo', i === activo);
            }
        }

        function mostrarControles(visible) {
            [prev, next, dotsCont].forEach(function (el) {
                if (el) el.style.display = visible ? '' : 'none';
            });
        }

        // ---- Autoplay: avanza solo cada 5s, pausa al hover, respeta reduced-motion ----
        function iniciarAutoplay() {
            if (reducir || !hayOverflow()) return; // sin autoplay si reduced-motion o si todo cabe
            detenerAutoplay();
            timer = setInterval(function () { irAPagina(paginaActual() + 1); }, AUTOPLAY_MS);
        }
        function detenerAutoplay() {
            if (timer) { clearInterval(timer); timer = null; }
        }
        function reiniciarAutoplay() { detenerAutoplay(); iniciarAutoplay(); }

        // ---- Listeners ----
        function onPrev() { irAPagina(paginaActual() - 1); reiniciarAutoplay(); }
        function onNext() { irAPagina(paginaActual() + 1); reiniciarAutoplay(); }
        if (prev) prev.addEventListener('click', onPrev);
        if (next) next.addEventListener('click', onNext);
        track.addEventListener('scroll', actualizarDots, { passive: true });
        contenedor.addEventListener('mouseenter', detenerAutoplay); // pausa al pasar el mouse
        contenedor.addEventListener('mouseleave', iniciarAutoplay); // reanuda al salir

        function onResize() { construirDots(); ajustar(); }
        window.addEventListener('resize', onResize);

        // Si todas las tarjetas caben sin scroll, oculta flechas/puntos y no hay autoplay.
        function ajustar() {
            if (hayOverflow()) { mostrarControles(true); }
            else { mostrarControles(false); detenerAutoplay(); }
        }

        // Estado para limpieza en re-init.
        contenedor._bunkiCarrusel = {
            destroy: function () {
                detenerAutoplay();
                if (prev) prev.removeEventListener('click', onPrev);
                if (next) next.removeEventListener('click', onNext);
                track.removeEventListener('scroll', actualizarDots);
                contenedor.removeEventListener('mouseenter', detenerAutoplay);
                contenedor.removeEventListener('mouseleave', iniciarAutoplay);
                window.removeEventListener('resize', onResize);
            }
        };

        construirDots();
        ajustar();
        iniciarAutoplay();
    }
};
