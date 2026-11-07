
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

import model.entity.Solicitud;
import model.enums.EstadoSolicitud;


import java.time.LocalDate;
import java.time.LocalDateTime;
import model.entity.Prestamo;
import model.entity.Cuota;
import dao.PrestamoDAO;
import dao.CuotaDAO;
import strategy.AmortizacionFrancesa;
import utils.CalculadoraCuotas;

public class SolicitudDAO {

    // REGISTRAR SOLICITUD****************************************************
    public boolean registrarSolicitud(Solicitud solicitud)
            throws DAOException {

        String sqlBloquear =
                "SELECT id FROM cliente WHERE id = ? FOR UPDATE";

        String sqlPendiente =
                "SELECT s.id FROM solicitud_prestamo s "
                + "WHERE s.cliente_fk = ? "
                + "AND s.estado_solicitud = 'pendiente' LIMIT 1";

        String sqlPrestamo =
                "SELECT p.id FROM prestamo p "
                + "JOIN solicitud_prestamo s "
                + "ON p.solicitud_fk = s.id "
                + "WHERE s.cliente_fk = ? "
                + "AND p.estado IN ('activo', 'vencido') LIMIT 1";

        String sqlInsertar =
                "INSERT INTO solicitud_prestamo "
                + "(cliente_fk, asesor_fk, monto_solicitado, "
                + "cantidad_cuotas, estado_solicitud, fecha_solicitud) "
                + "VALUES (?, ?, ?, ?, 'pendiente', NOW())";

        try (Connection con = Conexion.conectar()) {

            con.setAutoCommit(false);

            try {
                
                try (PreparedStatement ps =
                        con.prepareStatement(sqlBloquear)) {

                    ps.setInt(1, solicitud.getClienteId());

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }
                    }
                }


                if (existeRegistro(con, sqlPendiente,
                        solicitud.getClienteId())) {
                    con.rollback();
                    return false;
                }

              
                if (existeRegistro(con, sqlPrestamo,
                        solicitud.getClienteId())) {
                    con.rollback();
                    return false;
                }

              
                try (PreparedStatement ps =
                        con.prepareStatement(sqlInsertar,
                                Statement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, solicitud.getClienteId());

                    if (solicitud.getAsesorId() == null) {
                        ps.setNull(2, java.sql.Types.INTEGER);
                    } else {
                        ps.setInt(2, solicitud.getAsesorId());
                    }

                    ps.setBigDecimal(3,
                            solicitud.getMontoSolicitado());
                    ps.setInt(4, solicitud.getCantidadCuotas());

                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            solicitud.setId(rs.getInt(1));
                        }
                    }
                }

                con.commit();
                return true;

            } catch (SQLException e) {

                try {
                    con.rollback();
                } catch (SQLException error) {
                    e.addSuppressed(error);
                }

                throw new DAOException(
                        "Error al registrar solicitud.", e);

            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Error de conexion o transaccion.", e);
        }
    }

    // COMPROBAR EXISTENCIA CON LA MISMA CONEXION*****************************+
    private boolean existeRegistro(Connection con,
            String sql, int clienteId) throws SQLException {

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, clienteId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // CONSULTAR SOLICITUDES DE UN CLIENTE*******************************************
    public List<Solicitud> listarPorCliente(int clienteId)
            throws DAOException {

        return listarSolicitudes(
                "WHERE cliente_fk = ?", clienteId);
    }

    // CONSULTAR SOLICITUDES DE UN ASESOR**********************************************
    public List<Solicitud> listarPorAsesor(int asesorId)
            throws DAOException {

        return listarSolicitudes(
                "WHERE asesor_fk = ?", asesorId);
    }

    // CONSULTAR TODAS LAS SOLICITUDES***************************************
    public List<Solicitud> listarTodas()
            throws DAOException {

        return listarSolicitudes("", null);
    }

    // CONSULTA REUTILIZABLE (los filtros los tienen los 3 métodoa anteriores)*****************************************
    private List<Solicitud> listarSolicitudes(
            String filtro, Integer id) throws DAOException {

        String sql = "SELECT * FROM solicitud_prestamo "
                + filtro + " ORDER BY fecha_solicitud DESC, id DESC";

        List<Solicitud> solicitudes = new ArrayList<>();

        try (
            Connection con = Conexion.conectar();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            if (id != null) {
                ps.setInt(1, id);
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    solicitudes.add(construirSolicitud(rs));
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "No fue posible consultar solicitudes.", e);
        }

        return solicitudes;
    }

    // CONVERTIR FILA SQL EN SOLICITUD****************************
    private Solicitud construirSolicitud(ResultSet rs)
            throws SQLException {

        Timestamp evaluacion =
                rs.getTimestamp("fecha_evaluacion");

        return new Solicitud(
                rs.getInt("id"),
                rs.getInt("cliente_fk"),
                (Integer) rs.getObject("asesor_fk"),
                (Integer) rs.getObject("analista_fk"),
                rs.getBigDecimal("monto_solicitado"),
                rs.getInt("cantidad_cuotas"),
                EstadoSolicitud.valueOf(
                        rs.getString("estado_solicitud")),
                rs.getTimestamp("fecha_solicitud")
                        .toLocalDateTime(),
                evaluacion == null
                        ? null : evaluacion.toLocalDateTime(),
                rs.getString("motivo_rechazo")
        );
    }
    
    
    
    
    // CONSULTAR SOLICITUDES PENDIENTES (funcionalidad del analista que aprobara las solicitudes)
    public List<Solicitud> listarPendientes()
            throws DAOException {

        return listarSolicitudes(
                "WHERE estado_solicitud = 'pendiente'", null);
    }

    
    
    
    
    // APROBAR SOLICITUD Y GENERAR PRESTAMO CON CUOTAS*****************************************
    //Validar → Calcular → Actualizar solicitud → Insertar préstamo → Insertar cuotas → Commit
    public boolean aprobarSolicitud(int solicitudId, int analistaId)
            throws DAOException {

        String sqlCliente =
                "SELECT cliente_fk FROM solicitud_prestamo "
                + "WHERE id = ?";

        String sqlBloquearCliente =
                "SELECT id FROM cliente WHERE id = ? FOR UPDATE";

        String sqlSolicitud =
                "SELECT * FROM solicitud_prestamo "
                + "WHERE id = ? FOR UPDATE";
        
        
        
        
        String sqlPrestamoVigente =
                "SELECT p.id FROM prestamo p "
                + "JOIN solicitud_prestamo s "
                + "ON p.solicitud_fk = s.id "
                + "WHERE s.cliente_fk = ? "
                + "AND p.estado IN ('activo', 'vencido') LIMIT 1";

        String sqlActualizar =
                "UPDATE solicitud_prestamo "
                + "SET estado_solicitud = 'aceptado', "
                + "analista_fk = ?, fecha_evaluacion = NOW() "
                + "WHERE id = ? AND estado_solicitud = 'pendiente'";

        try (Connection con = Conexion.conectar()) {

            con.setAutoCommit(false);

            try {
                int clienteId;

                // 1. IDENTIFICAR AL CLIENTE
                try (PreparedStatement ps =
                        con.prepareStatement(sqlCliente)) {

                    ps.setInt(1, solicitudId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }

                        clienteId = rs.getInt("cliente_fk");
                    }
                }

                // 2. BLOQUEAR AL CLIENTE
                try (PreparedStatement ps =
                        con.prepareStatement(sqlBloquearCliente)) {

                    ps.setInt(1, clienteId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }
                    }
                }

                Solicitud solicitud;

                // 3. BLOQUEAR Y COMPROBAR LA SOLICITUD
                try (PreparedStatement ps =
                        con.prepareStatement(sqlSolicitud)) {

                    ps.setInt(1, solicitudId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }

                        solicitud = construirSolicitud(rs);
                    }
                }

                if (solicitud.getEstado() != EstadoSolicitud.pendiente) {
                    con.rollback();
                    return false;
                }

                // 4. COMPROBAR QUE NO EXISTA PRESTAMO VIGENTE
                if (existeRegistro(con, sqlPrestamoVigente, clienteId)) {
                    con.rollback();
                    return false;
                }

                // 5. CALCULAR CUOTAS UTILIZANDO STRATEGY
                CalculadoraCuotas calculadora =
                        new CalculadoraCuotas(new AmortizacionFrancesa());
                
                //momento de desembolso es justo cuando se aprueba la solicitud
                LocalDateTime desembolso = LocalDateTime.now();

                List<Cuota> cuotas = calculadora.generarCalendario(
                        solicitud.getMontoSolicitado(),
                        solicitud.getCantidadCuotas(),
                        desembolso.toLocalDate()
                );

                var totalPagar = calculadora.calcularTotalPagar(cuotas);

                // 6. CREAR OBJETO PRESTAMO
                Prestamo prestamo = new Prestamo(
                        solicitudId,
                        calculadora.getTasaMensualPorcentaje(),
                        totalPagar,
                        desembolso,
                        cuotas.get(cuotas.size() - 1).getFechaLimite()
                );

                // 7. ACTUALIZAR LA SOLICITUD
                //pasar solicitud a estado 'aceptado'
                try (PreparedStatement ps =
                        con.prepareStatement(sqlActualizar)) {

                    ps.setInt(1, analistaId);
                    ps.setInt(2, solicitudId);

                    if (ps.executeUpdate() != 1) {
                        con.rollback();
                        return false;
                    }
                }

                // 8. INSERTAR PRESTAMO CON LA MISMA CONEXION
                PrestamoDAO prestamoDAO = new PrestamoDAO();
                prestamoDAO.insertar(prestamo, con);

                // 9. ASIGNAR ID DEL PRESTAMO A CADA CUOTA
                for (Cuota cuota : cuotas) {
                    cuota.setPrestamoId(prestamo.getId());
                }

                // 10. INSERTAR TODAS LAS CUOTAS
                CuotaDAO cuotaDAO = new CuotaDAO();
                cuotaDAO.insertarCuotas(cuotas, con);

                // 11. CONFIRMAR TODA LA OPERACION
                con.commit();

                return true;

            } catch (SQLException | RuntimeException e) {

                try {
                    con.rollback();
                } catch (SQLException error) {
                    e.addSuppressed(error);
                }

                throw new DAOException(
                        "No se pudo completar la aprobacion.", e);

            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "No se pudo completar la transaccion.", e);
        }
    }

    
    
    
    
    
    
    // RECHAZAR SOLICITUD PENDIENTE******************************************************
    public boolean rechazarSolicitud(
            int solicitudId, int analistaId, String motivo)
            throws DAOException {

        if (motivo == null || motivo.isBlank()
                || motivo.length() > 45) {

            throw new IllegalArgumentException(
                    "El motivo de rechazo no es valido.");
        }

        String sql =
                "UPDATE solicitud_prestamo "
                + "SET estado_solicitud = 'denegado', "
                + "analista_fk = ?, fecha_evaluacion = NOW(), "
                + "motivo_rechazo = ? "
                + "WHERE id = ? AND estado_solicitud = 'pendiente'";

        try (
            Connection con = Conexion.conectar();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, analistaId);
            ps.setString(2, motivo);
            ps.setInt(3, solicitudId);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new DAOException(
                    "No se pudo rechazar la solicitud.", e);
        }
    }

    
}
