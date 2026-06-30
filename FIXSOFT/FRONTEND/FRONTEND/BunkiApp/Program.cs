using BunkiApp.Components;
using BunkiApp.Models;
using BunkiApp.Services;
using BunkiApp.Security;

var builder = WebApplication.CreateBuilder(args);

// URL del backend REST. UNA sola fuente para TODO: las llamadas server-side (HttpClient de abajo)
// y los enlaces de descarga del navegador (BackendUrls). Default localhost para desarrollo (todo en
// una máquina). En el DEPLOY el frontend y el backend están en instancias distintas, así que se
// setea la variable de entorno BUNKI_BACKEND_URL con la IP pública del backend, p. ej.:
//   BUNKI_BACKEND_URL=http://3.89.203.116:8080/BunkiBackend/webresources/
var backendUrl = Environment.GetEnvironmentVariable("BUNKI_BACKEND_URL")
                 ?? builder.Configuration["Backend:Url"]
                 ?? "http://localhost:8080/BunkiBackend/webresources/";
if (!backendUrl.EndsWith("/")) backendUrl += "/";

// Agregar servicios de Blazor
builder.Services.AddRazorComponents()
    .AddInteractiveServerComponents();

// Registro de servicios de la aplicación
builder.Services.AddSingleton<DataService>();
builder.Services.AddScoped<AppState>(); // <-- ¡Perfecto! Ahora es un servicio Scoped por usuario
builder.Services.AddScoped<MapInterop>(); // Puente C#->JS del mapa (Leaflet)

// --- Integración REST de Alojamiento ---
builder.Services.AddHttpClient<AlojamientoServiceRest>(client =>
{
    client.BaseAddress = new Uri(backendUrl);
    client.Timeout = TimeSpan.FromSeconds(20);
});
builder.Services.AddScoped<AlojamientoProvider>();

// --- Integración REST de Reserva ---
builder.Services.AddHttpClient<ReservaServiceRest>(client =>
{
    client.BaseAddress = new Uri(backendUrl);
    client.Timeout = TimeSpan.FromSeconds(20);
});
builder.Services.AddScoped<ReservaProvider>();

// --- Integración REST de Usuario ---
builder.Services.AddHttpClient<UsuarioServiceRest>(client =>
{
    client.BaseAddress = new Uri(backendUrl);
    client.Timeout = TimeSpan.FromSeconds(20);
});
builder.Services.AddScoped<UsuarioProvider>();

// --- Integración REST de Pago (transacción NO cableada; lista para historial futuro) ---
builder.Services.AddHttpClient<PagoServiceRest>(client =>
{
    client.BaseAddress = new Uri(backendUrl);
    client.Timeout = TimeSpan.FromSeconds(20);
});
builder.Services.AddScoped<PagoProvider>();

// --- Integración REST de Reseña (creación NO cableada; reseñas se leen vía Alojamiento) ---
builder.Services.AddHttpClient<ResenaServiceRest>(client =>
{
    client.BaseAddress = new Uri(backendUrl);
    client.Timeout = TimeSpan.FromSeconds(20);
});
builder.Services.AddScoped<ResenaProvider>();

// --- Integración REST de Tipo de Cambio (tasas de moneda; AppState las carga una vez al inicio) ---
builder.Services.AddHttpClient<TipoCambioServiceRest>(client =>
{
    client.BaseAddress = new Uri(backendUrl);
    client.Timeout = TimeSpan.FromSeconds(20);
});

// --- Integración REST de Cuentas Bancarias del anfitrión (RF15) ---
builder.Services.AddHttpClient<CuentaBancariaServiceRest>(client =>
{
    client.BaseAddress = new Uri(backendUrl);
    client.Timeout = TimeSpan.FromSeconds(20);
});

// --- Integración REST de Mensajería (RF17): chat por reserva ---
builder.Services.AddHttpClient<MensajeServiceRest>(client =>
{
    client.BaseAddress = new Uri(backendUrl);
    client.Timeout = TimeSpan.FromSeconds(20);
});

// --- Integración REST de Notificaciones (RF18): feed y badge por usuario ---
builder.Services.AddHttpClient<NotificacionServiceRest>(client =>
{
    client.BaseAddress = new Uri(backendUrl);
    client.Timeout = TimeSpan.FromSeconds(20);
});

// --- Integración REST de Favoritos (RF27): wishlist persistente por usuario ---
builder.Services.AddHttpClient<FavoritoServiceRest>(client =>
{
    client.BaseAddress = new Uri(backendUrl);
    client.Timeout = TimeSpan.FromSeconds(20);
});

// --- Integración REST de Bloqueos de fecha (RF30): el anfitrión bloquea rangos del calendario ---
builder.Services.AddHttpClient<BloqueoServiceRest>(client =>
{
    client.BaseAddress = new Uri(backendUrl);
    client.Timeout = TimeSpan.FromSeconds(20);
});

// --- Integración REST de Denuncias (RF31): el huésped reporta alojamientos, el admin gestiona ---
builder.Services.AddHttpClient<DenunciaServiceRest>(client =>
{
    client.BaseAddress = new Uri(backendUrl);
    client.Timeout = TimeSpan.FromSeconds(20);
});

// --- Integración REST de Incidencias/soporte: usuarios abren tickets, el admin los gestiona ---
builder.Services.AddHttpClient<IncidenciaServiceRest>(client =>
{
    client.BaseAddress = new Uri(backendUrl);
    client.Timeout = TimeSpan.FromSeconds(20);
});

// --- Integración REST de validación documentaria (RF02): el usuario sube su DNI/pasaporte y el admin revisa ---
builder.Services.AddHttpClient<ValidacionServiceRest>(client =>
{
    client.BaseAddress = new Uri(backendUrl);
    client.Timeout = TimeSpan.FromSeconds(30);   // el documento viaja en base64 (puede ser grande)
});

builder.Services.AddHttpClient<AuditoriaServiceRest>(client =>
{
    client.BaseAddress = new Uri(backendUrl);
});

// Construye las rutas del proxy de descargas (comprobante, voucher, reportes). Ya NO son URLs al
// backend: apuntan al endpoint local "/descargas" (ver más abajo), que reenvía la descarga.
builder.Services.AddSingleton<BackendUrls>();

// HttpClient server-side del PROXY de descargas: baja el archivo del backend por la red INTERNA
// (mismo backendUrl que el resto de llamadas server-side). Así el backend no se expone a internet.
builder.Services.AddHttpClient("backend-download", c => c.BaseAddress = new Uri(backendUrl));

// Envío de correos (SMTP Gmail). Credenciales por env var BUNKI_GMAIL_USER/APP_PASSWORD; si no
// están, el servicio queda desactivado (no envía, no rompe). Ver EmailService.
builder.Services.AddSingleton<EmailService>();

// --- Integración REST de pago Niubiz (sandbox): sesión + autorización de la pasarela ---
builder.Services.AddHttpClient<NiubizServiceRest>(client =>
{
    client.BaseAddress = new Uri(backendUrl);
    client.Timeout = TimeSpan.FromSeconds(45);   // la pasarela sandbox puede tardar
});

// --- Autenticación por cookie (ClaimsPrincipal) ---
builder.Services.AddCookieAuth();

var app = builder.Build();

if (!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/Error", createScopeForErrors: true);
    app.UseHsts();
}

// Para URLs no encontradas (404), re-ejecuta la petición hacia la página /404 con marca,
// conservando el código de estado 404. Va antes de los archivos estáticos para no afectarlos.
app.UseStatusCodePagesWithReExecute("/404");

app.UseHttpsRedirection();
app.UseStaticFiles();

app.UseAuthentication();
app.UseAuthorization();

app.UseAntiforgery();

app.MapRazorComponents<App>()
   .AddInteractiveServerRenderMode();

app.MapAuthEndpoints();

// --- Proxy de descargas: el navegador pide el PDF/CSV AL FRONTEND (que ya es público) y este lo
// baja del backend por la red INTERNA y lo reenvía. Así el puerto del backend NO necesita abrirse a
// internet (solo accesible desde el frontend). El `recurso` viaja URL-encoded e incluye su propio
// query (p. ej. "ReporteRS/voucher/pdf?reserva=5"). Allowlist estricta para NO ser un proxy abierto
// (evita SSRF): solo se permiten recursos de reportes y comprobantes. RequireAuthorization: la cookie
// de sesión viaja en la navegación same-origin, así que un anónimo de internet no puede jalar nada. ---
app.MapGet("/descargas", async (HttpContext http, IHttpClientFactory httpFactory, string? recurso) =>
{
    if (string.IsNullOrWhiteSpace(recurso)
        || recurso.Contains("..") || recurso.Contains("://")
        || !(recurso.StartsWith("ReporteRS/") || recurso.StartsWith("PagoRS/")))
    {
        return Results.BadRequest("Recurso de descarga no permitido.");
    }

    var client = httpFactory.CreateClient("backend-download");
    HttpResponseMessage resp;
    try
    {
        resp = await client.GetAsync(recurso, HttpCompletionOption.ResponseHeadersRead);
    }
    catch
    {
        // Backend caído o inalcanzable desde el frontend.
        return Results.StatusCode(StatusCodes.Status502BadGateway);
    }

    if (!resp.IsSuccessStatusCode)
        return Results.StatusCode((int)resp.StatusCode);

    var bytes = await resp.Content.ReadAsByteArrayAsync();
    var contentType = resp.Content.Headers.ContentType?.ToString() ?? "application/octet-stream";
    // Preserva el nombre de archivo/adjunto que mande el backend (Content-Disposition), si lo trae.
    var cd = resp.Content.Headers.ContentDisposition?.ToString();
    if (!string.IsNullOrEmpty(cd))
        http.Response.Headers.ContentDisposition = cd;
    return Results.File(bytes, contentType);
}).RequireAuthorization();

// --- Callback de Niubiz (RF13): el lightbox hace POST del transactionToken aquí (modelo "action").
// Autoriza el pago, crea/confirma la reserva y registra el pago, luego redirige al modal de Pagos.
// DisableAntiforgery: el POST viene del lightbox de Niubiz, sin token antiforgery. ---
app.MapPost("/niubiz/callback", async (
        HttpContext http,
        BunkiApp.Services.NiubizServiceRest niubiz,
        BunkiApp.Services.ReservaProvider reservaProv,
        BunkiApp.Services.PagoProvider pagoProv) =>
{
    var form = await http.Request.ReadFormAsync();
    var transactionToken = form["transactionToken"].ToString();
    var q = http.Request.Query;

    string Inv(string k) => q[k].ToString();
    decimal amount = decimal.TryParse(Inv("amount"), System.Globalization.NumberStyles.Any,
        System.Globalization.CultureInfo.InvariantCulture, out var a) ? a : 0m;
    int alojamientoId = int.TryParse(Inv("aloj"), out var ai) ? ai : 0;
    int huespedId = int.TryParse(Inv("huesped"), out var hi) ? hi : 0;
    int huespedes = int.TryParse(Inv("huespedes"), out var hh) ? hh : 1;
    int reservaId = int.TryParse(Inv("reservaId"), out var ri) ? ri : 0;
    string pn = Inv("pn");
    DateTime.TryParse(Inv("entrada"), System.Globalization.CultureInfo.InvariantCulture,
        System.Globalization.DateTimeStyles.None, out var entrada);
    DateTime.TryParse(Inv("salida"), System.Globalization.CultureInfo.InvariantCulture,
        System.Globalization.DateTimeStyles.None, out var salida);

    string ctx = $"alojamientoId={alojamientoId}&entrada={entrada:yyyy-MM-dd}&salida={salida:yyyy-MM-dd}&huespedes={huespedes}";

    bool aprobado = false;
    string motivo = "";
    if (!string.IsNullOrEmpty(transactionToken))
    {
        try
        {
            var auth = await niubiz.AutorizarAsync(transactionToken, pn, amount);
            aprobado = auth.Aprobado;
            if (!aprobado)
            {
                var det = auth.Detalle ?? "";
                var m = System.Text.RegularExpressions.Regex.Match(det, "\"errorMessage\"\\s*:\\s*\"([^\"]+)\"");
                motivo = m.Success ? m.Groups[1].Value : det;
                // Adjunta el ACTION_CODE de Niubiz si está, para diagnóstico (p. ej. 'REJECT' + código).
                var ac = System.Text.RegularExpressions.Regex.Match(det, "\"ACTION_CODE\"\\s*:\\s*\"([^\"]+)\"");
                if (ac.Success) motivo += $" (ACTION_CODE {ac.Groups[1].Value})";
                if (motivo.Length > 200) motivo = motivo.Substring(0, 200);
            }
        }
        catch (Exception ex) { motivo = ex.Message; }
    }
    else
    {
        motivo = "No se recibió el token de la tarjeta.";
    }
    if (!aprobado)
    {
        // Pago rechazado por la pasarela: si se estaba pagando una reserva YA creada, la cancelamos
        // (no dejamos reservas sin pagar colgando). Para una reserva nueva no hay nada que cancelar.
        if (reservaId > 0)
        {
            try { await reservaProv.CancelarAsync(reservaId); motivo += " La reserva fue cancelada."; }
            catch { /* si no se pudo cancelar, igual avisamos del rechazo */ }
        }
        return Results.Redirect($"/pagos?{ctx}&pagoError=1&motivo={Uri.EscapeDataString(motivo)}");
    }

    int creada = reservaId;
    try
    {
        if (reservaId > 0)
        {
            var ex = await reservaProv.ObtenerPorIdAsync(reservaId);
            if (ex is not null) { ex.Estado = "Pendiente"; await reservaProv.ActualizarAsync(ex); }
        }
        else
        {
            var nueva = new BunkiApp.Models.Reserva
            {
                AlojamientoId = alojamientoId,
                HuespedId = huespedId,
                FechaEntrada = entrada,
                FechaSalida = salida,
                NumHuespedes = huespedes,
                Total = amount,
                Estado = "Pendiente"
            };
            creada = await reservaProv.RegistrarAsync(nueva);
            if (creada > 0) await pagoProv.RegistrarPagoDeReservaAsync(creada);
        }
    }
    catch { /* si falla la persistencia, igual el pago fue aprobado; el usuario verá la confirmación */ }

    return Results.Redirect($"/pagos?{ctx}&pagoOk=1&reservaCreada={creada}");
}).DisableAntiforgery();

app.Run();