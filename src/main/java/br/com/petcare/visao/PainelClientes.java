package br.com.petcare.visao;

import br.com.petcare.model.Cliente;
import br.com.petcare.servico.PetCareService;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JTextField;

/** Cadastro de clientes (RF01). */
public class PainelClientes extends PainelCrud<Cliente> {

    private static final long serialVersionUID = 1L;

    private final JTextField txtNome = new JTextField();
    private final JTextField txtCpf = new JTextField();
    private final JTextField txtTelefone = new JTextField();
    private final JTextField txtEmail = new JTextField();

    public PainelClientes(PetCareService negocio) {
        super(negocio);
        txtCpf.setToolTipText("Somente números ou no formato 000.000.000-00");
        txtTelefone.setToolTipText("DDD + número. Ex.: (21) 98765-4321");
        montar();
    }

    @Override
    protected String titulo() {
        return "Clientes";
    }

    @Override
    protected String[] colunas() {
        return new String[]{"Cód.", "Nome", "CPF", "Telefone", "E-mail"};
    }

    @Override
    protected Object[] linha(Cliente c) {
        return new Object[]{c.getId(), c.getNome(), c.getCpfFormatado(), c.getTelefoneFormatado(),
            c.getEmail() == null ? "" : c.getEmail()};
    }

    @Override
    protected String textoBusca(Cliente c) {
        return c.getNome() + " " + c.getCpfFormatado() + " " + c.getCpf() + " " + c.getTelefoneFormatado()
                + " " + (c.getEmail() == null ? "" : c.getEmail());
    }

    @Override
    protected List<Cliente> carregar() {
        return negocio.listarClientes();
    }

    @Override
    protected void persistir(Cliente c) {
        negocio.salvarCliente(c);
    }

    @Override
    protected void remover(Cliente c) {
        negocio.excluirCliente(c);
    }

    @Override
    protected JComponent criarFormulario() {
        Formulario f = new Formulario();
        f.campo("Nome completo *", txtNome, 'N', 0, 0, 2);
        f.campo("CPF *", txtCpf, 'F', 0, 2, 1);
        f.campo("Telefone *", txtTelefone, 'T', 1, 0, 1);
        f.campo("E-mail", txtEmail, 'E', 1, 1, 2);
        return f;
    }

    @Override
    protected void preencherFormulario(Cliente c) {
        txtNome.setText(c.getNome());
        txtCpf.setText(c.getCpfFormatado());
        txtTelefone.setText(c.getTelefoneFormatado());
        txtEmail.setText(c.getEmail() == null ? "" : c.getEmail());
    }

    @Override
    protected void limparFormulario() {
        txtNome.setText("");
        txtCpf.setText("");
        txtTelefone.setText("");
        txtEmail.setText("");
    }

    @Override
    protected Cliente lerFormulario(int id) {
        Cliente c = new Cliente();
        c.setId(id);
        c.setNome(txtNome.getText());
        c.setCpf(txtCpf.getText());
        c.setTelefone(txtTelefone.getText());
        c.setEmail(txtEmail.getText());
        return c;
    }

    @Override
    protected JComponent campoInicial() {
        return txtNome;
    }
}
