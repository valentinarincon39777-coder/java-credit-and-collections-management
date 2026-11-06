
package controller.panels;

import java.util.Scanner;
import model.entity.Empleado;
import view.panel.MenuPanelAnalista;
import view.Mensajes;

public class PanelAnalista {

    private final MenuPanelAnalista menu;
    private final Mensajes mensajes;

    public PanelAnalista(Scanner entrada) {
        menu = new MenuPanelAnalista(entrada);
        mensajes = new Mensajes();
    }

    public void iniciar(Empleado empleado) {

        int opcion;

        do {
            opcion = menu.mostrarMenu();

            switch (opcion) {

                case 1:
                    mensajes.informativo(
                            "Consultar solicitudes: funcionalidad en desarrollo."
                    );
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
