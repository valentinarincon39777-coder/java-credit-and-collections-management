
package dao;

import database.Conexion;
import exception.DAOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

import model.entity.Cuota;
import model.enums.EstadoCuota;

public class CuotaDAO {

    // INSERTAR TODAS LAS CUOTAS DE UN PRESTAMO*********************************++
    public void insertarCuotas(
            List<Cuota> cuotas, Connection con)
            throws SQLException {

        String sql = "INSERT INTO cuota "
                + "(prestamo_fk, numero_cuota, fecha_limite, "
                + "capital, interes, monto_pagar, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            for (Cuota cuota : cuotas) {

                ps.setInt(1, cuota.getPrestamoId());
                ps.setInt(2, cuota.getNumeroCuota());

                ps.setDate(3, java.sql.Date.valueOf(
                        cuota.getFechaLimite()));

                ps.setBigDecimal(4, cuota.getCapital());
                ps.setBigDecimal(5, cuota.getInteres());
                ps.setBigDecimal(6, cuota.getMontoPagar());
                ps.setString(7, cuota.getEstado().name());

                if (ps.executeUpdate() != 1) {
                    throw new SQLException(
                            "No se pudo insertar una cuota.");
                }

                try (ResultSet rs = ps.getGeneratedKeys()) {

                    if (!rs.next()) {
                        throw new SQLException(
                                "No se obtuvo el ID de la cuota.");
                    }

                    cuota.setId(rs.getInt(1));
                }
            }
        }
    }

    // CONSULTAR CUOTAS DE UN PRESTAMO**************************************
    public List<Cuota> listarPorPrestamo(int prestamoId)
            throws DAOException {

        String sql = "SELECT * FROM cuota "
                + "WHERE prestamo_fk = ? "
                + "ORDER BY numero_cuota";

        List<Cuota> cuotas = new ArrayList<>();

        try (
            Connection con = Conexion.conectar();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, prestamoId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    cuotas.add(construirCuota(rs));
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "No se pudieron consultar las cuotas.", e);
        }

        return cuotas;
    }

    // CONVERTIR RESULTSET EN CUOTA********************************
    private Cuota construirCuota(ResultSet rs)
            throws SQLException {

        return new Cuota(
                rs.getInt("id"),
                rs.getInt("prestamo_fk"),
                rs.getInt("numero_cuota"),
                rs.getDate("fecha_limite").toLocalDate(),
                rs.getBigDecimal("capital"),
                rs.getBigDecimal("interes"),
                rs.getBigDecimal("monto_pagar"),
                EstadoCuota.valueOf(
                        rs.getString("estado"))
        );
    }
}
