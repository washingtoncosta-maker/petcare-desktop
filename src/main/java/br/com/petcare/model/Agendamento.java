package br.com.petcare.model;

import br.com.petcare.excecao.PetCareException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Reserva de um serviço para um pet em determinado dia e horário. */
public class Agendamento extends Entidade {

    public static final LocalTime ABERTURA = LocalTime.of(8, 0);
    public static final LocalTime ULTIMO_HORARIO = LocalTime.of(17, 30);
    public static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Pet pet;
    private Servico servico;
    private LocalDateTime dataHora;
    private StatusAgendamento status = StatusAgendamento.AGENDADO;
    private String observacoes;

    public Agendamento() {
    }

    public Agendamento(Pet pet, Servico servico, LocalDateTime dataHora, String observacoes) {
        setPet(pet);
        setServico(servico);
        setDataHora(dataHora);
        setObservacoes(observacoes);
    }

    /** Horários de atendimento: 08:00 às 17:30, de 30 em 30 minutos. */
    public static List<LocalTime> horariosDisponiveis() {
        List<LocalTime> horarios = new ArrayList<>();
        for (LocalTime t = ABERTURA; !t.isAfter(ULTIMO_HORARIO); t = t.plusMinutes(30)) {
            horarios.add(t);
        }
        return horarios;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        if (pet == null) {
            throw new PetCareException("Selecione o pet que será atendido.");
        }
        this.pet = pet;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        if (servico == null) {
            throw new PetCareException("Selecione o serviço desejado.");
        }
        this.servico = servico;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        if (dataHora == null) {
            throw new PetCareException("Informe a data e o horário do atendimento.");
        }
        LocalTime t = dataHora.toLocalTime();
        if (t.isBefore(ABERTURA) || t.isAfter(ULTIMO_HORARIO) || t.getMinute() % 30 != 0) {
            throw new PetCareException("Horário fora do expediente (08:00 às 17:30, de 30 em 30 minutos).");
        }
        this.dataHora = dataHora.withSecond(0).withNano(0);
    }

    public StatusAgendamento getStatus() {
        return status;
    }

    public void setStatus(StatusAgendamento status) {
        this.status = status == null ? StatusAgendamento.AGENDADO : status;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        if (observacoes != null && observacoes.trim().length() > 255) {
            throw new PetCareException("As observações devem ter no máximo 255 caracteres.");
        }
        this.observacoes = (observacoes == null || observacoes.trim().isEmpty()) ? null : observacoes.trim();
    }

    public void concluir() {
        exigirAgendado("concluídos");
        status = StatusAgendamento.CONCLUIDO;
    }

    public void cancelar() {
        exigirAgendado("cancelados");
        status = StatusAgendamento.CANCELADO;
    }

    private void exigirAgendado(String acao) {
        if (status != StatusAgendamento.AGENDADO) {
            throw new PetCareException("Somente agendamentos com status \"Agendado\" podem ser " + acao + ".");
        }
    }

    public double getValor() {
        return servico.getPreco();
    }

    @Override
    public String toString() {
        return dataHora.format(FORMATO) + " - " + pet.getNome() + " - " + servico.getNome();
    }
}
