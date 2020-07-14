package com.santiago.talkinghand.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.activities.MainActivity;
import com.santiago.talkinghand.activities.PerfilActivity;
import com.santiago.talkinghand.providers.AuthProvider;
import com.santiago.talkinghand.providers.PublicacionProvider;
import com.santiago.talkinghand.providers.UsuarioProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilFragment extends Fragment {
    View mView;
    LinearLayout mLinearLayoutPerfilEditar;
    LinearLayout mLinearLayoutSalir;
    AuthProvider authProvider;
    UsuarioProvider usuarioProvider;
    PublicacionProvider publicacionProvider;

    CircleImageView fotoPerfil;
    TextView txtUsuario;
    TextView txtTelefono;
    TextView txtCorreo;
    TextView txtNumPublicaciones;

    String mUsuario = "";
    String mTelefono = "";
    String imagenPerfilURL = "";
    String mCorreo = "";

    public PerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_perfil, container, false);

        authProvider = new AuthProvider();
        usuarioProvider = new UsuarioProvider();
        publicacionProvider = new PublicacionProvider();

        fotoPerfil = mView.findViewById(R.id.imagenUsuario);
        txtUsuario = mView.findViewById(R.id.txtNombreUsuario);
        txtTelefono = mView.findViewById(R.id.txtTelefono);
        txtCorreo = mView.findViewById(R.id.txtCoreoUsuario);
        txtNumPublicaciones = mView.findViewById(R.id.numPublicaciones);


        mLinearLayoutPerfilEditar = mView.findViewById(R.id.editPerfil);
        mLinearLayoutPerfilEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irAPerfil();
            }
        });

        mLinearLayoutSalir = mView.findViewById(R.id.cerrarSesion);
        mLinearLayoutSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cerrarSesion();
            }
        });

        obtenerUsuario();
        getNumeroPublicaciones();

        return mView;
    }

    private void obtenerUsuario(){
        usuarioProvider.getUsuario(authProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("usuario")){
                        mUsuario = documentSnapshot.getString("usuario");
                        txtUsuario.setText(mUsuario);
                    }
                    if(documentSnapshot.contains("telefono")){
                        mTelefono = documentSnapshot.getString("telefono");
                        txtTelefono.setText(mTelefono);
                    }
                    if(documentSnapshot.contains("email")){
                        mCorreo = documentSnapshot.getString("email");
                        txtCorreo.setText(mCorreo);
                    }
                    if(documentSnapshot.contains("imagenPerfil")){
                        imagenPerfilURL = documentSnapshot.getString("imagenPerfil");
                        if(imagenPerfilURL != null){
                            if(!imagenPerfilURL.isEmpty()){
                                Picasso.with(getContext()).load(imagenPerfilURL).into(fotoPerfil);
                            }
                        }
                    }
                }
            }
        });
    }

    public void getNumeroPublicaciones(){
        publicacionProvider.getPublicacionesUsuario(authProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numeroPublicaciones = queryDocumentSnapshots.size();
                txtNumPublicaciones.setText(String.valueOf(numeroPublicaciones));
            }
        });

    }

    private void cerrarSesion() {
        authProvider.cerrarSesion();
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void irAPerfil() {
        Intent intent = new Intent(getContext(), PerfilActivity.class);
        startActivity(intent);
    }
}