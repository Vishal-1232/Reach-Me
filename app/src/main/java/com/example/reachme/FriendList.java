package com.example.reachme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.example.reachme.Adapters.FriendListAdapter;
import com.example.reachme.Models.FollowerModel;
import com.example.reachme.Models.Users;
import com.example.reachme.databinding.ActivityFriendListBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendList extends AppCompatActivity {
    ActivityFriendListBinding binding;
    ArrayList<Users>list;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        list = new ArrayList<>();
        database = FirebaseDatabase.getInstance();

        // setting recycler view
        FriendListAdapter friendListAdapter = new FriendListAdapter(this,list);
        binding.frndListRv.setAdapter(friendListAdapter);
        binding.frndListRv.setLayoutManager(new LinearLayoutManager(this));
        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .child("Friends").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Users users = dataSnapshot.getValue(Users.class);
                            users.setUserID(dataSnapshot.getKey());
                            list.add(users);
                        }
                        friendListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}