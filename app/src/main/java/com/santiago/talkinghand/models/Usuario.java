package com.santiago.talkinghand.models;

public class Usuario {

    private String Uid;
    private String Usuario;
    private String email;
    private String telefono;
    private long timeStamp;
    private String imagenPerfil;

    public Usuario() {
    }

    public Usuario(String uid, String usuario, String email, String telefono, long timeStamp, String imagenPerfil) {
        Uid = uid;
        Usuario = usuario;
        this.email = email;
        this.telefono = telefono;
        this.timeStamp = timeStamp;
        this.imagenPerfil = imagenPerfil;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getUsuario() {
        return Usuario;
    }

    public void setUsuario(String usuario) {
        Usuario = usuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getImagenPerfil() {
        return imagenPerfil;
    }

    public void setImagenPerfil(String imagenPerfil) {
        this.imagenPerfil = imagenPerfil;
    }
}
