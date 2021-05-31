package com.vncoder.messengerproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vncoder.messengerproject.Adapter.ChatAdapter;
import com.vncoder.messengerproject.Models.MessageMode;
import com.vncoder.messengerproject.databinding.ActivityChatDetailBinding;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailActivity extends AppCompatActivity {

    ActivityChatDetailBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);
        getSupportActionBar().hide();

        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        final String senderId = auth.getUid();

        String receiveId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");

        binding.userNameChat.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.ic_avatar_girl).into(binding.icAvatar);

        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatDetailActivity.this,MainActivity.class);
                startActivity(intent);

            }
        });

        final ArrayList<MessageMode> messageModes = new ArrayList<>();

        final ChatAdapter chatAdapter = new ChatAdapter(messageModes,this,receiveId);
        binding.chatRecyclerView.setAdapter(chatAdapter);

        final String senderRoom = senderId + receiveId;
        final String receiverRoom = receiveId +senderId;

        database.getReference().child("chats").child(senderRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageModes.clear();
               for (DataSnapshot snapshot1 : snapshot.getChildren()){
                   MessageMode modelChat = snapshot1.getValue(MessageMode.class);

                   modelChat.setMesageId(snapshot1.getKey());

                messageModes.add(modelChat);
               }
            chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {



            }
        });






        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String message = binding.etMessage.getText().toString();
               final  MessageMode mode = new MessageMode(senderId,message);
               mode.setTimestamp(new Date().getTime());
                binding.etMessage.setText("");
                database.getReference().child("chats").child(senderRoom).push().setValue(mode).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        database.getReference().child("chats").child(receiverRoom).push().setValue(mode).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });
                    }
                });


            }
        });













    }

}