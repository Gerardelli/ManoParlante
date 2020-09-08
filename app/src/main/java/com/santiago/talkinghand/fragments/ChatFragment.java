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

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.adapters.ChatsAdapter;
import com.santiago.talkinghand.adapters.PublicacionesAdapter;
import com.santiago.talkinghand.models.Chat;
import com.santiago.talkinghand.models.Publicacion;
import com.santiago.talkinghand.providers.AuthProvider;
import com.santiago.talkinghand.providers.ChatsProvider;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    ChatsAdapter mChatsAdapter;
    RecyclerView mRecyclerView;
    ChatsProvider mChatsProvider;
    AuthProvider mAuthProvider;

    Toolbar mToolbar;
    View mView;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_chat, container, false);
        mRecyclerView = mView.findViewById(R.id.recyclerViewChats);
        mChatsProvider = new ChatsProvider();
        mAuthProvider = new AuthProvider();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mToolbar = mView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Chats");

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query consulta = mChatsProvider.getTodos(mAuthProvider.getUid());
        FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>().
                setQuery(consulta, Chat.class).build();
        mChatsAdapter = new ChatsAdapter(options, getContext());
        mRecyclerView.setAdapter(mChatsAdapter);
        mChatsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mChatsAdapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mChatsAdapter.obtenerListener() != null){
            mChatsAdapter.obtenerListener().remove();
        }

        if(mChatsAdapter.obtenerListenerUltimoMensaje() != null){
            mChatsAdapter.obtenerListenerUltimoMensaje().remove();
        }
    }
}