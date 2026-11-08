
package controller;

import dao.PrestamoDAO;
import dao.ClienteDAO;
import exception.DAOException;

import java.util.List;
import java.util.Map;

import model.entity.Prestamo;
import model.entity.Cliente;
import model.enums.EstadoPrestamo;

import view.VistaReporte;
import view.Mensajes;

public class ControllerReporte {

    private final PrestamoDAO prestamoDAO;
    private final ClienteDAO clienteDAO;
    private final VistaReporte vista;
    private final Mensajes mensajes;

    public ControllerReporte() {
        prestamoDAO = new PrestamoDAO();
        clienteDAO = new ClienteDAO();
        vista = new VistaReporte();
        mensajes = new Mensajes();
    }

    // GENERAR REPORTE GENERAL DE CREDITOS usando expresiones Lambda y StreamAPI
    public void generarReporteGeneral() {

        try {

            // 1. ACTUALIZAR LOS VENCIMIENTOS
            prestamoDAO.actualizarVencimientos();

            // 2. CONSULTAR TODOS LOS PRESTAMOS
            List<Prestamo> prestamos =
                    prestamoDAO.listarTodos();

            // 3. FILTRAR PRESTAMOS ACTIVOS 
            List<Prestamo> activos = prestamos.stream()
                    .filter(p -> p.getEstado() == EstadoPrestamo.activo)
                    .toList();

            // 4. FILTRAR PRESTAMOS VENCIDOS CON STREAM API
            List<Prestamo> vencidos = prestamos.stream()
                    .filter(p -> p.getEstado() == EstadoPrestamo.vencido)
                    .toList();

            // 5. CONSULTAR CLIENTES MOROSOS
            Map<Cliente, Integer> morosos =
                    clienteDAO.listarClientesMorosos();

            // 6. ENVIAR LOS RESULTADOS A LA VISTA
            vista.mostrarReporte(activos, vencidos, morosos);

        } catch (DAOException e) {

            mensajes.error(
                    "No fue posible generar el reporte.");
        }
    }
}
