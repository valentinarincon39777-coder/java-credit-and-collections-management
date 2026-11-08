package dao;

import database.Conexion;
import exception.DAOException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;
import model.enums.EstadoPago;
import model.enums.MetodoPago;

import model.entity.Pago;
import java.sql.Timestamp;

public class PagoDAO {

    // CALCULAR TOTAL ABONADO A UNA CUOTA****************************************+++
    public BigDecimal calcularTotalAbonado(int cuotaId)
            throws DAOException {

        String sql = "SELECT COALESCE(SUM(monto), 0) "
                + "AS total_abonado FROM pago "
                + "WHERE cuota_fk = ? "
                + "AND estado = 'confirmado'";

        try (
                Connection con = Conexion.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cuotaId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getBigDecimal("total_abonado");
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "No se pudieron calcular los abonos.", e);
        }

        return BigDecimal.ZERO;
    }

    // REGISTRAR PAGO Y ACTUALIZAR SALDOS**************************************************
    //insertar pago
    //actualizar estado de cuota, si corresponde
    //actualizar saldo del prestamo
    public void registrarPago(Pago pago) throws DAOException {

        if (pago.getMonto() == null
                || pago.getMonto().signum() <= 0
                || pago.getMonto().scale() > 2) {
            throw new IllegalArgumentException(
                    "El monto debe ser positivo y tener maximo 2 decimales.");
        }
        //identificat y bloquear prestamo
        String sqlPrestamo
                = "SELECT p.id, p.saldo_pendiente "
                + "FROM prestamo p "
                + "JOIN cuota c ON c.prestamo_fk = p.id "
                + "WHERE c.id = ? "
                + "AND p.estado IN ('activo', 'vencido') "
                + "FOR UPDATE";
        //buscar cuota que por obligacion debe pagarse (fecha mas antigua)
        String sqlCuota
                = "SELECT id, monto_pagar FROM cuota "
                + "WHERE prestamo_fk = ? AND estado <> 'pagada' "
                + "ORDER BY fecha_limite, numero_cuota "
                + "LIMIT 1 FOR UPDATE";

        //calcular cuanto se ha abonado anteriormente (se hace tmb aquí pe el método de arriba usa otra conexion, y pro ello no lo reutilizamos)
        String sqlAbonos
                = "SELECT COALESCE(SUM(monto), 0) AS total "
                + "FROM pago WHERE cuota_fk = ? "
                + "AND estado = 'confirmado'";

        //regiistrar el pago confirmado
        String sqlInsertar
                = "INSERT INTO pago (cuota_fk, monto, metodo_pago, "
                + "fecha_confirmacion, estado, gestor_fk) "
                + "VALUES (?, ?, ?, NOW(), 'confirmado', ?)";

        String sqlCuotaPagada
                = "UPDATE cuota SET estado = 'pagada' WHERE id = ?";

        /*
        Si el saldo pendiente es exactamente igual al monto pagado, significa que el préstamo quedará en cero y cambia a pagado.
        Si todavía queda dinero por pagar, conserva su estado anterior.
        Independientemente de eso, se descuenta el monto del saldo.
         */
        String sqlSaldo
                = "UPDATE prestamo SET "
                + "estado = CASE WHEN saldo_pendiente = ? "
                + "THEN 'pagado' ELSE estado END, "
                + "saldo_pendiente = saldo_pendiente - ? "
                + "WHERE id = ? AND saldo_pendiente >= ?";

        try (Connection con = Conexion.conectar()) {

            con.setAutoCommit(false);

            try {
                int prestamoId;
                BigDecimal saldoPrestamo;

                // 1. BLOQUEAR EL PRESTAMO y evitar modificaciones simultaneas
                try (PreparedStatement ps
                        = con.prepareStatement(sqlPrestamo)) {

                    ps.setInt(1, pago.getCuotaId());

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalArgumentException(
                                    "La cuota no pertenece a un prestamo vigente.");
                        }

                        prestamoId = rs.getInt("id");
                        saldoPrestamo
                                = rs.getBigDecimal("saldo_pendiente");
                    }
                }

                // 2. OBTENER LA CUOTA MAS ANTIGUA SIN PAGAR
                //si o si está obligado a pagar dicha cuota antes de poder pagar cualqueir otra 
                int cuotaId;
                BigDecimal montoCuota;

                try (PreparedStatement ps
                        = con.prepareStatement(sqlCuota)) {

                    ps.setInt(1, prestamoId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalArgumentException(
                                    "No hay cuotas pendientes de pago.");
                        }

                        cuotaId = rs.getInt("id");
                        montoCuota = rs.getBigDecimal("monto_pagar");
                    }
                }

                if (pago.getCuotaId() != cuotaId) {
                    throw new IllegalArgumentException(
                            "Debe pagar primero la cuota mas antigua.");
                }

                // 3. CALCULAR LOS ABONOS ANTERIORES
                BigDecimal totalAbonado = BigDecimal.ZERO;

                try (PreparedStatement ps
                        = con.prepareStatement(sqlAbonos)) {

                    ps.setInt(1, cuotaId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            totalAbonado = rs.getBigDecimal("total");
                        }
                    }
                }
                
                //se resta lo que el cliente ya abonó en pagos anteriores, del monto original de la cuota
                BigDecimal restante
                        = montoCuota.subtract(totalAbonado);
                
                
                //verificar que la cuota todavia deba dinero
                if (restante.signum() <= 0) {
                    throw new IllegalArgumentException(
                            "La cuota ya esta completamente abonada.");
                }
                
                
                //verificar que el pago no supere lo que falta de la cuota
                if (pago.getMonto().compareTo(restante) > 0) {
                    throw new IllegalArgumentException(
                            "El pago supera el saldo restante de la cuota.");
                }
                
                //verificar que no supere el saldo general del prestamo
                if (pago.getMonto().compareTo(saldoPrestamo) > 0) {
                    throw new IllegalArgumentException(
                            "El pago supera el saldo del prestamo.");
                }

                // 4. INSERTAR EL PAGO CONFIRMADO
                try (PreparedStatement ps
                        = con.prepareStatement(sqlInsertar)) {

                    ps.setInt(1, cuotaId);
                    ps.setBigDecimal(2, pago.getMonto());
                    ps.setString(3, pago.getMetodoPago().name());
                    ps.setInt(4, pago.getGestorId());

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "No se pudo registrar el pago.");
                    }
                }

                // 5. MARCAR LA CUOTA COMO PAGADA SI SE COMPLETO (si el monto pagado es igual al retsante que falta por pagar)
                
            
                if (pago.getMonto().compareTo(restante) == 0) {

                    try (PreparedStatement ps =
                            con.prepareStatement(sqlCuotaPagada)) {

                        ps.setInt(1, cuotaId);

                        if (ps.executeUpdate() != 1) {
                            throw new SQLException(
                                    "No se pudo completar la cuota.");
                        }
                    }

                    // ASIGNAR PUNTOS SOLO AL COMPLETAR LA CUOTA
                    actualizarPuntosCliente(cuotaId, con);
                }


                // 6. ACTUALIZAR SALDO Y ESTADO DEL PRESTAMO
                try (PreparedStatement ps
                        = con.prepareStatement(sqlSaldo)) {

                    ps.setBigDecimal(1, pago.getMonto());
                    ps.setBigDecimal(2, pago.getMonto());
                    ps.setInt(3, prestamoId);
                    ps.setBigDecimal(4, pago.getMonto());

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "No se pudo actualizar el saldo.");
                    }
                }

                con.commit();

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "No se pudo completar el registro del pago.", e);
        }
    }
    
    
    
    // CONSULTAR PAGOS DE UN CLIENTE*****************************************************
    public List<Pago> listarPorCliente(int clienteId)
            throws DAOException {

        String sql = "SELECT p.* FROM pago p "
                + "JOIN cuota c ON p.cuota_fk = c.id "
                + "JOIN prestamo pr ON c.prestamo_fk = pr.id "
                + "JOIN solicitud_prestamo s "
                + "ON pr.solicitud_fk = s.id "
                + "WHERE s.cliente_fk = ? "
                + "AND p.estado = 'confirmado' "
                + "ORDER BY p.fecha_confirmacion DESC, p.id DESC";

        return consultarPagos(sql, clienteId);
    }

    // CONSULTAR PAGOS REGISTRADOS POR UN GESTOR*******************************************
    public List<Pago> listarPorGestor(int gestorId)
            throws DAOException {

        String sql = "SELECT * FROM pago "
                + "WHERE gestor_fk = ? "
                + "AND estado = 'confirmado' "
                + "ORDER BY fecha_confirmacion DESC, id DESC";

        return consultarPagos(sql, gestorId);
    }

    // CONSULTAR TODOS LOS PAGOS CONFIRMADOS
    public List<Pago> listarTodos() throws DAOException {

        String sql = "SELECT * FROM pago "
                + "WHERE estado = 'confirmado' "
                + "ORDER BY fecha_confirmacion DESC, id DESC";

        return consultarPagos(sql, null);
    }

    // METODO AUXILIAR PARA EJECUTAR LAS CONSULTAS (se usa para los 3 metodos anteriores)******************************************
    private List<Pago> consultarPagos(
            String sql, Integer filtro) throws DAOException {

        List<Pago> pagos = new ArrayList<>();

        try (
                Connection con = Conexion.conectar();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            //se pone el id, ya sea el del cliente o el del gestor. 
            if (filtro != null) {
                ps.setInt(1, filtro);
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    java.sql.Timestamp fechaSolicitud =
                            rs.getTimestamp("fecha_solicitud");

                    java.sql.Timestamp fechaConfirmacion =
                            rs.getTimestamp("fecha_confirmacion");

                    Pago pago = new Pago(
                            rs.getInt("id"),
                            rs.getInt("cuota_fk"),
                            rs.getBigDecimal("monto"),
                            MetodoPago.valueOf(
                                    rs.getString("metodo_pago")),
                            fechaSolicitud == null ? null
                                    : fechaSolicitud.toLocalDateTime(),
                            
                            
                            fechaConfirmacion == null ? null
                                    : fechaConfirmacion.toLocalDateTime(),
                            
                            
                            
                            
                            EstadoPago.valueOf(
                                    rs.getString("estado")),
                            rs.getInt("gestor_fk")
                    );

                    pagos.add(pago);
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "No se pudieron consultar los pagos.", e);
        }

        return pagos;
    }
    
    
    // ASIGNAR PUNTOS AL COMPLETAR UNA CUOTA*************************************************************
    //reglas: 
    //por cada ultima cuota pagada A TIEMPO, 3 puntos positivos. 
    // por cada ultima cuota pagada con retraso, 3 puntos negativos por cada mora
    private void actualizarPuntosCliente(
            int cuotaId, Connection con) throws SQLException {

        String sqlDatos =
                "SELECT s.cliente_fk, "
                + "DATEDIFF(CURDATE(), c.fecha_limite) AS dias_mora "
                + "FROM cuota c "
                + "JOIN prestamo p ON c.prestamo_fk = p.id "
                + "JOIN solicitud_prestamo s ON p.solicitud_fk = s.id "
                + "WHERE c.id = ?";
                // datedif(fecha_actual, fecha_limite), substract

        String sqlActualizar =
                "UPDATE cliente SET "
                + "puntos_positivos = puntos_positivos + ?, "
                + "puntos_negativos = puntos_negativos + ? "
                + "WHERE id = ?";

        int clienteId;
        int diasMora;

        // 1. OBTENER CLIENTE Y DIAS DE MORA
        try (PreparedStatement ps = con.prepareStatement(sqlDatos)) {

            ps.setInt(1, cuotaId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    throw new SQLException(
                            "No se encontraron datos de la cuota.");
                }

                clienteId = rs.getInt("cliente_fk");
                diasMora = rs.getInt("dias_mora");
            }
        }

        // 2. CALCULAR PUNTOS
        int puntosPositivos = 0;
        int puntosNegativos = 0;

        if (diasMora <= 0) {
            puntosPositivos = 3;
        } else {
            puntosNegativos = Math.multiplyExact(diasMora, 3);
        }

        // 3. ACTUALIZAR PUNTOS DEL CLIENTE
        try (PreparedStatement ps =
                con.prepareStatement(sqlActualizar)) {

            ps.setInt(1, puntosPositivos);
            ps.setInt(2, puntosNegativos);
            ps.setInt(3, clienteId);

            if (ps.executeUpdate() != 1) {
                throw new SQLException(
                        "No se pudieron actualizar los puntos.");
            }
        }
    }



}
