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
public class MenuPanelCliente {
    
       private final Validacion validacion;

    public MenuPanelCliente(Scanner entrada) {
        this.validacion = new Validacion(entrada);
    }

    public int mostrarMenu() {

        System.out.println("""
                           
                =================================
                       PANEL DEL CLIENTE
                =================================

                1. Solicitar prestamo
                2. Consultar solicitudes
                3. Consultar cuotas
                4. Consultar pagos
                5. Realizar pago
                6. Consultar prestamos
                7. Información para actualizar datos
                0. Cerrar sesion

                """);

        return validacion.validarEnteroRango(
                "Seleccione una opcion: ", 0, 7
        );
    }
    
    
}
