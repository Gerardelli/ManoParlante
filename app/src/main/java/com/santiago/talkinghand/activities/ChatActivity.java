package com.santiago.talkinghand.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.santiago.talkinghand.R;
import com.santiago.talkinghand.models.Chat;
import com.santiago.talkinghand.providers.ChatsProvider;

import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    String mExtraIdUsuario1;
    String mExtraIdUsuario2;
    ChatsProvider mChatsProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mExtraIdUsuario1 = getIntent().getStringExtra("idUsuario1");
        mExtraIdUsuario2 = getIntent().getStringExtra("idUsuario2");

        mChatsProvider = new ChatsProvider();
        
        crearChat();
    }

    private void crearChat() {
        Chat chat = new Chat();
        chat.setIdUsuario1(mExtraIdUsuario1);
        chat.setIdUsuario2(mExtraIdUsuario2);
        chat.setEscribiendo(false);
        chat.setTimeStamp(new Date().getTime());
        mChatsProvider.crear(chat);
    }
}