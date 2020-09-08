package com.santiago.talkinghand.models;

public class Video {

    private String id;
    private String descripcion;
    private String video;
    private String idUsuario;
    private long timeStamp;

    public Video() {
    }

    public Video(String id, String descripcion, String video, String idUsuario, long timeStamp) {
        this.id = id;
        this.descripcion = descripcion;
        this.video = video;
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

    public String getVideo() {
        return video;
    }

    public void setVideo(String imagen) {
        this.video = imagen;
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
