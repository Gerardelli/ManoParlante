package com.santiago.talkinghand.providers;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.santiago.talkinghand.models.Token;

public class TokenProvider {
    CollectionReference mCollection;

    public TokenProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Tokens");
    }

    public void crearToken(final String idUsuario){
        if(idUsuario == null){
            return;
        }else {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    Token token = new Token(instanceIdResult.getToken());
                    mCollection.document(idUsuario).set(token);
                }
            });
        }
    }

    public Task<DocumentSnapshot> getToken(String idUsuario){
        return mCollection.document(idUsuario).get();
    }
}
