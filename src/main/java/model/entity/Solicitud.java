
package model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import model.enums.EstadoSolicitud;

public class Solicitud {

    private int id;
    private int clienteId;
    private Integer asesorId;
    private Integer analistaId;

    private BigDecimal montoSolicitado;
    private int cantidadCuotas;
    private EstadoSolicitud estado;

    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaEvaluacion;
    private String motivoRechazo;

    
    public Solicitud(int clienteId, Integer asesorId,
            BigDecimal montoSolicitado, int cantidadCuotas) {

        this.clienteId = clienteId;
        this.asesorId = asesorId;
        this.montoSolicitado = montoSolicitado;
        this.cantidadCuotas = cantidadCuotas;
        this.estado = EstadoSolicitud.pendiente;
        this.fechaSolicitud = LocalDateTime.now();
    }

    // Constructor para recuperar de MySQL
    public Solicitud(int id, int clienteId, Integer asesorId,
            Integer analistaId, BigDecimal montoSolicitado,
            int cantidadCuotas, EstadoSolicitud estado,
            LocalDateTime fechaSolicitud,
            LocalDateTime fechaEvaluacion,
            String motivoRechazo) {

        this(clienteId, asesorId, montoSolicitado, cantidadCuotas);
        this.id = id;
        this.analistaId = analistaId;
        this.estado = estado;
        this.fechaSolicitud = fechaSolicitud;
        this.fechaEvaluacion = fechaEvaluacion;
        this.motivoRechazo = motivoRechazo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public Integer getAsesorId() {
        return asesorId;
    }

    public Integer getAnalistaId() {
        return analistaId;
    }

    public BigDecimal getMontoSolicitado() {
        return montoSolicitado;
    }

    public int getCantidadCuotas() {
        return cantidadCuotas;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public LocalDateTime getFechaEvaluacion() {
        return fechaEvaluacion;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }
}
