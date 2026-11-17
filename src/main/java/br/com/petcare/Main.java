package br.com.petcare;

import br.com.petcare.dao.ConexaoBD;
import br.com.petcare.excecao.PetCareException;
import br.com.petcare.visao.TelaPrincipal;
import br.com.petcare.visao.Tema;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/** PetCare Desktop - ponto de entrada. Verifica o acesso ao MySQL antes de abrir a tela principal. */
public class Main {

    public static void main(String[] args) {
        Locale.setDefault(new Locale("pt", "BR"));
        Tema.instalar();
        try {
            ConexaoBD.testar();
        } catch (PetCareException e) {
            JOptionPane.showMessageDialog(null,
                    "Não foi possível conectar ao banco de dados.\n\n" + e.getMessage()
                    + "\n\nVerifique:\n• se o MySQL está em execução;\n• se o script sql/petcare.sql foi executado no MySQL Workbench;"
                    + "\n• se usuário e senha em src/main/resources/db.properties estão corretos.",
                    "PetCare — Erro de conexão", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        SwingUtilities.invokeLater(() -> new TelaPrincipal().setVisible(true));
    }
}
