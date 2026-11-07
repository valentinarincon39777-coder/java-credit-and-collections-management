
package model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import model.enums.EstadoPago;
import model.enums.MetodoPago;

public class Pago {

    private int id;
    private int cuotaId;
    private BigDecimal monto;
    private MetodoPago metodoPago;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaConfirmacion;
    private EstadoPago estado;
    private int gestorId;

    // CONSTRUCTOR PARA REGISTRAR PAGOS
    public Pago(int cuotaId, BigDecimal monto,
            MetodoPago metodoPago, int gestorId) {

        this.cuotaId = cuotaId;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.gestorId = gestorId;
        this.estado = EstadoPago.confirmado;
    }

    // CONSTRUCTOR PARA RECUPERAR PAGOS DE MYSQL
    public Pago(int id, int cuotaId, BigDecimal monto,
            MetodoPago metodoPago,
            LocalDateTime fechaSolicitud,
            LocalDateTime fechaConfirmacion,
            EstadoPago estado, int gestorId) {
 
        this(cuotaId, monto, metodoPago, gestorId);

        this.id = id;
        this.fechaSolicitud = fechaSolicitud;
        this.fechaConfirmacion = fechaConfirmacion;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCuotaId() {
        return cuotaId;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public LocalDateTime getFechaConfirmacion() {
        return fechaConfirmacion;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public int getGestorId() {
        return gestorId;
    }
}

