namespace BunkiApp.Models;

/// <summary>
/// DTO que refleja lo que devuelve GET TipoCambioRS del backend.
/// OJO con la semántica de TasaAPen: es "cuántos PEN vale 1 unidad de esta moneda"
/// (USD=3.75, EUR=4.05, PEN=1.0). Es el INVERSO de la "Tasa" que usa el frontend
/// (unidades de moneda por 1 PEN), así que al cargar hay que invertirla. Ver AppState.CargarTasasAsync.
/// </summary>
public class TipoCambioDto
{
    public string Codigo { get; set; } = "";
    public string Simbolo { get; set; } = "";
    public decimal TasaAPen { get; set; }
}
