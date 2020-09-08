package com.santiago.talkinghand.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.activities.MainActivity;
import com.santiago.talkinghand.activities.PerfilActivity;
import com.santiago.talkinghand.adapters.MyPublicacionesAdapter;
import com.santiago.talkinghand.adapters.PublicacionesAdapter;
import com.santiago.talkinghand.models.Publicacion;
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
    MyPublicacionesAdapter mAdapter;

    CircleImageView fotoPerfil;
    TextView txtUsuario;
    TextView txtTelefono;
    TextView txtCorreo;
    TextView txtNumPublicaciones;
    TextView txtPublicaciones;
    RecyclerView mRecyclerView;

    String mUsuario = "";
    String mTelefono = "";
    String imagenPerfilURL = "";
    String mCorreo = "";

    ListenerRegistration mListener;

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
        txtPublicaciones = mView.findViewById(R.id.textPublicaciones);
        mRecyclerView = mView.findViewById(R.id.recyclerViewMyPublicacion);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);


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
        verificarPublicaciones();

        return mView;
    }

    private void verificarPublicaciones() {
        mListener = publicacionProvider.getPublicacionesUsuario(authProvider.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
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

    @Override
    public void onStart() {
        super.onStart();
        Query consulta = publicacionProvider.getPublicacionesUsuario(authProvider.getUid());
        FirestoreRecyclerOptions<Publicacion> options = new FirestoreRecyclerOptions.Builder<Publicacion>().
                setQuery(consulta, Publicacion.class).build();
        mAdapter = new MyPublicacionesAdapter(options, getContext());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mListener != null){
            mListener.remove();
        }
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