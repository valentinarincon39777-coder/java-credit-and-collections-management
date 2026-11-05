/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.panels;

/**
 *
 * @author valen
 */

import model.entity.Empleado;
public class PanelAnalista {
      public void iniciar(Empleado empleado) {

        System.out.println("=== PANEL ANALISTA ===");
        System.out.println("Empleado: " + empleado.getNombre());
        System.out.println("Panel en construcción.");
    }
    
}
