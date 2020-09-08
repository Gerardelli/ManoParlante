package com.santiago.talkinghand.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.santiago.talkinghand.models.FCMBody;
import com.santiago.talkinghand.models.FCMResponse;
import com.santiago.talkinghand.models.Mensaje;
import com.santiago.talkinghand.providers.AuthProvider;
import com.santiago.talkinghand.providers.MensajeProvider;
import com.santiago.talkinghand.providers.NotificationProvider;
import com.santiago.talkinghand.providers.TokenProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.santiago.talkinghand.service.MyFirebaseMessagingClient.NOTIFICATION_REPLY;

public class MensajeReceiver extends BroadcastReceiver {

    String mExtraIdEmisor;
    String mExtraIdReceptor;
    String mExtraIdChat;
    String mExtraUsuarioEmisor;
    String mExtraUsuarioReceptor;
    String mExtraImagenEmisor;
    String mExtraImagenReceptor;
    int mExtraIdNotificacion;
    TokenProvider mTokenProvider;
    NotificationProvider mNotificationProvider;
    AuthProvider mAuthProvider;

    @Override
    public void onReceive(Context context, Intent intent) {
        mExtraIdEmisor = intent.getExtras().getString("idEmisor");
        mExtraIdReceptor = intent.getExtras().getString("idReceptor");
        mExtraIdChat = intent.getExtras().getString("idChat");
        mExtraUsuarioEmisor = intent.getExtras().getString("usuarioEmisor");
        mExtraUsuarioReceptor = intent.getExtras().getString("usuarioReceptor");
        mExtraImagenEmisor = intent.getExtras().getString("imagenEmisor");
        mExtraImagenReceptor = intent.getExtras().getString("imagenReceptor");
        mExtraIdNotificacion = intent.getExtras().getInt("idNotificacion");

        mTokenProvider = new TokenProvider();
        mNotificationProvider = new NotificationProvider();
        mAuthProvider = new AuthProvider();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.cancel(mExtraIdNotificacion);

        String mensaje = getMesaageText(intent).toString();
        enviarMensaje(mensaje);

    }

    private void enviarMensaje(String txtmensaje) {
        final Mensaje mensaje = new Mensaje();
        mensaje.setIdChat(mExtraIdChat);
        mensaje.setIdEmisor(mExtraIdReceptor);
        mensaje.setIdReceptor(mExtraIdEmisor);
        mensaje.setTimeStamp(new Date().getTime());
        mensaje.setVisto(false);
        mensaje.setIdChat(mExtraIdChat);
        mensaje.setMensaje(txtmensaje);

        MensajeProvider mensajeProvider = new MensajeProvider();

        mensajeProvider.crear(mensaje).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    obtenerToken(mensaje);
                }
            }
        });
    }

    private  void obtenerToken(final Mensaje mensaje){
        mTokenProvider.getToken(mExtraIdEmisor).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("token")){
                        String token = documentSnapshot.getString("token");
                        ArrayList<Mensaje> mensajesArray = new ArrayList<>();
                        mensajesArray.add(mensaje);
                        Gson gson = new Gson();
                        String mensajes = gson.toJson(mensajesArray);
                        enviarNotificacion(token, mensajes, mensaje);
                    }
                }
            }
        });

    }

    private void enviarNotificacion(final String token, String mensajes, Mensaje mensaje){
        final Map<String, String> data = new HashMap<>();
        data.put("title", "Nuevo Mensaje");
        data.put("body", mensaje.getMensaje());
        data.put("idNotificacion", String.valueOf(mExtraIdNotificacion));
        data.put("mensajes", mensajes);
        data.put("usuarioEmisor", mExtraUsuarioEmisor);
        data.put("usuarioReceptor", mExtraUsuarioReceptor);
        data.put("idEmisor", mensaje.getIdEmisor());
        data.put("idReceptor", mensaje.getIdReceptor());
        data.put("idChat", mensaje.getIdChat());
        data.put("imagenEmisor", mExtraImagenEmisor);
        data.put("imagenReceptor", mExtraImagenReceptor);

        FCMBody body = new FCMBody(token, "high", "4500s", data);
        mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {

            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {
                Log.d("ERROR","El error fue" + t.getMessage());
            }
        });
    }

    private CharSequence getMesaageText(Intent intent){
        Bundle remoteInput = android.app.RemoteInput.getResultsFromIntent(intent);
        if(remoteInput != null){
            return remoteInput.getCharSequence(NOTIFICATION_REPLY);
        }
        return null;
    }
}
