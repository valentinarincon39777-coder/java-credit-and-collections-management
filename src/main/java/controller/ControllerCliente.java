package controller;

import dao.ClienteDAO;
import exception.DAOException;

import java.util.Scanner;

import model.entity.Cliente;

import view.Mensajes;
import view.VistaCliente;

public class ControllerCliente {

    private final ClienteDAO clienteDAO;
    private final VistaCliente vista;
    private final Mensajes mensajes;

    public ControllerCliente(Scanner entrada) {

        clienteDAO = new ClienteDAO();
        vista = new VistaCliente(entrada);
        mensajes = new Mensajes();
    }

    // REGISTRAR NUEVO CLIENTE************************************
    public void registrarCliente() {

        Cliente cliente = vista.solicitarDatosRegistro();

        try {

            if (clienteDAO.existeDocumento(cliente.getDocumento())) {
                mensajes.error(
                        "Ya existe una persona con esa cedula.");
                return;
            }

            clienteDAO.registrarCliente(cliente);

            mensajes.exito(
                    "Cliente registrado correctamente. ID: "
                    + cliente.getId());

        } catch (DAOException e) {

            mensajes.error(
                    "No fue posible registrar el cliente.");

        }
    }

    // SUBMENU DE GESTION DE CLIENTES****************************************
    public void gestionarClientes() {

        int opcion;

        do {
            opcion = vista.mostrarMenuGestion();

            switch (opcion) {

                case 1:
                    registrarCliente();
                    break;

                case 2:
                    consultarClientes();
                    break;

                case 3:
                    actualizarCliente();
                    break;

                case 4:
                    eliminarCliente();
                    break;

                case 0:
                    mensajes.informativo(
                            "Regresando al panel del asesor.");
                    break;
            }

        } while (opcion != 0);
    }

    // CONSULTAR CLIENTES************************************
    public void consultarClientes() {

        int opcion;

        do {
            opcion = vista.mostrarMenuConsultas();

            try {

                switch (opcion) {

                    case 1:
                        vista.mostrarClientes(
                                clienteDAO.listarClientes());
                        break;

                    case 2:
                        String documento
                                = vista.solicitarDocumentoBusqueda();

                        Cliente cliente
                                = clienteDAO.buscarPorDocumento(documento);

                        if (cliente == null) {
                            mensajes.informativo(
                                    "No se encontro el cliente.");
                        } else {
                            vista.mostrarCliente(cliente);
                        }
                        break;

                    case 3:
                        String nombre
                                = vista.solicitarNombreBusqueda();

                        vista.mostrarClientes(
                                clienteDAO.buscarPorNombre(nombre));
                        break;

                    case 4:
                        vista.mostrarClientes(
                                clienteDAO.listarPorPuntos(true));
                        break;

                    case 5:
                        vista.mostrarClientes(
                                clienteDAO.listarPorPuntos(false));
                        break;

                    case 0:
                        break;
                }

            } catch (DAOException e) {
                mensajes.error(
                        "No fue posible realizar la consulta. "
                        + "Intente nuevamente.");
            }

        } while (opcion != 0);
    }

    // ACTUALIZAR CLIENTE DESDE EL ASESOR******************************
    public void actualizarCliente() {

        String documento = vista.solicitarDocumentoBusqueda();

        try {
            Cliente cliente = clienteDAO.buscarPorDocumento(documento);

            if (cliente == null) {
                mensajes.error("No se encontro el cliente.");
                return;
            }

            vista.mostrarCliente(cliente);

            int opcion;

            do {
                opcion = vista.mostrarMenuActualizar();

                if (opcion == 0) {
                    break;
                }

                boolean actualizado;

                if (opcion == 6) {

                    actualizado = clienteDAO.actualizarSalario(
                            cliente.getId(),
                            vista.solicitarNuevoSalario()
                    );

                } else {

                    String nuevoValor
                            = vista.solicitarNuevoDato(opcion);

                    if (opcion == 2
                            && !nuevoValor.equals(cliente.getDocumento())
                            && clienteDAO.existeDocumento(nuevoValor)) {

                        mensajes.error(
                                "Esa cedula ya esta registrada.");
                        continue;
                    }

                    actualizado = clienteDAO.actualizarDatoPersonal(
                            cliente.getId(), opcion, nuevoValor
                    );
                }

                if (actualizado) {

                    mensajes.exito(
                            "Datos actualizados correctamente.");

                    cliente = clienteDAO.buscarPorDocumento(
                            opcion == 2
                                    ? clienteDAO.buscarDocumentoPorId(
                                            cliente.getId())
                                    : cliente.getDocumento()
                    );

                    if (cliente != null) {
                        vista.mostrarCliente(cliente);
                    }

                } else {
                    mensajes.error(
                            "No se pudo actualizar el cliente.");
                }

            } while (opcion != 0);

        } catch (DAOException e) {
            mensajes.error(
                    "No fue posible completar la actualizacion.");
        }
    }

    // ELIMINAR CLIENTE DESDE EL ASESOR*****************************************
    public void eliminarCliente() {

        String documento = vista.solicitarDocumentoBusqueda();

        try {

            Cliente cliente
                    = clienteDAO.buscarPorDocumento(documento);

            if (cliente == null) {
                mensajes.error("No se encontro el cliente.");
                return;
            }

            vista.mostrarCliente(cliente);

            if (!vista.confirmarEliminacion()) {
                mensajes.informativo(
                        "Eliminacion cancelada.");
                return;
            }

            boolean eliminado
                    = clienteDAO.eliminarCliente(cliente.getId());

            if (eliminado) {
                mensajes.exito(
                        "Cliente eliminado correctamente.");
            } else {
                mensajes.error(
                        "No se puede eliminar el cliente. "
                        + "Puede tener solicitudes registradas.");
            }

        } catch (DAOException e) {
            mensajes.error(
                    "No fue posible eliminar el cliente. "
                    + "Verifique que no tenga algún registro asociado.");
        }
    }

}
