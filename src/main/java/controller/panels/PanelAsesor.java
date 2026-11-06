package controller.panels;

import java.util.Scanner;
import model.entity.Empleado;
import view.panel.MenuPanelAsesor;
import view.Mensajes;

import controller.ControllerCliente;

public class PanelAsesor {

    private final MenuPanelAsesor menu;
    private final Mensajes mensajes;
    private final ControllerCliente controllerCliente;

    public PanelAsesor(Scanner entrada) {
        menu = new MenuPanelAsesor(entrada);
        mensajes = new Mensajes();
        controllerCliente = new ControllerCliente(entrada);
    }

    public void iniciar(Empleado empleado) {

        int opcion;

        do {
            opcion = menu.mostrarMenu();

            switch (opcion) {

                case 1:
                    controllerCliente.registrarCliente();
                    break;

                case 2:
                    mensajes.informativo(
                            "Registrar solicitud de prestamo: funcionalidad en desarrollo."
                    );
                    break;

                case 3:
                    mensajes.informativo(
                            "Consultar mis solicitudes: funcionalidad en desarrollo."
                    );
                    break;

                case 0:
                    mensajes.informativo("Sesion cerrada correctamente.");
                    break;
            }

        } while (opcion != 0);
    }
}
