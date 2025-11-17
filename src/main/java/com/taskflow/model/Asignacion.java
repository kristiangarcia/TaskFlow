package com.taskflow.model;

import javafx.beans.property.*;
import java.time.LocalDate;

/**
 * Modelo de Asignación que representa la tabla 'asignaciones' en Supabase
 * Relación M:N entre usuarios y tareas
 */
public class Asignacion {
    private final IntegerProperty idAsignacion;
    private final IntegerProperty tareaId;
    private final IntegerProperty usuarioId;
    private final StringProperty rolAsignacion;
    private final DoubleProperty horasAsignadas;
    private final BooleanProperty completado;
    private final StringProperty notas;
    private final ObjectProperty<LocalDate> fechaAsignacion;

    /**
     * Constructor completo
     */
    public Asignacion(int idAsignacion, int tareaId, int usuarioId, String rolAsignacion,
                      double horasAsignadas, boolean completado, String notas, LocalDate fechaAsignacion) {
        this.idAsignacion = new SimpleIntegerProperty(idAsignacion);
        this.tareaId = new SimpleIntegerProperty(tareaId);
        this.usuarioId = new SimpleIntegerProperty(usuarioId);
        this.rolAsignacion = new SimpleStringProperty(rolAsignacion);
        this.horasAsignadas = new SimpleDoubleProperty(horasAsignadas);
        this.completado = new SimpleBooleanProperty(completado);
        this.notas = new SimpleStringProperty(notas);
        this.fechaAsignacion = new SimpleObjectProperty<>(fechaAsignacion);
    }

    /**
     * Constructor sin notas y fecha (usa valores por defecto)
     */
    public Asignacion(int idAsignacion, int tareaId, int usuarioId, String rolAsignacion,
                      double horasAsignadas, boolean completado) {
        this(idAsignacion, tareaId, usuarioId, rolAsignacion, horasAsignadas, completado, null, LocalDate.now());
    }

    /**
     * Constructor simplificado para nuevas asignaciones
     */
    public Asignacion(int idAsignacion, int tareaId, int usuarioId, String rolAsignacion, double horasAsignadas) {
        this(idAsignacion, tareaId, usuarioId, rolAsignacion, horasAsignadas, false, null, LocalDate.now());
    }

    // ==================== Getters de Properties ====================
    public IntegerProperty idAsignacionProperty() { return idAsignacion; }
    public IntegerProperty tareaIdProperty() { return tareaId; }
    public IntegerProperty usuarioIdProperty() { return usuarioId; }
    public StringProperty rolAsignacionProperty() { return rolAsignacion; }
    public DoubleProperty horasAsignadasProperty() { return horasAsignadas; }
    public BooleanProperty completadoProperty() { return completado; }
    public StringProperty notasProperty() { return notas; }
    public ObjectProperty<LocalDate> fechaAsignacionProperty() { return fechaAsignacion; }

    // ==================== Getters de valores ====================
    public int getIdAsignacion() { return idAsignacion.get(); }
    public int getTareaId() { return tareaId.get(); }
    public int getUsuarioId() { return usuarioId.get(); }
    public String getRolAsignacion() { return rolAsignacion.get(); }
    public double getHorasAsignadas() { return horasAsignadas.get(); }
    public boolean isCompletado() { return completado.get(); }
    public String getNotas() { return notas.get(); }
    public LocalDate getFechaAsignacion() { return fechaAsignacion.get(); }

    // ==================== Setters de valores ====================
    public void setIdAsignacion(int idAsignacion) { this.idAsignacion.set(idAsignacion); }
    public void setTareaId(int tareaId) { this.tareaId.set(tareaId); }
    public void setUsuarioId(int usuarioId) { this.usuarioId.set(usuarioId); }
    public void setRolAsignacion(String rolAsignacion) { this.rolAsignacion.set(rolAsignacion); }
    public void setHorasAsignadas(double horasAsignadas) { this.horasAsignadas.set(horasAsignadas); }
    public void setCompletado(boolean completado) { this.completado.set(completado); }
    public void setNotas(String notas) { this.notas.set(notas); }
    public void setFechaAsignacion(LocalDate fechaAsignacion) { this.fechaAsignacion.set(fechaAsignacion); }

    @Override
    public String toString() {
        return "Asignación #" + idAsignacion.get() + " - Tarea: " + tareaId.get() + ", Usuario: " + usuarioId.get();
    }
}
