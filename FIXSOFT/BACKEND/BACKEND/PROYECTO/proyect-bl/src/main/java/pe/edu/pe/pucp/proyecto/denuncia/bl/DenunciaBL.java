package pe.edu.pe.pucp.proyecto.denuncia.bl;

import pe.edu.pe.pucp.proyecto.denuncia.Denuncia;

import java.util.List;

/** Lógica de negocio de denuncias (RF31). */
public interface DenunciaBL {

    /** Todas las denuncias para el panel del admin. */
    List<Denuncia> listarTodas();

    /** Valida y registra una denuncia (estado inicial EN_REVISION); devuelve su id. */
    int agregar(Denuncia denuncia);

    /** Cambia el estado de una denuncia (solo EN_REVISION / RESUELTO / CERRADO). */
    void cambiarEstado(int idDenuncia, String estado);
}
