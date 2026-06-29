// Accesibilidad para TODOS los modales (.modal-overlay) sin tocar cada componente:
// - role="dialog" + aria-modal + aria-labelledby
// - foco inicial dentro del panel y restauración del foco al cerrar
// - trampa de foco (Tab/Shift+Tab no se escapan del modal)
// - cerrar con Escape (dispara el botón de cierre del propio modal, así Blazor actualiza su estado)
(function () {
    var lastFocused = null;

    function visibles(panel) {
        var sel = 'a[href], button:not([disabled]), input:not([disabled]), textarea:not([disabled]), select:not([disabled]), [tabindex]:not([tabindex="-1"])';
        return Array.prototype.slice.call(panel.querySelectorAll(sel))
            .filter(function (el) { return el.offsetParent !== null; });
    }

    // Botón de cierre del modal (X / Cancelar / Cerrar). Devuelve null si no hay.
    function botonCierre(panel) {
        var btn = panel.querySelector('[data-modal-close]')
            || panel.querySelector('button[aria-label="Cerrar"], button[title="Cerrar"]');
        if (btn) return btn;
        var botones = Array.prototype.slice.call(panel.querySelectorAll('button'));
        return botones.find(function (b) {
            return /^(cancelar|cerrar|cerrar caso|no, volver|volver)$/i.test((b.textContent || '').trim());
        }) || null;
    }

    function realzar(overlay) {
        if (overlay.__a11y) return;
        overlay.__a11y = true;

        var panel = overlay.querySelector('.modal-panel') || overlay;
        panel.setAttribute('role', 'dialog');
        panel.setAttribute('aria-modal', 'true');
        if (!panel.hasAttribute('tabindex')) panel.setAttribute('tabindex', '-1');

        var titulo = panel.querySelector('h1, h2, h3');
        if (titulo) {
            if (!titulo.id) titulo.id = 'modal-title-' + Math.floor(Math.random() * 1e9).toString(36);
            panel.setAttribute('aria-labelledby', titulo.id);
        }

        lastFocused = document.activeElement;
        var f = visibles(panel);
        (f[0] || panel).focus();

        // Cerrar al hacer clic en el fondo (backdrop), de forma consistente en todos los modales.
        overlay.__click = function (e) {
            if (e.target === overlay) {
                var b = botonCierre(panel);
                if (b) b.click();   // solo botones (no el overlay) para evitar bucles
            }
        };
        overlay.addEventListener('click', overlay.__click);

        overlay.__key = function (e) {
            if (e.key === 'Escape') {
                e.preventDefault();
                var t = botonCierre(panel) || overlay;   // para Escape sí vale el overlay como último recurso
                if (t) t.click();
            } else if (e.key === 'Tab') {
                var items = visibles(panel);
                if (!items.length) { e.preventDefault(); panel.focus(); return; }
                var first = items[0], last = items[items.length - 1];
                if (e.shiftKey && document.activeElement === first) { e.preventDefault(); last.focus(); }
                else if (!e.shiftKey && document.activeElement === last) { e.preventDefault(); first.focus(); }
            }
        };
        overlay.addEventListener('keydown', overlay.__key);
    }

    function restaurarFoco() {
        if (lastFocused && typeof lastFocused.focus === 'function') {
            try { lastFocused.focus(); } catch (e) {}
        }
        lastFocused = null;
    }

    var obs = new MutationObserver(function (muts) {
        muts.forEach(function (m) {
            m.addedNodes.forEach(function (n) {
                if (n.nodeType !== 1) return;
                if (n.classList && n.classList.contains('modal-overlay')) realzar(n);
                else if (n.querySelectorAll) n.querySelectorAll('.modal-overlay').forEach(realzar);
            });
            m.removedNodes.forEach(function (n) {
                if (n.nodeType === 1 && n.classList && n.classList.contains('modal-overlay')) restaurarFoco();
            });
        });
    });

    function arrancar() { obs.observe(document.body, { childList: true, subtree: true }); }
    if (document.body) arrancar();
    else document.addEventListener('DOMContentLoaded', arrancar);
})();
