package com.taskflow.util;

/**
 * Clase utilitaria con metodos de validacion de campos
 */
public class Validaciones {

    /**
     * Valida que un texto no este vacio
     */
    public static boolean esTextoVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    /**
     * Valida formato de email
     */
    public static boolean esEmailValido(String email) {
        if (esTextoVacio(email)) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Valida formato de telefono (9-15 digitos)
     */
    public static boolean esTelefonoValido(String telefono) {
        if (esTextoVacio(telefono)) {
            return false;
        }
        return telefono.matches("^[0-9]{9,15}$");
    }

    /**
     * Valida que un numero sea positivo
     */
    public static boolean esNumeroPositivo(String numero) {
        try {
            int valor = Integer.parseInt(numero);
            return valor > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida que un numero decimal sea positivo
     */
    public static boolean esDecimalPositivo(String numero) {
        try {
            double valor = Double.parseDouble(numero);
            return valor > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida longitud minima de texto
     */
    public static boolean longitudMinima(String texto, int minimo) {
        if (esTextoVacio(texto)) {
            return false;
        }
        return texto.trim().length() >= minimo;
    }

    /**
     * Valida longitud maxima de texto
     */
    public static boolean longitudMaxima(String texto, int maximo) {
        if (esTextoVacio(texto)) {
            return true;
        }
        return texto.trim().length() <= maximo;
    }
}
