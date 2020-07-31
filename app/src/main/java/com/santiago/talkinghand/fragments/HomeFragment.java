package com.santiago.talkinghand.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.activities.MainActivity;
import com.santiago.talkinghand.activities.PublicacionActivity;
import com.santiago.talkinghand.adapters.PublicacionesAdapter;
import com.santiago.talkinghand.models.Publicacion;
import com.santiago.talkinghand.providers.AuthProvider;
import com.santiago.talkinghand.providers.PublicacionProvider;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    View mView;
    FloatingActionButton mFabPublicacion;
    Toolbar mToolbar;
    RecyclerView mRecyclerView;
    PublicacionProvider mPublicacionProvider;
    PublicacionesAdapter publicacionesAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_home, container, false);

        mToolbar = mView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Mano Parlante");

        setHasOptionsMenu(true);

        mFabPublicacion = mView.findViewById(R.id.fabPublicacion);
        mFabPublicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irAPublicacion();
            }
        });

        mRecyclerView = mView.findViewById(R.id.recyclerViewHome);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mPublicacionProvider = new PublicacionProvider();

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query consulta = mPublicacionProvider.getTodos();
        FirestoreRecyclerOptions<Publicacion> options = new FirestoreRecyclerOptions.Builder<Publicacion>().
                setQuery(consulta, Publicacion.class).build();
        publicacionesAdapter = new PublicacionesAdapter(options, getContext());
        mRecyclerView.setAdapter(publicacionesAdapter);
        publicacionesAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        publicacionesAdapter.stopListening();
    }

    private void irAPublicacion() {
        Intent intent = new Intent(getContext(), PublicacionActivity.class);
        startActivity(intent);
    }

}