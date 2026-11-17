package br.com.petcare.dao;

import br.com.petcare.model.Cliente;
import br.com.petcare.repositorio.RepositorioCliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Acesso à tabela {@code cliente}. */
public class ClienteDAO implements RepositorioCliente {

    private static final String SELECT = "SELECT id, nome, cpf, telefone, email FROM cliente";

    /** Lê um cliente do ResultSet; {@code prefixo} é o prefixo dos aliases (vazio ou "c_"). */
    static Cliente mapear(ResultSet rs, String prefixo) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt(prefixo + "id"));
        c.setNome(rs.getString(prefixo + "nome"));
        c.setCpf(rs.getString(prefixo + "cpf"));
        c.setTelefone(rs.getString(prefixo + "telefone"));
        c.setEmail(rs.getString(prefixo + "email"));
        return c;
    }

    @Override
    public List<Cliente> listar() {
        try (Connection con = ConexaoBD.obter();
                PreparedStatement ps = con.prepareStatement(SELECT + " ORDER BY nome");
                ResultSet rs = ps.executeQuery()) {
            List<Cliente> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(mapear(rs, ""));
            }
            return lista;
        } catch (SQLException e) {
            throw DaoUtil.erro("listar clientes", e);
        }
    }

    @Override
    public Cliente buscarPorId(int id) {
        try (Connection con = ConexaoBD.obter(); PreparedStatement ps = con.prepareStatement(SELECT + " WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs, "") : null;
            }
        } catch (SQLException e) {
            throw DaoUtil.erro("buscar cliente", e);
        }
    }

    @Override
    public void salvar(Cliente c) {
        try (Connection con = ConexaoBD.obter()) {
            if (c.isNovo()) {
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO cliente (nome, cpf, telefone, email) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    preencher(ps, c);
                    c.setId(DaoUtil.inserir(ps));
                }
            } else {
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE cliente SET nome = ?, cpf = ?, telefone = ?, email = ? WHERE id = ?")) {
                    preencher(ps, c);
                    ps.setInt(5, c.getId());
                    DaoUtil.exigirAlterado(ps.executeUpdate());
                }
            }
        } catch (SQLException e) {
            throw DaoUtil.erro("salvar cliente", e);
        }
    }

    private void preencher(PreparedStatement ps, Cliente c) throws SQLException {
        ps.setString(1, c.getNome());
        ps.setString(2, c.getCpf());
        ps.setString(3, c.getTelefone());
        ps.setString(4, c.getEmail());
    }

    @Override
    public void excluir(int id) {
        try (Connection con = ConexaoBD.obter(); PreparedStatement ps = con.prepareStatement("DELETE FROM cliente WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw DaoUtil.erro("excluir cliente", e);
        }
    }

    @Override
    public boolean cpfJaCadastrado(String cpf, int ignorarId) {
        try (Connection con = ConexaoBD.obter();
                PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM cliente WHERE cpf = ? AND id <> ?")) {
            ps.setString(1, cpf);
            ps.setInt(2, ignorarId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw DaoUtil.erro("verificar CPF", e);
        }
    }
}
