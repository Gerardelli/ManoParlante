package com.santiago.talkinghand.models;

import java.util.ArrayList;

public class Chat {
    private String id;
    private String idUsuario1;
    private String idUsuario2;
    private boolean escribiendo;
    private int idNotificacion;
    private long timeStamp;
    private ArrayList<String> ids;

    public Chat() {
    }

    public Chat(String id, String idUsuario1, String idUsuario2, boolean escribiendo, int idNotificacion, long timeStamp, ArrayList<String> ids) {
        this.id = id;
        this.idUsuario1 = idUsuario1;
        this.idUsuario2 = idUsuario2;
        this.escribiendo = escribiendo;
        this.idNotificacion = idNotificacion;
        this.timeStamp = timeStamp;
        this.ids = ids;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public ArrayList<String> getIds() {
        return ids;
    }

    public void setIds(ArrayList<String> ids) {
        this.ids = ids;
    }

    public int getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(int idNotificacion) {
        this.idNotificacion = idNotificacion;
    }
}
