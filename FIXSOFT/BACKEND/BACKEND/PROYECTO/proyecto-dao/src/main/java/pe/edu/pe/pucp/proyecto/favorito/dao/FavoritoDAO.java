package pe.edu.pe.pucp.proyecto.favorito.dao;

import java.util.List;

/** DAO de Favoritos (RF27): relación usuario - alojamiento marcado como favorito. */
public interface FavoritoDAO {

    /** Ids de alojamientos marcados como favoritos por un usuario. */
    List<Integer> listarIdsPorUsuario(int idUsuario);

    /** Marca un alojamiento como favorito (idempotente: ignora duplicados). */
    void agregar(int idUsuario, int idAlojamiento);

    /** Quita un alojamiento de favoritos. */
    void quitar(int idUsuario, int idAlojamiento);
}
