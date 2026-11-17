package br.com.petcare.model;

/** Classe base de todos os registros persistidos. Id 0 = ainda não gravado. */
public abstract class Entidade {

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isNovo() {
        return id == 0;
    }
}
