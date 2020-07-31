package com.santiago.talkinghand.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.activities.DetallePublicacionActivity;
import com.santiago.talkinghand.activities.UsuarioPublicacionActivity;
import com.santiago.talkinghand.models.Usuario;
import com.santiago.talkinghand.providers.AuthProvider;
import com.santiago.talkinghand.providers.UsuarioProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsuariosAdapter extends FirestoreRecyclerAdapter<Usuario, UsuariosAdapter.ViewHolder> {
    Context context;
    UsuarioProvider mUsuarioProvider;
    AuthProvider mAuthProvider;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public UsuariosAdapter(@NonNull FirestoreRecyclerOptions<Usuario> options, Context context) {
        super(options);
        this.context = context;
        mAuthProvider = new AuthProvider();
        mUsuarioProvider = new UsuarioProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull final Usuario usuario) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);
        final String usuarioId = documentSnapshot.getId();
        holder.txtUsuario.setText(usuario.getUsuario());
        holder.txtCorreo.setText(usuario.getEmail());

        if(usuario.getImagenPerfil() != null){
            if(!usuario.getImagenPerfil().isEmpty()){
                Picasso.with(context).load(usuario.getImagenPerfil()).into(holder.circleImagePerfil);
            }
        }
        holder.btnVerPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!usuarioId.equals("")){
                    Intent intent = new Intent( context, UsuarioPublicacionActivity.class);
                    intent.putExtra("idUser", usuarioId);
                    context.startActivity(intent);
                }else {
                    Toast.makeText(context, "Id se esta cargando", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_usuarios, parent, false);
        return new ViewHolder(view);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImagePerfil;
        TextView txtUsuario;
        TextView txtCorreo;
        Button btnVerPerfil;
        View viewHolder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUsuario = itemView.findViewById(R.id.txtUsername);
            txtCorreo = itemView.findViewById(R.id.txtCorreo);
            circleImagePerfil = itemView.findViewById(R.id.circlePerfil);
            btnVerPerfil = itemView.findViewById(R.id.btnVerPerfil);
        }
    }
}
