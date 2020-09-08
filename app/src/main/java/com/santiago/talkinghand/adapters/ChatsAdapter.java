package com.santiago.talkinghand.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.santiago.talkinghand.R;
import com.santiago.talkinghand.activities.ChatActivity;
import com.santiago.talkinghand.models.Chat;
import com.santiago.talkinghand.providers.AuthProvider;
import com.santiago.talkinghand.providers.ChatsProvider;
import com.santiago.talkinghand.providers.MensajeProvider;
import com.santiago.talkinghand.providers.UsuarioProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends FirestoreRecyclerAdapter<Chat, ChatsAdapter.ViewHolder> {
    Context context;
    UsuarioProvider mUsuarioProvider;
    AuthProvider mAuthProvider;
    ChatsProvider mChatsProvider;
    MensajeProvider mMensajeProvider;
    ListenerRegistration mListener;
    ListenerRegistration mListenerUltimoMensaje;

    public ChatsAdapter(FirestoreRecyclerOptions<Chat> options, Context context){
        super(options);
        this.context = context;
        mUsuarioProvider = new UsuarioProvider();
        mAuthProvider = new AuthProvider();
        mChatsProvider = new ChatsProvider();
        mMensajeProvider = new MensajeProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull final Chat chat) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String chatId = document.getId();
        if(mAuthProvider.getUid().equals(chat.getIdUsuario1())){
            getInfoUsuario(chat.getIdUsuario2(), holder);
        }else {
            getInfoUsuario(chat.getIdUsuario1(), holder);
        }
        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irAlChat(chatId, chat.getIdUsuario1(), chat.getIdUsuario2());
            }
        });
        obtenerUltimoMensaje(chatId, holder.txtUltimoMensaje);

        String idEmisor = "";
        if(mAuthProvider.getUid().equals(chat.getIdUsuario1())){
            idEmisor = chat.getIdUsuario2();
        }else {
            idEmisor = chat.getIdUsuario1();
        }
        obtenerMensajesSinLeer(chatId, idEmisor, holder.txtMensajeSinLeer, holder.holderMensajesSinLeer);
    }

    private void obtenerMensajesSinLeer(String chatId, String idEmisor, final TextView txtMensajeSinLeer, final FrameLayout mHolderMensajesSinLeer) {

        mListener = mMensajeProvider.obtenetMensajesByChatandEmisor(chatId, idEmisor).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots != null){
                    int size = queryDocumentSnapshots.size();
                    if(size > 0){
                        mHolderMensajesSinLeer.setVisibility(View.VISIBLE);
                        txtMensajeSinLeer.setText(String.valueOf(size));
                    }else{
                        mHolderMensajesSinLeer.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    public ListenerRegistration obtenerListener(){
        return mListener;
    }

    public ListenerRegistration obtenerListenerUltimoMensaje(){
        return mListenerUltimoMensaje;
    }

    private void obtenerUltimoMensaje(String chatId, final TextView txtUltimoMensaje) {
       mListenerUltimoMensaje =  mMensajeProvider.obtenerUltimoMensaje(chatId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(queryDocumentSnapshots != null){
                    int size = queryDocumentSnapshots.size();
                    if(size > 0){
                        String ultimoMensaje = queryDocumentSnapshots.getDocuments().get(0).getString("mensaje");
                        txtUltimoMensaje.setText(ultimoMensaje);
                    }
                }

            }
        });
    }

    private void irAlChat(String chatId, String idUsuario1, String idUsuario2) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("idChat", chatId);
        intent.putExtra("idUsuario1", idUsuario1);
        intent.putExtra("idUsuario2", idUsuario2);
        context.startActivity(intent);
    }

    private void getInfoUsuario(String idUsuario, final ViewHolder holder){
        mUsuarioProvider.getUsuario(idUsuario).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("usuario")){
                        String usuario = documentSnapshot.getString("usuario");
                        holder.txtUsuario.setText(usuario);
                    }
                    if(documentSnapshot.contains("imagenPerfil")){
                        String imagenPerfil = documentSnapshot.getString("imagenPerfil");
                        if(imagenPerfil != null){
                            if(!imagenPerfil.isEmpty()){
                                Picasso.with(context).load(imagenPerfil).into(holder.imageViewPerfil);

                            }
                        }
                    }
                }
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chats, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtUsuario;
        TextView txtUltimoMensaje;
        TextView txtMensajeSinLeer;
        CircleImageView imageViewPerfil;
        FrameLayout holderMensajesSinLeer;
        View viewHolder;

        public ViewHolder(View view){
            super(view);
            txtMensajeSinLeer = view.findViewById(R.id.msjSinLeer);
            txtUsuario = view.findViewById(R.id.txtViewUsuarioChat);
            txtUltimoMensaje = view.findViewById(R.id.textUltimoMensaje);
            imageViewPerfil = view.findViewById(R.id.circleImageChat);
            holderMensajesSinLeer = view.findViewById(R.id.mensajesSinLeer);
            viewHolder = view;
        }
    }
}
