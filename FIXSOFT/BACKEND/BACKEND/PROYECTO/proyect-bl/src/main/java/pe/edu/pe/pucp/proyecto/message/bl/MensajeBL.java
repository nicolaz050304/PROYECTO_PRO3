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

    // Mensajes del chat de UNA reserva (el chat se organiza por reserva), ordenados por fecha ASC.
    List<Mensaje> listarPorChat(int idReserva);

    // Marca como leídos los mensajes de esa reserva dirigidos al lector (no los suyos).
    void marcarLeidos(int idReserva, int idUsuarioLector);

    // Cuántos mensajes no leídos tiene el usuario en esa reserva (badge tipo WhatsApp).
    int contarNoLeidos(int idReserva, int idUsuario);
}