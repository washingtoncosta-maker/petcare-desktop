package br.com.petcare.visao;

import br.com.petcare.excecao.PetCareException;
import br.com.petcare.model.Servico;
import br.com.petcare.servico.PetCareService;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

/** Catálogo de serviços (RF03). */
public class PainelServicos extends PainelCrud<Servico> {

    private static final long serialVersionUID = 1L;
    private static final Locale BR = new Locale("pt", "BR");

    private final JTextField txtNome = new JTextField();
    private final JTextField txtDescricao = new JTextField();
    private final JTextField txtPreco = new JTextField();
    private final JSpinner spnDuracao = new JSpinner(new SpinnerNumberModel(30, 15, 480, 15));

    public PainelServicos(PetCareService negocio) {
        super(negocio);
        txtPreco.setToolTipText("Valor em reais. Ex.: 45,90");
        montar();
    }

    @Override
    protected String titulo() {
        return "Serviços";
    }

    @Override
    protected String[] colunas() {
        return new String[]{"Cód.", "Serviço", "Descrição", "Duração", "Preço"};
    }

    @Override
    protected Object[] linha(Servico s) {
        return new Object[]{s.getId(), s.getNome(), s.getDescricao() == null ? "" : s.getDescricao(),
            s.getDuracaoMinutos() + " min", NumberFormat.getCurrencyInstance(BR).format(s.getPreco())};
    }

    @Override
    protected String textoBusca(Servico s) {
        return s.getNome() + " " + (s.getDescricao() == null ? "" : s.getDescricao());
    }

    @Override
    protected List<Servico> carregar() {
        return negocio.listarServicos();
    }

    @Override
    protected void persistir(Servico s) {
        negocio.salvarServico(s);
    }

    @Override
    protected void remover(Servico s) {
        negocio.excluirServico(s);
    }

    @Override
    protected JComponent criarFormulario() {
        Formulario f = new Formulario();
        f.campo("Nome do serviço *", txtNome, 'N', 0, 0, 2);
        f.campo("Preço (R$) *", txtPreco, 'R', 0, 2, 1);
        f.campo("Descrição", txtDescricao, 'D', 1, 0, 2);
        f.campo("Duração (min) *", spnDuracao, 'U', 1, 2, 1);
        return f;
    }

    @Override
    protected void preencherFormulario(Servico s) {
        txtNome.setText(s.getNome());
        txtDescricao.setText(s.getDescricao() == null ? "" : s.getDescricao());
        txtPreco.setText(String.format(BR, "%.2f", s.getPreco()));
        spnDuracao.setValue(s.getDuracaoMinutos());
    }

    @Override
    protected void limparFormulario() {
        txtNome.setText("");
        txtDescricao.setText("");
        txtPreco.setText("");
        spnDuracao.setValue(30);
    }

    @Override
    protected Servico lerFormulario(int id) {
        Servico s = new Servico();
        s.setId(id);
        s.setNome(txtNome.getText());
        s.setDescricao(txtDescricao.getText());
        s.setPreco(lerPreco());
        s.setDuracaoMinutos((Integer) spnDuracao.getValue());
        return s;
    }

    private double lerPreco() {
        String texto = txtPreco.getText().trim().replace("R$", "").replace(" ", "");
        if (texto.isEmpty()) {
            throw new PetCareException("Informe o preço do serviço.");
        }
        if (texto.contains(",")) {
            texto = texto.replace(".", "").replace(",", ".");
        }
        try {
            return Double.parseDouble(texto);
        } catch (NumberFormatException ex) {
            throw new PetCareException("Preço inválido. Use apenas números, por exemplo: 45,90");
        }
    }

    @Override
    protected JComponent campoInicial() {
        return txtNome;
    }
}
