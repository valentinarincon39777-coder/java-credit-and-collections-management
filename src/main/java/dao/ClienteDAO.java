package dao;

import database.Conexion;
import exception.DAOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.entity.Cliente;

public class ClienteDAO {

    // BUSCAR CLIENTE POR DOCUMENTO*******************************************
    public Cliente buscarPorDocumento(String documento)
            throws DAOException {

        String sql = "SELECT p.id, p.nombre, p.documento, "
                + "p.correo, p.telefono, p.direccion, "
                + "c.salario_formal, c.puntos_positivos, "
                + "c.puntos_negativos "
                + "FROM persona p "
                + "JOIN cliente c ON p.id = c.id "
                + "WHERE p.documento = ?";

        try (
                Connection con = Conexion.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, documento);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new Cliente(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("documento"),
                            rs.getString("correo"),
                            rs.getString("telefono"),
                            rs.getString("direccion"),
                            rs.getBigDecimal("salario_formal"),
                            rs.getInt("puntos_positivos"),
                            rs.getInt("puntos_negativos")
                    );
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Error al buscar el cliente.", e);
        }

        return null;
    }

    // REGISTRAR CLIENTE*********************************************
    public void registrarCliente(Cliente cliente)
            throws DAOException {

        String sqlPersona
                = "INSERT INTO persona "
                + "(nombre, documento, correo, telefono, direccion) "
                + "VALUES (?, ?, ?, ?, ?)";

        String sqlCliente
                = "INSERT INTO cliente "
                + "(id, salario_formal, puntos_positivos, puntos_negativos) "
                + "VALUES (?, ?, 0, 0)";

        try (Connection con = Conexion.conectar()) {

            con.setAutoCommit(false);

            try {

                int idGenerado;

                try (PreparedStatement ps
                        = con.prepareStatement(
                                sqlPersona,
                                Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, cliente.getNombre());
                    ps.setString(2, cliente.getDocumento());
                    ps.setString(3, cliente.getCorreo());
                    ps.setString(4, cliente.getTelefono());
                    ps.setString(5, cliente.getDireccion());

                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {

                        if (!rs.next()) {
                            throw new SQLException(
                                    "No se pudo obtener el ID.");
                        }

                        idGenerado = rs.getInt(1);
                    }
                }

                try (PreparedStatement ps
                        = con.prepareStatement(sqlCliente)) {

                    ps.setInt(1, idGenerado);
                    ps.setBigDecimal(
                            2, cliente.getSalarioFormal());

                    ps.executeUpdate();
                }

                con.commit();

                cliente.setId(idGenerado);

            } catch (SQLException e) {

                try {
                    con.rollback();
                } catch (SQLException errorRollback) {
                    e.addSuppressed(errorRollback);
                }

                throw new DAOException(
                        "Error al registrar el cliente.", e);

            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Error de conexion o transaccion.", e);
        }
    }
    
    
    
    
    // VERIFICAR SI EL DOCUMENTO YA EXISTE EN PERSONA
    public boolean existeDocumento(String documento)
            throws DAOException {

        String sql = "SELECT id FROM persona WHERE documento = ?";

        try (
            Connection con = Conexion.conectar();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, documento);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Error al verificar el documento.", e);
        }
    }

    
    
    
    
    
    
    
    
    
    
    
    
}






