package com.santiago.talkinghand.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.activities.ChatActivity;
import com.santiago.talkinghand.models.Mensaje;
import com.santiago.talkinghand.models.Mensaje;
import com.santiago.talkinghand.providers.AuthProvider;
import com.santiago.talkinghand.providers.UsuarioProvider;
import com.santiago.talkinghand.utils.RelativeTime;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MensajeAdapter extends FirestoreRecyclerAdapter<Mensaje, MensajeAdapter.ViewHolder> {
    Context context;
    UsuarioProvider mUsuarioProvider;
    AuthProvider mAuthProvider;

    public MensajeAdapter(FirestoreRecyclerOptions<Mensaje> options, Context context){
        super(options);
        this.context = context;
        mUsuarioProvider = new UsuarioProvider();
        mAuthProvider = new AuthProvider();

    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull final Mensaje mensaje) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String mensajeId = document.getId();
        holder.txtMensaje.setText(mensaje.getMensaje());
        String relativeTime = RelativeTime.timeFormatAMPM(mensaje.getTimeStamp(), context);
        holder.txtFecha.setText(relativeTime);

        if(mensaje.getIdEmisor().equals(mAuthProvider.getUid())){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(150, 0,0,0);
            holder.mLinearLayoutMensaje.setLayoutParams(params);
            holder.mLinearLayoutMensaje.setPadding(30,20,0,20);
            holder.mLinearLayoutMensaje.setBackground(context.getResources().getDrawable(R.drawable.rounded_linear_layout));
            holder.imageViewVisto.setVisibility(View.VISIBLE);
        }
        else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.setMargins(0, 0,150,0);
            holder.mLinearLayoutMensaje.setLayoutParams(params);
            holder.mLinearLayoutMensaje.setPadding(30,20,30,20);
            holder.mLinearLayoutMensaje.setBackground(context.getResources().getDrawable(R.drawable.rounded_linear_layout_gray));
            holder.imageViewVisto.setVisibility(View.GONE);
        }
        if(mensaje.isVisto()){
            holder.imageViewVisto.setImageResource(R.drawable.ic_check_blue);
        }else {
            holder.imageViewVisto.setImageResource(R.drawable.ic_check_gray);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_mensaje, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtFecha;
        TextView txtMensaje;
        ImageView imageViewVisto;
        View viewHolder;
        LinearLayout mLinearLayoutMensaje;

        public ViewHolder(View view){
            super(view);
            txtFecha = view.findViewById(R.id.txtFechaMensaje);
            txtMensaje = view.findViewById(R.id.txtMensaje);
            imageViewVisto = view.findViewById(R.id.visto);
            mLinearLayoutMensaje = view.findViewById(R.id.linearLayoutMensaje);
            viewHolder = view;
        }
    }
}
