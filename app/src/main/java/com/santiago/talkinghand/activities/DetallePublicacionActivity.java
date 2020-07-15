package com.santiago.talkinghand.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.adapters.SliderAdapter;
import com.santiago.talkinghand.models.SliderItem;
import com.santiago.talkinghand.providers.PublicacionProvider;
import com.santiago.talkinghand.providers.UsuarioProvider;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetallePublicacionActivity extends AppCompatActivity {
    SliderView mSliderView;
    SliderAdapter mSliderAdapter;
    List<SliderItem> mSliderItems = new ArrayList<>();
    String mExtraPublicacionId;
    PublicacionProvider mPublicacionProvider;

    UsuarioProvider mUsuarioProvider;

    TextView mTextDescripcion;
    TextView mTextUsuario;
    TextView mTextCorreo;
    CircleImageView fotoPerfil;
    CircleImageView btnIrPublicaciones;
    Button btnVerPerfil;

    String mIdUsuario = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_publicacion);

        mSliderView = findViewById(R.id.imageSlider);
        mUsuarioProvider = new UsuarioProvider();
        mPublicacionProvider = new PublicacionProvider();
        mExtraPublicacionId = getIntent().getStringExtra("id");

        mTextDescripcion = findViewById(R.id.txtDescripcionPost);
        mTextUsuario = findViewById(R.id.txtUsernamePost);
        mTextCorreo = findViewById(R.id.txtCorreoPost);
        fotoPerfil = findViewById(R.id.circlePerfilPost);
        btnIrPublicaciones = findViewById(R.id.btnIrPublicaciones);
        btnVerPerfil = findViewById(R.id.btnVerPerfil);

        btnIrPublicaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnVerPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarPerfil();
            }
        });

        getPublicacion();
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