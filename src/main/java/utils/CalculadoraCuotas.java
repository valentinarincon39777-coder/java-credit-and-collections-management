
package utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import model.entity.Cuota;
import strategy.EstrategiaAmortizacion;




public class CalculadoraCuotas {
    
    
    //se establece la tasa mensuaal de 2%
    private static final BigDecimal TASA_MENSUAL =
            new BigDecimal("0.02");

    private final EstrategiaAmortizacion estrategia;

    public CalculadoraCuotas(EstrategiaAmortizacion estrategia) {
        this.estrategia = estrategia;
    }

    // GENERAR CALENDARIO DE CUOTAS********************************
    public List<Cuota> generarCalendario(
            BigDecimal monto,
            int cantidadCuotas,
            LocalDate fechaDesembolso) {

        return estrategia.generarCuotas(
                monto,
                cantidadCuotas,
                TASA_MENSUAL,
                fechaDesembolso
        );
    }

    // CALCULAR EL TOTAL A PAGAR****************************************
    public BigDecimal calcularTotalPagar(List<Cuota> cuotas) {
        
        

        BigDecimal total = BigDecimal.ZERO;

        for (Cuota cuota : cuotas) {
            total = total.add(cuota.getMontoPagar());
        }

        return total;
    }

    // OBTENER TASA MENSUAL EXPRESADA EN PORCENTAJE**********************************
    public BigDecimal getTasaMensualPorcentaje() {
        return TASA_MENSUAL.movePointRight(2);
    }
}
