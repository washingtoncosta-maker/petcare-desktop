package br.com.petcare.visao;

import br.com.petcare.excecao.PetCareException;
import br.com.petcare.model.Agendamento;
import br.com.petcare.model.Pet;
import br.com.petcare.model.Servico;
import br.com.petcare.model.StatusAgendamento;
import br.com.petcare.servico.PetCareService;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;

/** Agenda de atendimentos (RF05), com conclusão e cancelamento (RF06). */
public class PainelAgendamentos extends PainelCrud<Agendamento> {

    private static final long serialVersionUID = 1L;
    private static final Locale BR = new Locale("pt", "BR");

    private final JComboBox<Pet> cmbPet = new JComboBox<>();
    private final JComboBox<Servico> cmbServico = new JComboBox<>();
    private final JSpinner spnData = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
    private final JComboBox<LocalTime> cmbHorario
            = new JComboBox<>(Agendamento.horariosDisponiveis().toArray(new LocalTime[0]));
    private final JTextField txtObservacoes = new JTextField();
    private final JButton btnConcluir = new JButton("Concluir atendimento");
    private final JButton btnCancelar = new JButton("Cancelar agendamento");

    public PainelAgendamentos(PetCareService negocio) {
        super(negocio);
        spnData.setEditor(new JSpinner.DateEditor(spnData, "dd/MM/yyyy"));
        montar();
    }

    @Override
    protected String titulo() {
        return "Agendamentos";
    }

    @Override
    protected String[] colunas() {
        return new String[]{"Cód.", "Data/hora", "Pet", "Tutor", "Serviço", "Valor", "Status"};
    }

    @Override
    protected Object[] linha(Agendamento a) {
        return new Object[]{a.getId(), a.getDataHora().format(Agendamento.FORMATO), a.getPet().getNome(),
            a.getPet().getDono().getNome(), a.getServico().getNome(),
            NumberFormat.getCurrencyInstance(BR).format(a.getValor()), a.getStatus()};
    }

    @Override
    protected String textoBusca(Agendamento a) {
        return a.getDataHora().format(Agendamento.FORMATO) + " " + a.getPet().getNome() + " "
                + a.getPet().getDono().getNome() + " " + a.getServico().getNome() + " " + a.getStatus();
    }

    @Override
    protected void prepararCampos() {
        cmbPet.removeAllItems();
        for (Pet p : negocio.listarPets()) {
            cmbPet.addItem(p);
        }
        cmbServico.removeAllItems();
        for (Servico s : negocio.listarServicos()) {
            cmbServico.addItem(s);
        }
    }

    @Override
    protected List<Agendamento> carregar() {
        List<Agendamento> lista = negocio.listarAgendamentos();
        lista.sort((x, y) -> x.getDataHora().compareTo(y.getDataHora()));
        return lista;
    }

    @Override
    protected void persistir(Agendamento a) {
        negocio.salvarAgendamento(a);
    }

    @Override
    protected void remover(Agendamento a) {
        negocio.excluirAgendamento(a);
    }

    @Override
    protected JComponent criarFormulario() {
        Formulario f = new Formulario();
        f.campo("Pet (tutor) *", cmbPet, 'E', 0, 0, 2);
        f.campo("Serviço *", cmbServico, 'V', 0, 2, 1);
        f.campo("Data *", spnData, 'D', 1, 0, 1);
        f.campo("Horário *", cmbHorario, 'H', 1, 1, 1);
        f.campo("Observações", txtObservacoes, 'O', 1, 2, 1);
        return f;
    }

    @Override
    protected void adicionarAcoes() {
        Tema.botaoSucesso(btnConcluir);
        Tema.botaoSecundario(btnCancelar);
        btnConcluir.setToolTipText("Marca o atendimento selecionado como concluído");
        btnCancelar.setToolTipText("Cancela o agendamento selecionado");
        btnConcluir.addActionListener(e -> concluir());
        btnCancelar.addActionListener(e -> cancelar());
        barraAcoes.add(btnConcluir);
        barraAcoes.add(btnCancelar);
    }

    private void concluir() {
        Agendamento a = getSelecionado();
        if (a == null) {
            avisar("Selecione um agendamento na tabela.");
            return;
        }
        try {
            negocio.concluirAgendamento(a);
            atualizar();
            definirStatus("Atendimento concluído.");
        } catch (PetCareException ex) {
            avisar(ex.getMessage());
        }
    }

    private void cancelar() {
        Agendamento a = getSelecionado();
        if (a == null) {
            avisar("Selecione um agendamento na tabela.");
            return;
        }
        try {
            negocio.cancelarAgendamento(a);
            atualizar();
            definirStatus("Agendamento cancelado.");
        } catch (PetCareException ex) {
            avisar(ex.getMessage());
        }
    }

    @Override
    protected void preencherFormulario(Agendamento a) {
        selecionarPorId(cmbPet, a.getPet().getId());
        selecionarPorId(cmbServico, a.getServico().getId());
        spnData.setValue(Date.from(a.getDataHora().toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        cmbHorario.setSelectedItem(a.getDataHora().toLocalTime());
        txtObservacoes.setText(a.getObservacoes() == null ? "" : a.getObservacoes());
    }

    @Override
    protected void limparFormulario() {
        if (cmbPet.getItemCount() > 0) {
            cmbPet.setSelectedIndex(0);
        }
        if (cmbServico.getItemCount() > 0) {
            cmbServico.setSelectedIndex(0);
        }
        spnData.setValue(new Date());
        cmbHorario.setSelectedIndex(0);
        txtObservacoes.setText("");
    }

    @Override
    protected Agendamento lerFormulario(int id) {
        Agendamento atual = getSelecionado();
        if (atual != null && atual.getStatus() != StatusAgendamento.AGENDADO) {
            throw new PetCareException("Agendamentos concluídos ou cancelados não podem ser alterados.");
        }
        LocalTime hora = (LocalTime) cmbHorario.getSelectedItem();
        if (hora == null) {
            throw new PetCareException("Selecione o horário do atendimento.");
        }
        LocalDate dia = ((Date) spnData.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Agendamento a = new Agendamento();
        a.setId(id);
        a.setPet((Pet) cmbPet.getSelectedItem());
        a.setServico((Servico) cmbServico.getSelectedItem());
        a.setDataHora(LocalDateTime.of(dia, hora));
        a.setObservacoes(txtObservacoes.getText());
        if (atual == null && a.getDataHora().isBefore(LocalDateTime.now())) {
            throw new PetCareException("Não é possível agendar em uma data/horário que já passou.");
        }
        return a;
    }

    @Override
    protected JComponent campoInicial() {
        return cmbPet;
    }
}
