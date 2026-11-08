package controller.panels;

import java.util.Scanner;
import model.entity.Empleado;
import view.panel.MenuPanelAdmin;
import view.Mensajes;
import controller.ControllerEmpleado;
import controller.ControllerCliente;
import controller.ControllerSolicitud;
import controller.ControllerPrestamo;
import controller.ControllerPago;

public class PanelAdmin {

    private final MenuPanelAdmin menu;
    private final Mensajes mensajes;
    private final ControllerEmpleado controllerEmpleado;
    private final ControllerCliente controllerCliente;
    private final ControllerSolicitud controllerSolicitud;
    private final ControllerPrestamo controllerPrestamo;
    private final ControllerPago controllerPago;

    public PanelAdmin(Scanner entrada) {
        menu = new MenuPanelAdmin(entrada);
        mensajes = new Mensajes();
        controllerEmpleado = new ControllerEmpleado(entrada);
        controllerCliente = new ControllerCliente(entrada);
        controllerSolicitud = new ControllerSolicitud(entrada);
        controllerPrestamo = new ControllerPrestamo(entrada);
        controllerPago = new ControllerPago(entrada);
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
                    controllerPrestamo.consultarComoPersonal();
                    break;

                case 6:
                    controllerPago.consultarTodos();
                    break;

                case 7:
                    controllerPrestamo.consultarCartera();
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
