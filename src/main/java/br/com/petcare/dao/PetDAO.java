package br.com.petcare.dao;

import br.com.petcare.model.Pet;
import br.com.petcare.model.Porte;
import br.com.petcare.repositorio.RepositorioPet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Acesso à tabela {@code pet} (com o cliente/tutor carregado por JOIN). */
public class PetDAO implements RepositorioPet {

    /** Colunas de pet (p_*) e do tutor (c_*); reaproveitado pelo AgendamentoDAO. */
    static final String COLUNAS = "p.id AS p_id, p.nome AS p_nome, p.especie AS p_especie, p.raca AS p_raca, "
            + "p.porte AS p_porte, c.id AS c_id, c.nome AS c_nome, c.cpf AS c_cpf, "
            + "c.telefone AS c_telefone, c.email AS c_email";
    private static final String SELECT = "SELECT " + COLUNAS
            + " FROM pet p JOIN cliente c ON c.id = p.id_cliente";

    static Pet mapear(ResultSet rs) throws SQLException {
        Pet p = new Pet();
        p.setId(rs.getInt("p_id"));
        p.setNome(rs.getString("p_nome"));
        p.setEspecie(rs.getString("p_especie"));
        p.setRaca(rs.getString("p_raca"));
        p.setPorte(Porte.valueOf(rs.getString("p_porte")));
        p.setDono(ClienteDAO.mapear(rs, "c_"));
        return p;
    }

    @Override
    public List<Pet> listar() {
        try (Connection con = ConexaoBD.obter();
                PreparedStatement ps = con.prepareStatement(SELECT + " ORDER BY p.nome");
                ResultSet rs = ps.executeQuery()) {
            List<Pet> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw DaoUtil.erro("listar pets", e);
        }
    }

    @Override
    public Pet buscarPorId(int id) {
        try (Connection con = ConexaoBD.obter(); PreparedStatement ps = con.prepareStatement(SELECT + " WHERE p.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw DaoUtil.erro("buscar pet", e);
        }
    }

    @Override
    public void salvar(Pet p) {
        try (Connection con = ConexaoBD.obter()) {
            if (p.isNovo()) {
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO pet (nome, especie, raca, porte, id_cliente) VALUES (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    preencher(ps, p);
                    p.setId(DaoUtil.inserir(ps));
                }
            } else {
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE pet SET nome = ?, especie = ?, raca = ?, porte = ?, id_cliente = ? WHERE id = ?")) {
                    preencher(ps, p);
                    ps.setInt(6, p.getId());
                    DaoUtil.exigirAlterado(ps.executeUpdate());
                }
            }
        } catch (SQLException e) {
            throw DaoUtil.erro("salvar pet", e);
        }
    }

    private void preencher(PreparedStatement ps, Pet p) throws SQLException {
        ps.setString(1, p.getNome());
        ps.setString(2, p.getEspecie());
        ps.setString(3, p.getRaca());
        ps.setString(4, p.getPorte().name());
        ps.setInt(5, p.getDono().getId());
    }

    @Override
    public void excluir(int id) {
        try (Connection con = ConexaoBD.obter(); PreparedStatement ps = con.prepareStatement("DELETE FROM pet WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw DaoUtil.erro("excluir pet", e);
        }
    }
}
