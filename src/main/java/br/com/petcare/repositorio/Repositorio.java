package br.com.petcare.repositorio;

import br.com.petcare.model.Entidade;
import java.util.List;

/** Contrato genérico de persistência (CRUD). */
public interface Repositorio<T extends Entidade> {

    List<T> listar();

    /** @return o registro ou {@code null} se não existir */
    T buscarPorId(int id);

    /** Insere quando o id é 0 (e preenche o id gerado); caso contrário, atualiza. */
    void salvar(T objeto);

    void excluir(int id);
}
