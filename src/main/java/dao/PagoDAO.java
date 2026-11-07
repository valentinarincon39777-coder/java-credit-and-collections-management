package dao;

import database.Conexion;
import exception.DAOException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

                    try (PreparedStatement ps
                            = con.prepareStatement(sqlCuotaPagada)) {

                        ps.setInt(1, cuotaId);
                        ps.executeUpdate();
                    }
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

}
