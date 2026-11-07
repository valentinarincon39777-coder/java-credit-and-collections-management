package controller.panels;

import java.util.Scanner;
import model.entity.Cliente;
import view.panel.MenuPanelCliente;
import view.Mensajes;
import controller.ControllerSolicitud;

public class PanelCliente {

    private final MenuPanelCliente menu;
    private final Mensajes mensajes;
    private final ControllerSolicitud controllerSolicitud;

    public PanelCliente(Scanner entrada) {
        menu = new MenuPanelCliente(entrada);
        mensajes = new Mensajes();
        controllerSolicitud = new ControllerSolicitud(entrada);
    }

    public void iniciar(Cliente cliente) {

        int opcion;

        do {
            opcion = menu.mostrarMenu();

            switch (opcion) {

                case 1:
                    controllerSolicitud.registrarComoCliente(cliente);
                    break;

                case 2:
                    controllerSolicitud.consultarComoCliente(cliente);
                    break;

                case 3:
                    mensajes.informativo(
                            "Consultar prestamos y cuotas: funcionalidad en desarrollo."
                    );
                    break;

                case 4:
                    mensajes.informativo(
                            "Consultar pagos: funcionalidad en desarrollo."
                    );
                    break;

                case 5:
                    mensajes.informativo(
                            "Realizar pago: Lo sentimos! Realizar pagos está fura de servicio en tu menú"
                                    + "Acercáte al getsor de cobros para realizar tu pago."
                    );
                    break;

                

                case 6:
                    mensajes.informativo(
                            "Por favor, comuníquese con un asesor para"
                            + " la actualización de sus datos. Gracias"
                    );
                    break;

                case 0:
                    mensajes.informativo("Sesion cerrada correctamente.");
                    break;
            }

        } while (opcion != 0);
    }
}
