
package model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class ReporteGuardado implements Serializable {

    //el archivo guradará los datos del reporte en ese momento
    
    private static final long serialVersionUID = 1L;

    private final LocalDateTime fechaGeneracion;
    private final List<String> prestamosActivos;
    private final List<String> prestamosVencidos;
    private final List<String> clientesMorosos;

    public ReporteGuardado(
            List<String> prestamosActivos,
            List<String> prestamosVencidos,
            List<String> clientesMorosos) {

        this.fechaGeneracion = LocalDateTime.now();
        this.prestamosActivos = prestamosActivos;
        this.prestamosVencidos = prestamosVencidos;
        this.clientesMorosos = clientesMorosos;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public List<String> getPrestamosActivos() {
        return prestamosActivos;
    }

    public List<String> getPrestamosVencidos() {
        return prestamosVencidos;
    }

    public List<String> getClientesMorosos() {
        return clientesMorosos;
    }
}
