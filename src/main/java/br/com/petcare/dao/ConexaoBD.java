package br.com.petcare.dao;

import br.com.petcare.excecao.PetCareException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Fábrica de conexões JDBC com o MySQL.
 * Os parâmetros (URL, usuário e senha) ficam em src/main/resources/db.properties.
 */
public final class ConexaoBD {

    private static Properties propriedades;

    private ConexaoBD() {
    }

    public static Connection obter() {
        Properties p = carregar();
        try {
            return DriverManager.getConnection(p.getProperty("db.url"),
                    p.getProperty("db.usuario"), p.getProperty("db.senha", ""));
        } catch (SQLException e) {
            throw new PetCareException("Não foi possível conectar ao banco de dados: " + e.getMessage(), e);
        }
    }

    /** Abre e fecha uma conexão para verificar se o banco está acessível. */
    public static void testar() {
        try (Connection c = obter()) {
            c.isValid(2);
        } catch (SQLException e) {
            throw new PetCareException("Falha ao validar a conexão: " + e.getMessage(), e);
        }
    }

    private static synchronized Properties carregar() {
        if (propriedades == null) {
            Properties p = new Properties();
            try (InputStream in = ConexaoBD.class.getResourceAsStream("/db.properties")) {
                if (in == null) {
                    throw new PetCareException("Arquivo db.properties não encontrado em src/main/resources.");
                }
                p.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new PetCareException("Não foi possível ler o arquivo db.properties.", e);
            }
            propriedades = p;
        }
        return propriedades;
    }
}
