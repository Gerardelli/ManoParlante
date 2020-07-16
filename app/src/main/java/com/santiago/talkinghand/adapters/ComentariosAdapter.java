package com.santiago.talkinghand.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.activities.DetallePublicacionActivity;
import com.santiago.talkinghand.models.Comentario;
import com.santiago.talkinghand.providers.UsuarioProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ComentariosAdapter extends FirestoreRecyclerAdapter<Comentario, ComentariosAdapter.ViewHolder> {
    Context context;
    UsuarioProvider mUsuarioProvider;

    public ComentariosAdapter(FirestoreRecyclerOptions<Comentario> options, Context context){
        super(options);
        this.context = context;
        mUsuarioProvider = new UsuarioProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Comentario comentario) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String comentarioId = document.getId();
        String idUsuario = document.getString("idUsuario");
        holder.txtComentario.setText(comentario.getComentario());
        getInfoUsuario(idUsuario, holder);
    }

    private void getInfoUsuario(String idUsuario, final ViewHolder holder){
        mUsuarioProvider.getUsuario(idUsuario).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("usuario")){
                        String usuario = documentSnapshot.getString("usuario");
                        holder.txtUsuario.setText(usuario);
                    }
                    if(documentSnapshot.contains("imagenPerfil")){
                        String imagenPerfil = documentSnapshot.getString("imagenPerfil");
                        if(imagenPerfil != null){
                            if(!imagenPerfil.isEmpty()){
                                Picasso.with(context).load(imagenPerfil).into(holder.imageViewPerfil);

                            }
                        }
                    }
                }
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_comentario, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtUsuario;
        TextView txtComentario;
        CircleImageView imageViewPerfil;
        View viewHolder;

        public ViewHolder(View view){
            super(view);
            txtUsuario = view.findViewById(R.id.txtUsernameComentario);
            txtComentario = view.findViewById(R.id.txtDescripcionComentario);
            imageViewPerfil = view.findViewById(R.id.circleComentPerfil);
            viewHolder = view;
        }
    }
}
