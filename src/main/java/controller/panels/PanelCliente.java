
package controller.panels;

import java.util.Scanner;
import model.entity.Cliente;
import view.panel.MenuPanelCliente;
import view.Mensajes;



public class PanelCliente {

    private final MenuPanelCliente menu;
    private final Mensajes mensajes;

    public PanelCliente(Scanner entrada) {
        menu = new MenuPanelCliente(entrada);
        mensajes = new Mensajes();
    }

    public void iniciar(Cliente cliente) {

        int opcion;

        do {
            opcion = menu.mostrarMenu();

            switch (opcion) {

                case 1:
                    mensajes.informativo(
                            "Solicitar prestamo: funcionalidad en desarrollo."
                    );
                    break;

                case 2:
                    mensajes.informativo(
                            "Consultar solicitudes: funcionalidad en desarrollo."
                    );
                    break;

                case 3:
                    mensajes.informativo(
                            "Consultar cuotas: funcionalidad en desarrollo."
                    );
                    break;

                case 4:
                    mensajes.informativo(
                            "Consultar pagos: funcionalidad en desarrollo."
                    );
                    break;

                case 5:
                    mensajes.informativo(
                            "Realizar pago: funcionalidad en desarrollo."
                    );
                    break;

                case 6:
                    mensajes.informativo(
                            "Consultar prestamos: funcionalidad en desarrollo."
                    );
                    break;

                case 7:
                    mensajes.informativo(
                            "Actualizar mis datos: funcionalidad en desarrollo."
                    );
                    break;

                case 0:
                    mensajes.informativo("Sesion cerrada correctamente.");
                    break;
            }

        } while (opcion != 0);
    }
}
