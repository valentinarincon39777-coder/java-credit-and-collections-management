package controller.panels;

import java.util.Scanner;
import model.entity.Empleado;
import view.panel.MenuPanelCobros;
import view.Mensajes;
import controller.ControllerCliente;

import controller.ControllerPrestamo;

import controller.ControllerPago;

public class PanelCobros {

    private final MenuPanelCobros menu;
    private final Mensajes mensajes;
    private final ControllerCliente controllerCliente;
    private final ControllerPrestamo controllerPrestamo;
    private final ControllerPago controllerPago;

    public PanelCobros(Scanner entrada) {
        menu = new MenuPanelCobros(entrada);
        mensajes = new Mensajes();
        controllerCliente = new ControllerCliente(entrada);
        controllerPrestamo = new ControllerPrestamo(entrada);
        controllerPago = new ControllerPago(entrada);

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
                    controllerPrestamo.consultarComoPersonal();
                    break;

                case 3:
                    controllerPago.consultarComoGestor(empleado);
                    break;

                case 4:
                    mensajes.informativo(
                            "Función deshabilitada temporalmente"
                    );
                    break;

                case 5:
                    controllerPago.registrarPago(empleado);
                    break;

                case 0:
                    mensajes.informativo("Sesion cerrada correctamente.");
                    break;
            }

        } while (opcion != 0);
    }
}
