package br.com.petcare.repositorio;

import br.com.petcare.model.Agendamento;
import java.time.LocalDateTime;

public interface RepositorioAgendamento extends Repositorio<Agendamento> {

    /** Existe outro agendamento com status AGENDADO exatamente nesse horário? */
    boolean horarioOcupado(LocalDateTime dataHora, int ignorarId);
}
