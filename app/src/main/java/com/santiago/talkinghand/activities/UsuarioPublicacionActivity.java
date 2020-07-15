package com.santiago.talkinghand.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.providers.AuthProvider;
import com.santiago.talkinghand.providers.PublicacionProvider;
import com.santiago.talkinghand.providers.UsuarioProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsuarioPublicacionActivity extends AppCompatActivity {
    CircleImageView fotoPerfil;
    CircleImageView btnBack;
    TextView txtUsuario;
    TextView txtTelefono;
    TextView txtCorreo;
    TextView txtNumPublicaciones;

    AuthProvider authProvider;
    UsuarioProvider usuarioProvider;
    PublicacionProvider publicacionProvider;

    String mUsuario = "";
    String mTelefono = "";
    String imagenPerfilURL = "";
    String mCorreo = "";

    String mExtraUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_publicacion);

        authProvider = new AuthProvider();
        usuarioProvider = new UsuarioProvider();
        publicacionProvider = new PublicacionProvider();

        fotoPerfil = findViewById(R.id.imagenUsuario);
        txtUsuario = findViewById(R.id.txtNombreUsuario);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtCorreo = findViewById(R.id.txtCoreoUsuario);
        txtNumPublicaciones = findViewById(R.id.numPublicaciones);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mExtraUser = getIntent().getStringExtra("idUser");
        obtenerUsuario();
        getNumeroPublicaciones();
    }

    private void obtenerUsuario(){
        usuarioProvider.getUsuario(mExtraUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                                Picasso.with(UsuarioPublicacionActivity.this).load(imagenPerfilURL).into(fotoPerfil);
                            }
                        }
                    }
                }
            }
        });
    }

    public void getNumeroPublicaciones(){
        publicacionProvider.getPublicacionesUsuario(mExtraUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numeroPublicaciones = queryDocumentSnapshots.size();
                txtNumPublicaciones.setText(String.valueOf(numeroPublicaciones));
            }
        });

    }
}