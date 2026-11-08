package dao;

import database.Conexion;
import exception.DAOException;
import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.entity.Cliente;

import java.util.List;
import java.util.ArrayList;


import java.util.Map;
import java.util.LinkedHashMap;



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

    // VERIFICAR SI EL DOCUMENTO YA EXISTE EN PERSONA**************************
    public boolean existeDocumento(String documento)
            throws DAOException {

        String sql = "SELECT id FROM persona WHERE documento = ?";

        try (
                Connection con = Conexion.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, documento);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Error al verificar el documento.", e);
        }

    }

    // CONSULTAR TODOS LOS CLIENTES*************************
    public List<Cliente> listarClientes() throws DAOException {

        String sql = "SELECT p.*, c.salario_formal, "
                + "c.puntos_positivos, c.puntos_negativos "
                + "FROM persona p "
                + "JOIN cliente c ON p.id = c.id "
                + "ORDER BY p.nombre";

        List<Cliente> clientes = new ArrayList<>();

        try (
                Connection con = Conexion.conectar(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                clientes.add(construirCliente(rs));
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "No fue posible consultar los clientes.", e);
        }

        return clientes;
    }

    // BUSCAR CLIENTES POR NOMBRE*****************************
    public List<Cliente> buscarPorNombre(String nombre)
            throws DAOException {

        String sql = "SELECT p.*, c.salario_formal, "
                + "c.puntos_positivos, c.puntos_negativos "
                + "FROM persona p "
                + "JOIN cliente c ON p.id = c.id "
                + "WHERE p.nombre LIKE ? "
                + "ORDER BY p.nombre";

        List<Cliente> clientes = new ArrayList<>();

        try (
                Connection con = Conexion.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + nombre + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    clientes.add(construirCliente(rs));
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "No fue posible buscar por nombre.", e);
        }

        return clientes;
    }

    // ORDENAR CLIENTES SEGUN SUS PUNTOS*************************
    public List<Cliente> listarPorPuntos(boolean positivos)
            throws DAOException {

        String columna = positivos
                ? "c.puntos_positivos"
                : "c.puntos_negativos";

        String sql = "SELECT p.*, c.salario_formal, "
                + "c.puntos_positivos, c.puntos_negativos "
                + "FROM persona p "
                + "JOIN cliente c ON p.id = c.id "
                + "ORDER BY " + columna + " DESC, p.nombre";

        List<Cliente> clientes = new ArrayList<>();

        try (
                Connection con = Conexion.conectar(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                clientes.add(construirCliente(rs));
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "No fue posible ordenar los clientes.", e);
        }

        return clientes;
    }
    
    
    
    
    
    
    // CONSULTAR CLIENTES CON CUOTAS VENCIDAS (MOROSOS)*******************************************
    //este metodo se usará para los reportes
    //un cliente puede ser moroso aunque una prestamo siga activo, en lugar de vencido, siempre y cuando al menos una de sus cuotas esten vencidas
    //de igual forma recordar que un prestamo solo esta vencido cuando todas sus cuotas han vencido
    
    public Map<Cliente, Integer> listarClientesMorosos()
            throws DAOException {

        String sql = "SELECT p.*, c.salario_formal, "
                + "c.puntos_positivos, c.puntos_negativos, "
                + "m.cuotas_vencidas "
                + "FROM persona p "
                + "JOIN cliente c ON p.id = c.id "
                + "JOIN ( "
                + "SELECT s.cliente_fk, COUNT(*) AS cuotas_vencidas "
                + "FROM cuota cu "
                + "JOIN prestamo pr ON cu.prestamo_fk = pr.id "
                + "JOIN solicitud_prestamo s "
                + "ON pr.solicitud_fk = s.id "
                + "WHERE cu.estado = 'vencida' " //filtro
                + "GROUP BY s.cliente_fk "
                + ") m ON m.cliente_fk = c.id "
                + "ORDER BY m.cuotas_vencidas DESC, p.nombre";

        Map<Cliente, Integer> morosos = new LinkedHashMap<>();

        try (
                Connection con = Conexion.conectar();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Cliente cliente = construirCliente(rs);

                int cuotasVencidas =
                        rs.getInt("cuotas_vencidas");

                morosos.put(cliente, cuotasVencidas);
                
                //cuotasVencidas= cantidad de cuotas vencidas de dicho cliente
            }

        } catch (SQLException e) {

            throw new DAOException(
                    "No se pudieron consultar los clientes morosos.", e);
        }

        return morosos;
    }



    
    
    
    
    

    // CONVERTIR UNA FILA SQL EN UN OBJETO CLIENTE****************************
    private Cliente construirCliente(ResultSet rs)
            throws SQLException {

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
    
    
    // ACTUALIZAR DATOS PERSONALES*************************************************************
    public boolean actualizarDatoPersonal(
            int id, int campo, String valor)
            throws DAOException {

        String columna;

        switch (campo) {
            case 1:
                columna = "nombre";
                break;
            case 2:
                columna = "documento";
                break;
            case 3:
                columna = "correo";
                break;
            case 4:
                columna = "telefono";
                break;
            case 5:
                columna = "direccion";
                break;
            default:
                return false;
        }

        String sql = "UPDATE persona SET "
                + columna + " = ? WHERE id = ?";

        try (
            Connection con = Conexion.conectar();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, valor);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DAOException(
                    "No se pudo actualizar el dato.", e);
        }
    }

    
    
    
    
    
    
    
    
    
    // ACTUALIZAR SALARIO FORMAL***********************************************************************************
    public boolean actualizarSalario(
            int id, BigDecimal salario)
            throws DAOException {

        String sql = "UPDATE cliente "
                + "SET salario_formal = ? WHERE id = ?";

        try (
            Connection con = Conexion.conectar();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setBigDecimal(1, salario);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DAOException(
                    "No se pudo actualizar el salario.", e);
        }
    }

    
    
    // OBTENER DOCUMENTO ACTUAL POR ID (por si se actualizó el documento en en metodo actualizarCliente )****************************************
    public String buscarDocumentoPorId(int id)
            throws DAOException {

        String sql = "SELECT documento FROM persona WHERE id = ?";

        try (
            Connection con = Conexion.conectar();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getString("documento");
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "No se pudo consultar el documento.", e);
        }

        return null;
    }
    
    
    
    // ELIMINAR CLIENTE (solo si no tiene solicitudes)***************************************
    public boolean eliminarCliente(int id) throws DAOException {

        String sqlBuscar =
                "SELECT id FROM cliente WHERE id = ? FOR UPDATE"; 

        String sqlVerificar =
                "SELECT id FROM solicitud_prestamo "
                + "WHERE cliente_fk = ? ";

        String sqlCliente =
                "DELETE FROM cliente WHERE id = ?";

        String sqlPersona =
                "DELETE FROM persona WHERE id = ?";

        try (Connection con = Conexion.conectar()) {

            con.setAutoCommit(false);

            try {

                
                try (PreparedStatement ps =
                        con.prepareStatement(sqlBuscar)) {

                    ps.setInt(1, id);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }
                    }
                }

              
                try (PreparedStatement ps =
                        con.prepareStatement(sqlVerificar)) {

                    ps.setInt(1, id);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            con.rollback();
                            return false;
                        }
                    }
                }

           
                try (PreparedStatement ps =
                        con.prepareStatement(sqlCliente)) {

                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

              
                try (PreparedStatement ps =
                        con.prepareStatement(sqlPersona)) {

                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

               
                con.commit();
                return true;

            } catch (SQLException e) {

                try {
                    con.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                throw new DAOException(
                        "No se pudo eliminar el cliente.", e);
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Error de base de datos al eliminar.", e);
        }
    }
    
    
    
    

}
