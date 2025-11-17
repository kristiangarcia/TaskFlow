package com.taskflow.model;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modelo de Tarea que representa la tabla 'tareas' en Supabase
 */
public class Tarea {
    private final IntegerProperty idTarea;
    private final StringProperty titulo;
    private final StringProperty descripcion;
    private final StringProperty proyectoCategoria;
    private final ObjectProperty<EstadoTarea> estado;
    private final ObjectProperty<Prioridad> prioridad;
    private final ObjectProperty<LocalDate> fechaLimite;
    private final IntegerProperty tiempoEstimadoMins;
    private final ObjectProperty<byte[]> imagen;
    private final ObjectProperty<LocalDateTime> fechaCreacion;

    /**
     * Constructor completo
     */
    public Tarea(int idTarea, String titulo, String descripcion, String proyectoCategoria,
                 EstadoTarea estado, Prioridad prioridad, LocalDate fechaLimite,
                 Integer tiempoEstimadoMins, byte[] imagen, LocalDateTime fechaCreacion) {
        this.idTarea = new SimpleIntegerProperty(idTarea);
        this.titulo = new SimpleStringProperty(titulo);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.proyectoCategoria = new SimpleStringProperty(proyectoCategoria);
        this.estado = new SimpleObjectProperty<>(estado);
        this.prioridad = new SimpleObjectProperty<>(prioridad);
        this.fechaLimite = new SimpleObjectProperty<>(fechaLimite);
        this.tiempoEstimadoMins = new SimpleIntegerProperty(tiempoEstimadoMins != null ? tiempoEstimadoMins : 0);
        this.imagen = new SimpleObjectProperty<>(imagen);
        this.fechaCreacion = new SimpleObjectProperty<>(fechaCreacion);
    }

    /**
     * Constructor sin imagen y con valores por defecto
     */
    public Tarea(int idTarea, String titulo, String descripcion, String proyectoCategoria,
                 EstadoTarea estado, Prioridad prioridad, LocalDate fechaLimite) {
        this(idTarea, titulo, descripcion, proyectoCategoria, estado, prioridad,
             fechaLimite, null, null, LocalDateTime.now());
    }

    // ==================== Getters de Properties ====================
    public IntegerProperty idTareaProperty() { return idTarea; }
    public StringProperty tituloProperty() { return titulo; }
    public StringProperty descripcionProperty() { return descripcion; }
    public StringProperty proyectoCategoriaProperty() { return proyectoCategoria; }
    public ObjectProperty<EstadoTarea> estadoProperty() { return estado; }
    public ObjectProperty<Prioridad> prioridadProperty() { return prioridad; }
    public ObjectProperty<LocalDate> fechaLimiteProperty() { return fechaLimite; }
    public IntegerProperty tiempoEstimadoMinsProperty() { return tiempoEstimadoMins; }
    public ObjectProperty<byte[]> imagenProperty() { return imagen; }
    public ObjectProperty<LocalDateTime> fechaCreacionProperty() { return fechaCreacion; }

    // ==================== Getters de valores ====================
    public int getIdTarea() { return idTarea.get(); }
    public String getTitulo() { return titulo.get(); }
    public String getDescripcion() { return descripcion.get(); }
    public String getProyectoCategoria() { return proyectoCategoria.get(); }
    public EstadoTarea getEstado() { return estado.get(); }
    public Prioridad getPrioridad() { return prioridad.get(); }
    public LocalDate getFechaLimite() { return fechaLimite.get(); }
    public int getTiempoEstimadoMins() { return tiempoEstimadoMins.get(); }
    public byte[] getImagen() { return imagen.get(); }
    public LocalDateTime getFechaCreacion() { return fechaCreacion.get(); }

    // ==================== Setters de valores ====================
    public void setIdTarea(int idTarea) { this.idTarea.set(idTarea); }
    public void setTitulo(String titulo) { this.titulo.set(titulo); }
    public void setDescripcion(String descripcion) { this.descripcion.set(descripcion); }
    public void setProyectoCategoria(String proyectoCategoria) { this.proyectoCategoria.set(proyectoCategoria); }
    public void setEstado(EstadoTarea estado) { this.estado.set(estado); }
    public void setPrioridad(Prioridad prioridad) { this.prioridad.set(prioridad); }
    public void setFechaLimite(LocalDate fechaLimite) { this.fechaLimite.set(fechaLimite); }
    public void setTiempoEstimadoMins(int tiempoEstimadoMins) { this.tiempoEstimadoMins.set(tiempoEstimadoMins); }
    public void setImagen(byte[] imagen) { this.imagen.set(imagen); }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion.set(fechaCreacion); }

    @Override
    public String toString() {
        return titulo.get() + " (" + estado.get() + ")";
    }
}
