package com.taskflow.util;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.Control;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.Severity;

/**
 * Clase para manejar validación de formularios con retroalimentación por campo
 */
public class ValidadorFormulario {

    private Map<String, String> errores;
    private ValidationSupport validationSupport;
    private Map<String, Control> controles;

    public ValidadorFormulario() {
        this.errores = new HashMap<>();
        this.controles = new HashMap<>();
        this.validationSupport = new ValidationSupport();
        this.validationSupport.setErrorDecorationEnabled(true);
    }

    /**
     * Valida que un campo no esté vacío
     */
    public void validarNoVacio(String nombreCampo, String valor, String mensaje) {
        if (Validaciones.esTextoVacio(valor)) {
            agregarError(nombreCampo, mensaje);
        } else {
            removerError(nombreCampo);
        }
    }

    /**
     * Valida un nombre
     */
    public void validarNombre(String nombreCampo, String valor) {
        if (!Validaciones.esNombreValido(valor)) {
            agregarError(nombreCampo, "Debe tener entre 3 y 100 caracteres");
        } else {
            removerError(nombreCampo);
        }
    }

    /**
     * Valida un email
     */
    public void validarEmail(String nombreCampo, String valor) {
        if (Validaciones.esTextoVacio(valor)) {
            agregarError(nombreCampo, "Es obligatorio");
        } else if (!Validaciones.esEmailValido(valor)) {
            agregarError(nombreCampo, "Formato inválido (ej: usuario@dominio.com)");
        } else {
            removerError(nombreCampo);
        }
    }

    /**
     * Valida un teléfono (opcional)
     */
    public void validarTelefono(String nombreCampo, String valor) {
        if (!Validaciones.esTelefonoValido(valor)) {
            agregarError(nombreCampo, "Debe tener 9-15 dígitos (opcional)");
        } else {
            removerError(nombreCampo);
        }
    }

    /**
     * Valida una contraseña
     */
    public void validarPassword(String nombreCampo, String valor, boolean esObligatorio) {
        if (esObligatorio) {
            if (!Validaciones.esContraseñaValida(valor)) {
                agregarError(nombreCampo, "Mínimo 6 caracteres");
            } else {
                removerError(nombreCampo);
            }
        } else {
            // Opcional: solo validar si hay valor
            if (!valor.isEmpty() && !Validaciones.esContraseñaValida(valor)) {
                agregarError(nombreCampo, "Mínimo 6 caracteres");
            } else {
                removerError(nombreCampo);
            }
        }
    }

    /**
     * Valida un rol
     */
    public void validarRol(String nombreCampo, String valor) {
        if (!Validaciones.esRolValido(valor)) {
            agregarError(nombreCampo, "Seleccione admin o empleado");
        } else {
            removerError(nombreCampo);
        }
    }

    /**
     * Valida un número positivo
     */
    public void validarNumeroPositivo(String nombreCampo, String valor, boolean esObligatorio) {
        if (esObligatorio) {
            if (!Validaciones.esNumeroPositivo(valor)) {
                agregarError(nombreCampo, "Debe ser un número positivo");
            } else {
                removerError(nombreCampo);
            }
        } else {
            // Opcional: solo validar si hay valor
            if (!Validaciones.esTextoVacio(valor) && !Validaciones.esNumeroPositivo(valor)) {
                agregarError(nombreCampo, "Debe ser un número positivo (opcional)");
            } else {
                removerError(nombreCampo);
            }
        }
    }

    /**
     * Valida con una condición personalizada
     */
    public void validarCondicion(String nombreCampo, boolean condicion, String mensajeError) {
        if (!condicion) {
            agregarError(nombreCampo, mensajeError);
        } else {
            removerError(nombreCampo);
        }
    }

    /**
     * Agrega un error para un campo
     */
    public void agregarError(String nombreCampo, String error) {
        errores.put(nombreCampo, error);
    }

    /**
     * Remueve un error para un campo
     */
    public void removerError(String nombreCampo) {
        errores.remove(nombreCampo);
    }

    /**
     * Verifica si hay errores
     */
    public boolean tieneErrores() {
        return !errores.isEmpty();
    }

    /**
     * Obtiene todos los errores formateados como string
     */
    public String obtenerErroresFormateados() {
        if (errores.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : errores.entrySet()) {
            sb.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Obtiene el error para un campo específico
     */
    public String obtenerError(String nombreCampo) {
        return errores.get(nombreCampo);
    }

    /**
     * Obtiene todos los errores como mapa
     */
    public Map<String, String> obtenerErrores() {
        return new HashMap<>(errores);
    }

    /**
     * Limpia todos los errores
     */
    public void limpiar() {
        errores.clear();
        controles.clear();
    }

    /**
     * Registra un control para validación visual
     */
    public void registrarControl(String nombreCampo, Control control) {
        controles.put(nombreCampo, control);
    }

    /**
     * Valida el formulario y muestra errores visuales en los controles
     */
    public void validarVisualmente() {
        // Limpiar decoraciones previas de TODOS los controles
        for (Control control : controles.values()) {
            control.setStyle("");
            control.getStyleClass().remove("has-error");
        }

        // Crear nuevo ValidationSupport
        validationSupport = new ValidationSupport();
        validationSupport.setErrorDecorationEnabled(true);

        // Registrar validadores para TODOS los campos
        for (Map.Entry<String, Control> entry : controles.entrySet()) {
            String nombreCampo = entry.getKey();
            Control control = entry.getValue();
            String error = errores.get(nombreCampo);

            if (error != null) {
                // Hay error en este campo - registrar validador que siempre falla
                validationSupport.registerValidator(control, false,
                    Validator.createPredicateValidator(
                        v -> false,
                        error,
                        Severity.ERROR
                    )
                );
            } else {
                // No hay error - registrar validador que siempre pasa
                validationSupport.registerValidator(control, true,
                    Validator.createPredicateValidator(
                        v -> true,
                        "",
                        Severity.ERROR
                    )
                );
            }
        }

        // Inicializar decoraciones
        validationSupport.initInitialDecoration();
    }

    /**
     * Obtiene el ValidationSupport para control externo si es necesario
     */
    public ValidationSupport getValidationSupport() {
        return validationSupport;
    }
}
