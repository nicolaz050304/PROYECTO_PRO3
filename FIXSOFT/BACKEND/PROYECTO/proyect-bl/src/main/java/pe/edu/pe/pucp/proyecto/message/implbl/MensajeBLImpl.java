package pe.edu.pe.pucp.proyecto.message.implbl;

import pe.edu.pe.pucp.proyecto.menssage.dao.MensajeIDAO;
import pe.edu.pe.pucp.proyecto.menssage.impl.MensajeImpl;
import pe.edu.pe.pucp.proyecto.message.bl.MensajeBL;
import pe.edu.pe.pucp.proyecto.messages.Mensaje;

import java.util.List;

public class MensajeBLImpl implements MensajeBL {

    private MensajeIDAO daoMensaje = new MensajeImpl();

    @Override
    public int insertar(Mensaje mensaje) {
        // --- LÓGICA DE NEGOCIO: VALIDACIONES DEL CHAT ---

        // 1. Validar que el mensaje no esté vacío
        if (mensaje.getTexto() == null || mensaje.getTexto().trim().isEmpty()) {
            throw new RuntimeException("Error: No se puede enviar un mensaje vacío.");
        }

        // 2. Validar que no exceda un límite de caracteres (ej. 500 para evitar spam)
        if (mensaje.getTexto().length() > 500) {
            throw new RuntimeException("Error: El mensaje es demasiado largo (máximo 500 caracteres).");
        }

        // 3. Validar que exista el emisor y la reserva asociada
        if (mensaje.getEmisor() == null || mensaje.getReserva() == null) {
            throw new RuntimeException("Error: El mensaje debe tener un emisor y una reserva válidos.");
        }

        return daoMensaje.save(mensaje).getIdMensaje();
    }

    @Override
    public List<Mensaje> listarTodos() {
        return daoMensaje.listAll();
    }

    @Override
    public Mensaje obtenerPorId(Integer id) {
        return daoMensaje.load(id);
    }

    @Override
    public int modificar(Mensaje mensaje) {
        // Por regla general en aplicaciones de chat (como WhatsApp sin editar),
        // a veces no se permite modificar. Si tu app lo permite, validamos igual:
        if (mensaje.getTexto() == null || mensaje.getTexto().trim().isEmpty()) {
            throw new RuntimeException("Error: El mensaje modificado no puede estar vacío.");
        }
        return daoMensaje.update(mensaje).getIdMensaje();
    }

    @Override
    public int eliminar(Mensaje mensaje) {
        daoMensaje.remove(mensaje);
        return 1;
    }
}