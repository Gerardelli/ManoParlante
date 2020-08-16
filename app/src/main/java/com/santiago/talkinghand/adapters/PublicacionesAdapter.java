package com.santiago.talkinghand.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.activities.DetallePublicacionActivity;
import com.santiago.talkinghand.models.Like;
import com.santiago.talkinghand.models.Publicacion;
import com.santiago.talkinghand.providers.AuthProvider;
import com.santiago.talkinghand.providers.LikesProvider;
import com.santiago.talkinghand.providers.UsuarioProvider;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class PublicacionesAdapter extends FirestoreRecyclerAdapter<Publicacion, PublicacionesAdapter.ViewHolder> {
    Context context;
    UsuarioProvider mUsuarioProvider;
    LikesProvider mLikesProvider;
    AuthProvider mAuthProvider;
    ListenerRegistration mListener;

    public PublicacionesAdapter(FirestoreRecyclerOptions<Publicacion> options, Context context){
        super(options);
        this.context = context;
        mUsuarioProvider = new UsuarioProvider();
        mLikesProvider = new LikesProvider();
        mAuthProvider = new AuthProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Publicacion publicacion) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String publicacionId = document.getId();
        holder.txtDescipcionPublicacion.setText(publicacion.getDescripcion());

        if(publicacion.getImagen() != null){
            if(!publicacion.getImagen().isEmpty()){
                Picasso.with(context).load(publicacion.getImagen()).into(holder.imageViewPublicacion);
            }
        }
        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetallePublicacionActivity.class);
                intent.putExtra("id", publicacionId);
                context.startActivity(intent);
            }
        });

        holder.imageViewLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Like like = new Like();
                like.setIdUsuario(mAuthProvider.getUid());
                like.setIdPublicacion(publicacionId);
                like.setTimeStamp(new Date().getTime());
                like(like, holder);
            }
        });

        getUsuarioInfo(publicacion.getIdUsuario(), holder);
        getNumberLikesByPublicacion(publicacionId, holder);
        verificarLike(publicacionId, mAuthProvider.getUid(), holder);
    }

    private void getNumberLikesByPublicacion(String idPublicacion, final ViewHolder holder){
        mListener = mLikesProvider.getLikesByPublicacion(idPublicacion).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots != null){
                    int numberLikes = queryDocumentSnapshots.size();
                    holder.txtLikes.setText(String.valueOf(numberLikes) + " Me gusta");
                }
            }
        });
    }

    private void like(final Like like, final ViewHolder holder) {

        mLikesProvider.getLikeByPublicacionAndUsuario(like.getIdPublicacion(), mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberDocuments = queryDocumentSnapshots.size();
                if(numberDocuments > 0){
                    String idLike = queryDocumentSnapshots.getDocuments().get(0).getId();
                    holder.imageViewLikes.setImageResource(R.drawable.icon_like_gray);
                    mLikesProvider.delete(idLike);
                }else{
                    holder.imageViewLikes.setImageResource(R.drawable.icon_like_blue);
                    mLikesProvider.create(like);
                }
            }
        });
    }

    private void verificarLike(String idPublicacion, String idUsuario, final ViewHolder holder) {

        mLikesProvider.getLikeByPublicacionAndUsuario(idPublicacion, idUsuario).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberDocuments = queryDocumentSnapshots.size();
                if(numberDocuments > 0){
                    holder.imageViewLikes.setImageResource(R.drawable.icon_like_blue);
                }else{
                    holder.imageViewLikes.setImageResource(R.drawable.icon_like_gray);
                }
            }
        });
    }

    private void getUsuarioInfo(String idUsuario, final ViewHolder holder) {
            mUsuarioProvider.getUsuario(idUsuario).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        if(documentSnapshot.contains("usuario")){
                            String username = documentSnapshot.getString("usuario");
                            holder.txtUsuario.setText(username);
                        }
                    }
                }
            });
    }

    public ListenerRegistration getListener(){
        return mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_publicacion, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtDescipcionPublicacion;
        TextView txtUsuario;
        TextView txtLikes;
        ImageView imageViewPublicacion;
        ImageView imageViewLikes;
        View viewHolder;

        public ViewHolder(View view){
            super(view);
            txtDescipcionPublicacion = view.findViewById(R.id.txtPublicacionCard);
            txtUsuario = view.findViewById(R.id.txtUsuarioPublicacionCard);
            txtLikes = view.findViewById(R.id.txtLike);
            imageViewPublicacion = view.findViewById(R.id.publicacionCard);
            imageViewLikes = view.findViewById(R.id.imageViewLike);
            viewHolder = view;
        }
    }
}
