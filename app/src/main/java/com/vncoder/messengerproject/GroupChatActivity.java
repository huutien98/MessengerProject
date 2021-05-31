package com.vncoder.messengerproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vncoder.messengerproject.Adapter.ChatAdapter;
import com.vncoder.messengerproject.Models.MessageMode;
import com.vncoder.messengerproject.databinding.ActivityGroupChatBinding;

import java.util.ArrayList;
import java.util.Date;

public class GroupChatActivity extends AppCompatActivity {

    ActivityGroupChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();
        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final ArrayList<MessageMode> messageModes = new ArrayList<>();

        final String senderId = FirebaseAuth.getInstance().getUid();
        binding.userNames.setText("Friends Gruop");

        final ChatAdapter adapter = new ChatAdapter(messageModes, this);
        binding.chatRecycleView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecycleView.setLayoutManager(layoutManager);

        database.getReference().child("Group Chat")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModes.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        MessageMode mode = dataSnapshot.getValue(MessageMode.class);
                        messageModes.add(mode);
                    }
                    adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("tiennh","onCancelled");
                    }
                });

        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mesage = binding.etMessage.getText().toString();
                final MessageMode mode = new MessageMode(senderId,mesage);
                mode.setTimestamp(new Date().getTime());

                binding.etMessage.setText("");
                database.getReference().child("Group chat")
                        .push()
                        .setValue(mode)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });



            }
        });

    }


}