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

public class MenuPanelAdmin {
    
    
    
    private final Validacion validacion;

    public MenuPanelAdmin(Scanner entrada) {
        this.validacion = new Validacion(entrada);
    }

    public int mostrarMenu() {

        System.out.println("""
                           
                =================================
                    PANEL ADMINISTRADOR
                =================================

                1. Gestionar empleados
                2. Consultar clientes
                3. Consultar perfil financiero
                4. Consultar solicitudes
                5. Consultar prestamos
                6. Consultar pagos
                7. Consultar cartera
                8. Generar reportes
                9. Exportaciones y backups
                0. Cerrar sesion

                """);

        return validacion.validarEnteroRango(
                "Seleccione una opcion: ", 0, 9
        );
    }
    
    
    
    
    
    
}
