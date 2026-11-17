package br.com.petcare.visao;

import br.com.petcare.excecao.PetCareException;
import br.com.petcare.model.Entidade;
import br.com.petcare.servico.PetCareService;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 * Tela-padrão de cadastro: busca + tabela + formulário + botões Novo/Salvar/Excluir.
 * As subclasses informam apenas o que é específico de cada cadastro.
 * Atalhos: Ctrl+N (novo), Ctrl+S (salvar), Ctrl+E (excluir).
 */
public abstract class PainelCrud<T extends Entidade> extends JPanel implements Atualizavel {

    private static final long serialVersionUID = 1L;

    protected final PetCareService negocio;
    protected final JPanel barraAcoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

    private final JTextField campoBusca = new JTextField(22);
    private final JLabel rotuloStatus = new JLabel(" ");
    private final JButton btnNovo = new JButton("Novo");
    private final JButton btnSalvar = new JButton("Salvar");
    private final JButton btnExcluir = new JButton("Excluir");
    private DefaultTableModel modelo;
    private JTable tabela;
    private List<T> itens = new ArrayList<>();
    private List<T> visiveis = new ArrayList<>();
    private T selecionado;

    protected PainelCrud(PetCareService negocio) {
        this.negocio = negocio;
    }

    // ------------------------------------------------------------ pontos de extensão
    protected abstract String titulo();

    protected abstract String[] colunas();

    protected abstract Object[] linha(T item);

    protected abstract String textoBusca(T item);

    protected abstract List<T> carregar();

    protected abstract void persistir(T item);

    protected abstract void remover(T item);

    protected abstract JComponent criarFormulario();

    protected abstract void preencherFormulario(T item);

    protected abstract void limparFormulario();

    /** Cria o objeto a partir do formulário. Deve lançar PetCareException se algo for inválido. */
    protected abstract T lerFormulario(int id);

    protected abstract JComponent campoInicial();

    /** Chamado antes de cada recarga (ex.: atualizar listas de seleção). */
    protected void prepararCampos() {
    }

    // ---------------------------------------------------------------- construção
    /** Monta a tela. Deve ser chamado ao final do construtor da subclasse. */
    protected final void montar() {
        setLayout(new BorderLayout(0, 14));
        setBackground(Tema.FUNDO);
        setBorder(BorderFactory.createEmptyBorder(22, 26, 18, 26));

        JLabel titulo = new JLabel(titulo());
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.TEXTO);

        JLabel rotuloBusca = new JLabel("Buscar:");
        rotuloBusca.setForeground(Tema.TEXTO_SECUNDARIO);
        rotuloBusca.setFont(Tema.FONTE_NEGRITO);
        rotuloBusca.setDisplayedMnemonic('B');
        rotuloBusca.setLabelFor(campoBusca);
        Tema.campo(campoBusca);
        campoBusca.getAccessibleContext().setAccessibleName("Buscar em " + titulo());
        campoBusca.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                aplicarFiltro();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                aplicarFiltro();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                aplicarFiltro();
            }
        });
        JPanel busca = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        busca.setOpaque(false);
        busca.add(rotuloBusca);
        busca.add(campoBusca);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.add(titulo, BorderLayout.WEST);
        topo.add(busca, BorderLayout.EAST);
        add(topo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(colunas(), 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
        tabela = new JTable(modelo);
        Tema.tabela(tabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getAccessibleContext().setAccessibleName("Tabela de " + titulo());
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int l = tabela.getSelectedRow();
            if (l >= 0 && l < visiveis.size()) {
                selecionado = visiveis.get(l);
                preencherFormulario(selecionado);
                btnExcluir.setEnabled(true);
                aoSelecionar(selecionado);
                definirStatus("Editando o registro nº " + selecionado.getId() + ".");
            }
        });
        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setBorder(BorderFactory.createLineBorder(Tema.BORDA));
        rolagem.getViewport().setBackground(Tema.SUPERFICIE);
        add(rolagem, BorderLayout.CENTER);

        JPanel caixaFormulario = new JPanel(new BorderLayout());
        caixaFormulario.setBackground(Tema.SUPERFICIE);
        caixaFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BORDA),
                BorderFactory.createEmptyBorder(14, 16, 6, 16)));
        JComponent formulario = criarFormulario();
        caixaFormulario.add(formulario, BorderLayout.CENTER);
        enterSalva(formulario);

        Tema.botaoSecundario(btnNovo);
        Tema.botaoPrimario(btnSalvar);
        Tema.botaoPerigo(btnExcluir);
        btnNovo.setToolTipText("Limpa o formulário para um novo cadastro (Ctrl+N)");
        btnSalvar.setToolTipText("Grava o registro (Ctrl+S)");
        btnExcluir.setToolTipText("Exclui o registro selecionado (Ctrl+E)");
        btnNovo.addActionListener(e -> novo());
        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        btnExcluir.setEnabled(false);
        barraAcoes.setOpaque(false);
        barraAcoes.add(btnSalvar);
        barraAcoes.add(btnNovo);
        barraAcoes.add(btnExcluir);
        adicionarAcoes();

        rotuloStatus.setForeground(Tema.TEXTO_SECUNDARIO);
        JPanel rodape = new JPanel(new BorderLayout(0, 8));
        rodape.setOpaque(false);
        rodape.add(caixaFormulario, BorderLayout.CENTER);
        JPanel linhaBotoes = new JPanel(new BorderLayout());
        linhaBotoes.setOpaque(false);
        linhaBotoes.add(barraAcoes, BorderLayout.WEST);
        linhaBotoes.add(rotuloStatus, BorderLayout.EAST);
        rodape.add(linhaBotoes, BorderLayout.SOUTH);
        add(rodape, BorderLayout.SOUTH);

        registrarAtalho(KeyEvent.VK_N, "novo", this::novo);
        registrarAtalho(KeyEvent.VK_S, "salvar", this::salvar);
        registrarAtalho(KeyEvent.VK_E, "excluir", this::excluir);
    }

    /** Permite às subclasses incluir botões extras em {@link #barraAcoes}. */
    protected void adicionarAcoes() {
    }

    protected void aoSelecionar(T item) {
    }

    // -------------------------------------------------------------------- ações
    @Override
    public final void atualizar() {
        prepararCampos();
        try {
            itens = carregar();
        } catch (PetCareException ex) {
            itens = new ArrayList<>();
            avisar(ex.getMessage());
        }
        aplicarFiltro();
        novo();
    }

    protected final void novo() {
        tabela.clearSelection();
        selecionado = null;
        btnExcluir.setEnabled(false);
        limparFormulario();
        definirStatus("Novo registro.");
        campoInicial().requestFocusInWindow();
    }

    protected final void salvar() {
        try {
            T objeto = lerFormulario(selecionado == null ? 0 : selecionado.getId());
            persistir(objeto);
            atualizar();
            definirStatus("Registro salvo com sucesso.");
        } catch (PetCareException ex) {
            avisar(ex.getMessage());
        }
    }

    protected final void excluir() {
        if (selecionado == null) {
            avisar("Selecione um registro na tabela para excluir.");
            return;
        }
        Object[] opcoes = {"Excluir", "Cancelar"};
        int r = JOptionPane.showOptionDialog(this, "Deseja realmente excluir este registro?\nEsta ação não pode ser desfeita.",
                "Confirmar exclusão", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, opcoes, opcoes[1]);
        if (r != 0) {
            return;
        }
        try {
            remover(selecionado);
            atualizar();
            definirStatus("Registro excluído.");
        } catch (PetCareException ex) {
            avisar(ex.getMessage());
        }
    }

    // ------------------------------------------------------------------ apoio
    protected final T getSelecionado() {
        return selecionado;
    }

    protected final void avisar(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    protected final void definirStatus(String mensagem) {
        rotuloStatus.setText(mensagem);
    }

    protected static <E extends Entidade> void selecionarPorId(JComboBox<E> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getId() == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void aplicarFiltro() {
        String termo = campoBusca.getText().trim().toLowerCase();
        visiveis = new ArrayList<>();
        modelo.setRowCount(0);
        for (T item : itens) {
            if (termo.isEmpty() || textoBusca(item).toLowerCase().contains(termo)) {
                visiveis.add(item);
                modelo.addRow(linha(item));
            }
        }
    }

    private void enterSalva(Component c) {
        if (c instanceof JTextField) {
            ((JTextField) c).addActionListener(e -> salvar());
        }
        if (c instanceof Container) {
            for (Component filho : ((Container) c).getComponents()) {
                enterSalva(filho);
            }
        }
    }

    private void registrarAtalho(int tecla, String nome, Runnable acao) {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(tecla, InputEvent.CTRL_DOWN_MASK), nome);
        getActionMap().put(nome, new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (isShowing()) {
                    acao.run();
                }
            }
        });
    }
}
