package com.santiago.talkinghand.models;

public class Publicacion {
    private String id;
    private String descripcion;
    private String imagen;
    private String idUsuario;
    private long timeStamp;

    public Publicacion() {
    }

    public Publicacion(String id, String descripcion, String imagen, String idUsuario, long timeStamp) {
        this.id = id;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.idUsuario = idUsuario;
        this.timeStamp = timeStamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
