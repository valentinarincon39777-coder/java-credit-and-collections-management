package controller.panels;

import java.util.Scanner;
import model.entity.Empleado;
import view.panel.MenuPanelAnalista;
import view.Mensajes;
import controller.ControllerSolicitud;

public class PanelAnalista {

    private final MenuPanelAnalista menu;
    private final Mensajes mensajes;
    private final ControllerSolicitud controllerSolicitud;

    public PanelAnalista(Scanner entrada) {
        menu = new MenuPanelAnalista(entrada);
        mensajes = new Mensajes();
        controllerSolicitud = new ControllerSolicitud(entrada);
    }

    public void iniciar(Empleado empleado) {

        int opcion;

        do {
            opcion = menu.mostrarMenu();

            switch (opcion) {

                case 1:
                    controllerSolicitud.consultarTodas();
                    break;

                case 2:
                    mensajes.informativo(
                            "Evaluar solicitudes pendientes: funcionalidad en desarrollo."
                    );
                    break;

                case 0:
                    mensajes.informativo("Sesion cerrada correctamente.");
                    break;
            }

        } while (opcion != 0);
    }
}
