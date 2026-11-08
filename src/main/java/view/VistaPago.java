
package view;

import java.math.BigDecimal;
import java.util.List;
import model.entity.Cliente;
import model.entity.Prestamo;
import model.entity.Cuota;
import model.entity.Pago;

public class VistaPago {

    // MOSTRAR EL PRESTAMO Y SUS CUOTAS**********************************
    public void mostrarPrestamo(
            Cliente cliente, Prestamo prestamo,
            List<Cuota> cuotas) {

        System.out.println("\n========== REGISTRAR PAGO ==========");
        System.out.println("Cliente: " + cliente.getNombre());
        System.out.println("Documento: " + cliente.getDocumento());
        System.out.println("Prestamo #" + prestamo.getId());
        System.out.println("Saldo pendiente: $"
                + prestamo.getSaldoPendiente());
        System.out.println("------------------------------------");

        System.out.println("CUOTAS DEL PRESTAMO");

        for (Cuota cuota : cuotas) {
            System.out.println(
                    cuota.getNumeroCuota() + ". "
                    + cuota.getFechaLimite()
                    + " | $" + cuota.getMontoPagar()
                    + " | " + cuota.getEstado());
        }

        System.out.println("------------------------------------");
    }

    // MOSTRAR LA CUOTA QUE OBLIGATORIAMENTE SE DEBE PAGAR***************************
    public void mostrarCuotaAPagar(
            Cuota cuota, BigDecimal totalAbonado) {

        BigDecimal restante =
                cuota.getMontoPagar().subtract(totalAbonado);

        System.out.println("\nCUOTA A PAGAR: #"
                + cuota.getNumeroCuota());

        System.out.println("Monto original: $"
                + cuota.getMontoPagar());

        System.out.println("Total abonado: $"
                + totalAbonado);

        System.out.println("Saldo restante: $"
                + restante);

        System.out.println("------------------------------------");
    }

    // MOSTRAR METODOS DE PAGO************************************
    public void mostrarMetodosPago() {

        System.out.println("\nMETODO DE PAGO");
        System.out.println("1. Efectivo");
        System.out.println("2. Tarjeta");
        System.out.println("3. Yappy");
    }

    // MOSTRAR RESUMEN ANTES DE CONFIRMAR**********************************
    public void mostrarResumen(
            BigDecimal monto, String metodo) {

        System.out.println("\n========== CONFIRMAR PAGO ==========");
        System.out.println("Monto: $" + monto);
        System.out.println("Metodo: " + metodo);
        System.out.println("------------------------------------");
    }
    
    
    
    // MOSTRAR HISTORIAL DE PAGOS**************************
    public void mostrarPagos(List<Pago> pagos) {

        System.out.println("\n========== HISTORIAL DE PAGOS ==========");

        for (Pago pago : pagos) {

            System.out.println("+" + "=".repeat(55) + "+");

            System.out.printf("| %-53s |%n",
                    "PAGO #" + pago.getId());

            System.out.println("+" + "-".repeat(55) + "+");

            System.out.printf("| %-53s |%n",
                    "Cuota ID: " + pago.getCuotaId());

            System.out.printf("| %-53s |%n",
                    "Monto pagado: $" + pago.getMonto());

            System.out.printf("| %-53s |%n",
                    "Metodo: " + pago.getMetodoPago());

            System.out.printf("| %-53s |%n",
                    "Fecha: " + pago.getFechaConfirmacion());

            System.out.printf("| %-53s |%n",
                    "Estado: " + pago.getEstado());

            System.out.printf("| %-53s |%n",
                    "Gestor ID: " + pago.getGestorId());

            System.out.println("+" + "=".repeat(55) + "+");
            System.out.println();
        }
    }

    
    
    
    
}
