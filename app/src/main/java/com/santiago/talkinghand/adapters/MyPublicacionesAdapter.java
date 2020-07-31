package com.santiago.talkinghand.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.activities.DetallePublicacionActivity;
import com.santiago.talkinghand.models.Publicacion;
import com.santiago.talkinghand.providers.AuthProvider;
import com.santiago.talkinghand.providers.LikesProvider;
import com.santiago.talkinghand.providers.PublicacionProvider;
import com.santiago.talkinghand.providers.UsuarioProvider;
import com.santiago.talkinghand.utils.RelativeTime;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPublicacionesAdapter extends FirestoreRecyclerAdapter<Publicacion, MyPublicacionesAdapter.ViewHolder> {
    Context context;
    UsuarioProvider mUsuarioProvider;
    LikesProvider mLikesProvider;
    AuthProvider mAuthProvider;
    PublicacionProvider mPublicacionProvider;

    public MyPublicacionesAdapter(FirestoreRecyclerOptions<Publicacion> options, Context context){
        super(options);
        this.context = context;
        mUsuarioProvider = new UsuarioProvider();
        mLikesProvider = new LikesProvider();
        mAuthProvider = new AuthProvider();
        mPublicacionProvider = new PublicacionProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Publicacion publicacion) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String publicacionId = document.getId();
        String relativeTime = RelativeTime.getTimeAgo(publicacion.getTimeStamp(), context);
        holder.mTextViewRelativeTime.setText(relativeTime);
        holder.mTextViewDescripcion.setText(publicacion.getDescripcion());

        if(publicacion.getIdUsuario().equals(mAuthProvider.getUid())){
            holder.imageViewDelete.setVisibility(View.VISIBLE);
        }else{
            holder.imageViewDelete.setVisibility(View.GONE);
        }

        if(publicacion.getImagen() != null){
            if(!publicacion.getImagen().isEmpty()){
                Picasso.with(context).load(publicacion.getImagen()).into(holder.circleImageViewPost);
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
        holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarConfirmEliminar(publicacionId);
            }
        });
    }

    private void mostrarConfirmEliminar(final String publicacionId) {
        new AlertDialog.Builder(context).setIcon(R.drawable.ic_alert)
                                        .setTitle("Eliminar Publicación")
                                        .setMessage("¿Esta seguro de realizar la acción?")
                                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                eliminarPulicacion(publicacionId);
                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .show();
    }

    private void eliminarPulicacion(String publicacionId) {
        mPublicacionProvider.eliminar(publicacionId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context,"Publicacion eliminada", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(context, "No se elimino la publicacion", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_mis_pulicaciones, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView mTextViewDescripcion;
        TextView mTextViewRelativeTime;
        CircleImageView circleImageViewPost;
        ImageView imageViewDelete;
        View viewHolder;

        public ViewHolder(View view){
            super(view);
            mTextViewDescripcion = view.findViewById(R.id.txtViewDescripcion);
            mTextViewRelativeTime = view.findViewById(R.id.textViewRelativeTime);
            circleImageViewPost = view.findViewById(R.id.circleImagePublicacion);
            imageViewDelete = view.findViewById(R.id.deletePublicacion);
            viewHolder = view;
        }
    }
}
