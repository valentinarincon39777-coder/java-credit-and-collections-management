/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

/**
 *
 * @author valen
 */

import java.util.List;
import java.util.Scanner;
import model.entity.Empleado;
import model.enums.Rol;
import utils.Validacion;

public class VistaEmpleado {

    private final Validacion validacion;

    public VistaEmpleado(Scanner entrada) {
        this.validacion = new Validacion(entrada);

    }

    // SOLICITAR DATOS REGISTRAR EMPLEADO****************************
    public Empleado solicitarDatosRegistro() {
        System.out.println("""
                
                ================================
                     REGISTRAR EMPLEADO
                ================================
                """);

        String nombre;

        do {
            nombre = validacion.validarTexto("Ingrese el nombre completo: ");

            if (nombre.length() > 45) {
                System.out.println("El nombre no puede superar  45 caracteres");
            }

        } while (nombre.length() > 45);

        String documento;

        do {

            documento = validacion.validarDocumento("Ingrese la cedula");
            if (documento.length() > 35) {

                System.out.println("La cedula  no puede superar 35 caracteres");
            }

        } while (documento.length() > 35);

        String correo = validacion.validarCorreo("Ingrese el correo:");
        String telefono = validacion.validarTelefono("Ingrese el telefono:");
        String direccion = validacion.validarTextoLibre("Ingrese la direccion:", 45);

        int opcion = validacion.validarEnteroRango(
                """
        
                           Seleccione  rol:

                           1. Administrador general
                           2. Asesor de creditos
                           3. Analista de creditos
                           4. Gestor de cobros
                        

                           """, 1, 4
        );
        Rol rol;

        rol = switch (opcion) {
            case 1 ->
                Rol.administrador_general;
            case 2 ->
                Rol.asesor_creditos;
            case 3 ->
                Rol.analista_creditos;
            case 4 ->
                Rol.gestor_cobros;
            default ->
                null;
        };

        return new Empleado(nombre, documento, correo, telefono, direccion, rol);

    }



    
    // MENU DE GESTION DE EMPLEADOS****************************************
    public int mostrarMenuGestion() {

        System.out.println("""
                
                ===== GESTIONAR EMPLEADOS =====

                1. Registrar empleado
                2. Consultar empleados
                3. Actualizar empleado
                4. Desactivar empleado
                0. Volver al panel administrador

                """);

        return validacion.validarEnteroRango(
                "Seleccione una opcion:", 0, 4);
    }

    // MENU PARA CONSULTAR EMPLEADOS*******************************
    public int mostrarMenuConsultas() {

        System.out.println("""
                
                ===== CONSULTAR EMPLEADOS =====

                1. Ver todos los empleados
                2. Buscar por cedula
                0. Volver

                """);

        return validacion.validarEnteroRango(
                "Seleccione una opcion:", 0, 2);
    }

    // SOLICITAR DOCUMENTO*************************************************+
    public String solicitarDocumentoBusqueda() {

        return validacion.validarDocumento(
                "Ingrese la cedula del empleado:");
    }

    // MOSTRAR UN EMPLEADO******************************************************************
    public void mostrarEmpleado(Empleado empleado) {

        System.out.println("-----------------------------");
        System.out.println("ID: " + empleado.getId());
        System.out.println("Nombre: " + empleado.getNombre());
        System.out.println("Cedula: " + empleado.getDocumento());
        System.out.println("Correo: " + empleado.getCorreo());
        System.out.println("Telefono: " + empleado.getTelefono());
        System.out.println("Direccion: " + empleado.getDireccion());
        System.out.println("Rol: " + empleado.getRol());
        System.out.println("Estado: " + empleado.getEstado());
    }

    // MOSTRAR LISTA DE EMPLEADOS
    public void mostrarEmpleados(List<Empleado> empleados) {

        if (empleados.isEmpty()) {
            System.out.println("No se encontraron empleados.");
            return;
        }

        System.out.println("\nEMPLEADOS ENCONTRADOS: "
                + empleados.size());

        for (Empleado empleado : empleados) {
            mostrarEmpleado(empleado);
        }
    }

    // MENU PARA ACTUALIZAR EMPLEADO*********************************************
    public int mostrarMenuActualizar() {

        System.out.println("""
                
                ===== ACTUALIZAR EMPLEADO =====

                1. Nombre
                2. Cedula
                3. Correo
                4. Telefono
                5. Direccion
                6. Rol
                0. Volver

                """);

        return validacion.validarEnteroRango(
                "Seleccione el dato a modificar:", 0, 6);
    }

    // SOLICITAR NUEVO DATO PERSONAL********************************************************+
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
                String documento;

                do {
                    documento = validacion.validarDocumento(
                            "Ingrese la nueva cedula:");

                    if (documento.length() > 35) {
                        System.out.println(
                                "Maximo 35 caracteres.");
                    }

                } while (documento.length() > 35);

                return documento;

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

    // SELECCIONAR NUEVO ROL
    public Rol solicitarNuevoRol() {

        int opcion = validacion.validarEnteroRango(
                """
                
                Seleccione el nuevo rol:

                1. Administrador general
                2. Asesor de creditos
                3. Analista de creditos
                4. Gestor de cobros

                """, 1, 4);

        return switch (opcion) {
            case 1 -> Rol.administrador_general;
            case 2 -> Rol.asesor_creditos;
            case 3 -> Rol.analista_creditos;
            case 4 -> Rol.gestor_cobros;
            default -> throw new IllegalStateException(
                    "Rol no valido");
        };
    }

    // CONFIRMAR DESACTIVACION
    public boolean confirmarDesactivacion() {

        char respuesta = validacion.validarLetra(
                "¿Confirma que desea desactivar al empleado? (S/N):",
                "SN");

        return respuesta == 'S';
    }











}
