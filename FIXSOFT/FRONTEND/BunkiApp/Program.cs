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

builder.Services.AddScoped<MapInterop>();

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

