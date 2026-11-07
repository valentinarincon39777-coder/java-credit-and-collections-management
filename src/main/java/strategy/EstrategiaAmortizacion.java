/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package strategy;

/**
 *
 * @author valen
 */




import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import model.entity.Cuota;


public interface EstrategiaAmortizacion {
    
    
    List<Cuota> generarCuotas(
    
            BigDecimal monto, 
            int cantidadCuotas,
            BigDecimal tasaMenusual,
            LocalDate fechaDesembolso
    
    );
    
}
