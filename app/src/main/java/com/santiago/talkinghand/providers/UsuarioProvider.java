package com.santiago.talkinghand.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.santiago.talkinghand.models.Usuario;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UsuarioProvider {

    private CollectionReference mFirestoreCollection;

    public UsuarioProvider(){
        mFirestoreCollection = FirebaseFirestore.getInstance().collection("Usuarios");
    }

    public Task<DocumentSnapshot> getUsuario(String id){
        return mFirestoreCollection.document(id).get();
    }

    public Task<Void> create(Usuario user){
        return  mFirestoreCollection.document(user.getUid()).set(user);
    }

    public Query getUsuarios(){
        return mFirestoreCollection.orderBy("timeStamp", Query.Direction.DESCENDING);
    }
    public Query getUsuarioByUsername(String username){
        return mFirestoreCollection.orderBy("usuario").startAt(username).endAt(username+'\uf8ff');
    }

    public Task<Void> updateUsuario(Usuario usuario){
        Map<String, Object> map = new HashMap<>();
        map.put("usuario", usuario.getUsuario());
        map.put("telefono", usuario.getTelefono());
        map.put("timeStamp", new Date().getTime());
        map.put("imagenPerfil", usuario.getImagenPerfil());
        return mFirestoreCollection.document(usuario.getUid()).update(map);
    }
}
