package com.santiago.talkinghand.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.models.Publicacion;
import com.squareup.picasso.Picasso;

public class PublicacionesAdapter extends FirestoreRecyclerAdapter<Publicacion, PublicacionesAdapter.ViewHolder> {
    Context context;

    public PublicacionesAdapter(FirestoreRecyclerOptions<Publicacion> options, Context context){
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Publicacion publicacion) {
        holder.txtDescipcionPublicacion.setText(publicacion.getDescripcion());

        if(publicacion.getImagen() != null){
            if(!publicacion.getImagen().isEmpty()){
                Picasso.with(context).load(publicacion.getImagen()).into(holder.imageViewPublicacion);
            }
        }
        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_publicacion, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtDescipcionPublicacion;
        ImageView imageViewPublicacion;
        View viewHolder;

        public ViewHolder(View view){
            super(view);
            txtDescipcionPublicacion = view.findViewById(R.id.txtPublicacionCard);
            imageViewPublicacion = view.findViewById(R.id.publicacionCard);
            viewHolder = view;
        }
    }
}
