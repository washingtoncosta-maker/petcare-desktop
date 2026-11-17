package br.com.petcare.dao;

import br.com.petcare.model.Servico;
import br.com.petcare.repositorio.RepositorioServico;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Acesso à tabela {@code servico}. */
public class ServicoDAO implements RepositorioServico {

    /** Colunas com alias s_*, reaproveitadas pelo AgendamentoDAO. */
    static final String COLUNAS = "s.id AS s_id, s.nome AS s_nome, s.descricao AS s_descricao, "
            + "s.preco AS s_preco, s.duracao_minutos AS s_duracao";
    private static final String SELECT = "SELECT " + COLUNAS + " FROM servico s";

    static Servico mapear(ResultSet rs) throws SQLException {
        Servico s = new Servico();
        s.setId(rs.getInt("s_id"));
        s.setNome(rs.getString("s_nome"));
        s.setDescricao(rs.getString("s_descricao"));
        s.setPreco(rs.getDouble("s_preco"));
        s.setDuracaoMinutos(rs.getInt("s_duracao"));
        return s;
    }

    @Override
    public List<Servico> listar() {
        try (Connection con = ConexaoBD.obter();
                PreparedStatement ps = con.prepareStatement(SELECT + " ORDER BY s.nome");
                ResultSet rs = ps.executeQuery()) {
            List<Servico> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw DaoUtil.erro("listar serviços", e);
        }
    }

    @Override
    public Servico buscarPorId(int id) {
        try (Connection con = ConexaoBD.obter(); PreparedStatement ps = con.prepareStatement(SELECT + " WHERE s.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw DaoUtil.erro("buscar serviço", e);
        }
    }

    @Override
    public void salvar(Servico s) {
        try (Connection con = ConexaoBD.obter()) {
            if (s.isNovo()) {
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO servico (nome, descricao, preco, duracao_minutos) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    preencher(ps, s);
                    s.setId(DaoUtil.inserir(ps));
                }
            } else {
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE servico SET nome = ?, descricao = ?, preco = ?, duracao_minutos = ? WHERE id = ?")) {
                    preencher(ps, s);
                    ps.setInt(5, s.getId());
                    DaoUtil.exigirAlterado(ps.executeUpdate());
                }
            }
        } catch (SQLException e) {
            throw DaoUtil.erro("salvar serviço", e);
        }
    }

    private void preencher(PreparedStatement ps, Servico s) throws SQLException {
        ps.setString(1, s.getNome());
        ps.setString(2, s.getDescricao());
        ps.setDouble(3, s.getPreco());
        ps.setInt(4, s.getDuracaoMinutos());
    }

    @Override
    public void excluir(int id) {
        try (Connection con = ConexaoBD.obter(); PreparedStatement ps = con.prepareStatement("DELETE FROM servico WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw DaoUtil.erro("excluir serviço", e);
        }
    }

    @Override
    public boolean nomeJaCadastrado(String nome, int ignorarId) {
        try (Connection con = ConexaoBD.obter();
                PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM servico WHERE LOWER(nome) = LOWER(?) AND id <> ?")) {
            ps.setString(1, nome);
            ps.setInt(2, ignorarId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw DaoUtil.erro("verificar serviço", e);
        }
    }
}
