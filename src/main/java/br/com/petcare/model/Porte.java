package br.com.petcare.model;

public enum Porte {
    PEQUENO("Pequeno"), MEDIO("Médio"), GRANDE("Grande");

    private final String rotulo;

    Porte(String rotulo) {
        this.rotulo = rotulo;
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
