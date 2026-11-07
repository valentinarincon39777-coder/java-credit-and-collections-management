
package strategy;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.entity.Cuota;

public class AmortizacionFrancesa
        implements EstrategiaAmortizacion {

    private static final MathContext PRECISION =
            new MathContext(24, RoundingMode.HALF_UP);

    @Override
    public List<Cuota> generarCuotas(
            BigDecimal monto,
            int cantidadCuotas,
            BigDecimal tasaMensual,
            LocalDate fechaDesembolso) {

        // VALIDACIONES BASICAS
        if (monto == null || tasaMensual == null
                || fechaDesembolso == null
                || monto.compareTo(BigDecimal.ZERO) <= 0
                || tasaMensual.compareTo(BigDecimal.ZERO) < 0
                || cantidadCuotas < 1) {

            throw new IllegalArgumentException(
                    "Datos de amortizacion invalidos.");
        }

        monto = monto.setScale(2, RoundingMode.HALF_UP);

        // CALCULAR LA CUOTA MENSUAL FIJA
        BigDecimal cuotaFija;

        if (tasaMensual.compareTo(BigDecimal.ZERO) == 0) {

            cuotaFija = monto.divide(
                    BigDecimal.valueOf(cantidadCuotas),
                    2, RoundingMode.HALF_UP);

        } else {

            BigDecimal factor =
                    BigDecimal.ONE.add(tasaMensual)
                            .pow(cantidadCuotas, PRECISION);

            cuotaFija = monto.multiply(tasaMensual, PRECISION)
                    .multiply(factor, PRECISION)
                    .divide(
                            factor.subtract(BigDecimal.ONE),
                            2, RoundingMode.HALF_UP);
        }

        List<Cuota> cuotas = new ArrayList<>();

        BigDecimal saldoCapital = monto;

        // GENERAR EL CALENDARIO
        for (int i = 1; i <= cantidadCuotas; i++) {

            BigDecimal interes = saldoCapital
                    .multiply(tasaMensual)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal capital;
            BigDecimal montoCuota;

            if (i == cantidadCuotas) {

                // LA ULTIMA CUOTA LIQUIDA TODO EL CAPITAL
                capital = saldoCapital;
                montoCuota = capital.add(interes);

            } else {

                capital = cuotaFija.subtract(interes);
                montoCuota = cuotaFija;

                if (capital.compareTo(BigDecimal.ZERO) <= 0
                        || capital.compareTo(saldoCapital) >= 0) {

                    throw new IllegalArgumentException(
                            "Monto y cuotas incompatibles.");
                }
            }

            if (montoCuota.compareTo(
                    new BigDecimal("99999999.99")) > 0) {

                throw new IllegalArgumentException(
                        "El monto de una cuota supera el limite SQL.");
            }

            LocalDate fechaLimite =
                    fechaDesembolso.plusMonths(i);

            Cuota cuota = new Cuota(
                    0,
                    i,
                    fechaLimite,
                    capital,
                    interes,
                    montoCuota
            );

            cuotas.add(cuota);

            saldoCapital = saldoCapital.subtract(capital);
        }

        return cuotas;
    }
}
