// Toggle mostrar/ocultar contraseña para formularios de render ESTÁTICO (Login, que es un POST plano sin
// interactividad Blazor). Recibe el botón; alterna el type del input dentro de su .pw-field y el ícono.
// (Registro sí es interactivo y hace el toggle con estado Blazor, sin este archivo.)
window.bunkiPw = {
    toggle: function (btn) {
        try {
            var wrap = btn.closest(".pw-field");
            if (!wrap) return;
            var input = wrap.querySelector("input");
            if (!input) return;
            var oculto = input.type === "password";
            input.type = oculto ? "text" : "password";
            var icon = btn.querySelector("i");
            if (icon) icon.className = oculto ? "fas fa-eye-slash" : "fas fa-eye";
            btn.setAttribute("aria-label", oculto ? "Ocultar contraseña" : "Mostrar contraseña");
        } catch (e) { /* nunca romper el formulario por el toggle */ }
    }
};
