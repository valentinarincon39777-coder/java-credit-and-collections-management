
package view;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import model.entity.Cliente;
import model.entity.Prestamo;

public class VistaReporte {

    // MOSTRAR REPORTE GENERAL DE CREDITOS
    public void mostrarReporte(
            List<Prestamo> activos,
            List<Prestamo> vencidos,
            Map<Cliente, Integer> morosos) {

        System.out.println();
        System.out.println("+" + "=".repeat(75) + "+");
        imprimirLinea("REPORTE GENERAL DE CREDITOS - CREDIYA");
        imprimirLinea("Fecha: " + LocalDate.now());
        System.out.println("+" + "=".repeat(75) + "+");

        // PRIMERA SECCION
        imprimirLinea("1. PRESTAMOS ACTIVOS");
        System.out.println("+" + "-".repeat(75) + "+");

        mostrarPrestamos(activos);

        imprimirLinea("Total de prestamos activos: " + activos.size());
        System.out.println("+" + "=".repeat(75) + "+");

        // SEGUNDA SECCION
        imprimirLinea("2. PRESTAMOS VENCIDOS");
        System.out.println("+" + "-".repeat(75) + "+");

        mostrarPrestamos(vencidos);

        imprimirLinea("Total de prestamos vencidos: " + vencidos.size());
        System.out.println("+" + "=".repeat(75) + "+");

        // TERCERA SECCION
        imprimirLinea("3. CLIENTES MOROSOS");
        System.out.println("+" + "-".repeat(75) + "+");

        if (morosos.isEmpty()) {
            imprimirLinea("No hay clientes morosos.");
        } else {
            //expresion lambda
            morosos.forEach((cliente, cantidad) -> {
                imprimirLinea(
                        cliente.getNombre()
                        + " | Documento: " + cliente.getDocumento()
                        + " | Cuotas vencidas: " + cantidad);
            });
        }

        imprimirLinea("Total de clientes morosos: " + morosos.size());
        System.out.println("+" + "=".repeat(75) + "+");
        System.out.println();
    }

    // MOSTRAR LOS PRESTAMOS DE UNA SECCION
    private void mostrarPrestamos(List<Prestamo> prestamos) {

        if (prestamos.isEmpty()) {
            imprimirLinea("No hay prestamos en esta categoria.");
            return;
        }

        prestamos.forEach(prestamo -> {
            imprimirLinea(
                    "Prestamo #" + prestamo.getId()
                    + " | Saldo: $" + prestamo.getSaldoPendiente()
                    + " | Vence: " + prestamo.getFechaVencimientoFinal());
        });
    }

    // IMPRIMIR LINEAS CON BORDES ASCII
    private void imprimirLinea(String texto) {
        System.out.printf("| %-73s |%n", texto);
    }
}
