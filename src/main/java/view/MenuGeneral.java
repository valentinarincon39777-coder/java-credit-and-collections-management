
package view;

import java.util.Scanner;
import model.enums.Rol;
import utils.Validacion;

public class MenuGeneral {

    private final Validacion validacion;

    public MenuGeneral(Scanner entrada) {
        this.validacion = new Validacion(entrada);
    }

    // MENU PRINCIPAL
    public int mostrarMenuPrincipal() {

        System.out.println("""
                           
                           =================================
                                  CREDIYA S.A.S.
                                SISTEMA DE CREDITOS
                           =================================

                           1. Ingresar como cliente
                           2. Ingresar como empleado
                           0. Salir

                           """);

        return validacion.validarEnteroRango(
                "Seleccione una opcion: ", 0, 2
        );
    }

    // SELECCIONAR ROL DEL EMPLEADO
    public Rol seleccionarRolEmpleado() {

        System.out.println("""
                           
                           =================================
                                ACCESO DE EMPLEADOS
                           =================================

                           Seleccione su rol:

                           1. Administrador general
                           2. Asesor de creditos
                           3. Analista de creditos
                           4. Gestor de cobros
                           0. Volver

                           """);

        int opcion = validacion.validarEnteroRango(
                "Seleccione una opcion: ", 0, 4
        );

        return switch (opcion) {
            case 1 -> Rol.administrador_general;
            case 2 -> Rol.asesor_creditos;
            case 3 -> Rol.analista_creditos;
            case 4 -> Rol.gestor_cobros;
            default -> null;
        };
    }

    // SOLICITAR DOCUMENTO
    public String solicitarDocumento() {

        return validacion.validarDocumento(
                "Ingrese su cedula: "
        );
    }

    // MENSAJES
    public void mostrarMensaje(String mensaje) {
        System.out.println("\n" + mensaje + "\n");
    }

    public void mostrarError(String mensaje) {
        System.out.println("\nERROR: " + mensaje + "\n");
    }

    public void mostrarDespedida() {
        System.out.println("""
                           
                           =================================
                           Gracias por utilizar CrediYa.
                           Hasta pronto.
                           =================================
                           """);
    }
}
