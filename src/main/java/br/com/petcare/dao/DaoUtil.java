package br.com.petcare.dao;

import br.com.petcare.excecao.PetCareException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Funções comuns aos DAOs. */
final class DaoUtil {

    private DaoUtil() {
    }

    /** Traduz erros do MySQL em mensagens compreensíveis para o usuário. */
    static PetCareException erro(String acao, SQLException e) {
        switch (e.getErrorCode()) {
            case 1451:
                return new PetCareException("Não é possível excluir: existem registros vinculados a este item.", e);
            case 1452:
                return new PetCareException("Registro relacionado não encontrado. Atualize a tela e tente novamente.", e);
            case 1062:
                return new PetCareException("Já existe um registro com estes dados.", e);
            default:
                return new PetCareException("Erro ao " + acao + ": " + e.getMessage(), e);
        }
    }

    /** Executa um INSERT e devolve o id gerado (AUTO_INCREMENT). */
    static int inserir(PreparedStatement ps) throws SQLException {
        ps.executeUpdate();
        try (ResultSet chaves = ps.getGeneratedKeys()) {
            return chaves.next() ? chaves.getInt(1) : 0;
        }
    }

    static void exigirAlterado(int linhas) {
        if (linhas == 0) {
            throw new PetCareException("Registro não encontrado. Ele pode ter sido excluído por outro usuário.");
        }
    }
}
