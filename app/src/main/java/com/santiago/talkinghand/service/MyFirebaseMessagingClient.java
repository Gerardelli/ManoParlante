package com.santiago.talkinghand.service;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.channel.NotificationHelper;
import com.santiago.talkinghand.models.Mensaje;
import com.santiago.talkinghand.receivers.MensajeReceiver;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingClient extends FirebaseMessagingService {

    public static final String NOTIFICATION_REPLY = "NotificationReplay";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String body = data.get("body");
        if(title != null){
            if(title.equals("Nuevo Mensaje")){
                showNotificationMensaje(data);
            }else{
                showNotification(title, body);
            }
        }
    }

    private void showNotification(String title, String body) {
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = notificationHelper.getNotification(title, body);

        Random random = new Random();
        int n = random.nextInt(1000000);
        notificationHelper.getManager().notify(n, builder.build());
    }

    private void showNotificationMensaje(Map<String,String> data) {
        final String imagenEmisor = data.get("imagenEmisor");
        final String imagenReceptor = data.get("imagenReceptor");
        Log.d("Entro", "Nuevo Mensaje");
        obtenerImagenEmisor(data, imagenEmisor, imagenReceptor);

    }

    private void obtenerImagenEmisor(final Map<String,String> data, final String imagenEmisor, final String imagenReceptor) {

        new Handler(Looper.getMainLooper())
                .post(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.with(getApplicationContext())
                                .load(imagenEmisor)
                                .into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(final Bitmap bitmapEmisor, Picasso.LoadedFrom from) {
                                        obtenerImagenReceptor(data , imagenReceptor, bitmapEmisor);
                                    }

                                    @Override
                                    public void onBitmapFailed(Drawable errorDrawable) {
                                        obtenerImagenReceptor(data, imagenReceptor, null);

                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });
                    }
                });

    }

    private void obtenerImagenReceptor(final Map<String,String> data, String imagenReceptor, final Bitmap imagenEmisor){
        Picasso.with(getApplicationContext())
                .load(imagenReceptor)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmapReceptor, Picasso.LoadedFrom from) {
                        notificacionMensaje(data, bitmapReceptor,  imagenEmisor);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        notificacionMensaje(data, imagenEmisor, null);

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }

    private void notificacionMensaje(Map<String, String> data, Bitmap bitmapReceptor, Bitmap bitmapEmisor){

        final String usuarioEmisor = data.get("usuarioEmisor");
        final String usuarioReceptor = data.get("usuarioReceptor");
        final String ultimoMensaje = data.get("ultimoMensaje");
        String mensajesJSON = data.get("mensajes");
        final String imagenEmisor = data.get("imagenEmisor");
        final String imagenReceptor = data.get("imagenReceptor");

        final String idEmisor = data.get("idEmisor");
        final String idReceptor = data.get("idReceptor");
        final String idChat = data.get("idChat");

        final int idNotificacion = Integer.parseInt(data.get("idNotificacion"));
        Intent intent = new Intent(this, MensajeReceiver.class);
        intent.putExtra("idEmisor", idEmisor);
        intent.putExtra("idReceptor", idReceptor);
        intent.putExtra("idChat", idChat);
        intent.putExtra("idNotificacion", idNotificacion);
        intent.putExtra("usuarioEmisor", usuarioEmisor);
        intent.putExtra("usuarioReceptor", usuarioReceptor);
        intent.putExtra("imagenEmisor", imagenEmisor);
        intent.putExtra("imagenReceptor", imagenReceptor);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteInput remoteInput = new RemoteInput.Builder(NOTIFICATION_REPLY).setLabel("Tu Mensaje...").build();

        final NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Responder",
                pendingIntent)
                .addRemoteInput(remoteInput)
                .build();


        Gson gson = new Gson();
        final Mensaje[] mensajes = gson.fromJson(mensajesJSON, Mensaje[].class);

        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = notificationHelper.getNotificacionMensaje(
                mensajes,
                usuarioEmisor,
                usuarioReceptor,
                ultimoMensaje,
                bitmapEmisor,
                bitmapReceptor,
                action
        );
        notificationHelper.getManager().notify(idNotificacion, builder.build());
    }
}
