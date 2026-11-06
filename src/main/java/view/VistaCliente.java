
package view;

import java.math.BigDecimal;
import java.util.List;
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
    
    
    // MENU DE GESTION DE CLIENTES******************************
    public int mostrarMenuGestion() {

        System.out.println("""
                
                ================================
                    GESTIONAR CLIENTES
                ================================

                1. Registrar cliente
                2. Consultar clientes
                3. Actualizar cliente
                4. Eliminar cliente
                0. Volver al panel del asesor

                """);

        return validacion.validarEnteroRango(
                "Seleccione una opcion:", 0, 4);
    }

    // MENU DE CONSULTAS***************************
    public int mostrarMenuConsultas() {

        System.out.println("""
                
                ================================
                    CONSULTAR CLIENTES
                ================================

                1. Ver todos los clientes
                2. Buscar por cedula
                3. Buscar por nombre
                4. Mejores clientes
                5. Peores clientes
                0. Volver

                """);

        return validacion.validarEnteroRango(
                "Seleccione una opcion:", 0, 5);
    }

    // PEDIR CEDULA PARA UNA CONSULTA**************************
    public String solicitarDocumentoBusqueda() {
        return validacion.validarDocumento(
                "Ingrese la cedula del cliente:");
    }

    // PEDIR NOMBRE PARA UNA CONSULTA**************************
    public String solicitarNombreBusqueda() {
        return validacion.validarTexto(
                "Ingrese el nombre o parte del nombre:");
    }

    // MOSTRAR UN CLIENTE**************************
    public void mostrarCliente(Cliente cliente) {

        System.out.println("-----------------------------");
        System.out.println("ID: " + cliente.getId());
        System.out.println("Nombre: " + cliente.getNombre());
        System.out.println("Cedula: " + cliente.getDocumento());
        System.out.println("Correo: " + cliente.getCorreo());
        System.out.println("Telefono: " + cliente.getTelefono());
        System.out.println("Direccion: " + cliente.getDireccion());
        System.out.println("Salario: $" + cliente.getSalarioFormal());
        System.out.println("Puntos positivos: "
                + cliente.getPuntosPositivos());
        System.out.println("Puntos negativos: "
                + cliente.getPuntosNegativos());
    }

    // MOSTRAR UNA LISTA DE CLIENTES**************************
    public void mostrarClientes(List<Cliente> clientes) {

        if (clientes.isEmpty()) {
            System.out.println("No se encontraron clientes.");
            return;
        }

        System.out.println("\nCLIENTES ENCONTRADOS: "
                + clientes.size());

        for (Cliente cliente : clientes) {
            mostrarCliente(cliente);
        }
    }
    
    
    // MENU PARA ACTUALIZAR CLIENTE****************************
    public int mostrarMenuActualizar() {

        System.out.println("""
                
                ===== ACTUALIZAR CLIENTE =====

                1. Nombre
                2. Cedula
                3. Correo
                4. Telefono
                5. Direccion
                6. Salario formal
                0. Volver

                """);

        return validacion.validarEnteroRango(
                "Seleccione el dato a modificar:", 0, 6);
    }

    // PEDIR NUEVO DATO PERSONAL*********************************************
    public String solicitarNuevoDato(int opcion) {

        switch (opcion) {

            case 1:
                String nombre;
                do {
                    nombre = validacion.validarTexto(
                            "Ingrese el nuevo nombre:");

                    if (nombre.length() > 45) {
                        System.out.println(
                                "Maximo 45 caracteres.");
                    }
                } while (nombre.length() > 45);

                return nombre;

            case 2:
                return validacion.validarDocumento(
                        "Ingrese la nueva cedula:");

            case 3:
                return validacion.validarCorreo(
                        "Ingrese el nuevo correo:");

            case 4:
                return validacion.validarTelefono(
                        "Ingrese el nuevo telefono:");

            case 5:
                return validacion.validarTextoLibre(
                        "Ingrese la nueva direccion:", 45);

            default:
                return "";
        }
    }

    // PEDIR NUEVO SALARIO*******************************
    public BigDecimal solicitarNuevoSalario() {

        BigDecimal salario;

        do {
            salario = validacion.validarMonto(
                    "Ingrese el nuevo salario:");

            if (salario.compareTo(
                    new BigDecimal("99999999.99")) > 0) {
                System.out.println(
                        "El salario supera el maximo permitido.");
            }

        } while (salario.compareTo(
                new BigDecimal("99999999.99")) > 0);

        return salario;
    }
    
    
    
    // CONFIRMAR ELIMINACION DE CLIENTE********************************
    public boolean confirmarEliminacion() {

        char respuesta = validacion.validarLetra(
                "¿Esta segura de eliminar este cliente? (S/N):",
                "SN"
        );

        return respuesta == 'S';
    }



    
    
    
    
    
    
}
