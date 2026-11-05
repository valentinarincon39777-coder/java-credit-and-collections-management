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

import model.entity.Cliente;

public class ClienteDAO {

    //ENCONTRAR CLIENTE POR SU DOCUEMNTO****************************
    
    public Cliente buscarPorDocumento(String documento) throws DAOException {
        String sql = "select p.id, p.nombre, p.documento, p.correo, p.telefono, p.direccion, "
                + "c.salario_formal, c.puntos_positivos, c.puntos_negativos "
                + "from persona p join cliente c on p.id=c.id "
                + "where p.documento=? ";

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
            throw new DAOException("Error al buscar el cliente \n", e);

        }

        return null;
    }

}
