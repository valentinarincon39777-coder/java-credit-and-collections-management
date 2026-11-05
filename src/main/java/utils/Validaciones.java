/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 *
 * @author valen
 */
public class Validaciones {

    //Validar documento**************************************************
    public String validarDocumento(String mensaje) {
        String dato;
        boolean validacion;
        do {
            validacion = true;
            System.out.println(mensaje);
            dato = new Scanner(System.in).nextLine();

            if (dato.isBlank()) {
                System.out.println("Error, No se aceptan espacios en blanco");
                validacion = false;
                continue;
            }

            dato = dato.strip();
            for (int i = 0; i < dato.length(); i++) {

                if (Character.isDigit(dato.charAt(i)) == false) {
                    if (dato.charAt(i) != '-') {
                        System.out.println("Error, solo se aceptan número o guiones");
                        validacion = false;
                        break;
                    }
                }
            }

        } while (validacion == false);
        return dato;

    }

    //validar telefono*********************************************
    public String validarTelefono(String mensaje) {
        String dato;
        do {
            System.out.println(mensaje);
            dato = new Scanner(System.in).nextLine().strip();
            if (dato.matches("\\+?[0-9][0-9 -]{5,18}[0-9]")) {
                return dato;
            }
            System.out.println("Error, ingresa un teléfono válido (solo números, espacios o guiones. Ej: 6000-0000)");
        } while (true);
    }

   
    
    //validar correo **********************************************
    public String validarCorreo(String mensaje) {
        String dato;
        do {
            System.out.println(mensaje);
            dato = new Scanner(System.in).nextLine().strip();
            if (dato.length() <= 45 && dato.matches("[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+")) {
                return dato;
            }
            System.out.println("Error, ingresa un correo válido (Ej: nombre@correo.com)");
        } while (true);
    }

    //validar BigDecimal (para salarios y montos) *******************************
    public BigDecimal validarMonto(String mensaje) {
        while (true) {
            System.out.println(mensaje);
            String dato = new Scanner(System.in).nextLine().strip().replace(',', '.');
            try {
                BigDecimal monto = new BigDecimal(dato);
                if (monto.signum() <= 0) {
                    System.out.println("Error, el monto debe ser mayor que 0");
                } else if (monto.scale() > 2) {
                    System.out.println("Error, máximo 2 decimales");
                } else {
                    return monto;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error, se espera un valor numérico (Ej: 1250.50)");
            }
        }
    }

    //Validar texto*****************************************************
    public String validarTexto(String mensaje) {
        String dato;
        boolean validacion;
        do {
            validacion = true;
            System.out.println(mensaje);
            dato = new Scanner(System.in).nextLine();
            if (dato.isBlank()) {
                System.out.println("Error, No se aceptan espacios en blanco");
                validacion = false;
                continue;
            }

            dato = dato.strip();
            for (int i = 0; i < dato.length(); i++) {

                if (Character.isLetter(dato.charAt(i)) == false) {
                    if (dato.charAt(i) != ' ') {
                        System.out.println("Error, solo se aceptan letras o espacios");
                        validacion = false;
                        break;
                    }
                }
            }
        } while (validacion == false);
        return dato;
    }

    //validar texto libre (direcciones, descripciones)*****************************************
    public String validarTextoLibre(String mensaje, int maximo) {
        String dato;
        do {
            System.out.println(mensaje);
            dato = new Scanner(System.in).nextLine().strip();
            if (!dato.isEmpty() && dato.length() <= maximo) {
                return dato;
            }
            System.out.println("Error, no puede estar vacío ni superar " + maximo + " caracteres");
        } while (true);
    }

    

    //Validar entero****************************************************
    public int validarEntero(String mensaje) {
        Scanner x = new Scanner(System.in);
        System.out.println(mensaje);
        while (x.hasNextInt() == false) {
            System.out.println("Error, se espera un valor entero");
            x.next();
        }
        return x.nextInt();
    }
    
    //validar fecha ***********************************
    
    public LocalDate validarFecha(String mensaje) {
    while (true) {
        System.out.println(mensaje + " (formato AAAA-MM-DD)");
        String dato = new Scanner(System.in).nextLine().strip();
        try {
            return LocalDate.parse(dato);
        } catch (DateTimeParseException e) {
            System.out.println("Error, fecha inválida (Ej: 2026-11-04)");
        }
    }
}
    
    
    //validar letra (para denegar, rechazar, aceptar etc)*********************************************
   public char validarLetra(String mensaje, String permitidas) {
    while (true) {
        System.out.println(mensaje);
        String dato = new Scanner(System.in).nextLine().strip().toUpperCase();
        if (dato.length() == 1 && permitidas.indexOf(dato.charAt(0)) >= 0) {
            return dato.charAt(0);
        }
        System.out.println("Error, elige una de estas opciones: " + permitidas);
    }
}
   
   //validar rango de enteros ************************************
   public int validarEnteroRango(String mensaje, int min, int max) {
    int numero;
    do {
        numero = validarEntero(mensaje);
        if (numero < min || numero > max) {
            System.out.println("Error, ingresa un número entre " + min + " y " + max);
        }
    } while (numero < min || numero > max);
    return numero;
}
    
    
    

    //Validar entero grande*****************************************************
    public long validarEnteroGrande(String mensaje) {
        Scanner x = new Scanner(System.in);
        System.out.println(mensaje);
        while (x.hasNextLong() == false) {
            System.out.println("Error, se espera un valor entero");
            x.next();
        }
        return x.nextLong();
    }

    
        
    //Validar decimal*****************************************************
    public double validarDecimal(String mensaje) {
        System.out.println(mensaje);
        Scanner x = new Scanner(System.in);
        while (x.hasNextDouble() == false) {
            System.out.println("Error, se espera un valor decimal");
            x.next();
        }
        return x.nextDouble();
    }
    
}
