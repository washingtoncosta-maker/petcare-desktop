package br.com.petcare.visao;

import br.com.petcare.model.Agendamento;
import br.com.petcare.servico.PetCareService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/** Painel inicial: indicadores do dia e próximos atendimentos. */
public class PainelInicio extends JPanel implements Atualizavel {

    private static final long serialVersionUID = 1L;
    private static final Locale BR = new Locale("pt", "BR");

    private final PetCareService negocio;
    private final JLabel numClientes = numero();
    private final JLabel numPets = numero();
    private final JLabel numHoje = numero();
    private final JLabel numReceita = numero();
    private final JLabel subtitulo = new JLabel(" ");
    private final JLabel vazio = new JLabel(" ");
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Data/hora", "Pet", "Tutor", "Serviço", "Valor"}, 0) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };

    public PainelInicio(PetCareService negocio) {
        this.negocio = negocio;
        setLayout(new BorderLayout(0, 18));
        setBackground(Tema.FUNDO);
        setBorder(BorderFactory.createEmptyBorder(22, 26, 18, 26));

        JLabel titulo = new JLabel("Início");
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.TEXTO);
        subtitulo.setForeground(Tema.TEXTO_SECUNDARIO);
        JPanel cabecalho = new JPanel();
        cabecalho.setOpaque(false);
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        cabecalho.add(titulo);
        cabecalho.add(subtitulo);

        JPanel cartoes = new JPanel(new GridLayout(1, 4, 16, 0));
        cartoes.setOpaque(false);
        cartoes.add(cartao("Clientes cadastrados", numClientes, Tema.PRIMARIA));
        cartoes.add(cartao("Pets cadastrados", numPets, Tema.ACENTO));
        cartoes.add(cartao("Atendimentos hoje", numHoje, Tema.SUCESSO));
        cartoes.add(cartao("Receita prevista hoje", numReceita, Tema.ESCURA));

        JPanel topo = new JPanel(new BorderLayout(0, 18));
        topo.setOpaque(false);
        topo.add(cabecalho, BorderLayout.NORTH);
        topo.add(cartoes, BorderLayout.CENTER);
        add(topo, BorderLayout.NORTH);

        JLabel tituloTabela = new JLabel("Próximos atendimentos");
        tituloTabela.setFont(Tema.FONTE_NEGRITO.deriveFont(17f));
        tituloTabela.setForeground(Tema.TEXTO);
        JTable tabela = new JTable(modelo);
        Tema.tabela(tabela);
        tabela.setFocusable(false);
        tabela.getAccessibleContext().setAccessibleName("Tabela dos próximos atendimentos");
        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setBorder(BorderFactory.createLineBorder(Tema.BORDA));
        rolagem.getViewport().setBackground(Tema.SUPERFICIE);
        vazio.setForeground(Tema.TEXTO_SECUNDARIO);

        JPanel centro = new JPanel(new BorderLayout(0, 8));
        centro.setOpaque(false);
        centro.add(tituloTabela, BorderLayout.NORTH);
        centro.add(rolagem, BorderLayout.CENTER);
        centro.add(vazio, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);
    }

    private static JLabel numero() {
        JLabel l = new JLabel("0");
        l.setFont(Tema.FONTE_NUMERO);
        l.setForeground(Tema.TEXTO);
        return l;
    }

    private static JPanel cartao(String legenda, JLabel valor, Color destaque) {
        JLabel texto = new JLabel(legenda);
        texto.setForeground(Tema.TEXTO_SECUNDARIO);
        texto.setFont(Tema.FONTE_NEGRITO);
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(Tema.SUPERFICIE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BORDA),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(5, 0, 0, 0, destaque),
                        BorderFactory.createEmptyBorder(14, 16, 14, 16))));
        p.add(texto, BorderLayout.NORTH);
        p.add(valor, BorderLayout.CENTER);
        return p;
    }

    @Override
    public void atualizar() {
        String data = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", BR));
        subtitulo.setText(Character.toUpperCase(data.charAt(0)) + data.substring(1)
                + " — bem-vindo ao PetCare Desktop");
        try {
            numClientes.setText(String.valueOf(negocio.contarClientes()));
            numPets.setText(String.valueOf(negocio.contarPets()));
            numHoje.setText(String.valueOf(negocio.contarAgendamentosHoje()));
            numReceita.setText(NumberFormat.getCurrencyInstance(BR).format(negocio.receitaPrevistaHoje()));
            modelo.setRowCount(0);
            List<Agendamento> proximos = negocio.proximosAgendamentos(8);
            for (Agendamento a : proximos) {
                modelo.addRow(new Object[]{a.getDataHora().format(Agendamento.FORMATO), a.getPet().getNome(),
                    a.getPet().getDono().getNome(), a.getServico().getNome(),
                    NumberFormat.getCurrencyInstance(BR).format(a.getValor())});
            }
            vazio.setText(proximos.isEmpty() ? "Nenhum atendimento futuro agendado." : " ");
        } catch (RuntimeException ex) {
            vazio.setText("Não foi possível carregar os indicadores: " + ex.getMessage());
        }
    }
}
