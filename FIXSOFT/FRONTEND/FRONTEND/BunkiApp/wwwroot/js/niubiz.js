// Lightbox de pago Niubiz (SANDBOX / QAS). Carga checkout.js bajo demanda y abre la pasarela.
// Esta versión de checkout.js usa el modelo de "action": al pagar, hace POST del transactionToken
// a la URL 'action'. Nuestro endpoint /niubiz/callback autoriza, crea la reserva y redirige.
(function () {
    var CHECKOUT_JS = "https://static-content-qas.vnforapps.com/v2/js/checkout.js";
    var cargando = null;

    function cargarCheckout() {
        if (window.VisanetCheckout) return Promise.resolve();
        if (cargando) return cargando;
        cargando = new Promise(function (resolve, reject) {
            var s = document.createElement("script");
            s.src = CHECKOUT_JS;
            s.async = true;
            s.onload = function () { resolve(); };
            s.onerror = function () { reject(new Error("No se pudo cargar checkout.js de Niubiz")); };
            document.head.appendChild(s);
        });
        return cargando;
    }

    window.bunkiNiubiz = {
        // cfg: { merchantId, sessionKey, purchaseNumber, amount, action }
        pagar: async function (cfg, dotNetRef) {
            try {
                await cargarCheckout();
                if (!window.VisanetCheckout) {
                    dotNetRef.invokeMethodAsync("OnNiubizError", "checkout.js no disponible");
                    return;
                }
                try { window.VisanetCheckout.reset && window.VisanetCheckout.reset(); } catch (e) {}

                window.VisanetCheckout.configure({
                    action: cfg.action,                 // POST del transactionToken a nuestro callback
                    merchantid: cfg.merchantId,
                    sessiontoken: cfg.sessionKey,
                    purchasenumber: String(cfg.purchaseNumber),
                    amount: cfg.amount,
                    expirationtime: 900,
                    timeouturl: cfg.action,
                    channel: "web",
                    showamount: true,
                    formbuttoncolor: "#1F6B5E"
                });
                window.VisanetCheckout.open();
            } catch (err) {
                dotNetRef.invokeMethodAsync("OnNiubizError", String(err && err.message ? err.message : err));
            }
        }
    };
})();
