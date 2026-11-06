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

public class MenuPanelAnalista {
    
     private final Validacion validacion;

    public MenuPanelAnalista(Scanner entrada) {
        this.validacion = new Validacion(entrada);
    }

    public int mostrarMenu() {

        System.out.println("""
                           
                =================================
                   PANEL ANALISTA DE CREDITOS
                =================================

                1. Consultar solicitudes
                2. Evaluar solicitudes pendientes
                0. Cerrar sesion

                """);

        return validacion.validarEnteroRango(
                "Seleccione una opcion: ", 0, 2
        );
    }
    
}
