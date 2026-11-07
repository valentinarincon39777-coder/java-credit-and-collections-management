/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author valen
 */

/*


REGLAS 
El cliente no puede tener una solicitud pendiente.

El cliente no puede tener un préstamo activo o vencido.

Se solicita monto y cantidad de cuotas.

La solicitud se guarda automáticamente como pendiente.

Si la registra un asesor, guardamos su ID; si la registra el cliente, asesor_fk queda vacío.
*/


import dao.ClienteDAO;
import dao.SolicitudDAO;
import exception.DAOException;

import java.util.Scanner;

import model.entity.Cliente;
import model.entity.Empleado;
import model.entity.Solicitud;

import view.Mensajes;
import view.VistaSolicitud;

import java.util.List;
import model.enums.Rol;
import model.enums.EstadoEmpleado;
import model.enums.EstadoSolicitud;


import model.entity.SolicitudEvaluacion;






public class ControllerSolicitud {
    
    private final SolicitudDAO solicitudDAO;
    private final ClienteDAO clienteDAO;
    private final VistaSolicitud vista;
    private final Mensajes mensajes;
    
    
    public ControllerSolicitud(Scanner entrada) {

        solicitudDAO = new SolicitudDAO();
        clienteDAO = new ClienteDAO();
        vista = new VistaSolicitud(entrada);
        mensajes = new Mensajes();
    }
    
    
     // REGISTRO DESDE EL CLIENTE************************************+
    public void registrarComoCliente(Cliente cliente) {

        registrarSolicitud(cliente, null);
    }

    // REGISTRO DESDE EL ASESOR*******************************************+
    public void registrarComoAsesor(Empleado asesor) {

        String documento = vista.solicitarDocumento();

        try {

            Cliente cliente =
                    clienteDAO.buscarPorDocumento(documento);

            if (cliente == null) {
                mensajes.error("No se encontro el cliente.");
                return;
            }

            registrarSolicitud(cliente, asesor.getId());

        } catch (DAOException e) {
            mensajes.error(
                    "No fue posible buscar al cliente.");
        }
    }
    
    
    

    // LOGICA COMPARTIDA DE REGISTRO (ya que se puede registrar solciitud desde cliente y asesor)*****************************
    private void registrarSolicitud(
            Cliente cliente, Integer asesorId) {

        try {

           

            var monto = vista.solicitarMonto();
            int cuotas = vista.solicitarCantidadCuotas();

            Solicitud solicitud = new Solicitud(
                    cliente.getId(),
                    asesorId,
                    monto,
                    cuotas
            );

            boolean registrado =
                    solicitudDAO.registrarSolicitud(solicitud);

            if (registrado) {

                mensajes.exito(
                        "Solicitud registrada correctamente. ID: "
                        + solicitud.getId());

            } else {

                mensajes.error(
                        "No se puede registrar la solicitud. "
                        + "El cliente puede tener una solicitud "
                        + "pendiente o un prestamo vigente.");
            }

        } catch (DAOException e) {

            mensajes.error(
                    "No fue posible registrar la solicitud.");
        }
    }

    // CONSULTAS DEL CLIENTE**************************************
    //consulta sus propias solicitudes(un cliente)
    public void consultarComoCliente(Cliente cliente) {

        try {
            vista.mostrarSolicitudes(
                    solicitudDAO.listarPorCliente(
                            cliente.getId()));

        } catch (DAOException e) {
            mensajes.error(
                    "No fue posible consultar las solicitudes.");
        }
    }

    // CONSULTAS DEL ASESOR*********************************
    //asesor consulta las solicitudes, solo las que el mismo registró
    public void consultarComoAsesor(Empleado asesor) {

        try {
            vista.mostrarSolicitudes(
                    solicitudDAO.listarPorAsesor(
                            asesor.getId()));

        } catch (DAOException e) {
            mensajes.error(
                    "No fue posible consultar las solicitudes.");
        }
    }

    // CONSULTAS DE ADMINISTRADOR Y ANALISTA***********************************
    //pueden ver TODAS las solicitudes
    public void consultarTodas() {

        try {
            vista.mostrarSolicitudes(
                    solicitudDAO.listarTodas());

        } catch (DAOException e) {
            mensajes.error(
                    "No fue posible consultar las solicitudes.");
        }
    }
    
    
    
    // EVALUAR SOLICITUDES PENDIENTES*********************************************
    public void evaluarSolicitudes(Empleado analista) {

        // comprobar que empleado sea analista activo
        if (analista == null
                || analista.getRol() != Rol.analista_creditos
                || analista.getEstado() != EstadoEmpleado.activo) {

            mensajes.error("No tienes permiso para evaluar solicitudes.");
            return;
        }

        try {

            // 1. LISTAR SOLICITUDES PENDIENTES
            List<SolicitudEvaluacion> pendientes =
                    solicitudDAO.listarPendientesConDatos();

            if (pendientes.isEmpty()) {
                mensajes.informativo(
                        "No hay solicitudes pendientes de evaluacion.");
                return;
            }

            vista.mostrarPendientesConDatos(pendientes);

            // 2. SOLICITAR ID
            int solicitudId = vista.solicitarIdEvaluacion();

            if (solicitudId == 0) {
                mensajes.informativo("Evaluacion cancelada.");
                return;
            }

            // 3. COMPROBAR QUE EL ID ESTE EN LA LISTA
            SolicitudEvaluacion seleccionada = null;

            for (SolicitudEvaluacion datos : pendientes) {

                if (datos.getSolicitud().getId() == solicitudId) {
                    seleccionada = datos;
                    break;
                }
            }

            if (seleccionada == null) {
                mensajes.error(
                        "La solicitud no existe o no esta pendiente.");
                return;
            }

       
            vista.mostrarPendientesConDatos(List.of(seleccionada));

         
            char decision = vista.solicitarDecision();

            if (decision == 'C') {
                mensajes.informativo("Evaluacion cancelada.");
                return;
            }

            boolean resultado;

            if (decision == 'A') {

                // APROBAR SOLICITUD
                resultado = solicitudDAO.aprobarSolicitud(
                        solicitudId, analista.getId());

                if (resultado) {

                    mensajes.exito(
                            "Solicitud aprobada correctamente. "
                            + "Se ha generado el prestamo "
                            + "y su calendario de cuotas.");

                } else {

                    mensajes.error(
                            "La solicitud ya no esta pendiente "
                            + "o el cliente tiene un prestamo vigente.");
                }

            } else {

                // DENEGAR SOLICITUD
                String motivo = vista.solicitarMotivoRechazo();

                resultado = solicitudDAO.rechazarSolicitud(
                        solicitudId,
                        analista.getId(),
                        motivo
                );

                if (resultado) {

                    mensajes.exito(
                            "Solicitud denegada correctamente.");

                } else {

                    mensajes.error(
                            "La solicitud ya fue evaluada "
                            + "o no existe.");
                }
            }

        } catch (DAOException e) {

            mensajes.error(
                    "No fue posible completar la evaluacion.");

        } catch (IllegalArgumentException e) {

            mensajes.error(
                    "Los datos de la solicitud no permiten "
                    + "completar la evaluacion.");
        }
    }

    
    
    
    
    
    
    
}
    
    
    
    
    
