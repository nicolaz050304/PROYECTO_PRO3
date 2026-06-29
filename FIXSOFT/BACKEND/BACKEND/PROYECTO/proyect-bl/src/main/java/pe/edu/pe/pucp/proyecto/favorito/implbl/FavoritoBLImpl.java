package pe.edu.pe.pucp.proyecto.favorito.implbl;

import pe.edu.pe.pucp.proyecto.favorito.bl.FavoritoBL;
import pe.edu.pe.pucp.proyecto.favorito.dao.FavoritoDAO;
import pe.edu.pe.pucp.proyecto.favorito.impl.FavoritoImpl;

import java.util.List;

public class FavoritoBLImpl implements FavoritoBL {

    private final FavoritoDAO dao = new FavoritoImpl();

    @Override
    public List<Integer> listarPorUsuario(int idUsuario) {
        if (idUsuario <= 0) return java.util.Collections.emptyList();
        return dao.listarIdsPorUsuario(idUsuario);
    }

    @Override
    public void agregar(int idUsuario, int idAlojamiento) {
        if (idUsuario <= 0 || idAlojamiento <= 0) return;
        dao.agregar(idUsuario, idAlojamiento);
    }

    @Override
    public void quitar(int idUsuario, int idAlojamiento) {
        if (idUsuario <= 0 || idAlojamiento <= 0) return;
        dao.quitar(idUsuario, idAlojamiento);
    }
}
