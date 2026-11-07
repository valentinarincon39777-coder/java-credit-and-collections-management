
package dao;

import database.Conexion;
import exception.DAOException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PagoDAO {

    // CALCULAR TOTAL ABONADO A UNA CUOTA****************************************+++
    public BigDecimal calcularTotalAbonado(int cuotaId)
            throws DAOException {

        String sql = "SELECT COALESCE(SUM(monto), 0) "
                + "AS total_abonado FROM pago "
                + "WHERE cuota_fk = ? "
                + "AND estado = 'confirmado'";

        try (
            Connection con = Conexion.conectar();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

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
}

