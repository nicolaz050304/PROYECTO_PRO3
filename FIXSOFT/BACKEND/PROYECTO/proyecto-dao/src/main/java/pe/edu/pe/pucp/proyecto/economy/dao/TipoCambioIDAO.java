package pe.edu.pe.pucp.proyecto.economy.dao;

import pe.edu.pe.pucp.proyecto.dao.IDAO;
import pe.edu.pe.pucp.proyecto.economy.TipoCambio;

import java.util.List;

public interface TipoCambioIDAO extends IDAO<TipoCambio, String> {
    // Lista todas las tasas (una fila por moneda).
    List<TipoCambio> listarTodas();

    // Para la fase de admin: actualiza simbolo y tasa de una moneda por su codigo.
    void actualizar(TipoCambio tipoCambio);
}
