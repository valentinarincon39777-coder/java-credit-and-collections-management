/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;




/*
Esta clase establece y gestiona la conexión estática a la base de datos crediya utilizando JDBC y DriverManager.
Centraliza los parámetros de acceso para que las capas DAO puedan conectarse de forma limpia y reutilizable.
 */


 //IMPORTACIONES
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class Conexion {
    
    public static Connection conectar() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/crediya";
        String user = "root";
        String password = "sql_116";
        return DriverManager.getConnection(url, user, password);
    }
    
    
}
