package com.taskflow.util;

import java.util.regex.Pattern;

/**
 * Clase utilitaria con metodos de validacion de campos
 */
public class Validaciones {

    // Patrones regex compilados para mejor rendimiento
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern NOMBRE_PATTERN = Pattern.compile(
            "^[a-zA-Záéíóúñ\\s]{3,100}$"
    );

    /**
     * Valida que un texto no esté vacío
     */
    public static boolean esTextoVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    /**
     * Valida nombre completo (mínimo 3 caracteres, máximo 100)
     */
    public static boolean esNombreValido(String nombre) {
        if (esTextoVacio(nombre)) {
            return false;
        }
        String nombre_trim = nombre.trim();
        return nombre_trim.length() >= 3 && nombre_trim.length() <= 100
                && NOMBRE_PATTERN.matcher(nombre_trim).matches();
    }

    /**
     * Valida formato de email
     */
    public static boolean esEmailValido(String email) {
        if (esTextoVacio(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Valida formato de teléfono (9-15 dígitos, puede incluir espacios, guiones o +)
     * NOTA: Este método devuelve true si está vacío porque el teléfono es opcional
     */
    public static boolean esTelefonoValido(String telefono) {
        if (esTextoVacio(telefono)) {
            return true; // Teléfono es opcional
        }
        // Eliminar espacios, guiones y +
        String telefonoLimpio = telefono.trim().replaceAll("[\\s+-]", "");
        // Validar que tenga entre 9 y 15 dígitos
        return telefonoLimpio.matches("^[0-9]{9,15}$");
    }

    /**
     * Valida que una contraseña cumpla requisitos mínimos
     * (mínimo 6 caracteres, sin espacios en blanco al inicio/final)
     */
    public static boolean esContraseñaValida(String password) {
        if (esTextoVacio(password)) {
            return false;
        }
        String password_trim = password.trim();
        return password_trim.length() >= 6
                && password_trim.length() <= 128;
    }

    /**
     * Valida que un rol sea válido
     */
    public static boolean esRolValido(String rol) {
        if (esTextoVacio(rol)) {
            return false;
        }
        return rol.trim().equals("admin") || rol.trim().equals("empleado");
    }

    /**
     * Valida que un número sea positivo
     */
    public static boolean esNumeroPositivo(String numero) {
        if (esTextoVacio(numero)) {
            return false;
        }
        try {
            int valor = Integer.parseInt(numero.trim());
            return valor > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida que un número decimal sea positivo
     */
    public static boolean esDecimalPositivo(String numero) {
        if (esTextoVacio(numero)) {
            return false;
        }
        try {
            double valor = Double.parseDouble(numero.trim());
            return valor > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida longitud mínima de texto
     */
    public static boolean longitudMinima(String texto, int minimo) {
        if (esTextoVacio(texto)) {
            return false;
        }
        return texto.trim().length() >= minimo;
    }

    /**
     * Valida longitud máxima de texto
     */
    public static boolean longitudMaxima(String texto, int maximo) {
        if (esTextoVacio(texto)) {
            return true;
        }
        return texto.trim().length() <= maximo;
    }

    /**
     * Valida que un texto esté en el rango de longitud especificado
     */
    public static boolean longitudEnRango(String texto, int minimo, int maximo) {
        if (esTextoVacio(texto)) {
            return false;
        }
        int length = texto.trim().length();
        return length >= minimo && length <= maximo;
    }

    /**
     * Valida que un número esté en un rango
     */
    public static boolean numeroEnRango(String numero, int minimo, int maximo) {
        try {
            int valor = Integer.parseInt(numero.trim());
            return valor >= minimo && valor <= maximo;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
