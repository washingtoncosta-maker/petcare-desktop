package br.com.petcare.visao;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

/** Painel de formulário com rótulo acima do campo, mnemônico (Alt+letra) e nome acessível. */
public class Formulario extends JPanel {

    private static final long serialVersionUID = 1L;

    public Formulario() {
        super(new GridBagLayout());
        setOpaque(false);
    }

    /**
     * @param rotulo    texto exibido acima do campo
     * @param campo     componente de entrada
     * @param mnemonico letra de atalho (Alt+letra) ou 0
     * @param linha     linha lógica do formulário
     * @param coluna    coluna inicial
     * @param largura   quantidade de colunas ocupadas
     */
    public void campo(String rotulo, JComponent campo, char mnemonico, int linha, int coluna, int largura) {
        JLabel texto = new JLabel(rotulo);
        texto.setForeground(Tema.TEXTO_SECUNDARIO);
        texto.setFont(Tema.FONTE_NEGRITO);
        texto.setLabelFor(campo);
        if (mnemonico != 0) {
            texto.setDisplayedMnemonic(mnemonico);
        }
        campo.getAccessibleContext().setAccessibleName(rotulo);
        if (campo instanceof JTextComponent) {
            Tema.campo((JTextComponent) campo);
        }
        Dimension d = campo.getPreferredSize();
        campo.setPreferredSize(new Dimension(d.width, Math.max(d.height, 34)));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = coluna;
        g.gridy = linha * 2;
        g.gridwidth = largura;
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(0, 0, 3, 14);
        add(texto, g);

        g = new GridBagConstraints();
        g.gridx = coluna;
        g.gridy = linha * 2 + 1;
        g.gridwidth = largura;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = largura;
        g.insets = new Insets(0, 0, 10, 14);
        add(campo, g);
    }
}
