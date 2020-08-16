package com.santiago.talkinghand.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import com.santiago.talkinghand.providers.AuthProvider;
import com.santiago.talkinghand.providers.UsuarioProvider;

import java.util.List;

public class HelperMensajeVisto {

    public static void actualizarEnlinea(boolean estado, Context context){
        UsuarioProvider usuarioProvider = new UsuarioProvider();
        AuthProvider authProvider = new AuthProvider();
        if(authProvider.getUid() !=  null){
            if(aplicacionSegudoPlano(context)){
                usuarioProvider.updateUsuarioEnlinea(authProvider.getUid(), estado);
            }
            else if(estado){
                usuarioProvider.updateUsuarioEnlinea(authProvider.getUid(), estado);
            }
        }
    }

    public static boolean aplicacionSegudoPlano(final Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
        if(!tasks.isEmpty()){
            ComponentName topActivity = tasks.get(0).topActivity;
            if(!topActivity.getPackageName().equals(context.getPackageName())){
                return true;
            }
        }
        return false;

    }
}
