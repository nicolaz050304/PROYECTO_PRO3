package pe.edu.pe.pucp.proyecto.accomodations.impl;

import pe.edu.pe.pucp.proyecto.accomodations.dao.AlojamientoIDAO;
import pe.edu.pe.pucp.proyecto.accomodations.*;
import pe.edu.pe.pucp.proyecto.manager.DBManager;
import pe.edu.pe.pucp.proyecto.users.Anfitrion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlojamientoImpl implements AlojamientoIDAO {

    @Override
    public Alojamiento load(Integer id) {
        // MODIFICADO: Añadidos latitud, longitud, estado_validacion e id_admin_validador
        String sql = "SELECT id_alojamiento, nombre, descripcion, precio_por_noche, direccion, " +
                "capacidad_max, calificacion_promedio, disponibilidad, pais, id_duenho, tipo, " +
                "latitud, longitud, estado_validacion, id_admin_validador, " + // Nuevos campos
                "num_pisos, con_patio, num_cocheras, num_habitaciones_casa, " +
                "num_piso, nro_departamento, nro_habitaciones_departamento, " +
                "nro_habitacion, tipo_cama, con_banho_privado FROM alojamiento WHERE id_alojamiento = ?";

        try (Connection connection = DBManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String tipo = rs.getString("tipo");
                    Alojamiento alojamiento = null;

                    // Datos comunes
                    Anfitrion duenho = new Anfitrion();
                    duenho.setIdUsuario(rs.getInt("id_duenho"));

                    if ("CASA".equals(tipo)) {
                        alojamiento = new Casa(rs.getString("nombre"), rs.getString("descripcion"),
                                rs.getDouble("precio_por_noche"), rs.getString("direccion"),
                                rs.getInt("capacidad_max"), rs.getDouble("calificacion_promedio"),
                                rs.getBoolean("disponibilidad"), duenho, rs.getString("pais"),
                                rs.getInt("num_pisos"), rs.getBoolean("con_patio"),
                                rs.getInt("num_cocheras"), rs.getInt("num_habitaciones_casa"));
                    } else if ("DEPARTAMENTO".equals(tipo)) {
                        alojamiento = new Departamento(rs.getString("nombre"), rs.getString("descripcion"),
                                rs.getDouble("precio_por_noche"), rs.getString("direccion"),
                                rs.getInt("capacidad_max"), rs.getDouble("calificacion_promedio"),
                                rs.getBoolean("disponibilidad"), duenho, rs.getString("pais"),
                                rs.getInt("num_piso"), rs.getString("nro_departamento"),
                                rs.getInt("nro_habitaciones_departamento"));
                    } else if ("HABITACION".equals(tipo)) {
                        alojamiento = new Habitacion(rs.getString("nombre"), rs.getString("descripcion"),
                                rs.getDouble("precio_por_noche"), rs.getString("direccion"),
                                rs.getInt("capacidad_max"), rs.getDouble("calificacion_promedio"),
                                rs.getBoolean("disponibilidad"), duenho, rs.getString("pais"),
                                rs.getString("nro_habitacion"), rs.getString("tipo_cama"),
                                rs.getBoolean("con_banho_privado"));
                    }

                    if (alojamiento != null) {
                        alojamiento.setIdAlojamiento(rs.getInt("id_alojamiento"));
                        alojamiento.setLatitud(rs.getDouble("latitud"));
                        alojamiento.setLongitud(rs.getDouble("longitud"));
                        alojamiento.setEstadoValidacion(rs.getString("estado_validacion"));
                    }
                    return alojamiento;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Alojamiento save(Alojamiento al) {
        // MODIFICADO: SQL con 24 parámetros (incluyendo lat/long y validación)
        String sql = "INSERT INTO alojamiento (nombre, descripcion, precio_por_noche, direccion, capacidad_max, " +
                "calificacion_promedio, disponibilidad, pais, id_duenho, latitud, longitud, estado_validacion, " +
                "tipo, num_pisos, con_patio, num_cocheras, num_habitaciones_casa, num_piso, " +
                "nro_departamento, nro_habitaciones_departamento, nro_habitacion, tipo_cama, con_banho_privado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // 1-9: Comunes
            pstmt.setString(1, al.getNombre());
            pstmt.setString(2, al.getDescripcion());
            pstmt.setDouble(3, al.getPrecioPorNoche());
            pstmt.setString(4, al.getDireccion());
            pstmt.setInt(5, al.getCapacidadMax());
            pstmt.setDouble(6, al.getCalificacionPromedio());
            pstmt.setBoolean(7, al.isDisponibilidad());
            pstmt.setString(8, al.getPais());
            pstmt.setInt(9, al.getDuenho().getIdUsuario());

            // 10-12: GPS y Auditoría
            pstmt.setDouble(10, al.getLatitud());
            pstmt.setDouble(11, al.getLongitud());
            pstmt.setString(12, al.getEstadoValidacion());

            // 14-23: Inicializamos específicos en NULL
            for (int i = 14; i <= 23; i++) pstmt.setNull(i, Types.NULL);

            if (al instanceof Casa c) {
                pstmt.setString(13, "CASA");
                pstmt.setInt(14, c.getNumPisos());
                pstmt.setBoolean(15, c.isConPatio());
                pstmt.setInt(16, c.getNumCocheras());
                pstmt.setInt(17, c.getNumHabitaciones());
            } else if (al instanceof Departamento d) {
                pstmt.setString(13, "DEPARTAMENTO");
                pstmt.setInt(18, d.getNumPiso());
                pstmt.setString(19, d.getNroDepartamento());
                pstmt.setInt(20, d.getNroHabitaciones());
            } else if (al instanceof Habitacion h) {
                pstmt.setString(13, "HABITACION");
                pstmt.setString(21, h.getNroHabitacion());
                pstmt.setString(22, h.getTipoCama());
                pstmt.setBoolean(23, h.isConbanhoPrivado());
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) al.setIdAlojamiento(rs.getInt(1));
                }
            }
            return al;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(Alojamiento al) {
        // CORRECCIÓN: Eliminación lógica para no romper llaves foráneas con Reservas
        String sql = "UPDATE alojamiento SET disponibilidad = 0 WHERE id_alojamiento = ?";
        try (Connection connection = DBManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, al.getIdAlojamiento());
            pstmt.executeUpdate();
            al.setDisponibilidad(false);
        } catch (SQLException e) {
            throw new RuntimeException("Error al desactivar alojamiento: " + e.getMessage());
        }
    }

    @Override
    public List<Alojamiento> listAll() {
        List<Alojamiento> lista = new ArrayList<>();
        // SQL completo con los campos de las 8 tablas
        String sql = "SELECT id_alojamiento, nombre, descripcion, precio_por_noche, direccion, " +
                "capacidad_max, calificacion_promedio, disponibilidad, pais, id_duenho, tipo, " +
                "latitud, longitud, estado_validacion, id_admin_validador, " +
                "num_pisos, con_patio, num_cocheras, num_habitaciones_casa, " +
                "num_piso, nro_departamento, nro_habitaciones_departamento, " +
                "nro_habitacion, tipo_cama, con_banho_privado FROM alojamiento";

        try (Connection connection = DBManager.getInstance().getConnection();
             Statement stm = connection.createStatement();
             ResultSet rs = stm.executeQuery(sql)) {

            while (rs.next()) {
                String tipo = rs.getString("tipo");
                Alojamiento al = null;

                // Cargamos al dueño (Anfitrión) - Solo el ID para no sobrecargar
                Anfitrion duenho = new Anfitrion();
                duenho.setIdUsuario(rs.getInt("id_duenho"));

                // Instanciación según el tipo
                if ("CASA".equals(tipo)) {
                    al = new Casa(rs.getString("nombre"), rs.getString("descripcion"),
                            rs.getDouble("precio_por_noche"), rs.getString("direccion"),
                            rs.getInt("capacidad_max"), rs.getDouble("calificacion_promedio"),
                            rs.getBoolean("disponibilidad"), duenho, rs.getString("pais"),
                            rs.getInt("num_pisos"), rs.getBoolean("con_patio"),
                            rs.getInt("num_cocheras"), rs.getInt("num_habitaciones_casa"));
                } else if ("DEPARTAMENTO".equals(tipo)) {
                    al = new Departamento(rs.getString("nombre"), rs.getString("descripcion"),
                            rs.getDouble("precio_por_noche"), rs.getString("direccion"),
                            rs.getInt("capacidad_max"), rs.getDouble("calificacion_promedio"),
                            rs.getBoolean("disponibilidad"), duenho, rs.getString("pais"),
                            rs.getInt("num_piso"), rs.getString("nro_departamento"),
                            rs.getInt("nro_habitaciones_departamento"));
                } else if ("HABITACION".equals(tipo)) {
                    al = new Habitacion(rs.getString("nombre"), rs.getString("descripcion"),
                            rs.getDouble("precio_por_noche"), rs.getString("direccion"),
                            rs.getInt("capacidad_max"), rs.getDouble("calificacion_promedio"),
                            rs.getBoolean("disponibilidad"), duenho, rs.getString("pais"),
                            rs.getString("nro_habitacion"), rs.getString("tipo_cama"),
                            rs.getBoolean("con_banho_privado"));
                }

                if (al != null) {
                    al.setIdAlojamiento(rs.getInt("id_alojamiento"));
                    al.setLatitud(rs.getDouble("latitud"));
                    al.setLongitud(rs.getDouble("longitud"));
                    al.setEstadoValidacion(rs.getString("estado_validacion"));
                    lista.add(al);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar alojamientos: " + e.getMessage());
        }
        return lista;
    }
    @Override
    public Alojamiento update(Alojamiento al) {
        // SQL con 23 parámetros (22 en el SET + 1 en el WHERE)
        String sql = "UPDATE alojamiento SET nombre=?, descripcion=?, precio_por_noche=?, direccion=?, " +
                "capacidad_max=?, calificacion_promedio=?, disponibilidad=?, pais=?, id_duenho=?, " +
                "latitud=?, longitud=?, estado_validacion=?, " + // 10, 11, 12
                "num_pisos=?, con_patio=?, num_cocheras=?, num_habitaciones_casa=?, " + // 13-16
                "num_piso=?, nro_departamento=?, nro_habitaciones_departamento=?, " + // 17-19
                "nro_habitacion=?, tipo_cama=?, con_banho_privado=? " + // 20-22
                "WHERE id_alojamiento=?"; // 23

        try (Connection connection = DBManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            // 1-9: Campos comunes
            pstmt.setString(1, al.getNombre());
            pstmt.setString(2, al.getDescripcion());
            pstmt.setDouble(3, al.getPrecioPorNoche());
            pstmt.setString(4, al.getDireccion());
            pstmt.setInt(5, al.getCapacidadMax());
            pstmt.setDouble(6, al.getCalificacionPromedio());
            pstmt.setBoolean(7, al.isDisponibilidad());
            pstmt.setString(8, al.getPais());
            pstmt.setInt(9, al.getDuenho().getIdUsuario());

            // 10-12: GPS y Auditoría
            pstmt.setDouble(10, al.getLatitud());
            pstmt.setDouble(11, al.getLongitud());
            pstmt.setString(12, al.getEstadoValidacion());

            // Limpieza de campos específicos (13 al 22)
            for (int i = 13; i <= 22; i++) pstmt.setNull(i, Types.NULL);

            // Llenado según el tipo real del objeto
            if (al instanceof Casa c) {
                pstmt.setInt(13, c.getNumPisos());
                pstmt.setBoolean(14, c.isConPatio());
                pstmt.setInt(15, c.getNumCocheras());
                pstmt.setInt(16, c.getNumHabitaciones());
            } else if (al instanceof Departamento d) {
                pstmt.setInt(17, d.getNumPiso());
                pstmt.setString(18, d.getNroDepartamento());
                pstmt.setInt(19, d.getNroHabitaciones());
            } else if (al instanceof Habitacion h) {
                pstmt.setString(20, h.getNroHabitacion());
                pstmt.setString(21, h.getTipoCama());
                pstmt.setBoolean(22, h.isConbanhoPrivado());
            }

            // 23: El ID para el WHERE
            pstmt.setInt(23, al.getIdAlojamiento());

            pstmt.executeUpdate();
            return al;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar alojamiento: " + e.getMessage());
        }
    }
}