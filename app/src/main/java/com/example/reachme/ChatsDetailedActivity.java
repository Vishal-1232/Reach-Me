package com.example.reachme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.EmojiCompatConfigurationView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.example.reachme.Encryption.AES;
import com.example.reachme.Fragments.ChatAttachmentsFragment;
import com.example.reachme.Models.FollowerModel;
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
import java.util.HashSet;
import java.util.Locale;

public class ChatsDetailedActivity extends AppCompatActivity {

    ActivityChatsDetailedBinding binding;

    FirebaseDatabase database;
    FirebaseAuth auth;

    String senderRoom;
    String reciverRoom;
    String userBlockId;


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
        userBlockId = reciverId;
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
                handler.postDelayed(userStoppedTyping, 1000);
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
        reciverRoom = reciverId + senderId;

        // getting chats from database

        database.getReference().child("Chats").child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    MessageModel model = snapshot1.getValue(MessageModel.class);
                                    model.setMessageId(snapshot1.getKey());
                                    if (model.getType().equals("Photo")){
                                        model.setMessage(model.getMessage());
                                    }else{
                                        model.setMessage(AES.decrypt(model.getMessage())); // decrypt message
                                    }
                                    messageModels.add(model);
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        chatAdapter.notifyDataSetChanged();
                                        //binding.chatsRecyclerView.smoothScrollToPosition(binding.chatsRecyclerView.getAdapter().getItemCount());
                                        binding.chatsRecyclerView.scrollToPosition(binding.chatsRecyclerView.getAdapter().getItemCount() - 1);
                                    }
                                });

                            }
                        };
                        Thread thread = new Thread(runnable);
                        thread.start();
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

        // storing chats in database or sending messages
        HashSet<String> friendList = new HashSet<>();
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getUid()).child("Friends")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        friendList.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                FollowerModel followerModel = dataSnapshot.getValue(FollowerModel.class);
                                followerModel.setId(dataSnapshot.getKey());
                                friendList.add(followerModel.getId());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!friendList.contains(reciverId)) {
                    //finish();
                    Toast.makeText(ChatsDetailedActivity.this, "You are blocked by the user", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (binding.message.getText().toString().isEmpty()) {
                    binding.message.setError("Enter text to send");
                    return;
                }
                String MESSAGE = binding.message.getText().toString();
                binding.message.setText("");
                Runnable sendMsg = new Runnable() {
                    @Override
                    public void run() {
                        String message = AES.encrypt(MESSAGE); // Encrypted Message
                        //String message = binding.message.getText().toString();
                        final MessageModel model = new MessageModel(senderId, message);
                        model.setTimeStamp(new Date().getTime());
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
                                                        sendNotification(MESSAGE, reciverId);
                                                    }
                                                });
                                    }
                                });
                    }
                };
                Thread thread = new Thread(sendMsg);
                thread.start();
            }
        });

        // Attachment work
        binding.attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatAttachmentsFragment chatAttachmentsFragment = new ChatAttachmentsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("SENDER_ROOM",senderRoom);
                bundle.putString("RECIVER_ROOM",reciverRoom);
                chatAttachmentsFragment.setArguments(bundle);
                chatAttachmentsFragment.show(getSupportFragmentManager(), chatAttachmentsFragment.getTag());
            }
        });
    }

    private void sendNotification(String message, String reciverId) {
        final Users[] curr = new Users[2];

        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                curr[0] = snapshot.getValue(Users.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        database.getReference().child("Users/" + reciverId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        curr[1] = snapshot.getValue(Users.class);
                        Toast.makeText(ChatsDetailedActivity.this, "Notification", Toast.LENGTH_SHORT).show();
                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(curr[1].getFcmTokken(), "New Message from " + curr[0].getUserName(), message, ChatsDetailedActivity.this, ChatsDetailedActivity.this);
                        notificationsSender.SendNotifications();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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
                blockUser();
                break;
            case R.id.report:
                Toast.makeText(this, "Feature will be available in next update", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "INVALID SELECTION!!", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void blockUser() {
        // using follower model as block user model
        FollowerModel followerModel = new FollowerModel(userBlockId, new Date().getTime());
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .child("BlockList")
                .child(userBlockId).setValue(followerModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        FirebaseDatabase.getInstance().getReference().child("Users")
                                .child(FirebaseAuth.getInstance().getUid())
                                .child("Friends")
                                .child(userBlockId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(ChatsDetailedActivity.this, "User Blocked", Toast.LENGTH_SHORT).show();
                                        finish();
                                        FirebaseDatabase.getInstance().getReference().child("Users")
                                                .child(userBlockId)
                                                .child("Friends")
                                                .child(FirebaseAuth.getInstance().getUid())
                                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        // Desteroying rooms
                                                        //sender room desteroyed
                                                        database.getReference().child("Chats").child(senderRoom).removeValue();
                                                        // Desteroying Reciver Room
                                                        database.getReference().child("Chats").child(reciverRoom).removeValue();
                                                    }
                                                });
                                        FirebaseDatabase.getInstance().getReference().child("Users")
                                                .child(userBlockId).child("BlockedBy")
                                                .child(FirebaseAuth.getInstance().getUid())
                                                .setValue(new FollowerModel(FirebaseAuth.getInstance().getUid(), new Date().getTime()));
                                    }
                                });
                    }
                });
    }

    private void clearChats() {
        database.getReference().child("Chats").child(senderRoom).setValue(null);
    }

}
