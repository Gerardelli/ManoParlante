package com.santiago.talkinghand.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.models.Video;
import com.santiago.talkinghand.providers.AuthProvider;
import com.santiago.talkinghand.providers.VideoProvider;

import java.io.File;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class VideoActivity extends AppCompatActivity {
    ImageView imgVideo;
    Button btnPublicar;
    VideoView vidVideo;
    TextInputEditText txtDescripcion;
    CircleImageView mCircleImageBack;

    //VARIABLES GLOBALES
    AuthProvider mAuthProvider;
    VideoProvider mVideoProvider;
    AlertDialog mDialog;
    String mDescripcion = "";
    File mClipFile;
    CharSequence options[];
    private StorageReference mStorage;

    AlertDialog.Builder mBuilderSeleccion;
    private final int CLIP_REQUEST_CODE = 1;
    private final int RECORDER_REQUEST_CODE = 2;
    Uri videoUri = null;
    Uri clipUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        mCircleImageBack = findViewById(R.id.btnBackVideos);
        imgVideo = findViewById(R.id.imgSubirVideo);
        vidVideo = findViewById(R.id.vidVideo);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        btnPublicar = findViewById(R.id.btnPublicar);
        mAuthProvider = new AuthProvider();
        mVideoProvider = new VideoProvider();
        mStorage = FirebaseStorage.getInstance().getReference();


        mCircleImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Guardando publicación")
                .setCancelable(false).build();

        mBuilderSeleccion = new AlertDialog.Builder(this);
        mBuilderSeleccion.setTitle("Selecciona una opción");
        options = new CharSequence[] {"Desde Galería", "Grabar Video"};

        imgVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarOpcionVideo(1);
            }
        });

        btnPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPublicacion();
            }
        });
    }

    private void clickPublicacion() {
        mDescripcion = txtDescripcion.getText().toString();

        if(!mDescripcion.isEmpty()){
            if(clipUri != null){
                guardarVideo(clipUri);
            }
            else if(videoUri != null){
                guardarVideo(videoUri);
            }
            else {
                Toast.makeText(VideoActivity.this, "Selecciona un video", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(VideoActivity.this, "Completa la descripción", Toast.LENGTH_LONG).show();
        }
    }

    private void seleccionarOpcionVideo(final int numeroVideo) {
        mBuilderSeleccion.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    if(numeroVideo ==1){
                        abrirGaleria(CLIP_REQUEST_CODE);
                    }
                }else if(i == 1){
                    if(numeroVideo ==1){
                        grabarVideo(RECORDER_REQUEST_CODE);
                    }
                }
            }
        });

        mBuilderSeleccion.show();
    }

    private void grabarVideo(int requestCode) {
        Intent tomarVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if(tomarVideoIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(tomarVideoIntent, requestCode);
        }
    }


    private void abrirGaleria(int requestCode) {
        Intent videoPickIntent = new Intent(Intent.ACTION_PICK);
        videoPickIntent.setType("video/*");
        startActivityForResult(videoPickIntent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode==CLIP_REQUEST_CODE && resultCode == RESULT_OK){
            try {
                imgVideo.setVisibility(View.GONE);
                clipUri = intent.getData();
                Toast.makeText(this, ""+clipUri, Toast.LENGTH_LONG).show();
                vidVideo.setVideoURI(clipUri);
                vidVideo.start();

            }catch (Exception e){
                Log.d("Error", "Se produjo un error" + e.getMessage());
                Toast.makeText(this, "Se produjo un error" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        if(requestCode == RECORDER_REQUEST_CODE && resultCode == RESULT_OK){
            try {
                imgVideo.setVisibility(View.GONE);
                videoUri = intent.getData();
                Toast.makeText(this, ""+videoUri, Toast.LENGTH_LONG).show();
                vidVideo.setVideoURI(videoUri);
                vidVideo.start();
            }catch (Exception e){
                Log.d("Error", "Se produjo un error" + e.getMessage());
                Toast.makeText(this, "Se produjo un error" + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
    }

    private void guardarVideo(Uri uri) {
        mDialog.show();
        final StorageReference filePath = mStorage.child("videos").child(uri.getLastPathSegment());
        filePath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Video video = new Video();
                            video.setVideo(url);
                            video.setDescripcion(mDescripcion);
                            video.setIdUsuario(mAuthProvider.getUid());
                            video.setTimeStamp(new Date().getTime());
                            mVideoProvider.guardarVideo(video).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> taskGuardar) {
                                    mDialog.dismiss();
                                    if(taskGuardar.isSuccessful()){
                                        limpiarFormulario();
                                        Toast.makeText(VideoActivity.this, "Informacion almacenada", Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(VideoActivity.this, "Informacion no fue almacenada", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    });
                    Toast.makeText(VideoActivity.this, "Video almacenado correctamente", Toast.LENGTH_LONG).show();
                }else {
                    mDialog.dismiss();
                    Toast.makeText(VideoActivity.this, "Hubo un error al almacenar la imagen", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void limpiarFormulario() {
        txtDescripcion.setText("");
        imgVideo.setVisibility(View.VISIBLE);
        mDescripcion = "";
        mClipFile = null;
    }
}