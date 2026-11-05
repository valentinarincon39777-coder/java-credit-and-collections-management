/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.panels;

/**
 *
 * @author valen
 */

import model.entity.Cliente;

public class PanelCliente {
    
        public void iniciar(Cliente cliente) {

        System.out.println("=== PANEL DEL CLIENTE ===");
        System.out.println("Cliente: " + cliente.getNombre());
        System.out.println("ID: " + cliente.getId());
        System.out.println("Panel en construcción.");
    }
    
}
