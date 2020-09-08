package com.santiago.talkinghand.channel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;

import com.santiago.talkinghand.R;
import com.santiago.talkinghand.models.Mensaje;

import java.util.Date;

public class NotificationHelper extends ContextWrapper {
    private static final String CHANNEL_ID ="com.santiago.talkinghand";
    private static final String CHANNEL_NAME ="TalkingHand";

    private NotificationManager manager;

    public NotificationHelper(Context context){
        super(context);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            crearChannels();
        }
    }

    //Canal de notificaciones
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void crearChannels(){
        NotificationChannel notificationChannel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
        );
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(Color.GRAY);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager getManager(){
        if(manager == null){
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public NotificationCompat.Builder getNotification(String title, String body){
        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setColor(Color.GRAY)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title));
    }


    public NotificationCompat.Builder getNotificacionMensaje(
            Mensaje[] mensajes,
            String usuarioEmisor,
            String usuarioReceptor,
            String ultimoMensaje,
            Bitmap bitmapEmisor,
            Bitmap bitmapReceptor,
            NotificationCompat.Action action){
        Person person1 = null;
        if(bitmapEmisor == null){
            person1 = new Person.Builder()
                    .setName(usuarioEmisor)
                    .setIcon(IconCompat.createWithResource(getApplicationContext(), R.drawable.account))
                    .build();
        }else{
            person1 = new Person.Builder()
                    .setName(usuarioEmisor)
                    .setIcon(IconCompat.createWithBitmap(bitmapEmisor))
                    .build();
        }

        Person person2 = null;
        if(bitmapReceptor == null){
            person2 = new Person.Builder()
                    .setName(usuarioReceptor)
                    .setIcon(IconCompat.createWithResource(getApplicationContext(), R.drawable.account))
                    .build();
        }else{
            person2 = new Person.Builder()
                    .setName(usuarioReceptor)
                    .setIcon(IconCompat.createWithBitmap(bitmapReceptor))
                    .build();
        }

        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(person1);
        NotificationCompat.MessagingStyle.Message message1 =
                new NotificationCompat.MessagingStyle.Message(
                        ultimoMensaje,
                        new Date().getTime(),
                        person1);

        messagingStyle.addMessage(message1);

        for (Mensaje m: mensajes){
            NotificationCompat.MessagingStyle.Message message2 =
                    new NotificationCompat.MessagingStyle.Message(
                            m.getMensaje(),
                            m.getTimeStamp(),
                            person2);
            messagingStyle.addMessage(message2);
        }
        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(messagingStyle)
                .addAction(action);
    }


}
