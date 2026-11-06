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

    }}
