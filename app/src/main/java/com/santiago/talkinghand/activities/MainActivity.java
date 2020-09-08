package com.santiago.talkinghand.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.models.Usuario;
import com.santiago.talkinghand.providers.AuthProvider;
import com.santiago.talkinghand.providers.UsuarioProvider;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {
    TextView mTxtRegistrar;
    TextInputEditText mTextEmail;
    TextInputEditText mTextPassword;
    Button mBtnLogin;
    AuthProvider mAuthProvider;
    UsuarioProvider mUsuarioProvider;
    private GoogleSignInClient mGoogleSignInClient;
    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTxtRegistrar = findViewById(R.id.txtRegistrar);
        mTextEmail = findViewById(R.id.txtEmail);
        mTextPassword = findViewById(R.id.txtPassword);
        mBtnLogin = findViewById(R.id.btnLogin);
        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Iniciando Sesión")
                .setCancelable(false).build();

        mAuthProvider = new AuthProvider();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mUsuarioProvider = new UsuarioProvider();
        
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        mTxtRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegistrarActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuthProvider.getUserSession() != null){
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void login() {
        final String email = mTextEmail.getText().toString();
        final String password = mTextPassword.getText().toString();

        if(email.isEmpty() && password.isEmpty()){
            Toast.makeText(MainActivity.this, "Ingresar correo y contraseña", Toast.LENGTH_LONG).show();
        }else if(email.isEmpty()){
            Toast.makeText(MainActivity.this, "Ingresar correo", Toast.LENGTH_LONG).show();
        }else if(password.isEmpty()){
            Toast.makeText(MainActivity.this, "Ingresar contraseña", Toast.LENGTH_LONG).show();
        }else {
            mDialog.show();
            mAuthProvider.login(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    mDialog.dismiss();
                    if(task.isSuccessful()){
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(MainActivity.this, "El correo o la contraseña con incorrectos", Toast.LENGTH_LONG).show();
                    }
                }
            });
            Log.d("campo", "email: " + email);
            Log.d("campo", "password: " + password);
        }
    }
}