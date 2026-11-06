/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author valen
 */
import dao.EmpleadoDAO;
import exception.DAOException;

import java.util.Scanner;

import model.entity.Empleado;

import view.Mensajes;
import view.VistaEmpleado;

public class ControllerEmpleado {

    private final EmpleadoDAO empleadoDAO;
    private final VistaEmpleado vista;
    private final Mensajes mensajes;

    public ControllerEmpleado(Scanner entrada) {
        this.empleadoDAO = new EmpleadoDAO();
        this.vista = new VistaEmpleado(entrada);
        this.mensajes = new Mensajes();

    }

    //REGISTRAR NUEVO EMPLEADO **********************************
    public void registrarEmpleado() {
        Empleado empleado = vista.solicitarDatosRegistro();

        try {
            if (empleadoDAO.existeDocumento(empleado.getDocumento())) {

                mensajes.error("Ya existe una persona con esa cédula");
                return;
            }

            empleadoDAO.registrarEmpleado(empleado);
            mensajes.exito("Empleado registrado correctamente. ID: "
                    + empleado.getId());

        } catch (DAOException e) {

            mensajes.error("No fue posible registrar el empleado.");

        }

    }

}
