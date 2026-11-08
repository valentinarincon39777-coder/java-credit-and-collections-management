
package view;

import java.util.List;
import java.util.Map;
import model.entity.Prestamo;
import model.entity.Cuota;

public class VistaPrestamo {

    
    // MOSTRAR UN PRESTAMO CON TODAS SUS CUOTAS*******************************++++
    public void mostrarPrestamoConCuotas(
            Prestamo prestamo, List<Cuota> cuotas) {

        System.out.println();
        System.out.println("+" + "=".repeat(66) + "+");

        imprimirLinea("PRESTAMO #" + prestamo.getId());

        System.out.println("+" + "=".repeat(66) + "+");

        imprimirLinea("Total a pagar: $" + prestamo.getTotalPagar());
        imprimirLinea("Saldo pendiente: $" + prestamo.getSaldoPendiente());
        imprimirLinea("Estado: " + prestamo.getEstado());

        System.out.println("+" + "-".repeat(66) + "+");

        imprimirLinea("CUOTAS");

        System.out.println("+" + "-".repeat(66) + "+");

        if (cuotas.isEmpty()) {

            imprimirLinea("No hay cuotas registradas.");

        } else {

            for (Cuota cuota : cuotas) {

                String detalle =
                        cuota.getNumeroCuota() + ". "
                        + cuota.getFechaLimite()
                        + " | $" + cuota.getMontoPagar()
                        + " | " + cuota.getEstado();

                imprimirLinea(detalle);
            }
        }

        System.out.println("+" + "=".repeat(66) + "+");
        System.out.println();
    }

    // IMPRIMIR TEXTO DENTRO DEL RECUADRO**********************
    private void imprimirLinea(String texto) {

        System.out.printf("| %-64s |%n", texto);
    }


    // MOSTRAR MENSAJE CUANDO NO EXISTEN PRESTAMOS*************************
    public void mostrarSinPrestamos() {

        System.out.println(
                "\nNo tienes prestamos registrados.");
    }
    
    
    
    // MOSTRAR EL SUB MENU DE LAS CONSULTAS DE PRESTAMOS Y CUOTAS para el panel de admin y getsor de cobros**********************************
    public void mostrarMenuConsultas() {
    System.out.println("\n====== CONSULTAR PRESTAMOS Y CUOTAS ======");
    System.out.println("1. Ver todos los prestamos y cuotas");
    System.out.println("2. Buscar prestamos y cuotas por cliente");
    System.out.println("3. Ver prestamos vencidos");
    System.out.println("0. Volver");
}
    
    
    // MOSTRAR RESUMEN FINANCIERO DE LA CARTERA******************************************
    public void mostrarCartera(Map<String, Number> cartera) {

        System.out.println();
        System.out.println("+" + "=".repeat(66) + "+");

        imprimirLinea("CARTERA DE CREDITOS - CREDIYA");

        System.out.println("+" + "=".repeat(66) + "+");

        imprimirLinea("Total de prestamos otorgados: "
                + cartera.get("totalPrestamos"));

        imprimirLinea("Total a cobrar: $"
                + cartera.get("totalCobrar"));

        imprimirLinea("Total recaudado: $"
                + cartera.get("totalRecaudado"));

        imprimirLinea("Saldo total pendiente: $"
                + cartera.get("saldoPendiente"));

        System.out.println("+" + "-".repeat(66) + "+");

        imprimirLinea("ESTADO DE LOS PRESTAMOS");

        System.out.println("+" + "-".repeat(66) + "+");

        imprimirLinea("Prestamos activos: "
                + cartera.get("activos"));

        imprimirLinea("Prestamos vencidos: "
                + cartera.get("vencidos"));

        imprimirLinea("Prestamos pagados: "
                + cartera.get("pagados"));

        System.out.println("+" + "=".repeat(66) + "+");
        System.out.println();
    }

    
    
    
    
    
}
