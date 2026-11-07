
package model.entity;

import java.math.BigDecimal;

public class SolicitudEvaluacion {

    private final Solicitud solicitud;
    private final String nombreCliente;
    private final String documentoCliente;
    private final BigDecimal salarioFormal;
    private final int puntosPositivos;
    private final int puntosNegativos;

    public SolicitudEvaluacion(
            Solicitud solicitud,
            String nombreCliente,
            String documentoCliente,
            BigDecimal salarioFormal,
            int puntosPositivos,
            int puntosNegativos) {

        this.solicitud = solicitud;
        this.nombreCliente = nombreCliente;
        this.documentoCliente = documentoCliente;
        this.salarioFormal = salarioFormal;
        this.puntosPositivos = puntosPositivos;
        this.puntosNegativos = puntosNegativos;
    }

    public Solicitud getSolicitud() {
        return solicitud;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public String getDocumentoCliente() {
        return documentoCliente;
    }

    public BigDecimal getSalarioFormal() {
        return salarioFormal;
    }

    public int getPuntosPositivos() {
        return puntosPositivos;
    }

    public int getPuntosNegativos() {
        return puntosNegativos;
    }
}
