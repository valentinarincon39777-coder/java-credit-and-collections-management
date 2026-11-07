
package dao;

import database.Conexion;
import exception.DAOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;

import model.entity.Prestamo;
import model.enums.EstadoPrestamo;

public class PrestamoDAO {

    // INSERTAR PRESTAMO CON UNA CONEXION EXISTENTE******************************************
    public void insertar(Prestamo prestamo, Connection con)
            throws SQLException {

        String sql = "INSERT INTO prestamo "
                + "(solicitud_fk, interes, total_pagar, "
                + "saldo_pendiente, fecha_desembolso, "
                + "fecha_vencimiento_final, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, prestamo.getSolicitudId());
            ps.setBigDecimal(2, prestamo.getInteres());
            ps.setBigDecimal(3, prestamo.getTotalPagar());
            ps.setBigDecimal(4, prestamo.getSaldoPendiente());

            ps.setTimestamp(5, Timestamp.valueOf(
                    prestamo.getFechaDesembolso()));

            ps.setDate(6, java.sql.Date.valueOf(
                    prestamo.getFechaVencimientoFinal()));

            ps.setString(7, prestamo.getEstado().name());

            if (ps.executeUpdate() != 1) {
                throw new SQLException(
                        "No se pudo insertar el prestamo.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (!rs.next()) {
                    throw new SQLException(
                            "No se obtuvo el ID del prestamo.");
                }

                prestamo.setId(rs.getInt(1));
            }
        }
    }

    // BUSCAR PRESTAMO POR ID***********************************
    public Prestamo buscarPorId(int id) throws DAOException {

        String sql = "SELECT * FROM prestamo WHERE id = ?";

        try (
            Connection con = Conexion.conectar();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return construirPrestamo(rs);
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "No se pudo buscar el prestamo.", e);
        }

        return null;
    }

    // CONSULTAR PRESTAMOS DE UN CLIENTE***************************
    public List<Prestamo> listarPorCliente(int clienteId)
            throws DAOException {

        String sql = "SELECT p.* FROM prestamo p "
                + "JOIN solicitud_prestamo s "
                + "ON p.solicitud_fk = s.id "
                + "WHERE s.cliente_fk = ? "
                + "ORDER BY p.id DESC";

        List<Prestamo> prestamos = new ArrayList<>();

        try (
            Connection con = Conexion.conectar();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, clienteId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    prestamos.add(construirPrestamo(rs));
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "No se pudieron consultar los prestamos.", e);
        }

        return prestamos;
    }

    // CONSTRUIR OBJETO PRESTAMO
    private Prestamo construirPrestamo(ResultSet rs)
            throws SQLException {

        return new Prestamo(
                rs.getInt("id"),
                rs.getInt("solicitud_fk"),
                rs.getBigDecimal("interes"),
                rs.getBigDecimal("total_pagar"),
                rs.getBigDecimal("saldo_pendiente"),
                rs.getTimestamp("fecha_desembolso")
                        .toLocalDateTime(),
                rs.getDate("fecha_vencimiento_final")
                        .toLocalDate(),
                EstadoPrestamo.valueOf(
                        rs.getString("estado"))
        );
    }
}
