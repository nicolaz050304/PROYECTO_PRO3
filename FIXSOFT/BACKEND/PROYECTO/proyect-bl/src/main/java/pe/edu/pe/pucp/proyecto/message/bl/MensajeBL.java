package pe.edu.pe.pucp.proyecto.message.bl;

import pe.edu.pe.pucp.proyecto.bl.IBL;
import pe.edu.pe.pucp.proyecto.messages.Mensaje;

import java.util.List;
// Aquí IntelliJ te pondrá tu import correcto cuando hagas Alt + Enter


public interface MensajeBL extends IBL<Mensaje, Integer> {

    int insertar(Mensaje mensaje);

    List<Mensaje> listarTodos();

    Mensaje obtenerPorId(Integer id);

    int modificar(Mensaje mensaje);

    int eliminar(Mensaje mensaje);
}