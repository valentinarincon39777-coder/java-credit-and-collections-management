
package view;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

import model.entity.Solicitud;
import utils.Validacion;

public class VistaSolicitud {

    private final Validacion validacion;

    public VistaSolicitud(Scanner entrada) {
        validacion = new Validacion(entrada);
    }

    // SOLICITAR MONTO*****************************************
    public BigDecimal solicitarMonto() {

        BigDecimal monto;

        do {
            monto = validacion.validarMonto(
                    "Ingrese el monto solicitado:");

            if (monto.compareTo(
                    new BigDecimal("9999999999.99")) > 0) {

                System.out.println(
                        "El monto supera el maximo permitido.");
            }

        } while (monto.compareTo(
                new BigDecimal("9999999999.99")) > 0);

        return monto;
    }

    // SOLICITAR CANTIDAD DE CUOTAS************************************
    public int solicitarCantidadCuotas() {

        return validacion.validarEnteroRango(
                "Ingrese la cantidad de cuotas mensuales:",
                1, 360);
    }

    // SOLICITAR CEDULA*************************************************
    public String solicitarDocumento() {

        return validacion.validarDocumento(
                "Ingrese la cedula del cliente:");
    }

    // MOSTRAR UNA SOLICITUD**********************************************+
    public void mostrarSolicitud(Solicitud solicitud) {

        System.out.println("-----------------------------");
        System.out.println("Solicitud ID: " + solicitud.getId());
        System.out.println("Cliente ID: " + solicitud.getClienteId());
        System.out.println("Asesor ID: " + solicitud.getAsesorId());

        System.out.println("Monto: $"
                + solicitud.getMontoSolicitado());

        System.out.println("Cuotas: "
                + solicitud.getCantidadCuotas());

        System.out.println("Estado: "
                + solicitud.getEstado());

        System.out.println("Fecha solicitud: "
                + solicitud.getFechaSolicitud());

        if (solicitud.getFechaEvaluacion() != null) {
            System.out.println("Fecha evaluacion: "
                    + solicitud.getFechaEvaluacion());
        }

        if (solicitud.getMotivoRechazo() != null) {
            System.out.println("Motivo rechazo: "
                    + solicitud.getMotivoRechazo());
        }
    }

    // MOSTRAR LISTA***************************************************+
    public void mostrarSolicitudes(List<Solicitud> solicitudes) {

        if (solicitudes.isEmpty()) {
            System.out.println("No se encontraron solicitudes.");
            return;
        }

        System.out.println("\nSOLICITUDES ENCONTRADAS: "
                + solicitudes.size());

        for (Solicitud solicitud : solicitudes) {
            mostrarSolicitud(solicitud);
        }
    }
}
