package br.com.petcare.servico;

import br.com.petcare.excecao.PetCareException;
import br.com.petcare.model.Agendamento;
import br.com.petcare.model.Cliente;
import br.com.petcare.model.Pet;
import br.com.petcare.model.Servico;
import br.com.petcare.model.StatusAgendamento;
import br.com.petcare.repositorio.RepositorioAgendamento;
import br.com.petcare.repositorio.RepositorioCliente;
import br.com.petcare.repositorio.RepositorioPet;
import br.com.petcare.repositorio.RepositorioServico;
import br.com.petcare.repositorio.Repositorios;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Camada de regras de negócio. As telas conversam somente com esta classe, que por sua vez
 * usa os repositórios. Assim, trocar a persistência (memória -> MySQL) não afeta as telas.
 *
 * Regras implementadas:
 * RN01 CPF válido e único por cliente; RN02 pet sempre vinculado a um cliente existente;
 * RN03 um único atendimento ativo por horário; RN04 só é possível concluir/cancelar agendamentos ativos;
 * RN05 não é possível excluir cliente, pet ou serviço que possuam registros vinculados.
 */
public class PetCareService {

    private final RepositorioCliente clientes = Repositorios.clientes();
    private final RepositorioPet pets = Repositorios.pets();
    private final RepositorioServico servicos = Repositorios.servicos();
    private final RepositorioAgendamento agendamentos = Repositorios.agendamentos();

    // ---------------------------------------------------------------- clientes
    public List<Cliente> listarClientes() {
        return clientes.listar();
    }

    public void salvarCliente(Cliente cliente) {
        if (clientes.cpfJaCadastrado(cliente.getCpf(), cliente.getId())) {
            throw new PetCareException("Já existe um cliente cadastrado com este CPF.");
        }
        clientes.salvar(cliente);
    }

    public void excluirCliente(Cliente cliente) {
        for (Pet p : pets.listar()) {
            if (p.getDono().getId() == cliente.getId()) {
                throw new PetCareException("Não é possível excluir: o cliente possui pets cadastrados ("
                        + p.getNome() + "). Exclua ou transfira os pets primeiro.");
            }
        }
        clientes.excluir(cliente.getId());
    }

    // -------------------------------------------------------------------- pets
    public List<Pet> listarPets() {
        return pets.listar();
    }

    public void salvarPet(Pet pet) {
        if (clientes.buscarPorId(pet.getDono().getId()) == null) {
            throw new PetCareException("O tutor selecionado não está mais cadastrado.");
        }
        pets.salvar(pet);
    }

    public void excluirPet(Pet pet) {
        for (Agendamento a : agendamentos.listar()) {
            if (a.getPet().getId() == pet.getId()) {
                throw new PetCareException("Não é possível excluir: o pet possui agendamentos registrados.");
            }
        }
        pets.excluir(pet.getId());
    }

    // ---------------------------------------------------------------- serviços
    public List<Servico> listarServicos() {
        return servicos.listar();
    }

    public void salvarServico(Servico servico) {
        if (servicos.nomeJaCadastrado(servico.getNome(), servico.getId())) {
            throw new PetCareException("Já existe um serviço com este nome.");
        }
        servicos.salvar(servico);
    }

    public void excluirServico(Servico servico) {
        for (Agendamento a : agendamentos.listar()) {
            if (a.getServico().getId() == servico.getId()) {
                throw new PetCareException("Não é possível excluir: o serviço possui agendamentos registrados.");
            }
        }
        servicos.excluir(servico.getId());
    }

    // ------------------------------------------------------------ agendamentos
    public List<Agendamento> listarAgendamentos() {
        return agendamentos.listar();
    }

    public void salvarAgendamento(Agendamento agendamento) {
        if (agendamento.getStatus() == StatusAgendamento.AGENDADO
                && agendamentos.horarioOcupado(agendamento.getDataHora(), agendamento.getId())) {
            throw new PetCareException("Já existe um atendimento agendado para "
                    + agendamento.getDataHora().format(Agendamento.FORMATO) + ". Escolha outro horário.");
        }
        agendamentos.salvar(agendamento);
    }

    public void concluirAgendamento(Agendamento agendamento) {
        agendamento.concluir();
        agendamentos.salvar(agendamento);
    }

    public void cancelarAgendamento(Agendamento agendamento) {
        agendamento.cancelar();
        agendamentos.salvar(agendamento);
    }

    public void excluirAgendamento(Agendamento agendamento) {
        agendamentos.excluir(agendamento.getId());
    }

    // --------------------------------------------------------------- painel inicial
    public int contarClientes() {
        return clientes.listar().size();
    }

    public int contarPets() {
        return pets.listar().size();
    }

    public int contarAgendamentosHoje() {
        return agendamentosAtivosDoDia(LocalDate.now()).size();
    }

    public double receitaPrevistaHoje() {
        double total = 0;
        for (Agendamento a : agendamentosAtivosDoDia(LocalDate.now())) {
            total += a.getValor();
        }
        return total;
    }

    public List<Agendamento> proximosAgendamentos(int limite) {
        LocalDateTime agora = LocalDateTime.now();
        List<Agendamento> lista = new ArrayList<>();
        for (Agendamento a : agendamentos.listar()) {
            if (a.getStatus() == StatusAgendamento.AGENDADO && !a.getDataHora().isBefore(agora)) {
                lista.add(a);
            }
        }
        lista.sort((x, y) -> x.getDataHora().compareTo(y.getDataHora()));
        return lista.size() > limite ? new ArrayList<>(lista.subList(0, limite)) : lista;
    }

    private List<Agendamento> agendamentosAtivosDoDia(LocalDate dia) {
        List<Agendamento> lista = new ArrayList<>();
        for (Agendamento a : agendamentos.listar()) {
            if (a.getStatus() == StatusAgendamento.AGENDADO && a.getDataHora().toLocalDate().equals(dia)) {
                lista.add(a);
            }
        }
        return lista;
    }
}
