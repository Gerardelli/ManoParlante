package com.santiago.talkinghand.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.santiago.talkinghand.models.Comentario;

public class ComentarioProvider {

    CollectionReference mCollection;

    public ComentarioProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Comentarios");
    }

    public Task<Void> create(Comentario comentario){
        return mCollection.document().set(comentario);
    }

    public Query getCommentByPublicacion(String idPost){
        return mCollection.whereEqualTo("idPublicacion", idPost);
    }
}
