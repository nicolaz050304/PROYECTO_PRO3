package pe.edu.pe.pucp.proyecto.bl; // Ajustar según tu paquete base

import java.util.List;

public interface IBL <T, ID> {
    List<T> listarTodos();
    T obtenerPorId(ID id);
    int insertar(T t);
    int modificar(T t);
    int eliminar(T t);
}
