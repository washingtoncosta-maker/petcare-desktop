package br.com.petcare.repositorio;

import br.com.petcare.model.Cliente;

public interface RepositorioCliente extends Repositorio<Cliente> {

    boolean cpfJaCadastrado(String cpf, int ignorarId);
}
