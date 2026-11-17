package br.com.petcare.visao;

import br.com.petcare.servico.PetCareService;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

/** Janela principal: menu lateral de navegação + área de conteúdo (CardLayout). */
public class TelaPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final Color FUNDO_HOVER = Color.decode("#1A5470");

    private final PetCareService negocio = new PetCareService();
    private final CardLayout cartas = new CardLayout();
    private final JPanel conteudo = new JPanel(cartas);
    private final Map<String, JButton> botoes = new LinkedHashMap<>();
    private final Map<String, Atualizavel> telas = new LinkedHashMap<>();
    private String atual;

    public TelaPrincipal() {
        super("PetCare Desktop — Gestão de Pet Shop");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1050, 650));
        setSize(1240, 740);
        setLocationRelativeTo(null);

        JPanel menu = new JPanel();
        menu.setBackground(Tema.ESCURA);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setPreferredSize(new Dimension(235, 0));
        menu.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        menu.add(criarMarca());
        menu.add(Box.createVerticalStrut(10));

        conteudo.setBackground(Tema.FUNDO);
        adicionar(menu, "inicio", "Início", 'I', 1, new PainelInicio(negocio));
        adicionar(menu, "clientes", "Clientes", 'C', 2, new PainelClientes(negocio));
        adicionar(menu, "pets", "Pets", 'P', 3, new PainelPets(negocio));
        adicionar(menu, "servicos", "Serviços", 'S', 4, new PainelServicos(negocio));
        adicionar(menu, "agenda", "Agendamentos", 'A', 5, new PainelAgendamentos(negocio));

        menu.add(Box.createVerticalGlue());
        JButton sair = criarBotaoMenu("Sair");
        sair.setMnemonic('R');
        sair.setToolTipText("Fechar o sistema (Alt+R)");
        sair.addActionListener(e -> confirmarSaida());
        menu.add(sair);

        add(menu, BorderLayout.WEST);
        add(conteudo, BorderLayout.CENTER);
        mostrar("inicio");
    }

    private JPanel criarMarca() {
        JLabel nome = new JLabel("PetCare");
        nome.setFont(Tema.FONTE_TITULO.deriveFont(26f));
        nome.setForeground(Color.WHITE);
        JLabel slogan = new JLabel("Gestão de pet shop");
        slogan.setForeground(Color.decode("#C6DCE7"));
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(24, 20, 14, 10));
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        p.add(nome);
        p.add(slogan);
        return p;
    }

    private void adicionar(JPanel menu, String chave, String rotulo, char mnemonico, int numero, JComponent painel) {
        JButton b = criarBotaoMenu(rotulo);
        b.setMnemonic(mnemonico);
        b.setToolTipText(rotulo + " (Alt+" + mnemonico + " ou Ctrl+" + numero + ")");
        b.addActionListener(e -> mostrar(chave));
        menu.add(b);
        botoes.put(chave, b);
        telas.put(chave, (Atualizavel) painel);
        conteudo.add(painel, chave);

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_0 + numero, InputEvent.CTRL_DOWN_MASK), "ir-" + chave);
        getRootPane().getActionMap().put("ir-" + chave, new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                mostrar(chave);
            }
        });
    }

    private JButton criarBotaoMenu(String rotulo) {
        JButton b = new JButton(rotulo);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFont(Tema.FONTE.deriveFont(16f));
        b.setForeground(Color.WHITE);
        b.setBackground(Tema.ESCURA);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setFocusPainted(true);
        b.setAlignmentX(LEFT_ALIGNMENT);
        b.setPreferredSize(new Dimension(235, 48));
        b.setMinimumSize(new Dimension(100, 48));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        b.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 10));
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (b != botoes.get(atual)) {
                    b.setBackground(FUNDO_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (b != botoes.get(atual)) {
                    b.setBackground(Tema.ESCURA);
                }
            }
        });
        return b;
    }

    private void mostrar(String chave) {
        atual = chave;
        for (Map.Entry<String, JButton> e : botoes.entrySet()) {
            boolean ativo = e.getKey().equals(chave);
            JButton b = e.getValue();
            b.setBackground(ativo ? Tema.PRIMARIA : Tema.ESCURA);
            b.setFont(ativo ? Tema.FONTE_NEGRITO.deriveFont(16f) : Tema.FONTE.deriveFont(16f));
            b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 5, 0, 0, ativo ? Tema.ACENTO : Tema.ESCURA),
                    BorderFactory.createEmptyBorder(0, 19, 0, 10)));
        }
        telas.get(chave).atualizar();
        cartas.show(conteudo, chave);
    }

    private void confirmarSaida() {
        Object[] opcoes = {"Sair", "Continuar no sistema"};
        int r = JOptionPane.showOptionDialog(this, "Deseja realmente sair do PetCare?", "Sair do sistema",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[1]);
        if (r == 0) {
            dispose();
            System.exit(0);
        }
    }
}
