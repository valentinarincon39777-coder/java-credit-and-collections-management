
package controller.panels;

import java.util.Scanner;
import model.entity.Empleado;
import view.panel.MenuPanelCobros;
import view.Mensajes;
import controller.ControllerCliente;

public class PanelCobros {

    private final MenuPanelCobros menu;
    private final Mensajes mensajes;
    private final ControllerCliente controllerCliente;

    public PanelCobros(Scanner entrada) {
        menu = new MenuPanelCobros(entrada);
        mensajes = new Mensajes();
        controllerCliente=new ControllerCliente(entrada);
        
    }

    public void iniciar(Empleado empleado) {

        int opcion;

        do {
            opcion = menu.mostrarMenu();

            switch (opcion) {

                case 1:
                
                           controllerCliente.consultarClientes(); 
                    
                    break;

                case 2:
                    mensajes.informativo(
                            "Consultar prestamos y sus cuotas: funcionalidad en desarrollo."
                    );
                    break;


                case 3:
                    mensajes.informativo(
                            "Consultar pagos: funcionalidad en desarrollo."
                    );
                    break;

                case 4:
                    mensajes.informativo(
                            "Confirmar o rechazar pagos: funcionalidad en desarrollo."
                    );
                    break;

                case 5:
                    mensajes.informativo(
                            "Registrar pago presencial: funcionalidad en desarrollo."
                    );
                    break;

                case 0:
                    mensajes.informativo("Sesion cerrada correctamente.");
                    break;
            }

        } while (opcion != 0);
    }
}
