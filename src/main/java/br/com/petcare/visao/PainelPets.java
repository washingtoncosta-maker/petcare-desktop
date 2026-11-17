package br.com.petcare.visao;

import br.com.petcare.model.Cliente;
import br.com.petcare.model.Pet;
import br.com.petcare.model.Porte;
import br.com.petcare.servico.PetCareService;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

/** Cadastro de pets (RF02). Cada pet pertence a um cliente. */
public class PainelPets extends PainelCrud<Pet> {

    private static final long serialVersionUID = 1L;

    private final JTextField txtNome = new JTextField();
    private final JComboBox<String> cmbEspecie = new JComboBox<>(new String[]{"Cão", "Gato", "Ave", "Roedor", "Outro"});
    private final JTextField txtRaca = new JTextField();
    private final JComboBox<Porte> cmbPorte = new JComboBox<>(Porte.values());
    private final JComboBox<Cliente> cmbDono = new JComboBox<>();

    public PainelPets(PetCareService negocio) {
        super(negocio);
        montar();
    }

    @Override
    protected String titulo() {
        return "Pets";
    }

    @Override
    protected String[] colunas() {
        return new String[]{"Cód.", "Nome", "Espécie", "Raça", "Porte", "Tutor"};
    }

    @Override
    protected Object[] linha(Pet p) {
        return new Object[]{p.getId(), p.getNome(), p.getEspecie(), p.getRaca() == null ? "" : p.getRaca(),
            p.getPorte(), p.getDono().getNome()};
    }

    @Override
    protected String textoBusca(Pet p) {
        return p.getNome() + " " + p.getEspecie() + " " + (p.getRaca() == null ? "" : p.getRaca())
                + " " + p.getPorte() + " " + p.getDono().getNome();
    }

    @Override
    protected void prepararCampos() {
        cmbDono.removeAllItems();
        for (Cliente c : negocio.listarClientes()) {
            cmbDono.addItem(c);
        }
    }

    @Override
    protected List<Pet> carregar() {
        return negocio.listarPets();
    }

    @Override
    protected void persistir(Pet p) {
        negocio.salvarPet(p);
    }

    @Override
    protected void remover(Pet p) {
        negocio.excluirPet(p);
    }

    @Override
    protected JComponent criarFormulario() {
        Formulario f = new Formulario();
        f.campo("Nome do pet *", txtNome, 'N', 0, 0, 1);
        f.campo("Espécie *", cmbEspecie, 'E', 0, 1, 1);
        f.campo("Raça", txtRaca, 'R', 0, 2, 1);
        f.campo("Porte *", cmbPorte, 'O', 1, 0, 1);
        f.campo("Tutor (cliente) *", cmbDono, 'T', 1, 1, 2);
        return f;
    }

    @Override
    protected void preencherFormulario(Pet p) {
        txtNome.setText(p.getNome());
        cmbEspecie.setSelectedItem(p.getEspecie());
        txtRaca.setText(p.getRaca() == null ? "" : p.getRaca());
        cmbPorte.setSelectedItem(p.getPorte());
        selecionarPorId(cmbDono, p.getDono().getId());
    }

    @Override
    protected void limparFormulario() {
        txtNome.setText("");
        cmbEspecie.setSelectedIndex(0);
        txtRaca.setText("");
        cmbPorte.setSelectedIndex(0);
        if (cmbDono.getItemCount() > 0) {
            cmbDono.setSelectedIndex(0);
        }
    }

    @Override
    protected Pet lerFormulario(int id) {
        Pet p = new Pet();
        p.setId(id);
        p.setNome(txtNome.getText());
        p.setEspecie((String) cmbEspecie.getSelectedItem());
        p.setRaca(txtRaca.getText());
        p.setPorte((Porte) cmbPorte.getSelectedItem());
        p.setDono((Cliente) cmbDono.getSelectedItem());
        return p;
    }

    @Override
    protected JComponent campoInicial() {
        return txtNome;
    }
}
