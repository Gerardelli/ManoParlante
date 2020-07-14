package com.santiago.talkinghand.activities;

import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.models.Usuario;
import com.santiago.talkinghand.providers.AuthProvider;
import com.santiago.talkinghand.providers.ImagenProvider;
import com.santiago.talkinghand.providers.UsuarioProvider;
import com.santiago.talkinghand.utils.FileUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class PerfilActivity extends AppCompatActivity {
    CircleImageView btnBackPerfil;
    CircleImageView fotoPerfil;
    Button btnActualizarPerfil;
    TextInputEditText txtUsuario;
    TextInputEditText txtTelefono;

    AlertDialog.Builder mBuilderSeleccion;
    CharSequence options[];
    private final int GALERIA_REQUEST_CODE_PERFIL = 1;
    private final int FOTO_REQUEST_CODE_PERFIL = 2;

    String mAbsoluteFotoPath;
    String mFotoPath;
    File mFotoFile;
    File mImageFile;
    String mUsuario = "";
    String mTelefono = "";
    String imagenPerfilURL = "";

    AlertDialog mDialog;
    ImagenProvider imagenProvider;
    UsuarioProvider usuarioProvider;
    AuthProvider authProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        btnBackPerfil = findViewById(R.id.btnBackPerfil);
        fotoPerfil = findViewById(R.id.imagenPerfil);
        btnActualizarPerfil = findViewById(R.id.btnActualizarPerfil);
        txtUsuario = findViewById(R.id.txtUsuarioEditar);
        txtTelefono = findViewById(R.id.txtTelefonoEditar);

        usuarioProvider = new UsuarioProvider();
        authProvider = new AuthProvider();
        imagenProvider = new ImagenProvider();

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Actualizando perfil")
                .setCancelable(false).build();

        mBuilderSeleccion = new android.app.AlertDialog.Builder(this);
        mBuilderSeleccion.setTitle("Selecciona una opción");
        options = new CharSequence[] {"Desde Galería", "Tomar Foto"};

        btnBackPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarOpcionImagen(1);
            }
        });

        btnActualizarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPerfil();
            }
        });

        obtenerUsuario();

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
                    if(documentSnapshot.contains("imagenPerfil")){
                        imagenPerfilURL = documentSnapshot.getString("imagenPerfil");
                        if(imagenPerfilURL != null){
                            if(!imagenPerfilURL.isEmpty()){
                                Picasso.with(PerfilActivity.this).load(imagenPerfilURL).into(fotoPerfil);
                            }
                        }
                    }

                }
            }
        });
    }

    private void clickPerfil() {
        mDialog.show();
        mUsuario = txtUsuario.getText().toString();
        mTelefono = txtTelefono.getText().toString();

        if(!mUsuario.isEmpty() && !mTelefono.isEmpty()){
            if(mImageFile != null){
                guardarImagen(mImageFile);
            }
            else if(mFotoFile != null){
                guardarImagen(mFotoFile);
            }
            else {
                Usuario usuario = new Usuario();
                usuario.setUsuario(mUsuario);
                usuario.setTelefono(mTelefono);
                usuario.setImagenPerfil(imagenPerfilURL);
                usuario.setUid(authProvider.getUid());
                actualizarInfoUsuario(usuario);
            }
        }else{
            Toast.makeText(PerfilActivity.this, "Completa la descripción", Toast.LENGTH_LONG).show();
        }
    }

    private void actualizarInfoUsuario(Usuario usuario){
        if(mDialog.isShowing()){
            mDialog.show();
        }
        usuarioProvider.updateUsuario(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> taskGuardar) {
                mDialog.dismiss();
                if(taskGuardar.isSuccessful()){
                    Toast.makeText(PerfilActivity.this, "Informacion almacenada", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(PerfilActivity.this, "Informacion no fue almacenada", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void guardarImagen(File imageFile) {
        mDialog.show();
        imagenProvider.guardar(PerfilActivity.this, imageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    imagenProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Usuario usuario = new Usuario();
                            usuario.setImagenPerfil(url);
                            usuario.setUsuario(mUsuario);
                            usuario.setTelefono(mTelefono);
                            usuario.setUid(authProvider.getUid());
                            actualizarInfoUsuario(usuario);
                        }
                    });
                    Toast.makeText(PerfilActivity.this, "Foto almacenada correctamente", Toast.LENGTH_LONG).show();
                }else{
                    mDialog.dismiss();
                    Toast.makeText(PerfilActivity.this, "Hubo un error al almacenar la imagen", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void seleccionarOpcionImagen(final int numeroImagen) {
        mBuilderSeleccion.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    if(numeroImagen ==1){
                        abrirGaleria(GALERIA_REQUEST_CODE_PERFIL);
                    }
                }else if(i == 1){
                    if(numeroImagen ==1){
                        tomarFoto(FOTO_REQUEST_CODE_PERFIL);
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
                Uri fotoUri = FileProvider.getUriForFile(PerfilActivity.this, "com.santiago.talkinghand", fotoFile);
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

        if(requestCode == FOTO_REQUEST_CODE_PERFIL) {
            mFotoPath = "file:" + photoFile.getAbsolutePath();
            mAbsoluteFotoPath = photoFile.getAbsolutePath();
        }
        return photoFile;
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
        if(requestCode == GALERIA_REQUEST_CODE_PERFIL && resultCode == RESULT_OK){
            try{
                mFotoFile = null;
                mImageFile = FileUtil.from(this, data.getData());
                fotoPerfil.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));

            }catch (Exception e){
                Log.d("Error", "Se produjo un error" + e.getMessage());
                Toast.makeText(this, "Se produjo un error" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        /**
         * FOTO DESDE CÁMARA
         */
        if(requestCode == FOTO_REQUEST_CODE_PERFIL && resultCode == RESULT_OK){
            mImageFile = null;
            mFotoFile = new File(mAbsoluteFotoPath);
            Picasso.with(PerfilActivity.this).load(mFotoPath).into(fotoPerfil);
        }
    }


}