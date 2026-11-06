
package controller.panels;

import java.util.Scanner;
import model.entity.Empleado;
import view.panel.MenuPanelAdmin;
import view.Mensajes;

public class PanelAdmin {

    private final MenuPanelAdmin menu;
    private final Mensajes mensajes;

    public PanelAdmin(Scanner entrada) {
        menu = new MenuPanelAdmin(entrada);
        mensajes = new Mensajes();
    }

    public void iniciar(Empleado empleado) {

        int opcion;

        do {
            opcion = menu.mostrarMenu();

            switch (opcion) {

                case 1:
                    mensajes.informativo(
                            "Gestionar empleados: funcionalidad en desarrollo."
                    );
                    break;

                case 2:
                    mensajes.informativo(
                            "Consultar clientes: funcionalidad en desarrollo."
                    );
                    break;

                case 3:
                    mensajes.informativo(
                            "Consultar perfil financiero: funcionalidad en desarrollo."
                    );
                    break;

                case 4:
                    mensajes.informativo(
                            "Consultar solicitudes: funcionalidad en desarrollo."
                    );
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
