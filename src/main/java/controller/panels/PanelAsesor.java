package controller.panels;

import java.util.Scanner;
import model.entity.Empleado;
import view.panel.MenuPanelAsesor;
import view.Mensajes;

import controller.ControllerCliente;
import controller.ControllerSolicitud;

public class PanelAsesor {

    private final MenuPanelAsesor menu;
    private final Mensajes mensajes;
    private final ControllerCliente controllerCliente;
    private final ControllerSolicitud controllerSolicitud;

    public PanelAsesor(Scanner entrada) {
        menu = new MenuPanelAsesor(entrada);
        mensajes = new Mensajes();
        controllerCliente = new ControllerCliente(entrada);
        controllerSolicitud = new ControllerSolicitud(entrada);
    }

    public void iniciar(Empleado empleado) {

        int opcion;

        do {
            opcion = menu.mostrarMenu();

            switch (opcion) {
                case 1:
                    controllerCliente.gestionarClientes();
                    break;

                case 2:
                    controllerSolicitud.registrarComoAsesor(empleado);
                    break;

                case 3:
                    controllerSolicitud.consultarComoAsesor(empleado);
                    break;

                case 0:
                    mensajes.informativo("Sesion cerrada correctamente.");
                    break;
            }

        } while (opcion != 0);
    }
}
