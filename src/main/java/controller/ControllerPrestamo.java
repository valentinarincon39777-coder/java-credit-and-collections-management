
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

public class ControllerPrestamo {

    private final PrestamoDAO prestamoDAO;
    private final CuotaDAO cuotaDAO;
    private final VistaPrestamo vista;
    private final Mensajes mensajes;

    public ControllerPrestamo() {

        prestamoDAO = new PrestamoDAO();
        cuotaDAO = new CuotaDAO();
        vista = new VistaPrestamo();
        mensajes = new Mensajes();
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
}
