package br.com.petcare.util;

import java.util.regex.Pattern;

/** Funções utilitárias de validação e formatação de dados de entrada. */
public final class Validador {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private Validador() {
    }

    public static boolean vazio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    public static String somenteDigitos(String texto) {
        return texto == null ? "" : texto.replaceAll("\\D", "");
    }

    /** Valida CPF pelo algoritmo oficial dos dígitos verificadores. */
    public static boolean cpfValido(String cpf) {
        String d = somenteDigitos(cpf);
        if (d.length() != 11 || d.matches("(\\d)\\1{10}")) {
            return false;
        }
        return digitoVerificador(d, 9) == d.charAt(9) - '0'
                && digitoVerificador(d, 10) == d.charAt(10) - '0';
    }

    private static int digitoVerificador(String d, int quantidade) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += (d.charAt(i) - '0') * (quantidade + 1 - i);
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }

    public static boolean emailValido(String email) {
        return email != null && EMAIL.matcher(email.trim()).matches();
    }

    /** 12345678909 -> 123.456.789-09 */
    public static String formatarCpf(String digitos) {
        String d = somenteDigitos(digitos);
        if (d.length() != 11) {
            return digitos == null ? "" : digitos;
        }
        return d.substring(0, 3) + "." + d.substring(3, 6) + "." + d.substring(6, 9) + "-" + d.substring(9);
    }

    /** 21987654321 -> (21) 98765-4321 ; 2133334444 -> (21) 3333-4444 */
    public static String formatarTelefone(String digitos) {
        String d = somenteDigitos(digitos);
        if (d.length() == 11) {
            return "(" + d.substring(0, 2) + ") " + d.substring(2, 7) + "-" + d.substring(7);
        }
        if (d.length() == 10) {
            return "(" + d.substring(0, 2) + ") " + d.substring(2, 6) + "-" + d.substring(6);
        }
        return digitos == null ? "" : digitos;
    }
}
