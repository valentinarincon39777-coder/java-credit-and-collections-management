/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entity;
import model.enums.EstadoEmpleado;
import model.enums.Rol;

/**
 *
 * @author valen
 */
public class Empleado extends Persona {
     private Rol rol;
    private EstadoEmpleado estado;

    public Empleado(String nombre, String documento, String correo, String telefono,
            String direccion, Rol rol) {
        super(nombre, documento, correo, telefono, direccion);
        this.rol = rol;
        this.estado = EstadoEmpleado.activo;
    }

    public Empleado(int id, String nombre, String documento, String correo, String telefono,
            String direccion, Rol rol, EstadoEmpleado estado) {
        super(id, nombre, documento, correo, telefono, direccion);
        this.rol = rol;
        this.estado = estado;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public EstadoEmpleado getEstado() {
        return estado;
    }

    public void setEstado(EstadoEmpleado estado) {
        this.estado = estado;
    }
    
    
    
    
    
}
