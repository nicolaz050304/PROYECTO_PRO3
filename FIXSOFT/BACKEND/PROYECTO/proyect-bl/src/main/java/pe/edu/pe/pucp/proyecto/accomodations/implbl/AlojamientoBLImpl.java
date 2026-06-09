package pe.edu.pe.pucp.proyecto.accomodations.implbl;

import pe.edu.pe.pucp.proyecto.accomodations.Alojamiento;
import pe.edu.pe.pucp.proyecto.accomodations.bl.AlojamientoBL;
import pe.edu.pe.pucp.proyecto.accomodations.dao.AlojamientoIDAO;
import pe.edu.pe.pucp.proyecto.accomodations.impl.AlojamientoImpl;

import java.util.List;

public class AlojamientoBLImpl implements AlojamientoBL {

    // Instanciamos el DAO de Alojamiento
    private AlojamientoIDAO daoAlojamiento = new AlojamientoImpl();

    @Override
    public int insertar(Alojamiento alojamiento) {
        // --- LÓGICA DE NEGOCIO: VALIDACIONES ---

        // 1. Validar que el precio por noche sea razonable (mayor a 0)
        if (alojamiento.getPrecioPorNoche() <= 0) {
            throw new RuntimeException("Error: El precio por noche debe ser mayor a 0.");
        }

        // 2. Validar que la capacidad máxima sea al menos de 1 persona
        if (alojamiento.getCapacidadMax() < 1) {
            throw new RuntimeException("Error: La capacidad máxima debe ser de al menos 1 persona.");
        }

        // 3. Validar que los campos obligatorios no estén vacíos
        if (alojamiento.getNombre() == null || alojamiento.getNombre().trim().isEmpty() ||
                alojamiento.getDireccion() == null || alojamiento.getDireccion().trim().isEmpty()) {
            throw new RuntimeException("Error: El nombre y la dirección del alojamiento son obligatorios.");
        }

        // Si pasa todas las validaciones comerciales, delegamos al DAO
        return daoAlojamiento.save(alojamiento).getIdAlojamiento();
    }

    @Override
    public List<Alojamiento> listarTodos() {
        return daoAlojamiento.listAll();
    }

    @Override
    public Alojamiento obtenerPorId(Integer id) {
        return daoAlojamiento.load(id);
    }

    @Override
    public int modificar(Alojamiento alojamiento) {
        // Al modificar, también debemos aplicar las mismas validaciones de negocio
        if (alojamiento.getPrecioPorNoche() <= 0 || alojamiento.getCapacidadMax() < 1) {
            throw new RuntimeException("Error: El precio y la capacidad deben ser valores positivos.");
        }

        return daoAlojamiento.update(alojamiento).getIdAlojamiento();
    }

    @Override
    public int eliminar(Alojamiento alojamiento) {
        // Baja lógica: En lugar de borrar de la BD, se cambia la disponibilidad a false
        alojamiento.setDisponibilidad(false);
        daoAlojamiento.update(alojamiento);
        return 1;
    }
}