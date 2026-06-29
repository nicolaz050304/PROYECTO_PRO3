package pe.edu.pe.pucp.proyecto.denuncia.dao;

import pe.edu.pe.pucp.proyecto.denuncia.Denuncia;

import java.util.List;

/** DAO de denuncias de huéspedes contra alojamientos (RF31). */
public interface DenunciaDAO {

    /** Todas las denuncias, de la más reciente a la más antigua (para el admin). */
    List<Denuncia> listarTodas();

    /** Inserta una denuncia y devuelve su id generado (0 si no se pudo). */
    int agregar(Denuncia denuncia);

    /** Cambia el estado de una denuncia. */
    void actualizarEstado(int idDenuncia, String estado);
}
