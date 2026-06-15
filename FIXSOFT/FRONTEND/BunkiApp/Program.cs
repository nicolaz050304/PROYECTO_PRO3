using BunkiApp.Components;
using BunkiApp.Models;
using BunkiApp.Services;

var builder = WebApplication.CreateBuilder(args);

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
    client.BaseAddress = new Uri("http://localhost:8080/BunkiBackend/webresources/");
    client.Timeout = TimeSpan.FromSeconds(5);
});
builder.Services.AddScoped<AlojamientoProvider>();

// --- Integración REST de Reserva ---
builder.Services.AddHttpClient<ReservaServiceRest>(client =>
{
    client.BaseAddress = new Uri("http://localhost:8080/BunkiBackend/webresources/");
    client.Timeout = TimeSpan.FromSeconds(5);
});
builder.Services.AddScoped<ReservaProvider>();

// --- Integración REST de Usuario ---
builder.Services.AddHttpClient<UsuarioServiceRest>(client =>
{
    client.BaseAddress = new Uri("http://localhost:8080/BunkiBackend/webresources/");
    client.Timeout = TimeSpan.FromSeconds(5);
});
builder.Services.AddScoped<UsuarioProvider>();

// --- Integración REST de Pago (transacción NO cableada; lista para historial futuro) ---
builder.Services.AddHttpClient<PagoServiceRest>(client =>
{
    client.BaseAddress = new Uri("http://localhost:8080/BunkiBackend/webresources/");
    client.Timeout = TimeSpan.FromSeconds(5);
});
builder.Services.AddScoped<PagoProvider>();

// --- Integración REST de Reseña (creación NO cableada; reseñas se leen vía Alojamiento) ---
builder.Services.AddHttpClient<ResenaServiceRest>(client =>
{
    client.BaseAddress = new Uri("http://localhost:8080/BunkiBackend/webresources/");
    client.Timeout = TimeSpan.FromSeconds(5);
});
builder.Services.AddScoped<ResenaProvider>();

var app = builder.Build();

if (!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/Error", createScopeForErrors: true);
    app.UseHsts();
}

app.UseHttpsRedirection();
app.UseStaticFiles();
app.UseAntiforgery();

app.MapRazorComponents<App>()
   .AddInteractiveServerRenderMode();

app.Run();