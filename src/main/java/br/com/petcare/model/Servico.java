package br.com.petcare.model;

import br.com.petcare.excecao.PetCareException;
import br.com.petcare.util.Validador;

/** Serviço oferecido pelo pet shop (banho, tosa, consulta etc.). */
public class Servico extends Entidade {

    private String nome;
    private String descricao;
    private double preco;
    private int duracaoMinutos;

    public Servico() {
    }

    public Servico(String nome, String descricao, double preco, int duracaoMinutos) {
        setNome(nome);
        setDescricao(descricao);
        setPreco(preco);
        setDuracaoMinutos(duracaoMinutos);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (Validador.vazio(nome)) {
            throw new PetCareException("Informe o nome do serviço.");
        }
        if (nome.trim().length() > 80) {
            throw new PetCareException("O nome do serviço deve ter no máximo 80 caracteres.");
        }
        this.nome = nome.trim();
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = Validador.vazio(descricao) ? null : descricao.trim();
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        if (preco <= 0) {
            throw new PetCareException("O preço do serviço deve ser maior que zero.");
        }
        this.preco = preco;
    }

    public int getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(int duracaoMinutos) {
        if (duracaoMinutos <= 0) {
            throw new PetCareException("A duração do serviço deve ser maior que zero.");
        }
        this.duracaoMinutos = duracaoMinutos;
    }

    @Override
    public String toString() {
        return nome;
    }
}
