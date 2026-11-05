/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package exception;

/**
 *
 * @author valen
 */
/*Es la que va a lanzar el DAO cuando falle algo con la base de datos, en vez de imprimir y devolver null*/

public class DAOException extends Exception {
    
    public DAOException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    
    
    
}
}