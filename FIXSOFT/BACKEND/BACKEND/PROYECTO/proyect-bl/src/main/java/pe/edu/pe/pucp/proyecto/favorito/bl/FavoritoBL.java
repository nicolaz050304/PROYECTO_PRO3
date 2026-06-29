package pe.edu.pe.pucp.proyecto.favorito.bl;

import java.util.List;

/** Lógica de negocio de Favoritos (RF27). */
public interface FavoritoBL {
    List<Integer> listarPorUsuario(int idUsuario);
    void agregar(int idUsuario, int idAlojamiento);
    void quitar(int idUsuario, int idAlojamiento);
}
