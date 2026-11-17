package br.com.petcare.model;

import br.com.petcare.excecao.PetCareException;
import br.com.petcare.util.Validador;

/** Animal atendido. Todo pet pertence a exatamente um cliente (tutor). */
public class Pet extends Entidade {

    private String nome;
    private String especie;
    private String raca;
    private Porte porte;
    private Cliente dono;

    public Pet() {
    }

    public Pet(String nome, String especie, String raca, Porte porte, Cliente dono) {
        setNome(nome);
        setEspecie(especie);
        setRaca(raca);
        setPorte(porte);
        setDono(dono);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (Validador.vazio(nome)) {
            throw new PetCareException("Informe o nome do pet.");
        }
        if (nome.trim().length() > 60) {
            throw new PetCareException("O nome do pet deve ter no máximo 60 caracteres.");
        }
        this.nome = nome.trim();
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        if (Validador.vazio(especie)) {
            throw new PetCareException("Informe a espécie do pet.");
        }
        this.especie = especie.trim();
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = Validador.vazio(raca) ? null : raca.trim();
    }

    public Porte getPorte() {
        return porte;
    }

    public void setPorte(Porte porte) {
        if (porte == null) {
            throw new PetCareException("Selecione o porte do pet.");
        }
        this.porte = porte;
    }

    public Cliente getDono() {
        return dono;
    }

    public void setDono(Cliente dono) {
        if (dono == null) {
            throw new PetCareException("Selecione o tutor do pet. Cadastre o cliente antes, se necessário.");
        }
        this.dono = dono;
    }

    @Override
    public String toString() {
        return nome + " (" + dono.getNome() + ")";
    }
}
