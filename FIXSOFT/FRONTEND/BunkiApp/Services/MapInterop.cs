using Microsoft.JSInterop;
using BunkiApp.Models;

namespace BunkiApp.Services;

public class MapInterop(IJSRuntime js)
{
    private readonly IJSRuntime _js = js;

    public async Task InitAsync(string elementId)
    {
        try { await _js.InvokeVoidAsync("bunki.mapa.init", elementId); }
        catch (JSException ex) { Console.WriteLine($"[MapInterop] {ex.Message}"); }
    }

    public async Task ActualizarMarcadoresAsync(IEnumerable<Alojamiento> alojamientos)
    {
        try
        {
            var payload = alojamientos.Select(a => new {
                a.Id,
                a.Nombre,
                a.Distrito,
                a.PrecioNoche,
                a.Rating,
                a.TotalResenas,
                ImagenUrl = a.ImagenUrl ?? string.Empty
            });
            await _js.InvokeVoidAsync("bunki.mapa.actualizarMarcadores", payload);
        }
        catch (JSException ex) { Console.WriteLine($"[MapInterop] {ex.Message}"); }
    }

    public async Task ResaltarDistritoAsync(string distrito)
    {
        try { await _js.InvokeVoidAsync("bunki.mapa.resaltarDistrito", distrito); }
        catch (JSException ex) { Console.WriteLine($"[MapInterop] {ex.Message}"); }
    }

    public async Task LimpiarDistritoAsync()
    {
        try { await _js.InvokeVoidAsync("bunki.mapa.limpiarDistrito"); }
        catch { }
    }

    public async Task InvalidarTamanoAsync()
    {
        try { await _js.InvokeVoidAsync("bunki.mapa.invalidarTamano"); }
        catch { }
    }
}