package com.santiago.talkinghand.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.models.Publicacion;
import com.santiago.talkinghand.providers.AuthProvider;
import com.santiago.talkinghand.providers.ImagenProvider;
import com.santiago.talkinghand.providers.PublicacionProvider;
import com.santiago.talkinghand.utils.FileUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class PublicacionActivity extends AppCompatActivity {
    //COMPONENTES DE PANTALLA
    ImageView imgPhoto;
    Button btnPublicar;
    ImagenProvider mImagenProvider;
    TextInputEditText txtDescripcion;
    CircleImageView mCircleImageBack;

    //VARIABLES GLOBALES
    AuthProvider mAuthProvider;
    PublicacionProvider mPubProvider;
    AlertDialog mDialog;
    String mDescripcion = "";
    String mAbsoluteFotoPath;
    String mFotoPath;
    File mImageFile;
    File mFotoFile;
    CharSequence options[];

    AlertDialog.Builder mBuilderSeleccion;
    private final int GALERIA_REQUEST_CODE = 1;
    private final int PHOTO_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicacion);

        mCircleImageBack = findViewById(R.id.btnBackHome);
        mImagenProvider = new ImagenProvider();
        imgPhoto = findViewById(R.id.imgSubirPhoto);
        btnPublicar = findViewById(R.id.btnPublicar);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        mPubProvider = new PublicacionProvider();
        mAuthProvider = new AuthProvider();

        mCircleImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(PublicacionActivity.this, HomeActivity.class);
                startActivity(home);
                finish();
            }
        });

        btnPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPublicacion();
            }
        });

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Guardando publicación")
                .setCancelable(false).build();

        mBuilderSeleccion = new AlertDialog.Builder(this);
        mBuilderSeleccion.setTitle("Selecciona una opción");
        options = new CharSequence[] {"Desde Galería", "Tomar Foto"};

        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarOpcionImagen(1);
            }
        });
    }

    private void seleccionarOpcionImagen(final int numeroImagen) {
        mBuilderSeleccion.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    if(numeroImagen ==1){
                        abrirGaleria(GALERIA_REQUEST_CODE);
                    }
                }else if(i == 1){
                    if(numeroImagen ==1){
                       tomarFoto(PHOTO_REQUEST_CODE);
                    }
                }
            }
        });

        mBuilderSeleccion.show();
    }

    private void tomarFoto(int requestCode) {
        Intent tomarFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(tomarFotoIntent.resolveActivity(getPackageManager()) != null){
            File fotoFile = null;
            try{
                fotoFile = createFotoFile(requestCode);
            }catch (Exception e){
                Toast.makeText(this, "Hubo un error con el archivo" + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            if(fotoFile != null){
                Uri fotoUri = FileProvider.getUriForFile(PublicacionActivity.this, "com.santiago.talkinghand", fotoFile);
                tomarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
                startActivityForResult(tomarFotoIntent, requestCode); //VERIFICAR SI MARCA ALGÚN ERROR
            }
        }
    }

    private File createFotoFile(int requestCode) throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(
                new Date() + "_photo",
                ".jpg",
                storageDir
        );

        if(requestCode == PHOTO_REQUEST_CODE) {
            mFotoPath = "file:" + photoFile.getAbsolutePath();
            mAbsoluteFotoPath = photoFile.getAbsolutePath();
        }
        return photoFile;
    }

    private void clickPublicacion() {
        mDescripcion = txtDescripcion.getText().toString();

        if(!mDescripcion.isEmpty()){
            if(mImageFile != null){
                guardarImagen(mImageFile);
            }
            else if(mFotoFile != null){
                guardarImagen(mFotoFile);
            }
            else {
                Toast.makeText(PublicacionActivity.this, "Selecciona una imagen", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(PublicacionActivity.this, "Completa la descripción", Toast.LENGTH_LONG).show();
        }
    }

    private void guardarImagen(File imageFile) {
        mDialog.show();
        mImagenProvider.guardar(PublicacionActivity.this, imageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    mImagenProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Publicacion publicacion = new Publicacion();
                            publicacion.setImagen(url);
                            publicacion.setDescripcion(mDescripcion);
                            publicacion.setIdUsuario(mAuthProvider.getUid());
                            publicacion.setTimeStamp(new Date().getTime());
                            mPubProvider.guardarPublicacion(publicacion).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> taskGuardar) {
                                    mDialog.dismiss();
                                    if(taskGuardar.isSuccessful()){
                                        limpiarFormulario();
                                        Toast.makeText(PublicacionActivity.this, "Informacion almacenada", Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(PublicacionActivity.this, "Informacion no fue almacenada", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    });
                    Toast.makeText(PublicacionActivity.this, "Foto almacenada correctamente", Toast.LENGTH_LONG).show();
                }else{
                    mDialog.dismiss();
                    Toast.makeText(PublicacionActivity.this, "Hubo un error al almacenar la imagen", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void limpiarFormulario() {
        txtDescripcion.setText("");
        imgPhoto.setImageResource(R.drawable.camara);
        mDescripcion = "";
        mImageFile = null;
    }

    private void abrirGaleria(int requestCode) {
        Intent galeria = new Intent(Intent.ACTION_GET_CONTENT);
        galeria.setType("image/*");
        startActivityForResult(galeria, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * SELECCION DE IMAGEN DESDE GALERIA
         */
        if(requestCode == GALERIA_REQUEST_CODE && resultCode == RESULT_OK){
            try{
                mFotoFile = null;
                mImageFile = FileUtil.from(this, data.getData());
                imgPhoto.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));

            }catch (Exception e){
                Log.d("Error", "Se produjo un error" + e.getMessage());
                Toast.makeText(this, "Se produjo un error" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        /**
         * FOTO DESDE CÁMARA
         */
        if(requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK){
            mImageFile = null;
            mFotoFile = new File(mAbsoluteFotoPath);
            Picasso.with(PublicacionActivity.this).load(mFotoPath).into(imgPhoto);
        }
    }
}