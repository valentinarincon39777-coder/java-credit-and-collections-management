package controller;

import dao.ClienteDAO;
import dao.PrestamoDAO;
import dao.CuotaDAO;
import dao.PagoDAO;

import exception.DAOException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import static java.util.stream.Collectors.toList;

import model.entity.Cliente;
import model.entity.Empleado;
import model.entity.Prestamo;
import model.entity.Cuota;
import model.entity.Pago;

import model.enums.EstadoPrestamo;
import model.enums.EstadoCuota;
import model.enums.MetodoPago;

import utils.Validacion;
import view.VistaPago;
import view.Mensajes;

public class ControllerPago {

    private final ClienteDAO clienteDAO;
    private final PrestamoDAO prestamoDAO;
    private final CuotaDAO cuotaDAO;
    private final PagoDAO pagoDAO;

    private final Validacion validacion;
    private final VistaPago vista;
    private final Mensajes mensajes;

    public ControllerPago(Scanner entrada) {

        clienteDAO = new ClienteDAO();
        prestamoDAO = new PrestamoDAO();
        cuotaDAO = new CuotaDAO();
        pagoDAO = new PagoDAO();

        validacion = new Validacion(entrada);
        vista = new VistaPago();
        mensajes = new Mensajes();
    }

    // REGISTRAR PAGO DESDE EL PANEL DEL GESTOR*******************************************
    public void registrarPago(Empleado gestor) {

        try {

            // 1. BUSCAR CLIENTE POR DOCUMENTO
            String documento = validacion.validarDocumento(
                    "Ingrese la cedula del cliente:");

            Cliente cliente
                    = clienteDAO.buscarPorDocumento(documento);

            if (cliente == null) {
                mensajes.error("Cliente no encontrado.");
                return;
            }

            // 2. BUSCAR PRESTAMO ACTIVO O VENCIDO
            prestamoDAO.actualizarVencimientos();

            List<Prestamo> prestamos
                    = prestamoDAO.listarPorCliente(cliente.getId());

            Prestamo prestamo = null;

            for (Prestamo p : prestamos) {

                if (p.getEstado() == EstadoPrestamo.activo
                        || p.getEstado() == EstadoPrestamo.vencido) {

                    prestamo = p;
                    break;
                }
            }
            //no identifica ningun prestamo con estado activo o vencido, se mantiene en null
            if (prestamo == null) {
                mensajes.informativo(
                        "El cliente no tiene prestamos por pagar.");
                return;
            }

            // 3. CONSULTAR Y MOSTRAR LAS CUOTAS
            List<Cuota> cuotas
                    = cuotaDAO.listarPorPrestamo(prestamo.getId());

            vista.mostrarPrestamo(cliente, prestamo, cuotas);

            // 4. IDENTIFICAR LA CUOTA MAS ANTIGUA SIN PAGAR
            Cuota cuotaAPagar = null;

            //lista de las cuotas del prestamo vencido o activo
            for (Cuota cuota : cuotas) {

                if (cuota.getEstado() != EstadoCuota.pagada) {

                    if (cuotaAPagar == null
                            || cuota.getFechaLimite().isBefore(
                                    cuotaAPagar.getFechaLimite())) {

                        cuotaAPagar = cuota;
                    }
                }
            }

            if (cuotaAPagar == null) {
                mensajes.informativo(
                        "No hay cuotas pendientes de pago.");
                return;
            }

            // 5. CALCULAR ABONOS ANTERIORES
            BigDecimal totalAbonado
                    = pagoDAO.calcularTotalAbonado(
                            cuotaAPagar.getId());

            //se retsa del monto total de la cuota, lo abonado, para saber uqe face falta pagar
            BigDecimal restante
                    = cuotaAPagar.getMontoPagar()
                            .subtract(totalAbonado);

            vista.mostrarCuotaAPagar(
                    cuotaAPagar, totalAbonado);

            // 6. SOLICITAR MONTO
            BigDecimal monto;

            do {

                monto = validacion.validarMonto(
                        "Ingrese el monto a pagar:");

                if (monto.compareTo(restante) > 0) {
                    mensajes.error(
                            "El monto supera el saldo restante de la cuota.");
                }

            } while (monto.compareTo(restante) > 0);

            // 7. SELECCIONAR METODO DE PAGO
            vista.mostrarMetodosPago();

            int opcionMetodo = validacion.validarEnteroRango(
                    "Seleccione el metodo de pago:", 1, 3);

            MetodoPago metodo;

            switch (opcionMetodo) {
                case 1:
                    metodo = MetodoPago.efectivo;
                    break;
                case 2:
                    metodo = MetodoPago.tarjeta;
                    break;
                default:
                    metodo = MetodoPago.yappy;
                    break;
            }

            // 8. CONFIRMAR OPERACION
            vista.mostrarResumen(monto, metodo.name());

            char confirmar = validacion.validarLetra(
                    "Desea confirmar el pago? (S/N):", "SN");

            if (confirmar == 'N') {
                mensajes.informativo("Pago cancelado.");
                return;
            }

            // 9. REGISTRAR EL PAGO CONFIRMADO
            Pago pago = new Pago(
                    cuotaAPagar.getId(),
                    monto,
                    metodo,
                    gestor.getId()
            );

            pagoDAO.registrarPago(pago);

            mensajes.exito(
                    "Pago registrado y confirmado correctamente.");

        } catch (DAOException e) {

            mensajes.error(
                    "No fue posible completar el registro del pago.");

        } catch (IllegalArgumentException e) {

            mensajes.error(e.getMessage());
        }
    }

    // CONSULTAR PAGOS DEL CLIENTE*****************************************************
    public void consultarComoCliente(Cliente cliente) {

        try {
            mostrarHistorial(
                    pagoDAO.listarPorCliente(cliente.getId()));

        } catch (DAOException e) {
            mensajes.error("No fue posible consultar los pagos.");
        }
    }

    // CONSULTAR PAGOS REGISTRADOS POR EL GESTOR****************************************
    public void consultarComoGestor(Empleado gestor) {

        try {
            mostrarHistorial(
                    pagoDAO.listarPorGestor(gestor.getId()));

        } catch (DAOException e) {
            mensajes.error("No fue posible consultar los pagos.");
        }
    }

    // CONSULTAR TODOS LOS PAGOS: ADMINISTRADOR**********************************
    public void consultarTodos() {

        try {
            mostrarHistorial(pagoDAO.listarTodos());

        } catch (DAOException e) {
            mensajes.error("No fue posible consultar los pagos.");
        }
    }

    // METODO AUXILIAR PARA MOSTRAR LOS RESULTADOS de los 3 métodos de arriba **********************************************
    private void mostrarHistorial(List<Pago> pagos) {

        if (pagos.isEmpty()) {
            mensajes.informativo(
                    "No se encontraron pagos registrados.");
            return;
        }

        vista.mostrarPagos(pagos);
    }

    //METODO ESPECIAL PARA EL EXAMEN  de consultar pagos con stream API ***************************************
    public void consultarUltimoMesComoGestor(Empleado gestor) {

        try {

            List<Pago> pagos = pagoDAO.listarPorGestor(gestor.getId());

            List<Pago> pagosOrdenados = pagos.stream()
           .toList();
            
            mostrarHistorial(pagosOrdenados);

        } catch (DAOException e) {

            mensajes.error("NO FUE POSIBLE CONSULTAR LOS PAGOS ");
        }

    }

}
