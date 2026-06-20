package pe.edu.pe.pucp.proyecto.economy.implbl;

import pe.edu.pe.pucp.proyecto.economy.TipoCambio;
import pe.edu.pe.pucp.proyecto.economy.bl.TipoCambioBL;
import pe.edu.pe.pucp.proyecto.economy.dao.TipoCambioIDAO;
import pe.edu.pe.pucp.proyecto.economy.impl.TipoCambioImpl;

import java.util.List;

public class TipoCambioBLImpl implements TipoCambioBL {

    // El BL siempre llama al DAO para persistir datos
    private TipoCambioIDAO daoTipoCambio = new TipoCambioImpl();

    @Override
    public List<TipoCambio> listarTodas() {
        return daoTipoCambio.listarTodas();
    }

    @Override
    public void actualizar(TipoCambio tipoCambio) {
        daoTipoCambio.actualizar(tipoCambio);
    }

    // Implementación de métodos genéricos de IBL
    @Override public List<TipoCambio> listarTodos() { return daoTipoCambio.listarTodas(); }
    @Override public TipoCambio obtenerPorId(String codigo) { return daoTipoCambio.load(codigo); }
    @Override public int insertar(TipoCambio tipoCambio) { daoTipoCambio.save(tipoCambio); return 1; }
    @Override public int modificar(TipoCambio tipoCambio) { daoTipoCambio.actualizar(tipoCambio); return 1; }
    @Override public int eliminar(TipoCambio tipoCambio) { daoTipoCambio.remove(tipoCambio); return 1; }
}
