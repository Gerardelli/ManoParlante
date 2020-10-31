package com.santiago.talkinghand.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.fragments.ChatFragment;
import com.santiago.talkinghand.fragments.HomeFragment;
import com.santiago.talkinghand.fragments.InformacionFragment;
import com.santiago.talkinghand.fragments.PerfilFragment;
import com.santiago.talkinghand.fragments.SearchFragment;
import com.santiago.talkinghand.providers.AuthProvider;
import com.santiago.talkinghand.providers.TokenProvider;
import com.santiago.talkinghand.providers.UsuarioProvider;
import com.santiago.talkinghand.utils.HelperMensajeVisto;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigation;
    TokenProvider mTokenProvider;
    AuthProvider mAuthProvider;
    UsuarioProvider mUsuarioProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mTokenProvider = new TokenProvider();
        mAuthProvider = new AuthProvider();
        mUsuarioProvider = new UsuarioProvider();

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(new HomeFragment());
        createToken();
    }

    @Override
    protected void onStart() {
        super.onStart();
        HelperMensajeVisto.actualizarEnlinea(true, HomeActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        HelperMensajeVisto.actualizarEnlinea(false, HomeActivity.this);
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if(item.getItemId() == R.id.itemInicio){
                        //FRAGMENT INICIO
                        openFragment(new HomeFragment());
                    }else if (item.getItemId() == R.id.itemBusquedas){
                        //FRAGMENT BUSQUEDAS
                        openFragment(new SearchFragment());
                    }else if (item.getItemId() == R.id.itemMensajes){
                        //FRAGMENT MENSAJES
                        openFragment(new ChatFragment());
                    }else if (item.getItemId() == R.id.itemInfo){
                        //FRAGMENT INFORMCIÓN
                        openFragment(new InformacionFragment());
                    }else if (item.getItemId() == R.id.itemPerfil){
                        // FRAGMENT PERFIL
                        openFragment(new PerfilFragment());
                    }
                    return true;
                }
            };

    private void createToken(){
        mTokenProvider.crearToken(mAuthProvider.getUid());
    }
}