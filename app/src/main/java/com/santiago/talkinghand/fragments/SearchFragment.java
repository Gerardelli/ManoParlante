package com.santiago.talkinghand.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.adapters.UsuariosAdapter;
import com.santiago.talkinghand.models.Usuario;
import com.santiago.talkinghand.providers.UsuarioProvider;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener{
    View mView;
    RecyclerView mRecyclerView;
    UsuarioProvider mUsuarioProvider;
    UsuariosAdapter usuariosAdapter;
    UsuariosAdapter usuariosAdapterSearch;
    MaterialSearchBar mSearchBar;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_search, container, false);

        mRecyclerView = mView.findViewById(R.id.recyclerViewUsuarios);
        mSearchBar = mView.findViewById(R.id.searchBar);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mUsuarioProvider = new UsuarioProvider();

        mSearchBar.setOnSearchActionListener(this);

        return mView;
    }

    private void searchByUsuario(String usuario){
        Query consulta = mUsuarioProvider.getUsuarioByUsername(usuario);
        FirestoreRecyclerOptions<Usuario> options = new FirestoreRecyclerOptions.Builder<Usuario>().
                setQuery(consulta, Usuario.class).build();
        usuariosAdapterSearch = new UsuariosAdapter(options, getContext());
        usuariosAdapterSearch.notifyDataSetChanged();
        mRecyclerView.setAdapter(usuariosAdapterSearch);
        usuariosAdapterSearch.startListening();
    }

    private void obtenerUsuarios(){
        Query consulta = mUsuarioProvider.getUsuarios();
        FirestoreRecyclerOptions<Usuario> options = new FirestoreRecyclerOptions.Builder<Usuario>().
                setQuery(consulta, Usuario.class).build();
        usuariosAdapter = new UsuariosAdapter(options, getContext());
        usuariosAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(usuariosAdapter);
        usuariosAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        obtenerUsuarios();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuariosAdapter.stopListening();
        if(usuariosAdapterSearch != null ){
            usuariosAdapterSearch.stopListening();
        }
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if(!enabled){
            obtenerUsuarios();
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        searchByUsuario(text.toString());
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}