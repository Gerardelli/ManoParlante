package com.santiago.talkinghand.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.models.Usuario;
import com.santiago.talkinghand.providers.AuthProvider;
import com.santiago.talkinghand.providers.UsuarioProvider;
import java.util.Date;
import dmax.dialog.SpotsDialog;

public class Completar_Perfil_Activity extends AppCompatActivity {

    TextInputEditText mTextUsername;
    TextInputEditText mTextTelefono;
    Button mBtnRegistrar;
    AuthProvider mAuthProvider;
    UsuarioProvider mUsuarioProvider;
    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completar__perfil);

        mTextUsername = findViewById(R.id.txtUsuarioComplete);
        mTextTelefono = findViewById(R.id.txtTelefonoComplete);
        mBtnRegistrar = findViewById(R.id.btnComplete);

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Completando Perfil")
                .setCancelable(false).build();

        mAuthProvider = new AuthProvider();
        mUsuarioProvider = new UsuarioProvider();

        mBtnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualziar();
            }
        });
    }

    private void actualziar() {
        String username = mTextUsername.getText().toString();
        String telefono = mTextTelefono.getText().toString();

        if(!username.isEmpty() && !telefono.isEmpty()){
            updateUser(username, telefono);
        }else{
            Toast.makeText(this, "Ingresa los campos faltantes", Toast.LENGTH_LONG).show();
        }
    }

    private void updateUser(final String username, final String telefono) {

        String idUsuario = mAuthProvider.getUid();
        Usuario usuario = new Usuario();
        usuario.setUsuario(username);
        usuario.setTelefono(telefono);
        usuario.setTimeStamp(new Date().getTime());
        usuario.setUid(idUsuario);
        mDialog.show();
        mUsuarioProvider.updateUsuario(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task2) {
                mDialog.dismiss();
                if(task2.isSuccessful()){
                    Intent intent = new Intent(Completar_Perfil_Activity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    mDialog.dismiss();
                    Toast.makeText(Completar_Perfil_Activity.this, "Ocurrio un error al almacenar la información", Toast.LENGTH_LONG).show();
                }
            }
        });
        Toast.makeText(Completar_Perfil_Activity.this, "Cuenta creada correctamente", Toast.LENGTH_LONG).show();
    }
}