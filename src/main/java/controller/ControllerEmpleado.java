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

import java.util.List;

import model.enums.Rol;
import model.enums.EstadoEmpleado;

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
    
    
    
    // MENU DE GESTION DE EMPLEADOS*****************************
    public void gestionarEmpleados(Empleado administrador) {

        int opcion;

        do {
            opcion = vista.mostrarMenuGestion();

            switch (opcion) {

                case 1:
                    registrarEmpleado();
                    break;

                case 2:
                    consultarEmpleados();
                    break;

                case 3:
                    actualizarEmpleado(administrador);
                    break;

                case 4:
                    desactivarEmpleado(administrador);
                    break;

                case 0:
                    mensajes.informativo(
                            "Regresando al panel administrador.");
                    break;
            }

        } while (opcion != 0);
    }

    // CONSULTAR EMPLEADOS*********************************
    public void consultarEmpleados() {

        int opcion;

        do {
            opcion = vista.mostrarMenuConsultas();

            try {

                switch (opcion) {

                    case 1:
                        List<Empleado> empleados =
                                empleadoDAO.listarEmpleados();

                        vista.mostrarEmpleados(empleados);
                        break;

                    case 2:
                        String documento =
                                vista.solicitarDocumentoBusqueda();

                        Empleado empleado =
                                empleadoDAO.buscarPorDocumento(documento);

                        if (empleado == null) {
                            mensajes.error(
                                    "No se encontro el empleado.");
                        } else {
                            vista.mostrarEmpleado(empleado);
                        }
                        break;

                    case 0:
                        break;
                }

            } catch (DAOException e) {
                mensajes.error(
                        "No fue posible consultar los empleados.");
            }

        } while (opcion != 0);
    }

    // ACTUALIZAR EMPLEADO******************************************
    public void actualizarEmpleado(Empleado administrador) {

        String documento = vista.solicitarDocumentoBusqueda();

        try {

            Empleado empleado =
                    empleadoDAO.buscarPorDocumento(documento);

            if (empleado == null) {
                mensajes.error("No se encontro el empleado.");
                return;
            }

            vista.mostrarEmpleado(empleado);

            int opcion;

            do {
                opcion = vista.mostrarMenuActualizar();

                if (opcion == 0) {
                    break;
                }

                boolean actualizado;

                if (opcion == 6) {

                    // IMPEDIR CAMBIAR EL PROPIO ROL
                    if (empleado.getId() == administrador.getId()) {
                        mensajes.error(
                                "No puedes modificar tu propio rol.");
                        continue;
                    }

                    Rol nuevoRol = vista.solicitarNuevoRol();

                    actualizado = empleadoDAO.actualizarRol(
                            empleado.getId(), nuevoRol);

                } else {

                    String nuevoValor =
                            vista.solicitarNuevoDato(opcion);

             
                    if (opcion == 2 //opción 2 = cédula 
                            && !nuevoValor.equals(
                                    empleado.getDocumento())
                            && empleadoDAO.existeDocumento(nuevoValor)) {

                        mensajes.error(
                                "Esa cedula ya esta registrada.");
                        continue;
                    }

                    actualizado =
                            empleadoDAO.actualizarDatoPersonal(
                                    empleado.getId(),
                                    opcion,
                                    nuevoValor);
                }

                if (actualizado) {

                    mensajes.exito(
                            "Empleado actualizado correctamente.");

                  
                    empleado = empleadoDAO.buscarPorId(
                            empleado.getId());

                    if (empleado != null) {
                        vista.mostrarEmpleado(empleado);
                    } else {
                        mensajes.error(
                                "No se pudo recuperar el empleado.");
                        break;
                    }

                } else {
                    mensajes.error(
                            "No se pudo actualizar el empleado.");
                }

            } while (opcion != 0);

        } catch (DAOException e) {
            mensajes.error(
                    "No fue posible completar la actualizacion.");
        }
    }

    // DESACTIVAR EMPLEADO*********************************************
    public void desactivarEmpleado(Empleado administrador) {

        String documento = vista.solicitarDocumentoBusqueda();

        try {

            Empleado empleado =
                    empleadoDAO.buscarPorDocumento(documento);

            if (empleado == null) {
                mensajes.error("No se encontro el empleado.");
                return;
            }

            // EVITAR QUE EL ADMINISTRADOR SE DESACTIVE
            if (empleado.getId() == administrador.getId()) {
                mensajes.error(
                        "No puedes desactivar tu propia cuenta.");
                return;
            }

            // COMPROBAR EL ESTADO ACTUAL
            if (empleado.getEstado()
                    == EstadoEmpleado.inactivo) {

                mensajes.informativo(
                        "Este empleado ya esta inactivo.");
                return;
            }

            vista.mostrarEmpleado(empleado);

            // CONFIRMAR ANTES DE DESACTIVAR
            if (!vista.confirmarDesactivacion()) {

                mensajes.informativo(
                        "Desactivacion cancelada.");
                return;
            }

            boolean desactivado =
                    empleadoDAO.desactivarEmpleado(
                            empleado.getId());

            if (desactivado) {

                mensajes.exito(
                        "Empleado desactivado correctamente.");

            } else {

                mensajes.error(
                        "No fue posible desactivar al empleado.");
            }

        } catch (DAOException e) {

            mensajes.error(
                    "No fue posible completar la desactivacion.");
        }
    }

    

}
