/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entity;
import java.math.BigDecimal;

/**
 *
 * @author valen
 */
public class Cliente extends Persona {
    
    private BigDecimal salarioFormal;
    private int puntosPositivos;
    private int puntosNegativos;

    public Cliente(String nombre, String documento, String correo, String telefono,
            String direccion, BigDecimal salarioFormal) {
        super(nombre, documento, correo, telefono, direccion);
        this.salarioFormal = salarioFormal;
    }

    public Cliente(int id, String nombre, String documento, String correo, String telefono,
            String direccion, BigDecimal salarioFormal, int puntosPositivos, int puntosNegativos) {
        super(id, nombre, documento, correo, telefono, direccion);
        this.salarioFormal = salarioFormal;
        this.puntosPositivos = puntosPositivos;
        this.puntosNegativos = puntosNegativos;
    }

    public BigDecimal getSalarioFormal() {
        return salarioFormal;
    }

    public void setSalarioFormal(BigDecimal salarioFormal) {
        this.salarioFormal = salarioFormal;
    }

    public int getPuntosPositivos() {
        return puntosPositivos;
    }

    public void setPuntosPositivos(int puntosPositivos) {
        this.puntosPositivos = puntosPositivos;
    }

    public int getPuntosNegativos() {
        return puntosNegativos;
    }

    public void setPuntosNegativos(int puntosNegativos) {
        this.puntosNegativos = puntosNegativos;
    }
    
    
    
    
}
