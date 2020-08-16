package com.santiago.talkinghand.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.santiago.talkinghand.models.Mensaje;

import java.util.HashMap;
import java.util.Map;

public class MensajeProvider {
    CollectionReference mCollection;

    public MensajeProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Mensajes");
    }

    public Task<Void> crear(Mensaje mensaje){
        DocumentReference document = mCollection.document();
        mensaje.setId(document.getId());
        return document.set(mensaje);
    }

    public Query obtenerMensajesByChat(String idChat){
        return mCollection.whereEqualTo("idChat", idChat).orderBy("timeStamp", Query.Direction.ASCENDING);
    }

    public Query obtenerUltimoMensaje(String idChat){
        return mCollection.whereEqualTo("idChat", idChat).orderBy("timeStamp", Query.Direction.DESCENDING).limit(1);
    }

    public Query obtenetMensajesByChatandEmisor(String idChat, String idEmisor){
        return mCollection.whereEqualTo("idChat", idChat).whereEqualTo("idEmisor", idEmisor).whereEqualTo("visto", false);
    }

    public Task<Void> actualizarVisto(String idDocument, boolean estado){
        Map<String, Object> map = new HashMap<>();
        map.put("visto", estado);
        return mCollection.document(idDocument).update(map);
    }
}
