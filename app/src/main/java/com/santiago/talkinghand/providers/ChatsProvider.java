package com.santiago.talkinghand.providers;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.santiago.talkinghand.models.Chat;

import java.util.ArrayList;

public class ChatsProvider {
    CollectionReference mCollection;

    public ChatsProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Chats");
    }


    public void crear(Chat chat){
        mCollection.document(chat.getIdUsuario1() + chat.getIdUsuario2()).set(chat);
    }

    public Query getTodos(String idUsuario){
        return mCollection.whereArrayContains("ids", idUsuario);
    }

    public Query getChatUsuario1AndUsuario2(String idUsuario1, String idUsuario2){
        ArrayList ids = new ArrayList();
        ids.add(idUsuario1 + idUsuario2);
        ids.add(idUsuario2 + idUsuario1);
        return mCollection.whereIn("id",ids);
    }
}
