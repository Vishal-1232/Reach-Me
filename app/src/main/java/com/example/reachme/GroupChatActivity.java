package com.example.reachme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.reachme.Adapters.ChatAdapter;
import com.example.reachme.Models.MessageModel;
import com.example.reachme.databinding.ActivityGroupChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //GroupChatActivity.super.onBackPressed();
                Intent intent = new Intent(GroupChatActivity.this,MainActivity.class);
                //finish();
                startActivity(intent);
                finish();
            }
        });

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String senderId = FirebaseAuth.getInstance().getUid();

        binding.name.setText("Friends Forever");

        final ArrayList<MessageModel> messageModels = new ArrayList<>();
        final ChatAdapter chatAdapter = new ChatAdapter(this,messageModels);
        binding.chatsRecyclerView.setAdapter(chatAdapter);
        binding.chatsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Logic of reteriving Messages from database and add them into recycler view

        database.getReference().child("Group Chat")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            MessageModel model = dataSnapshot.getValue(MessageModel.class);
                            messageModels.add(model);
                        }
                        chatAdapter.notifyDataSetChanged();
                        binding.chatsRecyclerView.scrollToPosition(binding.chatsRecyclerView.getAdapter().getItemCount()-1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // Logic of storing Messages in database
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (binding.message.getText().toString().isEmpty()){
                    binding.message.setError("Enter text to send");
                    return;
                }

                final String message = binding.message.getText().toString();
                binding.message.setText("");

                final MessageModel messageModel = new MessageModel(senderId,message);
                messageModel.setTimeStamp(new Date().getTime());

                // Storing message in database
                database.getReference().child("Group Chat").push()
                        .setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GroupChatActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}