package com.santiago.talkinghand.models;

public class Comentario {

    private String id;
    private String comentario;
    private String idUsuario;
    private String idPublicacion;
    long timeStamp;

    public Comentario() {
    }

    public Comentario(String id, String comentario, String idUsuario, String idPublicacion, long timeStamp) {
        this.id = id;
        this.comentario = comentario;
        this.idUsuario = idUsuario;
        this.idPublicacion = idPublicacion;
        this.timeStamp = timeStamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdPublicacion() {
        return idPublicacion;
    }

    public void setIdPublicacion(String idPublicacion) {
        this.idPublicacion = idPublicacion;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
