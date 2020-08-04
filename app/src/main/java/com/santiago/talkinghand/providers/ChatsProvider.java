package com.santiago.talkinghand.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.santiago.talkinghand.models.Chat;

public class ChatsProvider {
    CollectionReference mColection;

    public ChatsProvider(){
        mColection = FirebaseFirestore.getInstance().collection("Chats");
    }

    public void crear(Chat chat){
        mColection.document(chat.getIdUsuario1()).collection("Usuarios").document(chat.getIdUsuario2()).set(chat);
        mColection.document(chat.getIdUsuario2()).collection("Usuarios").document(chat.getIdUsuario1()).set(chat);
    }
}
