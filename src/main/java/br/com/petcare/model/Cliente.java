package br.com.petcare.model;

import br.com.petcare.excecao.PetCareException;
import br.com.petcare.util.Validador;

/** Tutor (dono) dos animais atendidos no pet shop. */
public class Cliente extends Entidade {

    private String nome;
    private String cpf;       // somente dígitos
    private String telefone;  // somente dígitos (DDD + número)
    private String email;     // opcional

    public Cliente() {
    }

    public Cliente(String nome, String cpf, String telefone, String email) {
        setNome(nome);
        setCpf(cpf);
        setTelefone(telefone);
        setEmail(email);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (Validador.vazio(nome)) {
            throw new PetCareException("Informe o nome do cliente.");
        }
        if (nome.trim().length() > 100) {
            throw new PetCareException("O nome do cliente deve ter no máximo 100 caracteres.");
        }
        this.nome = nome.trim();
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        if (!Validador.cpfValido(cpf)) {
            throw new PetCareException("CPF inválido. Confira os 11 dígitos informados.");
        }
        this.cpf = Validador.somenteDigitos(cpf);
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        String d = Validador.somenteDigitos(telefone);
        if (d.length() != 10 && d.length() != 11) {
            throw new PetCareException("Telefone inválido. Informe DDD + número (ex.: 21 98765-4321).");
        }
        this.telefone = d;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (Validador.vazio(email)) {
            this.email = null;
            return;
        }
        if (!Validador.emailValido(email)) {
            throw new PetCareException("E-mail inválido. Exemplo: nome@dominio.com");
        }
        this.email = email.trim();
    }

    public String getCpfFormatado() {
        return Validador.formatarCpf(cpf);
    }

    public String getTelefoneFormatado() {
        return Validador.formatarTelefone(telefone);
    }

    @Override
    public String toString() {
        return nome;
    }
}
