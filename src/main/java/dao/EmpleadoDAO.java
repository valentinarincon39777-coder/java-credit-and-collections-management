/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author valen
 */
import database.Conexion;
import exception.DAOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.entity.Empleado;

import model.enums.EstadoEmpleado;
import model.enums.Rol;


import java.util.List;
import java.util.ArrayList;

public class EmpleadoDAO {

    //ENCONTRAR EMPLEADO POR SU DOCUMENTO***************************
    public Empleado buscarPorDocumento(String documento) throws DAOException {
        String sql = "select p.id, p.nombre, p.documento, p.correo, p.telefono, p.direccion, "
                + "e.rol, e.estado "
                + "from persona p join empleado e on p.id=e.id "
                + "where p.documento=?";

        try (Connection con = Conexion.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, documento);

            try (
                    ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new Empleado(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("documento"),
                            rs.getString("correo"),
                            rs.getString("telefono"),
                            rs.getString("direccion"),
                            Rol.valueOf(rs.getString("rol")),
                            EstadoEmpleado.valueOf(rs.getString("estado"))
                    );

                }
            }

        } catch (SQLException e) {

            throw new DAOException("Error al buscar el empleado \n", e);
        }

        return null;

    }
    
    
    
    
    

    //REGISTRAR EMPLEADO****************************
    public void registrarEmpleado(Empleado empleado)
            throws DAOException {

        String sqlPersona = "insert into persona "
                + "(nombre, documento, correo, telefono, direccion)"
                + "values(?, ?, ?, ?, ?)";

        String sqlEmpleado = "insert into empleado"
                + " (id, rol)"
                + "values(?, ?)";

        try (Connection con = Conexion.conectar()) {
            con.setAutoCommit(false);

            try {

                int idGenerado;

                try (PreparedStatement ps = con.prepareStatement(
                        sqlPersona, Statement.RETURN_GENERATED_KEYS
                )) {
                    ps.setString(1, empleado.getNombre());
                    ps.setString(2, empleado.getDocumento());
                    ps.setString(3, empleado.getCorreo());
                    ps.setString(4, empleado.getTelefono());
                    ps.setString(5, empleado.getDireccion());

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
                        = con.prepareStatement(sqlEmpleado)) {

                    ps.setInt(1, idGenerado);
                    ps.setString(
                            2, empleado.getRol().name());

                    ps.executeUpdate();
                }

                con.commit();

                empleado.setId(idGenerado);

            } catch (SQLException e) {

                try {
                    con.rollback();
                } catch (SQLException errorRollback) {
                    e.addSuppressed(errorRollback);
                }

                throw new DAOException(
                        "Error al registrar el empleado.", e);

            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Error de conexion o transaccion.", e);
        }
    }

    //VERIFICAR SI EL COCUMENTO YA EXISTE EN PERSONA****************************************************
    public boolean existeDocumento(String documento) throws DAOException {
        String sql = "select id from persona where documento=?";

        try (Connection con = Conexion.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, documento);

            try (ResultSet rs = ps.executeQuery()) {

                return rs.next();

            }

        } catch (SQLException e) {
            throw new DAOException("Error al verificar el documento.", e);

        }

    }
    
    
    
    
    // CONVERTIR RESULTSET EN EMPLEADO******************************************************++
    private Empleado construirEmpleado(ResultSet rs)
            throws SQLException {

        return new Empleado(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("documento"),
                rs.getString("correo"),
                rs.getString("telefono"),
                rs.getString("direccion"),
                Rol.valueOf(rs.getString("rol")),
                EstadoEmpleado.valueOf(rs.getString("estado"))
        );
    }

    // CONSULTAR TODOS LOS EMPLEADOS*************************************
    public List<Empleado> listarEmpleados()
            throws DAOException {

        String sql = "SELECT p.*, e.rol, e.estado "
                + "FROM persona p "
                + "JOIN empleado e ON p.id = e.id "
                + "ORDER BY p.nombre";

        List<Empleado> empleados = new ArrayList<>();

        try (
            Connection con = Conexion.conectar();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                empleados.add(construirEmpleado(rs));
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "No se pudieron consultar los empleados.", e);
        }

        return empleados;
    }

    // BUSCAR EMPLEADO POR ID ***************************************************
    public Empleado buscarPorId(int id)
            throws DAOException {

        String sql = "SELECT p.*, e.rol, e.estado "
                + "FROM persona p "
                + "JOIN empleado e ON p.id = e.id "
                + "WHERE p.id = ?";

        try (
            Connection con = Conexion.conectar();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return construirEmpleado(rs);
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "No se pudo buscar el empleado.", e);
        }

        return null;
    }

    // ACTUALIZAR DATOS PERSONALES************************************++
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
                    "No se pudieron actualizar los datos.", e);
        }
    }

    // ACTUALIZAR ROL DEL EMPLEADO**************************************
    public boolean actualizarRol(int id, Rol rol)
            throws DAOException {

        String sql = "UPDATE empleado "
                + "SET rol = ? WHERE id = ?";

        try (
            Connection con = Conexion.conectar();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, rol.name());
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DAOException(
                    "No se pudo actualizar el rol.", e);
        }
    }

    // DESACTIVAR EMPLEADO**************************************
    public boolean desactivarEmpleado(int id)
            throws DAOException {

        String sql = "UPDATE empleado "
                + "SET estado = 'inactivo' "
                + "WHERE id = ? AND estado = 'activo'";

        try (
            Connection con = Conexion.conectar();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DAOException(
                    "No se pudo desactivar el empleado.", e);
        }
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

}
