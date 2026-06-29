package pe.edu.pe.pucp.proyecto.economy.impl;

import pe.edu.pe.pucp.proyecto.economy.TipoCambio;
import pe.edu.pe.pucp.proyecto.economy.dao.TipoCambioIDAO;
import pe.edu.pe.pucp.proyecto.manager.DBManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoCambioImpl implements TipoCambioIDAO {

    private static final String SQL_SELECT_BY_ID =
            "SELECT codigo, simbolo, tasa_a_pen FROM tipo_cambio WHERE codigo = ?";

    private static final String SQL_SELECT_ALL =
            "SELECT codigo, simbolo, tasa_a_pen FROM tipo_cambio";

    private static final String SQL_INSERT =
            "INSERT INTO tipo_cambio (codigo, simbolo, tasa_a_pen) VALUES (?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE tipo_cambio SET simbolo = ?, tasa_a_pen = ? WHERE codigo = ?";

    private static final String SQL_DELETE =
            "DELETE FROM tipo_cambio WHERE codigo = ?";

    @Override
    public List<TipoCambio> listarTodas() {
        List<TipoCambio> lista = new ArrayList<>();
        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(construirTipoCambio(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar tipos de cambio: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public void actualizar(TipoCambio tipoCambio) {
        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, tipoCambio.getSimbolo());
            ps.setDouble(2, tipoCambio.getTasaAPen());
            ps.setString(3, tipoCambio.getCodigo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar tipo de cambio: " + e.getMessage(), e);
        }
    }

    @Override
    public TipoCambio load(String codigo) {
        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return construirTipoCambio(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error al cargar tipo de cambio: " + e.getMessage(), e);
        }
    }

    @Override
    public TipoCambio save(TipoCambio tipoCambio) {
        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {
            ps.setString(1, tipoCambio.getCodigo());
            ps.setString(2, tipoCambio.getSimbolo());
            ps.setDouble(3, tipoCambio.getTasaAPen());
            ps.executeUpdate();
            return tipoCambio;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar tipo de cambio: " + e.getMessage(), e);
        }
    }

    @Override
    public TipoCambio update(TipoCambio tipoCambio) {
        actualizar(tipoCambio);
        return tipoCambio;
    }

    @Override
    public void remove(TipoCambio tipoCambio) {
        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {
            ps.setString(1, tipoCambio.getCodigo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar tipo de cambio: " + e.getMessage(), e);
        }
    }

    private TipoCambio construirTipoCambio(ResultSet rs) throws SQLException {
        TipoCambio t = new TipoCambio();
        t.setCodigo(rs.getString("codigo"));
        t.setSimbolo(rs.getString("simbolo"));
        t.setTasaAPen(rs.getDouble("tasa_a_pen"));
        return t;
    }
}
