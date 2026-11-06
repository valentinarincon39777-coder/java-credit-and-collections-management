/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.panel;

/**
 *
 * @author valen
 */


import java.util.Scanner;
import utils.Validacion;

public class MenuPanelCobros {
    
    private final Validacion validacion;

    public MenuPanelCobros(Scanner entrada) {
        this.validacion = new Validacion(entrada);
    }

    public int mostrarMenu() {

        System.out.println("""
                           
                =================================
                    PANEL GESTOR DE COBROS
                =================================

                1. Consultar clientes
                2. Consultar prestamos
                3. Consultar cuotas
                4. Consultar pagos
                5. Confirmar o rechazar pagos
                6. Registrar pago presencial
                0. Cerrar sesion

                """);

        return validacion.validarEnteroRango(
                "Seleccione una opcion: ", 0, 6
        );
    }
    
    
    
}
