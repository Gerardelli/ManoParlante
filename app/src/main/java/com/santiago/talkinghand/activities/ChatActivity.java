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
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.gson.Gson;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.adapters.MensajeAdapter;
import com.santiago.talkinghand.models.Chat;
import com.santiago.talkinghand.models.FCMBody;
import com.santiago.talkinghand.models.FCMResponse;
import com.santiago.talkinghand.models.Mensaje;
import com.santiago.talkinghand.providers.AuthProvider;
import com.santiago.talkinghand.providers.ChatsProvider;
import com.santiago.talkinghand.providers.MensajeProvider;
import com.santiago.talkinghand.providers.NotificationProvider;
import com.santiago.talkinghand.providers.TokenProvider;
import com.santiago.talkinghand.providers.UsuarioProvider;
import com.santiago.talkinghand.utils.HelperMensajeVisto;
import com.santiago.talkinghand.utils.RelativeTime;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    String mExtraIdUsuario1;
    String mExtraIdUsuario2;
    String mExtraIdChat;
    long mIdNotificacionChat;
    private Vibrator vibrator;

    ChatsProvider mChatsProvider;
    UsuarioProvider mUsuarioProvider;
    MensajeProvider mMensajeProvider;
    AuthProvider mAuthProvider;
    MensajeAdapter mMensajeAdapter;
    NotificationProvider mNotificationProvider;
    TokenProvider mTokenProvider;

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

    String mNombreUsuario;
    String mNombreUsuarioChat;
    String mImagenEmisor = "";
    String mImagenReceptor = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mChatsProvider = new ChatsProvider();
        mMensajeProvider = new MensajeProvider();
        mAuthProvider = new AuthProvider();
        mUsuarioProvider = new UsuarioProvider();
        mNotificationProvider = new NotificationProvider();
        mTokenProvider = new TokenProvider();

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
        getInforUsuario();

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
            final Mensaje mensaje = new Mensaje();
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
                        obtenerToken(mensaje);
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
                        mNombreUsuario = documentSnapshot.getString("usuario");
                        mTextViewUsuario.setText(mNombreUsuario);
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
                        mImagenEmisor = documentSnapshot.getString("imagenPerfil");
                        if(mImagenEmisor != null){
                            if(!mImagenEmisor.isEmpty()){
                                Picasso.with(ChatActivity.this).load(mImagenEmisor).into(mCircleImagePerfil);
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
                    mIdNotificacionChat = queryDocumentSnapshots.getDocuments().get(0).getLong("idNotificacion");
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
        Random random = new Random();
        int n = random.nextInt(1000000);
        chat.setIdNotificacion(n);
        mIdNotificacionChat = n;

        ArrayList<String> ids = new ArrayList<>();
        ids.add(mExtraIdUsuario1);
        ids.add(mExtraIdUsuario2);
        chat.setIds(ids);
        mChatsProvider.crear(chat);
    }


    private void obtenerToken(final Mensaje mensaje) {
        String mIdUsuario = "";
        if(mAuthProvider.getUid().equals(mExtraIdUsuario1)){
            mIdUsuario = mExtraIdUsuario2;
        }else{
            mIdUsuario = mExtraIdUsuario1;
        }

        mTokenProvider.getToken(mIdUsuario).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("token")){
                        String token = documentSnapshot.getString("token");
                        obtenerTresMensajes(mensaje, token);
                    }
                }else{
                    Toast.makeText(ChatActivity.this, "No existe token", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void obtenerTresMensajes(final Mensaje mensaje, final String token) {
        mMensajeProvider.obteneTrestMensajesByChatandEmisor(mExtraIdChat, mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Mensaje> mensajeArrayList = new ArrayList<>();

                for(DocumentSnapshot d: queryDocumentSnapshots.getDocuments()){
                    if(d.exists()){
                        Mensaje mensaje = d.toObject(Mensaje.class);
                        mensajeArrayList.add(mensaje);
                    }
                }

                if(mensajeArrayList.size() == 0){
                    mensajeArrayList.add(mensaje);
                }

                Collections.reverse(mensajeArrayList);

                Gson gson = new Gson();
                String mensajes = gson.toJson(mensajeArrayList);
                enviarNotificacion(token, mensajes, mensaje);

            }
        });
    }

    private void enviarNotificacion(final String token, String mensajes, Mensaje mensaje){
        final Map<String, String> data = new HashMap<>();
        data.put("title", "Nuevo Mensaje");
        data.put("body", mensaje.getMensaje());
        data.put("idNotificacion", String.valueOf(mIdNotificacionChat));
        data.put("mensajes", mensajes);
        data.put("usuarioEmisor", mNombreUsuario);
        data.put("usuarioReceptor", mNombreUsuarioChat);
        data.put("idEmisor", mensaje.getIdEmisor());
        data.put("idReceptor", mensaje.getIdReceptor());
        data.put("idChat", mensaje.getIdChat());

        if(mImagenEmisor.equals("")){
            mImagenEmisor = "IMAGEN_NO_VALIDA";
        }
        if(mImagenReceptor.equals("")){
            mImagenReceptor = "IMAGEN_NO_VALIDA";
        }

        data.put("imagenEmisor", mImagenEmisor);
        data.put("imagenReceptor", mImagenReceptor);

        String idEmisor = "";
        if(mAuthProvider.getUid().equals(mExtraIdUsuario1)){
            idEmisor = mExtraIdUsuario2;
        }else {
            idEmisor = mExtraIdUsuario1;
        }
        mMensajeProvider.obtenerUltimoMensajeEmisor(mExtraIdChat, idEmisor).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();
                String ultimoMensaje = "";
                if(size > 0){
                    ultimoMensaje = queryDocumentSnapshots.getDocuments().get(0).getString("mensaje");
                    data.put("ultimoMensaje", ultimoMensaje);
                }
                FCMBody body = new FCMBody(token, "high", "4500s", data);
                vibrator = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);
                mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                        if(response.body() != null){
                            if(response.body().getSuccess()==1){
                                Toast.makeText(ChatActivity.this, "Mensaje enviado", Toast.LENGTH_LONG).show();
                                long tiempo = 1000;
                                vibrator.vibrate(tiempo);
                            }else {
                                Toast.makeText(ChatActivity.this, "Mensaje no enviado", Toast.LENGTH_LONG).show();
                            }
                        }else {
                            Toast.makeText(ChatActivity.this, "Mensaje no enviado", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {
                    }
                });
            }
        });
    }

    private void getInforUsuario(){
        mUsuarioProvider.getUsuario(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        if(documentSnapshot.contains("usuario")){
                            mNombreUsuarioChat = documentSnapshot.getString("usuario");
                        }
                        if(documentSnapshot.contains("imagenPerfil")){
                            mImagenReceptor = documentSnapshot.getString("imagenPerfil");
                        }
                    }
            }
        });

    }
}