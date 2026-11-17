package br.com.petcare.visao;

import br.com.petcare.model.StatusAgendamento;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Enumeration;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;

/**
 * Identidade visual do PetCare (ver documento de UX/UI da Etapa 2).
 * Todas as combinações de cor de texto/fundo têm contraste mínimo de 4,5:1 (WCAG 2.1 AA).
 */
public final class Tema {

    public static final Color PRIMARIA = Color.decode("#0B5C80");
    public static final Color ESCURA = Color.decode("#0E3B4F");
    public static final Color ACENTO = Color.decode("#F5A524");
    public static final Color SUCESSO = Color.decode("#1B7A3E");
    public static final Color PERIGO = Color.decode("#B42318");
    public static final Color FUNDO = Color.decode("#F4F7F9");
    public static final Color SUPERFICIE = Color.WHITE;
    public static final Color TEXTO = Color.decode("#1A2B33");
    public static final Color TEXTO_SECUNDARIO = Color.decode("#4A5C66");
    public static final Color BORDA = Color.decode("#C9D5DB");
    public static final Color SELECAO = Color.decode("#CFE6F2");
    public static final Color ZEBRA = Color.decode("#F7FAFB");

    public static final Font FONTE = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    public static final Font FONTE_NEGRITO = FONTE.deriveFont(Font.BOLD);
    public static final Font FONTE_TITULO = FONTE.deriveFont(Font.BOLD, 24f);
    public static final Font FONTE_NUMERO = FONTE.deriveFont(Font.BOLD, 30f);

    private Tema() {
    }

    /** Deve ser chamado antes de criar qualquer janela. */
    public static void instalar() {
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignorada) {
            // mantém o Look and Feel padrão
        }
        Enumeration<Object> chaves = UIManager.getDefaults().keys();
        while (chaves.hasMoreElements()) {
            Object chave = chaves.nextElement();
            if (UIManager.get(chave) instanceof FontUIResource) {
                UIManager.put(chave, new FontUIResource(FONTE));
            }
        }
        UIManager.put("ComboBox.background", SUPERFICIE);
        UIManager.put("ComboBox.foreground", TEXTO);
        UIManager.put("ComboBox.selectionBackground", SELECAO);
        UIManager.put("ComboBox.selectionForeground", TEXTO);
        UIManager.put("OptionPane.background", SUPERFICIE);
        UIManager.put("Panel.background", SUPERFICIE);
        UIManager.put("TextField.foreground", TEXTO);
        UIManager.put("TextField.selectionBackground", SELECAO);
        UIManager.put("TextField.selectionForeground", TEXTO);
    }

    public static void botaoPrimario(JButton b) {
        estilizar(b, PRIMARIA, Color.WHITE, PRIMARIA);
    }

    public static void botaoSucesso(JButton b) {
        estilizar(b, SUCESSO, Color.WHITE, SUCESSO);
    }

    public static void botaoPerigo(JButton b) {
        estilizar(b, PERIGO, Color.WHITE, PERIGO);
    }

    public static void botaoSecundario(JButton b) {
        estilizar(b, SUPERFICIE, PRIMARIA, PRIMARIA);
    }

    private static void estilizar(JButton b, Color fundo, Color texto, Color borda) {
        b.setBackground(fundo);
        b.setForeground(texto);
        b.setFont(FONTE_NEGRITO);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setFocusPainted(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borda, 1),
                BorderFactory.createEmptyBorder(7, 16, 7, 16)));
    }

    public static void campo(JTextComponent c) {
        c.setFont(FONTE);
        c.setForeground(TEXTO);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
    }

    /** Aplica o estilo padrão de tabelas: cabeçalho colorido, linhas zebradas e status com texto + cor. */
    public static void tabela(JTable t) {
        t.setRowHeight(30);
        t.setFont(FONTE);
        t.setForeground(TEXTO);
        t.setBackground(SUPERFICIE);
        t.setGridColor(BORDA);
        t.setShowVerticalLines(false);
        t.setSelectionBackground(SELECAO);
        t.setSelectionForeground(TEXTO);
        t.setFillsViewportHeight(true);
        t.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader cabecalho = t.getTableHeader();
        cabecalho.setReorderingAllowed(false);
        cabecalho.setPreferredSize(new Dimension(0, 34));
        cabecalho.setDefaultRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable tabela, Object valor, boolean sel,
                    boolean foco, int linha, int coluna) {
                super.getTableCellRendererComponent(tabela, valor, false, false, linha, coluna);
                setBackground(PRIMARIA);
                setForeground(Color.WHITE);
                setFont(FONTE_NEGRITO);
                setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
                return this;
            }
        });

        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable tabela, Object valor, boolean sel,
                    boolean foco, int linha, int coluna) {
                super.getTableCellRendererComponent(tabela, valor, sel, false, linha, coluna);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                setFont(FONTE);
                setForeground(TEXTO);
                setBackground(sel ? SELECAO : (linha % 2 == 0 ? SUPERFICIE : ZEBRA));
                if (valor instanceof StatusAgendamento) {
                    setFont(FONTE_NEGRITO);
                    switch ((StatusAgendamento) valor) {
                        case CONCLUIDO:
                            setForeground(SUCESSO);
                            break;
                        case CANCELADO:
                            setForeground(PERIGO);
                            break;
                        default:
                            setForeground(PRIMARIA);
                    }
                }
                return this;
            }
        });
    }
}
