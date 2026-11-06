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

public class MenuPanelAsesor {
    
    
    
    private final Validacion validacion;

    public MenuPanelAsesor(Scanner entrada) {
        this.validacion = new Validacion(entrada);
    }

    public int mostrarMenu() {

        System.out.println("""
                           
                =================================
                   PANEL ASESOR DE CREDITOS
                =================================

                1. Gestionar clientes
                2. Registrar solicitud de prestamo
                3. Consultar mis solicitudes
                0. Cerrar sesion

                """);

        return validacion.validarEnteroRango(
                "Seleccione una opcion: ", 0, 3
        );
    }
    
    
    
    
}
