package br.com.petcare.excecao;

/**
 * Exceção única do sistema. Representa tanto violações de regras de negócio
 * (ex.: CPF inválido) quanto falhas de persistência (ex.: banco indisponível).
 * A camada de visão apenas exibe a mensagem ao usuário.
 */
public class PetCareException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PetCareException(String mensagem) {
        super(mensagem);
    }

    public PetCareException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
