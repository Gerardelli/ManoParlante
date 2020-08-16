package com.santiago.talkinghand.models;

public class Usuario {

    private String Uid;
    private String Usuario;
    private String email;
    private String telefono;
    private long timeStamp;
    private String imagenPerfil;
    private  long online;
    private  long ultimaconexion;

    public Usuario() {
    }

    public Usuario(String uid, String usuario, String email, String telefono, long timeStamp, String imagenPerfil, long online, long ultimaconexion) {
        Uid = uid;
        Usuario = usuario;
        this.email = email;
        this.telefono = telefono;
        this.timeStamp = timeStamp;
        this.imagenPerfil = imagenPerfil;
        this.online = online;
        this.ultimaconexion = ultimaconexion;
    }

    public long getOnline() {
        return online;
    }

    public void setOnline(long online) {
        this.online = online;
    }

    public long getUltimaconexion() {
        return ultimaconexion;
    }

    public void setUltimaconexion(long ultimaconexion) {
        this.ultimaconexion = ultimaconexion;
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
