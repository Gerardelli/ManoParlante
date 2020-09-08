package com.santiago.talkinghand.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.santiago.talkinghand.models.Publicacion;
import com.santiago.talkinghand.models.Video;

public class VideoProvider {

    CollectionReference mCollection;

    public VideoProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Videos");
    }

    public Task<Void> guardarVideo(Video video){
        return mCollection.document().set(video);
    }

    public Query getTodos(){
        return mCollection.orderBy("timeStamp", Query.Direction.DESCENDING);
    }

    public Query getVideosUsuario(String id){
        return mCollection.whereEqualTo("idUsuario", id);
    }

    public Task<DocumentSnapshot> getVideoById(String id){
        return mCollection.document(id).get();
    }

    public Task<Void> eliminar(String idVideo){
        return mCollection.document(idVideo).delete();
    }
}
