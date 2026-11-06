/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author valen
 */
import controller.panels.PanelAdmin;
import controller.panels.PanelAnalista;
import controller.panels.PanelAsesor;
import controller.panels.PanelCobros;
import controller.panels.PanelCliente;

import dao.ClienteDAO;
import dao.EmpleadoDAO;

import exception.DAOException;

import java.util.Scanner;

import model.entity.Cliente;
import model.entity.Empleado;

import model.enums.EstadoEmpleado;
import model.enums.Rol;

import view.MenuGeneral;

public class ControllerGeneral {

    private final Scanner entrada;
    private final MenuGeneral menu;
    private final ClienteDAO clienteDAO;
    private final EmpleadoDAO empleadoDAO;

    public ControllerGeneral() {
        //solo instanciamos un scanner
        entrada = new Scanner(System.in);
        menu = new MenuGeneral(entrada);
        clienteDAO = new ClienteDAO();
        empleadoDAO = new EmpleadoDAO();
    }

    // MENÚ PRINCIPAL DEL SISTEMA********************************************************
    public void iniciar() {

        int opcion;

        do {
            opcion = menu.mostrarMenuPrincipal();

            switch (opcion) {
                case 1:
                    iniciarSesionCliente();
                    break;

                case 2:
                    iniciarSesionEmpleado();
                    break;

                case 0:
                    menu.mostrarDespedida();
                    break;
            }

        } while (opcion != 0);
    }

    // INICIO DE SESIÓN DEL CLIENTE*****************************************
    private void iniciarSesionCliente() {

        String documento = menu.solicitarDocumento();

        try {
            Cliente cliente = clienteDAO.buscarPorDocumento(documento);

            if (cliente == null) {
                menu.mostrarError(
                        "No se encontró la cédula. Acércate a un asesor para registrarte."
                );
                return;
            }

            menu.mostrarMensaje(
                    "¡Bienvenido/a, " + cliente.getNombre() + "!"
            );

            PanelCliente panel = new PanelCliente(entrada);
            panel.iniciar(cliente);

        } catch (DAOException e) {
            menu.mostrarError(
                    "No fue posible consultar la base de datos."
            );
           
        }
    }

    // INICIO DE SESIÓN DEL EMPLEADO**********************************************+
    private void iniciarSesionEmpleado() {

        Rol rolSeleccionado = menu.seleccionarRolEmpleado();

        if (rolSeleccionado == null) {
            return;
        }

        String documento = menu.solicitarDocumento();

        try {
            Empleado empleado = empleadoDAO.buscarPorDocumento(documento);

            if (empleado == null) {
                menu.mostrarError(
                        "No se encontró un empleado con esa cédula."
                );
                return;
            }

            if (empleado.getEstado() != EstadoEmpleado.activo) {
                menu.mostrarError(
                        "Tu cuenta está inactiva. No puedes ingresar."
                );
                return;
            }

            if (empleado.getRol() != rolSeleccionado) {
                menu.mostrarError(
                        "El rol seleccionado no corresponde a tu cuenta."
                );
                return;
            }

            menu.mostrarMensaje(
                    "¡Bienvenido/a, " + empleado.getNombre() + "!"
            );

            abrirPanelEmpleado(empleado);

        } catch (DAOException e) {
            menu.mostrarError(
                    "No fue posible consultar la base de datos."
            );
           
        }
    }

    // REDIRIGIR AL PANEL DEL EMPLEADO*************************************************************
    private void abrirPanelEmpleado(Empleado empleado) {

        switch (empleado.getRol()) {

            case administrador_general:
                PanelAdmin admin = new PanelAdmin(entrada);
                admin.iniciar(empleado);
                break;

            case asesor_creditos:
                PanelAsesor asesor = new PanelAsesor(entrada);
                asesor.iniciar(empleado);
                break;

            case analista_creditos:
                PanelAnalista analista = new PanelAnalista(entrada);
                analista.iniciar(empleado);
                break;

            case gestor_cobros:
                PanelCobros gestor = new PanelCobros(entrada);
                gestor.iniciar(empleado);
                break;
        }
    }

}
