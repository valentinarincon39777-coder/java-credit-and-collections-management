
package view;

import java.util.List;
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

    // IMPRIMIR TEXTO DENTRO DEL RECUADRO
    private void imprimirLinea(String texto) {

        System.out.printf("| %-64s |%n", texto);
    }


    // MOSTRAR MENSAJE CUANDO NO EXISTEN PRESTAMOS*************************
    public void mostrarSinPrestamos() {

        System.out.println(
                "\nNo tienes prestamos registrados.");
    }
}
