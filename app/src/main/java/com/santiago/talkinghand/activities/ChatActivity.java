package com.santiago.talkinghand.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.adapters.MensajeAdapter;
import com.santiago.talkinghand.adapters.MyPublicacionesAdapter;
import com.santiago.talkinghand.models.Chat;
import com.santiago.talkinghand.models.Mensaje;
import com.santiago.talkinghand.providers.AuthProvider;
import com.santiago.talkinghand.providers.ChatsProvider;
import com.santiago.talkinghand.providers.MensajeProvider;
import com.santiago.talkinghand.providers.UsuarioProvider;
import com.santiago.talkinghand.utils.HelperMensajeVisto;
import com.santiago.talkinghand.utils.RelativeTime;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    String mExtraIdUsuario1;
    String mExtraIdUsuario2;
    String mExtraIdChat;

    ChatsProvider mChatsProvider;
    UsuarioProvider mUsuarioProvider;
    MensajeProvider mMensajeProvider;
    AuthProvider mAuthProvider;
    MensajeAdapter mMensajeAdapter;

    EditText mTextMensaje;
    ImageView mImageEnviarMensaje;

    CircleImageView mCircleImagePerfil;
    TextView mTextViewUsuario;
    TextView mTextViewRelativeTime;
    ImageView mImageViewBack;
    RecyclerView mRecyclerViewMensaje;
    LinearLayoutManager mLinearLayoutManager;

    View mActionBarView;
    ListenerRegistration mListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mChatsProvider = new ChatsProvider();
        mMensajeProvider = new MensajeProvider();
        mAuthProvider = new AuthProvider();
        mUsuarioProvider = new UsuarioProvider();

        mTextMensaje = findViewById(R.id.txtEnviarMensaje);
        mImageEnviarMensaje = findViewById(R.id.imgEnviarMensaje);
        mRecyclerViewMensaje = findViewById(R.id.recyclerViewMensajes);

        mLinearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerViewMensaje.setLayoutManager(mLinearLayoutManager);

        mExtraIdUsuario1 = getIntent().getStringExtra("idUsuario1");
        mExtraIdUsuario2 = getIntent().getStringExtra("idUsuario2");
        mExtraIdChat  = getIntent().getStringExtra("idChat");

        mostrarToolbarPersonalizado(R.layout.toolbar_chat_perzonalizado);

        verificarChatExistente();
        mImageEnviarMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarMensaje();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        HelperMensajeVisto.actualizarEnlinea(false, ChatActivity.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        HelperMensajeVisto.actualizarEnlinea(true, ChatActivity.this);
        Query consulta = mMensajeProvider.obtenerMensajesByChat(mExtraIdChat);
        FirestoreRecyclerOptions<Mensaje> options = new FirestoreRecyclerOptions.Builder<Mensaje>().
                setQuery(consulta, Mensaje.class).build();
        mMensajeAdapter = new MensajeAdapter(options, ChatActivity.this);
        mRecyclerViewMensaje.setAdapter(mMensajeAdapter);
        mMensajeAdapter.startListening();
        mMensajeAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                actualizarVisto();
                int numberoMensajes = mMensajeAdapter.getItemCount();
                int ultimoMensaje = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if(ultimoMensaje == -1 || (positionStart >= (numberoMensajes -1) &&ultimoMensaje == (positionStart -1))){
                    mRecyclerViewMensaje.scrollToPosition(positionStart);
                }
            }
        });
    }



    @Override
    public void onStop() {
        super.onStop();
        mMensajeAdapter.stopListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mListener != null){
            mListener.remove();
        }
    }

    private void enviarMensaje() {
        String txtmensaje = mTextMensaje.getText().toString();
        if(!txtmensaje.isEmpty()){
            Mensaje mensaje = new Mensaje();
            mensaje.setIdChat(mExtraIdChat);
            if(mAuthProvider.getUid().equals(mExtraIdUsuario1)){
                mensaje.setIdEmisor(mExtraIdUsuario1);
                mensaje.setIdReceptor(mExtraIdUsuario2);
            }else{
                mensaje.setIdEmisor(mExtraIdUsuario2);
                mensaje.setIdReceptor(mExtraIdUsuario1);
            }
            mensaje.setTimeStamp(new Date().getTime());
            mensaje.setVisto(false);
            mensaje.setIdChat(mExtraIdChat);
            mensaje.setMensaje(txtmensaje);

            mMensajeProvider.crear(mensaje).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        mTextMensaje.setText("");
                        mMensajeAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    private void mostrarToolbarPersonalizado(int resorce) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mActionBarView = inflater.inflate(resorce, null);
        actionBar.setCustomView(mActionBarView);
        mCircleImagePerfil = mActionBarView.findViewById(R.id.circlePerfil);
        mTextViewUsuario = mActionBarView.findViewById(R.id.txtUsuario);
        mTextViewRelativeTime = mActionBarView.findViewById(R.id.txtRelativeTime);
        mImageViewBack = mActionBarView.findViewById(R.id.imgBack);
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        
        obtenerInfoUsuario();

    }

    private void obtenerInfoUsuario() {
        String idUsuarioInfo = "";
        if(mAuthProvider.getUid().equals(mExtraIdUsuario1)){
            idUsuarioInfo = mExtraIdUsuario2;
        }else {
            idUsuarioInfo = mExtraIdUsuario1;
        }
        mListener = mUsuarioProvider.getUsuarioTiempoReal(idUsuarioInfo).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("usuario")){
                        String username = documentSnapshot.getString("usuario");
                        mTextViewUsuario.setText(username);
                    }
                    if(documentSnapshot.contains("enlinea")){
                        boolean enlinea = documentSnapshot.getBoolean("enlinea");
                        if(enlinea){
                            mTextViewRelativeTime.setText("En línea");
                        }
                        else if(documentSnapshot.contains("ultimaconexion")){
                            long ultimaconexion = documentSnapshot.getLong("ultimaconexion");
                            String relativeTime = RelativeTime.getTimeAgo(ultimaconexion, ChatActivity.this);
                            mTextViewRelativeTime.setText(relativeTime);
                        }

                    }
                    if(documentSnapshot.contains("imagenPerfil")){
                        String imagenPerfil = documentSnapshot.getString("imagenPerfil");
                        if(imagenPerfil != null){
                            if(!imagenPerfil.isEmpty()){
                                Picasso.with(ChatActivity.this).load(imagenPerfil).into(mCircleImagePerfil);
                            }
                        }
                    }
                }
            }
        });
    }

    private void verificarChatExistente(){
        mChatsProvider.getChatUsuario1AndUsuario2(mExtraIdUsuario1, mExtraIdUsuario2).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();
                if(size == 0){
                    crearChat();
                }else {
                    actualizarVisto();
                }
            }
        });
    }

    private void actualizarVisto() {
        String idEmidor = "";
        if(mAuthProvider.getUid().equals(mExtraIdUsuario1)){
            idEmidor = mExtraIdUsuario2;
        }else{
            idEmidor = mExtraIdUsuario1;
        }
        mMensajeProvider.obtenetMensajesByChatandEmisor(mExtraIdChat,idEmidor).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot document : queryDocumentSnapshots.getDocuments()){
                    mMensajeProvider.actualizarVisto(document.getId(), true);
                }
            }
        });
    }

    private void crearChat() {
        Chat chat = new Chat();
        chat.setIdUsuario1(mExtraIdUsuario1);
        chat.setIdUsuario2(mExtraIdUsuario2);
        chat.setEscribiendo(false);
        chat.setTimeStamp(new Date().getTime());
        chat.setId(mExtraIdUsuario1 + mExtraIdUsuario2);

        ArrayList<String> ids = new ArrayList<>();
        ids.add(mExtraIdUsuario1);
        ids.add(mExtraIdUsuario2);
        chat.setIds(ids);
        mChatsProvider.crear(chat);
    }
}