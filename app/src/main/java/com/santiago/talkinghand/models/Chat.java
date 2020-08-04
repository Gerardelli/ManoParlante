package com.santiago.talkinghand.models;

public class Chat {
    private String idUsuario1;
    private String idUsuario2;
    private boolean escribiendo;
    private long timeStamp;

    public Chat() {
    }

    public Chat(String idUsuario1, String idUsuario2, boolean escribiendo, long timeStamp) {
        this.idUsuario1 = idUsuario1;
        this.idUsuario2 = idUsuario2;
        this.escribiendo = escribiendo;
        this.timeStamp = timeStamp;
    }

    public String getIdUsuario1() {
        return idUsuario1;
    }

    public void setIdUsuario1(String idUsuario1) {
        this.idUsuario1 = idUsuario1;
    }

    public String getIdUsuario2() {
        return idUsuario2;
    }

    public void setIdUsuario2(String idUsuario2) {
        this.idUsuario2 = idUsuario2;
    }

    public boolean isEscribiendo() {
        return escribiendo;
    }

    public void setEscribiendo(boolean escribiendo) {
        this.escribiendo = escribiendo;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
