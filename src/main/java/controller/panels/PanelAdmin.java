package controller.panels;

import java.util.Scanner;
import model.entity.Empleado;
import view.panel.MenuPanelAdmin;
import view.Mensajes;
import controller.ControllerEmpleado;
import controller.ControllerCliente;
import controller.ControllerSolicitud;

public class PanelAdmin {

    private final MenuPanelAdmin menu;
    private final Mensajes mensajes;
    private final ControllerEmpleado controllerEmpleado;
    private final ControllerCliente controllerCliente;
    private final ControllerSolicitud controllerSolicitud;

    public PanelAdmin(Scanner entrada) {
        menu = new MenuPanelAdmin(entrada);
        mensajes = new Mensajes();
        controllerEmpleado = new ControllerEmpleado(entrada);
        controllerCliente = new ControllerCliente(entrada);
        controllerSolicitud = new ControllerSolicitud(entrada);
    }

    public void iniciar(Empleado empleado) {

        int opcion;

        do {
            opcion = menu.mostrarMenu();

            switch (opcion) {

                case 1:
                    controllerEmpleado.gestionarEmpleados(empleado);
                    break;

                case 2:
                    controllerCliente.consultarClientes();

                    break;

                case 3:
                    mensajes.informativo(
                            "Consultar perfil financiero: funcionalidad inactiva por el momento"
                    );
                    break;

                case 4:
                    controllerSolicitud.consultarTodas();
                    break;

                case 5:
                    mensajes.informativo(
                            "Consultar prestamos: funcionalidad en desarrollo."
                    );
                    break;

                case 6:
                    mensajes.informativo(
                            "Consultar pagos: funcionalidad en desarrollo."
                    );
                    break;

                case 7:
                    mensajes.informativo(
                            "Consultar cartera: funcionalidad en desarrollo."
                    );
                    break;

                case 8:
                    mensajes.informativo(
                            "Generar reportes: funcionalidad en desarrollo."
                    );
                    break;

                case 9:
                    mensajes.informativo(
                            "Exportaciones y backups: funcionalidad en desarrollo."
                    );
                    break;

                case 0:
                    mensajes.informativo("Sesion cerrada correctamente.");
                    break;
            }

        } while (opcion != 0);
    }
}
