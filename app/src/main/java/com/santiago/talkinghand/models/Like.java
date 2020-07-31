package com.santiago.talkinghand.models;

public class Like {
    private String id;
    private String idPublicacion;
    private String idUsuario;
    private long timeStamp;

    public Like() {
    }

    public Like(String id, String idPublicacion, String idUsuario, long timeStamp) {
        this.id = id;
        this.idPublicacion = idPublicacion;
        this.idUsuario = idUsuario;
        this.timeStamp = timeStamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdPublicacion() {
        return idPublicacion;
    }

    public void setIdPublicacion(String idPublicacion) {
        this.idPublicacion = idPublicacion;
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
