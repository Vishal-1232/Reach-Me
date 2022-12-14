package com.example.reachme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.EmojiCompatConfigurationView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.example.reachme.Adapters.ChatAdapter;
import com.example.reachme.Models.MessageModel;
import com.example.reachme.Models.Users;
import com.example.reachme.databinding.ActivityChatsDetailedBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;

import com.vanniktech.emoji.twitter.TwitterEmojiProvider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ChatsDetailedActivity extends AppCompatActivity {

    ActivityChatsDetailedBinding binding;

    FirebaseDatabase database;
    FirebaseAuth auth;

    String senderRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatsDetailedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);


        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        final String senderId = auth.getUid();

        String reciverId = getIntent().getStringExtra("userID");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");

        binding.name.setText(userName);
        binding.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatsDetailedActivity.this, ProfileViewActivity.class);
                intent.putExtra("recId", reciverId);
                startActivity(intent);
            }
        });
        binding.status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatsDetailedActivity.this, ProfileViewActivity.class);
                intent.putExtra("recId", reciverId);
                startActivity(intent);
            }
        });

        // Typing indicator
        final Handler handler = new Handler();
        binding.message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                        .child("connectionStatus").setValue("typing...");
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(userStoppedTyping,1000);
            }
            Runnable userStoppedTyping = new Runnable() {
                @Override
                public void run() {
                    database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                            .child("connectionStatus").setValue("Online");
                }
            };
        });

        // Last seen
        DatabaseReference conn = database.getReference().child("Users/" + reciverId);

        conn.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String connectionStatus = snapshot.child("connectionStatus").getValue().toString();
                String lastSeen = snapshot.child("lastSeen").getValue().toString();

                String set = "";
                if (!connectionStatus.equals("Online") && !connectionStatus.equals("typing...")) {
                    set = "Last seen : " + getTimeDate(Long.parseLong(lastSeen));
                } else {
                    set = connectionStatus;
                }
                binding.status.setText(set);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Picasso.get().load(profilePic).placeholder(R.drawable.man).into(binding.profileimg);

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(ChatsDetailedActivity.this,MainActivity.class);
                startActivity(intent);
                finish();*/
                ChatsDetailedActivity.super.onBackPressed();
            }
        });
        binding.profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatsDetailedActivity.super.onBackPressed();
            }
        });

        // chat Adapter set
        ArrayList<MessageModel> messageModels = new ArrayList<>();

        final ChatAdapter chatAdapter = new ChatAdapter(this, messageModels, reciverId);
        binding.chatsRecyclerView.setAdapter(chatAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.chatsRecyclerView.setLayoutManager(linearLayoutManager);

        // Chatting logic

         senderRoom = senderId + reciverId;
        final String reciverRoom = reciverId + senderId;

        // getting chats from database

        database.getReference().child("Chats").child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            MessageModel model = snapshot1.getValue(MessageModel.class);
                            model.setMessageId(snapshot1.getKey());
                            messageModels.add(model);
                        }
                        chatAdapter.notifyDataSetChanged();
                        //binding.chatsRecyclerView.smoothScrollToPosition(binding.chatsRecyclerView.getAdapter().getItemCount());
                        binding.chatsRecyclerView.scrollToPosition(binding.chatsRecyclerView.getAdapter().getItemCount() - 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // initialize emoji popup
        EmojiManager.install(new TwitterEmojiProvider());

        EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(binding.getRoot()).build(binding.message);
        binding.emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emojiPopup.toggle();
            }
        });

        // storing chats in database

        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (binding.message.getText().toString().isEmpty()) {
                    binding.message.setError("Enter text to send");
                    return;
                }

                String message = binding.message.getText().toString();
                final MessageModel model = new MessageModel(senderId, message);
                model.setTimeStamp(new Date().getTime());
                binding.message.setText("");

                // storing message in database
                String randomKey = database.getReference().push().getKey();
                database.getReference().child("Chats").child(senderRoom).child(randomKey)
                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference().child("Chats").child(reciverRoom).child(randomKey)
                                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        });
                            }
                        });
            }
        });

    }

    public String getTimeDate(long timeStamp) {
        try {
            Date netDate = (new Date(timeStamp));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM d  h:mm a", Locale.getDefault());
            return simpleDateFormat.format(netDate);
        } catch (Exception e) {
            return "Time";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clearChats:
                clearChats();
                Toast.makeText(this, "Chats cleared", Toast.LENGTH_SHORT).show();
                break;
            case R.id.block:
                Toast.makeText(this, "Feature will be available in next update", Toast.LENGTH_SHORT).show();
                break;
            case R.id.report:
                Toast.makeText(this, "Feature will be available in next update", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "INVALID SELECTION!!", Toast.LENGTH_SHORT).show();;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearChats() {
        database.getReference().child("Chats").child(senderRoom).setValue(null);
    }
}
