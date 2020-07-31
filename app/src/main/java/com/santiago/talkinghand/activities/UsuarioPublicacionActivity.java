package com.santiago.talkinghand.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.adapters.MyPublicacionesAdapter;
import com.santiago.talkinghand.models.Publicacion;
import com.santiago.talkinghand.providers.AuthProvider;
import com.santiago.talkinghand.providers.PublicacionProvider;
import com.santiago.talkinghand.providers.UsuarioProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsuarioPublicacionActivity extends AppCompatActivity {
    CircleImageView fotoPerfil;
    TextView txtUsuario;
    TextView txtTelefono;
    TextView txtCorreo;
    TextView txtNumPublicaciones;
    RecyclerView mRecyclerView;
    TextView txtPublicaciones;
    Toolbar mToolbar;

    AuthProvider authProvider;
    UsuarioProvider usuarioProvider;
    PublicacionProvider publicacionProvider;
    MyPublicacionesAdapter mAdapter;

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
        txtPublicaciones = findViewById(R.id.textPublicaciones);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtCorreo = findViewById(R.id.txtCoreoUsuario);
        txtNumPublicaciones = findViewById(R.id.numPublicaciones);
        mRecyclerView = findViewById(R.id.recyclerViewMyPublicacion);
        mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UsuarioPublicacionActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mExtraUser = getIntent().getStringExtra("idUser");
        obtenerUsuario();
        getNumeroPublicaciones();
        verificarPublicaciones();
    }

    @Override
    public void onStart() {
        super.onStart();
        Query consulta = publicacionProvider.getPublicacionesUsuario(mExtraUser);
        FirestoreRecyclerOptions<Publicacion> options = new FirestoreRecyclerOptions.Builder<Publicacion>().
                setQuery(consulta, Publicacion.class).build();
        mAdapter = new MyPublicacionesAdapter(options, UsuarioPublicacionActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    private void verificarPublicaciones() {
        publicacionProvider.getPublicacionesUsuario(mExtraUser).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                int numeroPublicaciones = queryDocumentSnapshots.size();
                if(numeroPublicaciones > 0){
                    txtPublicaciones.setText("Publicaciones");
                    txtPublicaciones.setTextColor(Color.rgb(0,121,107));
                }else{
                    txtPublicaciones.setText("No hay publicaciones");
                    txtPublicaciones.setTextColor(Color.GRAY);
                }
            }
        });
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }
}