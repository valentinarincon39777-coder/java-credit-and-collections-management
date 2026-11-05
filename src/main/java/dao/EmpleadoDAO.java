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

import model.entity.Empleado;

import model.enums.EstadoEmpleado;
import model.enums.Rol;

public class EmpleadoDAO {

    //ENCONTRAR EMPLEADO POR SU DOCUMENTO***************************
    public Empleado buscarPorDocumento(String documento) throws DAOException {
        String sql = "select p.id, p.nombre, p.documento, p.correo, p.telefono, p.direccion "
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

                }}

            } catch (SQLException e) {

                throw new DAOException("Error al buscar el empleado \n", e);
            }

            return null;

        }}
