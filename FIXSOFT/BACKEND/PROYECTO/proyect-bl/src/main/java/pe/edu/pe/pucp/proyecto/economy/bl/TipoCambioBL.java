package pe.edu.pe.pucp.proyecto.economy.bl;

import pe.edu.pe.pucp.proyecto.bl.IBL;
import pe.edu.pe.pucp.proyecto.economy.TipoCambio;

import java.util.List;

public interface TipoCambioBL extends IBL<TipoCambio, String> {
    // Lista todas las tasas (una fila por moneda). Delega al DAO.
    List<TipoCambio> listarTodas();

    // Para la fase de admin: actualiza simbolo y tasa de una moneda. Delega al DAO.
    void actualizar(TipoCambio tipoCambio);
}
