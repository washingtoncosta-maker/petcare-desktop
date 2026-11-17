package br.com.petcare.repositorio;

import br.com.petcare.model.Servico;

public interface RepositorioServico extends Repositorio<Servico> {

    boolean nomeJaCadastrado(String nome, int ignorarId);
}
