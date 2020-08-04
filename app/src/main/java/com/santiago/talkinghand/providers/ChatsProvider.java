package com.santiago.talkinghand.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.santiago.talkinghand.models.Chat;

public class ChatsProvider {
    CollectionReference mCollection;

    public ChatsProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Chats");
    }


    public void crear(Chat chat){
        mCollection.document(chat.getIdUsuario1()).collection("Usuarios").document(chat.getIdUsuario2()).set(chat);
        mCollection.document(chat.getIdUsuario2()).collection("Usuarios").document(chat.getIdUsuario1()).set(chat);
    }

    public Query getTodos(String idUsuario){
        return mCollection.document(idUsuario).collection("Usuarios");
    }
}
