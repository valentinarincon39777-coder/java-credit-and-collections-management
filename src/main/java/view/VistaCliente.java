
package view;

import java.math.BigDecimal;
import java.util.Scanner;
import model.entity.Cliente;
import utils.Validacion;

public class VistaCliente {

    private final Validacion validacion;

    public VistaCliente(Scanner entrada) {
        this.validacion = new Validacion(entrada);
    }

    // SOLICITAR DATOS PARA REGISTRAR CLIENTE****************************
    public Cliente solicitarDatosRegistro() {

        System.out.println("""
                
                ================================
                     REGISTRAR CLIENTE
                ================================
                """);

        String nombre;
        do {
            nombre = validacion.validarTexto(
                    "Ingrese el nombre completo:");

            if (nombre.length() > 45) {
                System.out.println(
                        "El nombre no puede superar 45 caracteres.");
            }

        } while (nombre.length() > 45);

        String documento;
        do {
            documento = validacion.validarDocumento(
                    "Ingrese la cedula:");

            if (documento.length() > 35) {
                System.out.println(
                        "La cedula no puede superar 35 caracteres.");
            }

        } while (documento.length() > 35);

        String correo = validacion.validarCorreo(
                "Ingrese el correo:");

        String telefono = validacion.validarTelefono(
                "Ingrese el telefono:");

        String direccion = validacion.validarTextoLibre(
                "Ingrese la direccion:", 45);

        BigDecimal salario;
        do {
            salario = validacion.validarMonto(
                    "Ingrese el salario formal:");

            if (salario.compareTo(
                    new BigDecimal("99999999.99")) > 0) {
                System.out.println(
                        "El salario supera el monto permitido.");
            }

        } while (salario.compareTo(
                new BigDecimal("99999999.99")) > 0);

        return new Cliente(
                nombre,
                documento,
                correo,
                telefono,
                direccion,
                salario
        );
    }
}
