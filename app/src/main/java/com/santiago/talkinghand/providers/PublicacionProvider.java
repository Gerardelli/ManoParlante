package com.santiago.talkinghand.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.santiago.talkinghand.models.Publicacion;

public class PublicacionProvider {

    CollectionReference mCollection;

    public PublicacionProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Publicacion");
    }

    public Task<Void> guardarPublicacion(Publicacion publicacion){
        return mCollection.document().set(publicacion);
    }

    public Query getTodos(){
        return mCollection.orderBy("timeStamp", Query.Direction.DESCENDING);
    }

    public Query getPublicacionesUsuario(String id){
        return mCollection.whereEqualTo("idUsuario", id);
    }

    public Task<DocumentSnapshot> getPublicacionById(String id){
        return mCollection.document(id).get();
    }
}
