// Count-up de KPIs (Panel de Anfitrión y reutilizable).
// Sigue el patrón de interop del proyecto: objeto global window.bunkiCountUp, invocado desde Blazor con
// JS.InvokeVoidAsync("bunkiCountUp.init", "[data-countup]") en OnAfterRenderAsync(firstRender).
//
// Anima los números clave de 0 a su valor para dar vida al dashboard al cargar. ROBUSTO:
//   - El destino numérico viene en data-countup (número invariante: sin ambigüedad de separadores).
//   - El prefijo/sufijo NO numérico (ej. "S/ ", "%", "- ") se derivan del texto que ya pintó Blazor.
//   - En el último frame se RESTAURA el texto exacto de Blazor → nunca hay desajuste de formato/moneda.
//   - prefers-reduced-motion: NO anima (se deja el valor final). try/catch por elemento: si algo falla,
//     ese número se queda con su valor final y la app sigue normal.
window.bunkiCountUp = {
    init: function (selector) {
        try {
            var els = document.querySelectorAll(selector || "[data-countup]");
            if (!els.length) return;

            var reducir = window.matchMedia && window.matchMedia("(prefers-reduced-motion: reduce)").matches;
            if (reducir) return;   // sin movimiento: los valores ya están pintados por Blazor

            els.forEach(function (el) {
                try {
                    var destino = parseFloat(el.getAttribute("data-countup"));
                    if (!isFinite(destino)) return;

                    var decimales = parseInt(el.getAttribute("data-countup-decimals") || "0", 10) || 0;
                    var textoFinal = el.textContent;   // exacto, tal como lo renderizó Blazor (moneda incluida)

                    // Prefijo = lo previo al primer dígito; sufijo = lo posterior al último dígito.
                    var primero = textoFinal.search(/\d/);
                    if (primero < 0) return;
                    var ultimo = primero;
                    for (var i = textoFinal.length - 1; i >= 0; i--) {
                        if (textoFinal[i] >= "0" && textoFinal[i] <= "9") { ultimo = i; break; }
                    }
                    var prefijo = textoFinal.slice(0, primero);
                    var sufijo = textoFinal.slice(ultimo + 1);

                    function fmt(v) {
                        return v.toLocaleString("es-PE", {
                            minimumFractionDigits: decimales,
                            maximumFractionDigits: decimales
                        });
                    }

                    var DURACION = 900;
                    var inicio = null;
                    function paso(ts) {
                        if (inicio === null) inicio = ts;
                        var p = Math.min((ts - inicio) / DURACION, 1);
                        var eased = 1 - Math.pow(1 - p, 3);   // ease-out cúbico (arranca rápido, asienta suave)
                        var val = destino * eased;
                        el.textContent = prefijo + fmt(decimales ? val : Math.round(val)) + sufijo;
                        if (p < 1) {
                            requestAnimationFrame(paso);
                        } else {
                            el.textContent = textoFinal;       // snap al texto exacto: cero desajustes
                        }
                    }

                    el.textContent = prefijo + fmt(0) + sufijo;
                    requestAnimationFrame(paso);
                } catch (e) { /* este número se queda con su valor final */ }
            });
        } catch (e) { /* nunca romper la app por el count-up */ }
    }
};
