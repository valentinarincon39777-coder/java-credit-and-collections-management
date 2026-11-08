
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


import java.math.BigDecimal;
import java.util.Map;
import java.util.LinkedHashMap;

public class PrestamoDAO {

    // INSERTAR PRESTAMO CON CONEXION COMPARTIDA DE SOLICITUD******************************************
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

    // CONSTRUIR OBJETO PRESTAMO**************************************
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
    
    
    
    // ACTUALIZAR ESTADOS SEGUN LAS FECHAS DE VENCIMIENTO******************************
    
    //REGLA: un prestamo se marca como vencido SOLO si todas sus cuotas estan vencidas
    //o sea, si la fecha ultima de vencimiento esta vencida 
    public void actualizarVencimientos() throws DAOException {

        String sqlCuotas =
                "UPDATE cuota SET estado = 'vencida' "
                + "WHERE estado = 'pendiente' "
                + "AND fecha_limite < CURDATE()";

        String sqlPrestamos =
                "UPDATE prestamo SET estado = 'vencido' "
                + "WHERE estado = 'activo' "
                + "AND fecha_vencimiento_final < CURDATE()";

        try (Connection con = Conexion.conectar()) {

            con.setAutoCommit(false);

            try (
                PreparedStatement psCuotas =
                        con.prepareStatement(sqlCuotas);
                PreparedStatement psPrestamos =
                        con.prepareStatement(sqlPrestamos)
            ) {

                psCuotas.executeUpdate();
                psPrestamos.executeUpdate();

                con.commit();

            } catch (SQLException e) {

                con.rollback();
                throw e;
            }

        } catch (SQLException e) {

            throw new DAOException(
                    "No se pudieron actualizar los vencimientos.", e);
        }
    }
    
    
    
    // CONSULTAR TODOS LOS PRESTAMOS***********************************+++
    public List<Prestamo> listarTodos() throws DAOException {

        String sql = "SELECT * FROM prestamo ORDER BY id DESC";

        List<Prestamo> prestamos = new ArrayList<>();

        try (
            Connection con = Conexion.conectar();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                prestamos.add(construirPrestamo(rs));
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "No se pudieron consultar los prestamos.", e);
        }

        return prestamos;
    }

    // CONSULTAR PRESTAMOS VENCIDOS******************************************+
    public List<Prestamo> listarVencidos() throws DAOException {

        String sql = "SELECT * FROM prestamo "
                + "WHERE estado = 'vencido' "
                + "ORDER BY id DESC";

        List<Prestamo> prestamos = new ArrayList<>();

        try (
            Connection con = Conexion.conectar();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                prestamos.add(construirPrestamo(rs));
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "No se pudieron consultar los prestamos vencidos.", e);
        }

        return prestamos;
    }
    
    
    
    // CONSULTAR RESUMEN GENERAL DE LA CARTERA*********************************++++
    public Map<String, Number> consultarCartera()
            throws DAOException {

        String sql =
                "SELECT "
                + "COUNT(*) AS total_prestamos, "
                + "COALESCE(SUM(total_pagar), 0) AS total_cobrar, "
                + "COALESCE(SUM(saldo_pendiente), 0) AS saldo_pendiente, "
                + "(SELECT COALESCE(SUM(monto), 0) "
                + "FROM pago WHERE estado = 'confirmado') AS total_recaudado, "
                + "COUNT(CASE WHEN estado = 'activo' THEN 1 END) AS activos, "
                + "COUNT(CASE WHEN estado = 'vencido' THEN 1 END) AS vencidos, "
                + "COUNT(CASE WHEN estado = 'pagado' THEN 1 END) AS pagados "
                + "FROM prestamo";

        Map<String, Number> cartera = new LinkedHashMap<>();

        try (
                Connection con = Conexion.conectar();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {

                cartera.put("totalPrestamos",
                        rs.getInt("total_prestamos"));

                cartera.put("totalCobrar",
                        
                        //suma de los montos totales de todos los préstamos, incluyendo intereses.
                        
                        rs.getBigDecimal("total_cobrar"));

                cartera.put("totalRecaudado",
                        rs.getBigDecimal("total_recaudado"));

                cartera.put("saldoPendiente",
                        rs.getBigDecimal("saldo_pendiente"));

                cartera.put("activos", rs.getInt("activos"));
                cartera.put("vencidos", rs.getInt("vencidos"));
                cartera.put("pagados", rs.getInt("pagados"));
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "No se pudo consultar la cartera.", e);
        }

        return cartera;
    }
    
   
    
}
