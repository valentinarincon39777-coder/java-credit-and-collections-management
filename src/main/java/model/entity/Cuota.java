
package model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import model.enums.EstadoCuota;

public class Cuota {

    private int id;
    private int prestamoId;
    private int numeroCuota;
    private LocalDate fechaLimite;
    private BigDecimal capital;
    private BigDecimal interes;
    private BigDecimal montoPagar;
    private EstadoCuota estado;


    public Cuota(int prestamoId, int numeroCuota,
            LocalDate fechaLimite, BigDecimal capital,
            BigDecimal interes, BigDecimal montoPagar) {

        this.prestamoId = prestamoId;
        this.numeroCuota = numeroCuota;
        this.fechaLimite = fechaLimite;
        this.capital = capital;
        this.interes = interes;
        this.montoPagar = montoPagar;
        this.estado = EstadoCuota.pendiente;
    }

    
    public Cuota(int id, int prestamoId,
            int numeroCuota, LocalDate fechaLimite,
            BigDecimal capital, BigDecimal interes,
            BigDecimal montoPagar, EstadoCuota estado) {

        this(prestamoId, numeroCuota, fechaLimite,
                capital, interes, montoPagar);

        this.id = id;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrestamoId() {
        return prestamoId;
    }

    public void setPrestamoId(int prestamoId) {
        this.prestamoId = prestamoId;
    }

    public int getNumeroCuota() {
        return numeroCuota;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public BigDecimal getCapital() {
        return capital;
    }

    public BigDecimal getInteres() {
        return interes;
    }

    public BigDecimal getMontoPagar() {
        return montoPagar;
    }

    public EstadoCuota getEstado() {
        return estado;
    }
}
