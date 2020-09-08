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
import com.google.firebase.auth.AuthResult;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.models.Usuario;
import com.santiago.talkinghand.providers.AuthProvider;
import com.santiago.talkinghand.providers.UsuarioProvider;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class RegistrarActivity extends AppCompatActivity {

    CircleImageView mCircleImageViewBack;
    TextInputEditText mTextUsername;
    TextInputEditText mTextMail;
    TextInputEditText mTextPass;
    Button mBtnRegistrar;
    AuthProvider mAuthProvider;
    UsuarioProvider mUsuarioProvider;
    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        mCircleImageViewBack = findViewById(R.id.btnBack);
        mTextUsername = findViewById(R.id.txtUsuario);
        mTextMail = findViewById(R.id.txtCorreo);
        mTextPass = findViewById(R.id.txtContrasena);
        mBtnRegistrar = findViewById(R.id.btnRegistrar);

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Creando Usuario")
                .setCancelable(false).build();

        mAuthProvider = new AuthProvider();
        mUsuarioProvider = new UsuarioProvider();

        mBtnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrar();
            }
        });

        mCircleImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrarActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void registrar() {
        String username = mTextUsername.getText().toString();
        String email = mTextMail.getText().toString();
        String pass = mTextPass.getText().toString();

        if(!username.isEmpty() && !email.isEmpty() && !pass.isEmpty()){
            if(isEmailValid(email)){
                if(pass.length() >=6 ){
                    createUser(username, email, pass);
                }else{
                    Toast.makeText(this, "La contraseña debe ser al menos 6 caracteres", Toast.LENGTH_LONG).show();
                }
                Toast.makeText(this, "Campos completados correctamente", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Revisa los campos", Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(this, "Ingresa los campos faltantes", Toast.LENGTH_LONG).show();
        }
    }

    private void createUser(final String username, final String email, final String password) {
        mDialog.show();
        mAuthProvider.registrar(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String idUsuario = mAuthProvider.getUid();
                    Usuario usuario = new Usuario();
                    usuario.setUid(idUsuario);
                    usuario.setEmail(email);
                    usuario.setUsuario(username);
                    usuario.setTimeStamp(new Date().getTime());
                    mUsuarioProvider.create(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            mDialog.dismiss();
                            if(task2.isSuccessful()){
                                Intent intent = new Intent(RegistrarActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }else{
                                Toast.makeText(RegistrarActivity.this, "Ocurrio un error al almacenar la información", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    Toast.makeText(RegistrarActivity.this, "Cuenta creada correctamente", Toast.LENGTH_LONG).show();
                }else{
                    mDialog.dismiss();
                    Toast.makeText(RegistrarActivity.this, "No se pudo crear la cuenta", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public boolean isEmailValid(String email){
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}