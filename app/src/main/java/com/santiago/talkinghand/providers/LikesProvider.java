package com.santiago.talkinghand.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.santiago.talkinghand.models.Like;

public class LikesProvider {
    CollectionReference mCollection;
    public LikesProvider(){

        mCollection = FirebaseFirestore.getInstance().collection("Likes");

    }

    public Task<Void> create(Like like){
        DocumentReference document = mCollection.document();
        String id = document.getId();
        like.setId(id);
        return document.set(like);
    }

    public Query getLikesByPublicacion(String idPublicacion){
        return mCollection.whereEqualTo("idPublicacion", idPublicacion);
    }

    public Task<Void> delete(String id){
        return mCollection.document(id).delete();
    }

    public Query getLikeByPublicacionAndUsuario(String idPublicacion, String idUsuario){
        return mCollection.whereEqualTo("idPublicacion", idPublicacion).whereEqualTo("idUsuario",idUsuario);
    }
}
