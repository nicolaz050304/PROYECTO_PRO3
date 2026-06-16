package pe.edu.pe.pucp.proyecto.users.impl;

import pe.edu.pe.pucp.proyecto.manager.DBManager;
import pe.edu.pe.pucp.proyecto.users.*;
import pe.edu.pe.pucp.proyecto.users.dao.UsuarioIDAO;
import pe.edu.pe.pucp.proyecto.tipoDoc.TipoDocumento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioImpl implements UsuarioIDAO {

    @Override
    public Usuario load(Integer id) {
        String sql = "SELECT id_usuario, username, correo, password, nombre, apellido_paterno, " +
                "apellido_materno, pais, estado_sesion, estado_actual, telefono, " +
                "puntuacion_promedio, tipo_documento, numero_documento, nivel_acceso, " +
                "fecha_contratacion, area_responsabilidad, tipo_usuario, estado_validacion, " +
                "id_admin_validador FROM usuario WHERE id_usuario = ?";

        try (Connection connection = DBManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = null;
                    String rolesRaw = rs.getString("tipo_usuario");

                    if (rolesRaw.contains("ADMINISTRADOR")) {
                        Administrador admin = new Administrador();
                        admin.setNivelAcceso(rs.getInt("nivel_acceso"));
                        admin.setFechaContratacion(rs.getDate("fecha_contratacion"));
                        admin.setAreaResponsabilidad(rs.getString("area_responsabilidad"));
                        usuario = admin;
                    } else if (rolesRaw.contains("ANFITRION")) {
                        Anfitrion anfitrion = new Anfitrion();
                        anfitrion.setTelefono(rs.getString("telefono")); // CORREGIDO: getString
                        anfitrion.setPuntuacionPromedio(rs.getDouble("puntuacion_promedio"));
                        usuario = anfitrion;
                    } else {
                        Invitado invitado = new Invitado();
                        invitado.setTelefono(rs.getString("telefono")); // CORREGIDO: getString
                        usuario = invitado;
                    }

                    if (usuario != null) {
                        usuario.setIdUsuario(rs.getInt("id_usuario"));
                        usuario.setUsername(rs.getString("username"));
                        usuario.setCorreo(rs.getString("correo"));
                        usuario.setPassword(rs.getString("password"));
                        usuario.setNombre(rs.getString("nombre"));
                        usuario.setApellidoPaterno(rs.getString("apellido_paterno"));
                        usuario.setApellidoMaterno(rs.getString("apellido_materno"));
                        usuario.setPais(rs.getString("pais"));
                        usuario.setEstadoSesion(rs.getBoolean("estado_sesion"));
                        usuario.setEstadoActual(EstadoUsuario.valueOf(rs.getString("estado_actual")));
                        usuario.setNumeroDocumento(rs.getString("numero_documento"));
                        usuario.setEstadoValidacion(rs.getString("estado_validacion"));
                        usuario.setTelefono(rs.getString("telefono")); // CARGAMOS EL TELÉFONO AL PADRE

                        for (String rol : rolesRaw.split(",")) {
                            usuario.agregarRol(rol);
                        }

                        String tDoc = rs.getString("tipo_documento");
                        if (tDoc != null) {
                            usuario.setTipoDocumento(TipoDocumento.valueOf(tDoc));
                        }
                    }
                    return usuario;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Igual que load() pero filtrando por correo (UNIQUE en BD). Reutiliza la
     * misma lógica de discriminación de subclase (tipo_usuario) y poblado del
     * Set<roles>. Devuelve null si no existe. NO transforma el password.
     */
    @Override
    public Usuario buscarPorCorreo(String correo) {
        String sql = "SELECT id_usuario, username, correo, password, nombre, apellido_paterno, " +
                "apellido_materno, pais, estado_sesion, estado_actual, telefono, " +
                "puntuacion_promedio, tipo_documento, numero_documento, nivel_acceso, " +
                "fecha_contratacion, area_responsabilidad, tipo_usuario, estado_validacion, " +
                "id_admin_validador FROM usuario WHERE correo = ?";

        try (Connection connection = DBManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, correo);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = null;
                    String rolesRaw = rs.getString("tipo_usuario");

                    if (rolesRaw.contains("ADMINISTRADOR")) {
                        Administrador admin = new Administrador();
                        admin.setNivelAcceso(rs.getInt("nivel_acceso"));
                        admin.setFechaContratacion(rs.getDate("fecha_contratacion"));
                        admin.setAreaResponsabilidad(rs.getString("area_responsabilidad"));
                        usuario = admin;
                    } else if (rolesRaw.contains("ANFITRION")) {
                        Anfitrion anfitrion = new Anfitrion();
                        anfitrion.setTelefono(rs.getString("telefono"));
                        anfitrion.setPuntuacionPromedio(rs.getDouble("puntuacion_promedio"));
                        usuario = anfitrion;
                    } else {
                        Invitado invitado = new Invitado();
                        invitado.setTelefono(rs.getString("telefono"));
                        usuario = invitado;
                    }

                    if (usuario != null) {
                        usuario.setIdUsuario(rs.getInt("id_usuario"));
                        usuario.setUsername(rs.getString("username"));
                        usuario.setCorreo(rs.getString("correo"));
                        usuario.setPassword(rs.getString("password"));
                        usuario.setNombre(rs.getString("nombre"));
                        usuario.setApellidoPaterno(rs.getString("apellido_paterno"));
                        usuario.setApellidoMaterno(rs.getString("apellido_materno"));
                        usuario.setPais(rs.getString("pais"));
                        usuario.setEstadoSesion(rs.getBoolean("estado_sesion"));
                        usuario.setEstadoActual(EstadoUsuario.valueOf(rs.getString("estado_actual")));
                        usuario.setNumeroDocumento(rs.getString("numero_documento"));
                        usuario.setEstadoValidacion(rs.getString("estado_validacion"));
                        usuario.setTelefono(rs.getString("telefono"));

                        if (rolesRaw != null && !rolesRaw.isEmpty()) {
                            for (String rol : rolesRaw.split(",")) {
                                usuario.agregarRol(rol);
                            }
                        }

                        String tDoc = rs.getString("tipo_documento");
                        if (tDoc != null) {
                            usuario.setTipoDocumento(TipoDocumento.valueOf(tDoc));
                        }
                    }
                    return usuario;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Usuario update(Usuario usuario) {
        String sql = "UPDATE usuario SET username=?, correo=?, password=?, nombre=?, apellido_paterno=?, " +
                "apellido_materno=?, pais=?, estado_sesion=?, estado_actual=?, telefono=?, " +
                "puntuacion_promedio=?, tipo_documento=?, numero_documento=?, nivel_acceso=?, " +
                "fecha_contratacion=?, area_responsabilidad=?, tipo_usuario=?, estado_validacion=? " +
                "WHERE id_usuario=?";

        try (Connection connection = DBManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getUsername());
            pstmt.setString(2, usuario.getCorreo());
            pstmt.setString(3, usuario.getPassword());
            pstmt.setString(4, usuario.getNombre());
            pstmt.setString(5, usuario.getApellidoPaterno());
            pstmt.setString(6, usuario.getApellidoMaterno());
            pstmt.setString(7, usuario.getPais());
            pstmt.setBoolean(8, usuario.isEstadoSesion());
            pstmt.setString(9, usuario.getEstadoActual().toString());
            pstmt.setString(10, usuario.getTelefono()); // CORREGIDO: setString

            pstmt.setNull(11, Types.DECIMAL);
            pstmt.setNull(12, Types.VARCHAR);
            pstmt.setString(13, usuario.getNumeroDocumento());
            pstmt.setNull(14, Types.INTEGER);
            pstmt.setNull(15, Types.DATE);
            pstmt.setNull(16, Types.VARCHAR);

            if (usuario instanceof Administrador admin) {
                pstmt.setInt(14, admin.getNivelAcceso());
                if (admin.getFechaContratacion() != null) {
                    pstmt.setDate(15, new java.sql.Date(admin.getFechaContratacion().getTime()));
                }
                pstmt.setString(16, admin.getAreaResponsabilidad());
            } else if (usuario instanceof Cliente cliente) {
                pstmt.setDouble(11, cliente.getPuntuacionPromedio());
                if (cliente.getTipoDocumento() != null) {
                    pstmt.setString(12, cliente.getTipoDocumento().name());
                }
            }

            pstmt.setString(17, String.join(",", usuario.getRoles()));
            pstmt.setString(18, usuario.getEstadoValidacion());
            pstmt.setInt(19, usuario.getIdUsuario());

            pstmt.executeUpdate();
            return usuario;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public Usuario save(Usuario usuario) {
        String sql = "INSERT INTO usuario (username, correo, password, nombre, apellido_paterno, " +
                "apellido_materno, pais, estado_sesion, estado_actual, telefono, " +
                "puntuacion_promedio, tipo_documento, numero_documento, nivel_acceso, " +
                "fecha_contratacion, area_responsabilidad, tipo_usuario, estado_validacion) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, usuario.getUsername());
            pstmt.setString(2, usuario.getCorreo());
            pstmt.setString(3, usuario.getPassword());
            pstmt.setString(4, usuario.getNombre());
            pstmt.setString(5, usuario.getApellidoPaterno());
            pstmt.setString(6, usuario.getApellidoMaterno());
            pstmt.setString(7, usuario.getPais());
            pstmt.setBoolean(8, usuario.isEstadoSesion());
            pstmt.setString(9, usuario.getEstadoActual().toString());
            pstmt.setString(10, usuario.getTelefono()); // CORREGIDO: setString

            pstmt.setNull(11, Types.DECIMAL);
            pstmt.setNull(12, Types.VARCHAR);
            pstmt.setString(13, usuario.getNumeroDocumento());
            pstmt.setNull(14, Types.INTEGER);
            pstmt.setNull(15, Types.DATE);
            pstmt.setNull(16, Types.VARCHAR);

            pstmt.setString(17, String.join(",", usuario.getRoles()));
            pstmt.setString(18, usuario.getEstadoValidacion());

            if (usuario instanceof Administrador admin) {
                pstmt.setInt(14, admin.getNivelAcceso());
                if(admin.getFechaContratacion() != null)
                    pstmt.setDate(15, new java.sql.Date(admin.getFechaContratacion().getTime()));
                pstmt.setString(16, admin.getAreaResponsabilidad());
            } else if (usuario instanceof Cliente cliente) {
                pstmt.setDouble(11, cliente.getPuntuacionPromedio());
                if (cliente.getTipoDocumento() != null)
                    pstmt.setString(12, cliente.getTipoDocumento().name());
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) usuario.setIdUsuario(rs.getInt(1));
                }
            }
            return usuario;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Usuario> listAll() {
        String sql = "SELECT id_usuario, username, correo, password, nombre, apellido_paterno, " +
                "apellido_materno, pais, estado_sesion, estado_actual, telefono, " +
                "puntuacion_promedio, tipo_documento, numero_documento, nivel_acceso, " +
                "fecha_contratacion, area_responsabilidad, tipo_usuario, estado_validacion, " +
                "id_admin_validador FROM usuario";

        List<Usuario> lista = new ArrayList<>();

        try (Connection connection = DBManager.getInstance().getConnection();
             Statement stm = connection.createStatement();
             ResultSet rs = stm.executeQuery(sql)) {

            while (rs.next()) {
                Usuario usuario = null;
                String rolesRaw = rs.getString("tipo_usuario");

                if (rolesRaw.contains("ADMINISTRADOR")) {
                    Administrador admin = new Administrador();
                    admin.setNivelAcceso(rs.getInt("nivel_acceso"));
                    admin.setFechaContratacion(rs.getDate("fecha_contratacion"));
                    admin.setAreaResponsabilidad(rs.getString("area_responsabilidad"));
                    usuario = admin;
                } else if (rolesRaw.contains("ANFITRION")) {
                    Anfitrion anfitrion = new Anfitrion();
                    anfitrion.setTelefono(rs.getString("telefono")); // CORREGIDO
                    anfitrion.setPuntuacionPromedio(rs.getDouble("puntuacion_promedio"));
                    usuario = anfitrion;
                } else {
                    Invitado invitado = new Invitado();
                    invitado.setTelefono(rs.getString("telefono")); // CORREGIDO
                    usuario = invitado;
                }

                if (usuario != null) {
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setUsername(rs.getString("username"));
                    usuario.setCorreo(rs.getString("correo"));
                    usuario.setPassword(rs.getString("password"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setApellidoPaterno(rs.getString("apellido_paterno"));
                    usuario.setApellidoMaterno(rs.getString("apellido_materno"));
                    usuario.setPais(rs.getString("pais"));
                    usuario.setEstadoSesion(rs.getBoolean("estado_sesion"));
                    usuario.setEstadoActual(EstadoUsuario.valueOf(rs.getString("estado_actual")));
                    usuario.setNumeroDocumento(rs.getString("numero_documento"));
                    usuario.setEstadoValidacion(rs.getString("estado_validacion"));
                    usuario.setTelefono(rs.getString("telefono")); // CORREGIDO

                    if (rolesRaw != null && !rolesRaw.isEmpty()) {
                        for (String r : rolesRaw.split(",")) {
                            usuario.agregarRol(r);
                        }
                    }

                    String tDoc = rs.getString("tipo_documento");
                    if (tDoc != null) {
                        usuario.setTipoDocumento(TipoDocumento.valueOf(tDoc));
                    }

                    lista.add(usuario);
                }
            }
            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar usuarios: " + e.getMessage(), e);
        }
    }

    @Override
    public void remove(Usuario usuario) {
        String sql = "UPDATE usuario SET estado_actual = 'SUSPENDIDO', estado_sesion = 0 WHERE id_usuario = ?";
        try (Connection connection = DBManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, usuario.getIdUsuario());
            pstmt.executeUpdate();
            usuario.setEstadoActual(EstadoUsuario.SUSPENDIDO);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}