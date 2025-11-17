package com.taskflow.model;

import javafx.beans.property.*;
import java.time.LocalDate;

/**
 * Modelo de Usuario que representa la tabla 'usuarios' en Supabase
 */
public class Usuario {
    private final IntegerProperty idUsuario;
    private final StringProperty nombreCompleto;
    private final StringProperty email;
    private final StringProperty contraseñaHash;
    private final ObjectProperty<Rol> rol;
    private final ObjectProperty<byte[]> fotoPerfil;
    private final ObjectProperty<LocalDate> fechaRegistro;
    private final StringProperty telefono;
    private final BooleanProperty activo;

    /**
     * Constructor completo
     */
    public Usuario(int idUsuario, String nombreCompleto, String email, String contraseñaHash,
                   Rol rol, byte[] fotoPerfil, LocalDate fechaRegistro, String telefono, boolean activo) {
        this.idUsuario = new SimpleIntegerProperty(idUsuario);
        this.nombreCompleto = new SimpleStringProperty(nombreCompleto);
        this.email = new SimpleStringProperty(email);
        this.contraseñaHash = new SimpleStringProperty(contraseñaHash);
        this.rol = new SimpleObjectProperty<>(rol);
        this.fotoPerfil = new SimpleObjectProperty<>(fotoPerfil);
        this.fechaRegistro = new SimpleObjectProperty<>(fechaRegistro);
        this.telefono = new SimpleStringProperty(telefono);
        this.activo = new SimpleBooleanProperty(activo);
    }

    /**
     * Constructor sin foto de perfil y con valores por defecto
     */
    public Usuario(int idUsuario, String nombreCompleto, String email, String telefono, Rol rol, boolean activo) {
        this(idUsuario, nombreCompleto, email, "", rol, null, LocalDate.now(), telefono, activo);
    }

    // ==================== Getters de Properties ====================
    public IntegerProperty idUsuarioProperty() { return idUsuario; }
    public StringProperty nombreCompletoProperty() { return nombreCompleto; }
    public StringProperty emailProperty() { return email; }
    public StringProperty contraseñaHashProperty() { return contraseñaHash; }
    public ObjectProperty<Rol> rolProperty() { return rol; }
    public ObjectProperty<byte[]> fotoPerfilProperty() { return fotoPerfil; }
    public ObjectProperty<LocalDate> fechaRegistroProperty() { return fechaRegistro; }
    public StringProperty telefonoProperty() { return telefono; }
    public BooleanProperty activoProperty() { return activo; }

    // ==================== Getters de valores ====================
    public int getIdUsuario() { return idUsuario.get(); }
    public String getNombreCompleto() { return nombreCompleto.get(); }
    public String getEmail() { return email.get(); }
    public String getContraseñaHash() { return contraseñaHash.get(); }
    public Rol getRol() { return rol.get(); }
    public byte[] getFotoPerfil() { return fotoPerfil.get(); }
    public LocalDate getFechaRegistro() { return fechaRegistro.get(); }
    public String getTelefono() { return telefono.get(); }
    public boolean isActivo() { return activo.get(); }

    // ==================== Setters de valores ====================
    public void setIdUsuario(int idUsuario) { this.idUsuario.set(idUsuario); }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto.set(nombreCompleto); }
    public void setEmail(String email) { this.email.set(email); }
    public void setContraseñaHash(String contraseñaHash) { this.contraseñaHash.set(contraseñaHash); }
    public void setRol(Rol rol) { this.rol.set(rol); }
    public void setFotoPerfil(byte[] fotoPerfil) { this.fotoPerfil.set(fotoPerfil); }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro.set(fechaRegistro); }
    public void setTelefono(String telefono) { this.telefono.set(telefono); }
    public void setActivo(boolean activo) { this.activo.set(activo); }

    @Override
    public String toString() {
        return nombreCompleto.get() + " (" + rol.get() + ")";
    }
}
