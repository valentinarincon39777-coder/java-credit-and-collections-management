
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import model.entity.ReporteGuardado;




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
    
    
    
    // EXPORTAR COPIA SERIALIZADA DEL REPORTE GENERAL***************************************
    public void exportarReporte() {

        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar reporte de CrediYa");

        int opcion = selector.showSaveDialog(null);

        if (opcion != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File destino = selector.getSelectedFile();

        if (!destino.getName().toLowerCase().endsWith(".dat")) {
            destino = new File(destino.getAbsolutePath() + ".dat");
        }

        try {

            // 1. OBTENER DATOS ACTUALIZADOS
            prestamoDAO.actualizarVencimientos();

            List<Prestamo> prestamos = prestamoDAO.listarTodos();

            Map<Cliente, Integer> morosos =
                    clienteDAO.listarClientesMorosos();

            // 2. PREPARAR LOS DATOS DEL REPORTE CON STREAM API
            List<String> activos = new ArrayList<>(
                    prestamos.stream()
                            .filter(p -> p.getEstado()
                                    == EstadoPrestamo.activo)
                            .map(p -> "Prestamo #" + p.getId()
                                    + " | Saldo: $" + p.getSaldoPendiente()
                                    + " | Vence: "
                                    + p.getFechaVencimientoFinal())
                            .toList()
            );

            List<String> vencidos = new ArrayList<>(
                    prestamos.stream()
                            .filter(p -> p.getEstado()
                                    == EstadoPrestamo.vencido)
                            .map(p -> "Prestamo #" + p.getId()
                                    + " | Saldo: $" + p.getSaldoPendiente()
                                    + " | Vence: "
                                    + p.getFechaVencimientoFinal())
                            .toList()
            );

            List<String> clientesMorosos = new ArrayList<>(
                    morosos.entrySet().stream()
                            .map(e -> e.getKey().getNombre()
                                    + " | Documento: "
                                    + e.getKey().getDocumento()
                                    + " | Cuotas vencidas: "
                                    + e.getValue())
                            .toList()
            );

            // 3. CREAR EL OBJETO SERIALIZABLE
            ReporteGuardado reporte = new ReporteGuardado(
                    activos, vencidos, clientesMorosos);

            // 4. GUARDAR EL ARCHIVO
            try (ObjectOutputStream salida =
                    new ObjectOutputStream(
                            new FileOutputStream(destino))) {

                salida.writeObject(reporte);
            }

            mensajes.exito("Reporte exportado correctamente.");
            System.out.println("Archivo: " + destino.getAbsolutePath());

        } catch (DAOException | IOException e) {

            mensajes.error("No fue posible exportar el reporte.");
        }
    }

    
    
    
    
    
    
    
    
    
    
}
