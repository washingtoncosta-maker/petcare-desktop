package br.com.petcare.model;

public enum StatusAgendamento {
    AGENDADO("Agendado"), CONCLUIDO("Concluído"), CANCELADO("Cancelado");

    private final String rotulo;

    StatusAgendamento(String rotulo) {
        this.rotulo = rotulo;
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
