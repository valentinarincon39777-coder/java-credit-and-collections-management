
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
}
