package pe.edu.pe.pucp.proyecto.cuentabank.impl;

import pe.edu.pe.pucp.proyecto.cuentaBank.CuentaBancaria;
import pe.edu.pe.pucp.proyecto.cuentaBank.CuentaBancaria.TipoCuenta;
import pe.edu.pe.pucp.proyecto.cuentabank.dao.CuentaBancariaIDAO;
import pe.edu.pe.pucp.proyecto.economy.TipoMoneda;
import pe.edu.pe.pucp.proyecto.manager.DBManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CuentaBancariaImpl implements CuentaBancariaIDAO {

    private static volatile boolean columnaLista = false;

    public CuentaBancariaImpl() {
        asegurarColumnaPrincipal();
    }

    /**
     * RF15: la tabla `cuenta_bancaria` ya existía sin la columna `principal`. La agregamos en
     * caliente (ALTER TABLE previa consulta a information_schema), igual que con objetivo/num_huespedes,
     * sin script manual. Las filas existentes quedan en principal=0.
     */
    private static synchronized void asegurarColumnaPrincipal() {
        if (columnaLista) return;
        try (Connection c = DBManager.getInstance().getConnection()) {
            boolean existe = false;
            String check = "SELECT COUNT(*) FROM information_schema.columns " +
                    "WHERE table_schema = DATABASE() AND table_name = 'cuenta_bancaria' AND column_name = 'principal'";
            try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(check)) {
                if (rs.next() && rs.getInt(1) > 0) existe = true;
            }
            if (!existe) {
                try (Statement st = c.createStatement()) {
                    st.execute("ALTER TABLE cuenta_bancaria ADD COLUMN principal TINYINT(1) NOT NULL DEFAULT 0");
                }
            }
            columnaLista = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CuentaBancaria load(Integer id_cuenta) {
        // Incluimos id_usuario en el SELECT para mantener la coherencia
        String sql = "SELECT id_cuenta, id_usuario, numero_cuenta, tipo_cuenta, tipo_moneda, " +
                "saldo, titular, nro_banco, cci, verificada, principal " +
                "FROM cuenta_bancaria WHERE id_cuenta = ?";

        try (Connection connection = DBManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, id_cuenta);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extraerCuenta(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al cargar cuenta: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public CuentaBancaria save(CuentaBancaria cuentaBancaria) {
        // SQL actualizado con id_usuario
        String sql = "INSERT INTO cuenta_bancaria (id_usuario, numero_cuenta, tipo_moneda, cci, " +
                "nro_banco, tipo_cuenta, titular, saldo, verificada, principal) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, cuentaBancaria.getIdUsuario());
            pstmt.setString(2, cuentaBancaria.getNumeroCuenta());
            pstmt.setString(3, cuentaBancaria.getTipoMoneda().name());
            pstmt.setString(4, cuentaBancaria.getCCI());
            pstmt.setString(5, cuentaBancaria.getNroBanco());

            // Manejo del Enum TipoCuenta
            pstmt.setString(6, cuentaBancaria.getTipoCuenta() != null ? cuentaBancaria.getTipoCuenta().name() : null);

            pstmt.setString(7, cuentaBancaria.getTitular());
            pstmt.setDouble(8, cuentaBancaria.getSaldo());
            pstmt.setBoolean(9, cuentaBancaria.isVerificada());
            pstmt.setBoolean(10, cuentaBancaria.isPrincipal());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cuentaBancaria.setIdCuenta(generatedKeys.getInt(1));
                }
            }
            return cuentaBancaria;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar cuenta: " + e.getMessage());
        }
    }

    @Override
    public CuentaBancaria update(CuentaBancaria cuentaBancaria) {
        // UPDATE actualizado para incluir id_usuario si fuera necesario cambiar de dueño (raro, pero posible)
        String sql = "UPDATE cuenta_bancaria SET id_usuario = ?, numero_cuenta = ?, tipo_moneda = ?, " +
                "cci = ?, nro_banco = ?, tipo_cuenta = ?, titular = ?, saldo = ?, verificada = ?, principal = ? " +
                "WHERE id_cuenta = ?";

        try (Connection connection = DBManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, cuentaBancaria.getIdUsuario());
            pstmt.setString(2, cuentaBancaria.getNumeroCuenta());
            pstmt.setString(3, cuentaBancaria.getTipoMoneda().name());
            pstmt.setString(4, cuentaBancaria.getCCI());
            pstmt.setString(5, cuentaBancaria.getNroBanco());
            pstmt.setString(6, cuentaBancaria.getTipoCuenta().name());
            pstmt.setString(7, cuentaBancaria.getTitular());
            pstmt.setDouble(8, cuentaBancaria.getSaldo());
            pstmt.setBoolean(9, cuentaBancaria.isVerificada());
            pstmt.setBoolean(10, cuentaBancaria.isPrincipal());
            pstmt.setInt(11, cuentaBancaria.getIdCuenta());

            pstmt.executeUpdate();
            return cuentaBancaria;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar: " + e.getMessage());
        }
    }

    @Override
    public List<CuentaBancaria> listarTodos() {
        List<CuentaBancaria> cuentas = new ArrayList<>();
        String sql = "SELECT * FROM cuenta_bancaria";
        try (Connection connection = DBManager.getInstance().getConnection();
             Statement stm = connection.createStatement();
             ResultSet rs = stm.executeQuery(sql)) {
            while (rs.next()) {
                cuentas.add(extraerCuenta(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return cuentas;
    }

    /**
     * Lista las cuentas bancarias de un usuario (anfitrión) específico. Se filtra por
     * id_usuario porque cada anfitrión solo debe ver/gestionar SUS propias cuentas.
     * Reusa el mismo mapeo (extraerCuenta) que el resto de la clase y propaga el error si falla.
     */
    @Override
    public List<CuentaBancaria> listarPorUsuario(int idUsuario) {
        List<CuentaBancaria> cuentas = new ArrayList<>();
        String sql = "SELECT * FROM cuenta_bancaria WHERE id_usuario = ?";
        try (Connection connection = DBManager.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    cuentas.add(extraerCuenta(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar cuentas por usuario: " + e.getMessage(), e);
        }
        return cuentas;
    }

    @Override
    public List<CuentaBancaria> listarPorVerificacion() {
        List<CuentaBancaria> cuentas = new ArrayList<>();
        String sql = "SELECT * FROM cuenta_bancaria WHERE verificada = 1";
        try (Connection connection = DBManager.getInstance().getConnection();
             Statement stm = connection.createStatement();
             ResultSet rs = stm.executeQuery(sql)) {
            while (rs.next()) {
                cuentas.add(extraerCuenta(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return cuentas;
    }

    /**
     * Mapea el ResultSet a un objeto CuentaBancaria incluyendo la relación id_usuario.
     */
    private CuentaBancaria extraerCuenta(ResultSet rs) throws SQLException {
        CuentaBancaria cuenta = new CuentaBancaria();
        cuenta.setIdCuenta(rs.getInt("id_cuenta"));
        cuenta.setIdUsuario(rs.getInt("id_usuario")); // <--- CRÍTICO: Mapeo de la relación
        cuenta.setNumeroCuenta(rs.getString("numero_cuenta"));
        cuenta.setCCI(rs.getString("cci"));
        cuenta.setNroBanco(rs.getString("nro_banco"));
        cuenta.setTitular(rs.getString("titular"));
        cuenta.setSaldo(rs.getDouble("saldo"));
        cuenta.setVerificada(rs.getBoolean("verificada"));
        try { cuenta.setPrincipal(rs.getBoolean("principal")); } catch (SQLException ignore) { /* consulta sin la columna */ }

        String monedaStr = rs.getString("tipo_moneda");
        if (monedaStr != null) cuenta.setTipoMoneda(TipoMoneda.valueOf(monedaStr));

        String tipoCuentaStr = rs.getString("tipo_cuenta");
        if (tipoCuentaStr != null) cuenta.setTipoCuenta(TipoCuenta.valueOf(tipoCuentaStr));

        return cuenta;
    }

    /**
     * RF15: marca una cuenta como PRINCIPAL (de cobro) y desmarca las demás del mismo usuario,
     * garantizando una sola principal por anfitrión. Dos UPDATEs en la misma conexión.
     */
    @Override
    public void marcarPrincipal(int idCuenta, int idUsuario) {
        try (Connection connection = DBManager.getInstance().getConnection()) {
            try (PreparedStatement limpiar = connection.prepareStatement(
                    "UPDATE cuenta_bancaria SET principal = 0 WHERE id_usuario = ?")) {
                limpiar.setInt(1, idUsuario);
                limpiar.executeUpdate();
            }
            try (PreparedStatement marcar = connection.prepareStatement(
                    "UPDATE cuenta_bancaria SET principal = 1 WHERE id_cuenta = ? AND id_usuario = ?")) {
                marcar.setInt(1, idCuenta);
                marcar.setInt(2, idUsuario);
                marcar.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al marcar cuenta principal: " + e.getMessage(), e);
        }
    }

    @Override
    public void remove(CuentaBancaria cuentaBancaria) {
        String sql = "DELETE FROM cuenta_bancaria WHERE id_cuenta = ?";
        try (Connection connection = DBManager.getInstance().getConnection();
             PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, cuentaBancaria.getIdCuenta());
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar cuenta: " + e.getMessage(), e);
        }
    }
}