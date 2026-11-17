package br.com.petcare.dao;

import br.com.petcare.model.Agendamento;
import br.com.petcare.model.StatusAgendamento;
import br.com.petcare.repositorio.RepositorioAgendamento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Acesso à tabela {@code agendamento} (com pet, tutor e serviço carregados por JOIN). */
public class AgendamentoDAO implements RepositorioAgendamento {

    private static final String SELECT = "SELECT a.id AS a_id, a.data_hora AS a_data_hora, a.status AS a_status, "
            + "a.observacoes AS a_obs, " + PetDAO.COLUNAS + ", " + ServicoDAO.COLUNAS
            + " FROM agendamento a"
            + " JOIN pet p ON p.id = a.id_pet"
            + " JOIN cliente c ON c.id = p.id_cliente"
            + " JOIN servico s ON s.id = a.id_servico";

    private static Agendamento mapear(ResultSet rs) throws SQLException {
        Agendamento a = new Agendamento();
        a.setId(rs.getInt("a_id"));
        a.setPet(PetDAO.mapear(rs));
        a.setServico(ServicoDAO.mapear(rs));
        a.setDataHora(rs.getTimestamp("a_data_hora").toLocalDateTime());
        a.setStatus(StatusAgendamento.valueOf(rs.getString("a_status")));
        a.setObservacoes(rs.getString("a_obs"));
        return a;
    }

    @Override
    public List<Agendamento> listar() {
        try (Connection con = ConexaoBD.obter();
                PreparedStatement ps = con.prepareStatement(SELECT + " ORDER BY a.data_hora");
                ResultSet rs = ps.executeQuery()) {
            List<Agendamento> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw DaoUtil.erro("listar agendamentos", e);
        }
    }

    @Override
    public Agendamento buscarPorId(int id) {
        try (Connection con = ConexaoBD.obter(); PreparedStatement ps = con.prepareStatement(SELECT + " WHERE a.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw DaoUtil.erro("buscar agendamento", e);
        }
    }

    @Override
    public void salvar(Agendamento a) {
        try (Connection con = ConexaoBD.obter()) {
            if (a.isNovo()) {
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO agendamento (id_pet, id_servico, data_hora, status, observacoes) VALUES (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    preencher(ps, a);
                    a.setId(DaoUtil.inserir(ps));
                }
            } else {
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE agendamento SET id_pet = ?, id_servico = ?, data_hora = ?, status = ?, observacoes = ? WHERE id = ?")) {
                    preencher(ps, a);
                    ps.setInt(6, a.getId());
                    DaoUtil.exigirAlterado(ps.executeUpdate());
                }
            }
        } catch (SQLException e) {
            throw DaoUtil.erro("salvar agendamento", e);
        }
    }

    private void preencher(PreparedStatement ps, Agendamento a) throws SQLException {
        ps.setInt(1, a.getPet().getId());
        ps.setInt(2, a.getServico().getId());
        ps.setTimestamp(3, Timestamp.valueOf(a.getDataHora()));
        ps.setString(4, a.getStatus().name());
        ps.setString(5, a.getObservacoes());
    }

    @Override
    public void excluir(int id) {
        try (Connection con = ConexaoBD.obter(); PreparedStatement ps = con.prepareStatement("DELETE FROM agendamento WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw DaoUtil.erro("excluir agendamento", e);
        }
    }

    @Override
    public boolean horarioOcupado(LocalDateTime dataHora, int ignorarId) {
        try (Connection con = ConexaoBD.obter();
                PreparedStatement ps = con.prepareStatement(
                        "SELECT COUNT(*) FROM agendamento WHERE data_hora = ? AND status = 'AGENDADO' AND id <> ?")) {
            ps.setTimestamp(1, Timestamp.valueOf(dataHora));
            ps.setInt(2, ignorarId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw DaoUtil.erro("verificar horário", e);
        }
    }
}
