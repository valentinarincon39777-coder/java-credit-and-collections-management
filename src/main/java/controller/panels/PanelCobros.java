
package controller.panels;

import java.util.Scanner;
import model.entity.Empleado;
import view.panel.MenuPanelCobros;
import view.Mensajes;

public class PanelCobros {

    private final MenuPanelCobros menu;
    private final Mensajes mensajes;

    public PanelCobros(Scanner entrada) {
        menu = new MenuPanelCobros(entrada);
        mensajes = new Mensajes();
    }

    public void iniciar(Empleado empleado) {

        int opcion;

        do {
            opcion = menu.mostrarMenu();

            switch (opcion) {

                case 1:
                    mensajes.informativo(
                            "Consultar clientes: funcionalidad en desarrollo."
                    );
                    break;

                case 2:
                    mensajes.informativo(
                            "Consultar prestamos: funcionalidad en desarrollo."
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
                            "Confirmar o rechazar pagos: funcionalidad en desarrollo."
                    );
                    break;

                case 6:
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
