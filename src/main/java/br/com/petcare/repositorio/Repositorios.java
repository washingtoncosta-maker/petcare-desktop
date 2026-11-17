package br.com.petcare.repositorio;

import br.com.petcare.dao.AgendamentoDAO;
import br.com.petcare.dao.ClienteDAO;
import br.com.petcare.dao.PetDAO;
import br.com.petcare.dao.ServicoDAO;

/** Fábrica dos repositórios usados pelo sistema: DAOs JDBC que acessam o MySQL. */
public final class Repositorios {

    private static final RepositorioCliente CLIENTES = new ClienteDAO();
    private static final RepositorioPet PETS = new PetDAO();
    private static final RepositorioServico SERVICOS = new ServicoDAO();
    private static final RepositorioAgendamento AGENDAMENTOS = new AgendamentoDAO();

    private Repositorios() {
    }

    public static RepositorioCliente clientes() {
        return CLIENTES;
    }

    public static RepositorioPet pets() {
        return PETS;
    }

    public static RepositorioServico servicos() {
        return SERVICOS;
    }

    public static RepositorioAgendamento agendamentos() {
        return AGENDAMENTOS;
    }
}
