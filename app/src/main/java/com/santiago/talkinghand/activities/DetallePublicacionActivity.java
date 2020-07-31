package com.santiago.talkinghand.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.adapters.ComentariosAdapter;
import com.santiago.talkinghand.adapters.PublicacionesAdapter;
import com.santiago.talkinghand.adapters.SliderAdapter;
import com.santiago.talkinghand.models.Comentario;
import com.santiago.talkinghand.models.SliderItem;
import com.santiago.talkinghand.providers.AuthProvider;
import com.santiago.talkinghand.providers.ComentarioProvider;
import com.santiago.talkinghand.providers.LikesProvider;
import com.santiago.talkinghand.providers.PublicacionProvider;
import com.santiago.talkinghand.providers.UsuarioProvider;
import com.santiago.talkinghand.utils.RelativeTime;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetallePublicacionActivity extends AppCompatActivity {
    SliderView mSliderView;
    SliderAdapter mSliderAdapter;
    List<SliderItem> mSliderItems = new ArrayList<>();
    String mExtraPublicacionId;

    PublicacionProvider mPublicacionProvider;
    LikesProvider mLikesProvider;
    UsuarioProvider mUsuarioProvider;
    ComentarioProvider mComentarioProvider;
    AuthProvider mAuthProvider;
    ComentariosAdapter comentariosAdapter;

    Toolbar mToolbar;
    TextView mTextDescripcion;
    TextView mTextUsuario;
    TextView mTextCorreo;
    TextView mTextNumeroLikes;
    TextView mTextRelativeTime;
    CircleImageView fotoPerfil;
    Button btnVerPerfil;
    FloatingActionButton btnComentario;
    RecyclerView recyclerViewComentario;

    String mIdUsuario = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_publicacion);

        mSliderView = findViewById(R.id.imageSlider);
        mUsuarioProvider = new UsuarioProvider();
        mPublicacionProvider = new PublicacionProvider();
        mComentarioProvider = new ComentarioProvider();
        mAuthProvider = new AuthProvider();
        mLikesProvider = new LikesProvider();
        mExtraPublicacionId = getIntent().getStringExtra("id");

        mTextDescripcion = findViewById(R.id.txtDescripcionPost);
        mTextUsuario = findViewById(R.id.txtUsernamePost);
        mTextCorreo = findViewById(R.id.txtCorreoPost);
        mTextNumeroLikes = findViewById(R.id.txtViewLikes);
        mTextRelativeTime = findViewById(R.id.txtRelativeTime);
        fotoPerfil = findViewById(R.id.circlePerfilPost);
        btnVerPerfil = findViewById(R.id.btnVerPerfil);
        btnComentario = findViewById(R.id.btnComentario);
        recyclerViewComentario = findViewById(R.id.recyclerViewComentarios);
        mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DetallePublicacionActivity.this);
        recyclerViewComentario.setLayoutManager(linearLayoutManager);

        btnComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogComentario();
            }
        });

        btnVerPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarPerfil();
            }
        });

        getPublicacion();
        getNumeroLikes();
    }

    private void getNumeroLikes() {
        mLikesProvider.getLikesByPublicacion(mExtraPublicacionId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                int numeroLikes = queryDocumentSnapshots.size();
                mTextNumeroLikes.setText(String.valueOf(numeroLikes) + " Me gusta");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query consulta = mComentarioProvider.getCommentByPublicacion(mExtraPublicacionId);
        FirestoreRecyclerOptions<Comentario> options = new FirestoreRecyclerOptions.Builder<Comentario>().
                setQuery(consulta, Comentario.class).build();
        comentariosAdapter = new ComentariosAdapter(options, DetallePublicacionActivity.this);
        recyclerViewComentario.setAdapter(comentariosAdapter);
        comentariosAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        comentariosAdapter.startListening();
    }

    private void mostrarDialogComentario() {

        AlertDialog.Builder alBuilder = new AlertDialog.Builder(DetallePublicacionActivity.this);
        alBuilder.setTitle("COMENTARIO");

        final EditText editText = new EditText(DetallePublicacionActivity.this);
        editText.setHint("Ingresa tu comentario aquí");

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(36, 0, 36, 36);
        editText.setLayoutParams(layoutParams);

        RelativeLayout container = new RelativeLayout(DetallePublicacionActivity.this);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        container.setLayoutParams(relativeParams);
        container.addView(editText);

        alBuilder.setView(container);
        alBuilder.setPositiveButton("Comentar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String value = editText.getText().toString();
                if(!value.isEmpty()){
                    crearComentario(value);
                }
                else{
                    Toast.makeText(DetallePublicacionActivity.this, "Debes ingresar el comentario", Toast.LENGTH_LONG).show();
                }

            }
        });

        alBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alBuilder.show();

    }

    private void crearComentario(String value) {
        Comentario comentario = new Comentario();
        comentario.setComentario(value);
        comentario.setIdPublicacion(mExtraPublicacionId);
        comentario.setIdUsuario(mAuthProvider.getUid());
        comentario.setTimeStamp(new Date().getTime());
        mComentarioProvider.create(comentario).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(DetallePublicacionActivity.this, "Comentario creado", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(DetallePublicacionActivity.this, "Comentario sin registrar", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void mostrarPerfil() {

        if(!mIdUsuario.equals("")){
            Intent intent = new Intent(DetallePublicacionActivity.this, UsuarioPublicacionActivity.class);
            intent.putExtra("idUser", mIdUsuario);
            startActivity(intent);
        }else {
            Toast.makeText(DetallePublicacionActivity.this, "Id se esta cargando", Toast.LENGTH_LONG).show();
        }
    }

    private void instanceSlider(){

        mSliderAdapter = new SliderAdapter(DetallePublicacionActivity.this, mSliderItems);
        mSliderView.setSliderAdapter(mSliderAdapter);
        mSliderView.setIndicatorAnimation(IndicatorAnimationType.THIN_WORM);
        mSliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        mSliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        mSliderView.setIndicatorSelectedColor(Color.WHITE);
        mSliderView.setIndicatorUnselectedColor(Color.GRAY);
        mSliderView.setScrollTimeInSec(2);
        mSliderView.setAutoCycle(true);
        mSliderView.startAutoCycle();

    }

    private void getPublicacion(){
        mPublicacionProvider.getPublicacionById(mExtraPublicacionId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("imagen")){
                        String imagen = documentSnapshot.getString("imagen");
                        SliderItem sliderItem = new SliderItem();
                        sliderItem.setImageURL(imagen);
                        mSliderItems.add(sliderItem);
                    }
                    if(documentSnapshot.contains("descripcion")){
                        String descripcion = documentSnapshot.getString("descripcion");
                        mTextDescripcion.setText(descripcion);
                    }
                    if(documentSnapshot.contains("idUsuario")){
                        mIdUsuario = documentSnapshot.getString("idUsuario");
                        informacionPerfil(mIdUsuario);
                    }
                    if(documentSnapshot.contains("timeStamp")){
                        long mRelativeTime = documentSnapshot.getLong("timeStamp");
                        String relativeTime = RelativeTime.getTimeAgo(mRelativeTime, DetallePublicacionActivity.this);
                        mTextRelativeTime.setText(relativeTime);
                    }
                }

                instanceSlider();

            }
        });
    }

    private void informacionPerfil(String idUser) {
        mUsuarioProvider.getUsuario(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("usuario")){
                        String usuario = documentSnapshot.getString("usuario");
                        mTextUsuario.setText(usuario);
                    }
                    if(documentSnapshot.contains("email")){
                        String correo = documentSnapshot.getString("email");
                        mTextCorreo.setText(correo);
                    }
                    if(documentSnapshot.contains("imagenPerfil")){
                        String iperfil = documentSnapshot.getString("imagenPerfil");
                        Picasso.with(DetallePublicacionActivity.this).load(iperfil).into(fotoPerfil);

                    }
                }
            }
        });
    }

}