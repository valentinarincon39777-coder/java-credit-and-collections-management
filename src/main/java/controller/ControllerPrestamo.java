
package controller;

import dao.PrestamoDAO;
import dao.CuotaDAO;
import exception.DAOException;

import java.util.List;

import model.entity.Cliente;
import model.entity.Prestamo;
import model.entity.Cuota;

import view.VistaPrestamo;
import view.Mensajes;

import dao.ClienteDAO;
import java.util.Scanner;
import utils.Validacion;


import java.util.Map;

public class ControllerPrestamo {

    private final PrestamoDAO prestamoDAO;
    private final CuotaDAO cuotaDAO;
    private final VistaPrestamo vista;
    private final Mensajes mensajes;
    private final ClienteDAO clienteDAO;
private Validacion validacion;

    public ControllerPrestamo() {

        prestamoDAO = new PrestamoDAO();
        cuotaDAO = new CuotaDAO();
        vista = new VistaPrestamo();
        mensajes = new Mensajes();
        clienteDAO = new ClienteDAO();
    }
    
    //este constructor lo usaran el administrador y gestor de cobros
    public ControllerPrestamo(Scanner entrada) {
    this();
    validacion = new Validacion(entrada);
}

    // CONSULTAR PRESTAMOS Y CUOTAS DEL CLIENTE (sin filtros, le aparece todo lo suyo al cliente)*******************************************************
    public void consultarComoCliente(Cliente cliente) {

        try {

            // ACTUALIZAR ESTADOS VENCIDOS
            prestamoDAO.actualizarVencimientos();

            // OBTENER PRESTAMOS DEL CLIENTE
            List<Prestamo> prestamos =
                    prestamoDAO.listarPorCliente(cliente.getId());

            if (prestamos.isEmpty()) {
                vista.mostrarSinPrestamos();
                return;
            }

            // MOSTRAR CADA PRESTAMO CON SUS CUOTAS
            for (Prestamo prestamo : prestamos) {

                List<Cuota> cuotas =
                        cuotaDAO.listarPorPrestamo(prestamo.getId());

                vista.mostrarPrestamoConCuotas(prestamo, cuotas);
            }

        } catch (DAOException e) {

            mensajes.error(
                    "No fue posible consultar los prestamos.");
        }
    }
    
    
    
    // CONSULTAS PARA ADMINISTRADOR Y GESTOR DE COBROS (con los filtros correspondientes)****************************************************
    public void consultarComoPersonal() {

        int opcion;

        do {
            vista.mostrarMenuConsultas();

            opcion = validacion.validarEnteroRango(
                    "Seleccione una opcion: ", 0, 3);

            if (opcion == 0) {
                break;
            }

            try {

                prestamoDAO.actualizarVencimientos();

                List<Prestamo> prestamos;

                switch (opcion) {

                    case 1: //consulta todos los prestamos y sus cuotas
                        prestamos = prestamoDAO.listarTodos();
                        break;

                    case 2: //muestra prestamos de un cliente, filtrados
                        String documento = validacion.validarDocumento(
                                "Ingrese la cedula del cliente:");

                        Cliente cliente =
                                clienteDAO.buscarPorDocumento(documento);

                        if (cliente == null) {
                            mensajes.error("Cliente no encontrado.");
                            continue;
                        }

                        prestamos = prestamoDAO.listarPorCliente(
                                cliente.getId());
                        break;

                    case 3:
                        prestamos = prestamoDAO.listarVencidos();
                        break;

                    default:
                        continue;
                }
                
                
                
                PreMostrarPrestamosConCuotas(prestamos);

            } catch (DAOException e) {
                mensajes.error(
                        "No fue posible consultar los prestamos.");
            }

        } while (opcion != 0);
    }

    // MOSTRAR CADA PRESTAMO CON SUS CUOTAS************************************+
    private void PreMostrarPrestamosConCuotas(
            List<Prestamo> prestamos) throws DAOException {

        if (prestamos.isEmpty()) {
            mensajes.informativo(
                    "No se encontraron prestamos.");
            return;
        }

        for (Prestamo prestamo : prestamos) {

            List<Cuota> cuotas =
                    cuotaDAO.listarPorPrestamo(prestamo.getId());

            vista.mostrarPrestamoConCuotas(prestamo, cuotas);
        }
    }
    
    
    
    // CONSULTAR CARTERA GENERAL DEL ADMINISTRADOR****************************
    public void consultarCartera() {

        try {

            // ACTUALIZAR LOS ESTADOS ANTES DE CONSULTAR
            prestamoDAO.actualizarVencimientos();

            // OBTENER LOS DATOS FINANCIEROS DE LA CARTERA
            Map<String, Number> cartera =
                    prestamoDAO.consultarCartera();

            // ENVIAR LOS DATOS A LA VISTA
            vista.mostrarCartera(cartera);

        } catch (DAOException e) {

            mensajes.error(
                    "No fue posible consultar la cartera.");
        }
    }


    
    
    
    
    
    
}
