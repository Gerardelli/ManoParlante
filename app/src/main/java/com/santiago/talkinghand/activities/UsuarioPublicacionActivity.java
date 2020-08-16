package com.santiago.talkinghand.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
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
    FloatingActionButton mFabChat;

    AuthProvider authProvider;
    UsuarioProvider usuarioProvider;
    PublicacionProvider publicacionProvider;
    MyPublicacionesAdapter mAdapter;

    String mUsuario = "";
    String mTelefono = "";
    String imagenPerfilURL = "";
    String mCorreo = "";

    String mExtraUser;
    ListenerRegistration mListener;

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
        mFabChat = findViewById(R.id.fabChat);
        mExtraUser = getIntent().getStringExtra("idUser");

        if(authProvider.getUid().equals(mExtraUser)){
            mFabChat.hide();
        }

        mFabChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irAlChat();
            }
        });

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UsuarioPublicacionActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        obtenerUsuario();
        getNumeroPublicaciones();
        verificarPublicaciones();
    }

    private void irAlChat() {
        Intent intent = new Intent(UsuarioPublicacionActivity.this, ChatActivity.class);
        intent.putExtra("idUsuario1", authProvider.getUid());
        intent.putExtra("idUsuario2", mExtraUser); //id del usuario actual
        intent.putExtra("idChat", authProvider.getUid()+mExtraUser);
        startActivity(intent);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mListener != null){
            mListener.remove();
        }
    }

    private void verificarPublicaciones() {
        mListener = publicacionProvider.getPublicacionesUsuario(mExtraUser).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots != null){
                    int numeroPublicaciones = queryDocumentSnapshots.size();
                    if(numeroPublicaciones > 0){
                        txtPublicaciones.setText("Publicaciones");
                        txtPublicaciones.setTextColor(Color.rgb(0,121,107));
                    }else{
                        txtPublicaciones.setText("No hay publicaciones");
                        txtPublicaciones.setTextColor(Color.GRAY);
                    }
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