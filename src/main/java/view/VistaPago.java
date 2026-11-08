
package view;

import java.math.BigDecimal;
import java.util.List;
import model.entity.Cliente;
import model.entity.Prestamo;
import model.entity.Cuota;

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
}
