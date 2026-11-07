
package model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import model.enums.EstadoPrestamo;

public class Prestamo {

    private int id;
    private int solicitudId;
    private BigDecimal interes;
    private BigDecimal totalPagar;
    private BigDecimal saldoPendiente;
    private LocalDateTime fechaDesembolso;
    private LocalDate fechaVencimientoFinal;
    private EstadoPrestamo estado;

    
    public Prestamo(int solicitudId, BigDecimal interes,
            BigDecimal totalPagar,
            LocalDateTime fechaDesembolso,
            LocalDate fechaVencimientoFinal) {

        this.solicitudId = solicitudId;
        this.interes = interes;
        this.totalPagar = totalPagar;
        this.saldoPendiente = totalPagar;
        this.fechaDesembolso = fechaDesembolso;
        this.fechaVencimientoFinal = fechaVencimientoFinal;
        this.estado = EstadoPrestamo.activo;
    }


    public Prestamo(int id, int solicitudId,
            BigDecimal interes, BigDecimal totalPagar,
            BigDecimal saldoPendiente,
            LocalDateTime fechaDesembolso,
            LocalDate fechaVencimientoFinal,
            EstadoPrestamo estado) {

        this.solicitudId = solicitudId;
        this.id = id;
        this.interes = interes;
        this.totalPagar = totalPagar;
        this.saldoPendiente = saldoPendiente;
        this.fechaDesembolso = fechaDesembolso;
        this.fechaVencimientoFinal = fechaVencimientoFinal;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSolicitudId() {
        return solicitudId;
    }

    public BigDecimal getInteres() {
        return interes;
    }

    public BigDecimal getTotalPagar() {
        return totalPagar;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public LocalDateTime getFechaDesembolso() {
        return fechaDesembolso;
    }

    public LocalDate getFechaVencimientoFinal() {
        return fechaVencimientoFinal;
    }

    public EstadoPrestamo getEstado() {
        return estado;
    }
}
