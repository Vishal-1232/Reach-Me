package com.example.reachme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.widget.TextView;

import com.example.reachme.Adapters.FriendRequestsAdapter;
import com.example.reachme.Models.FollowerModel;
import com.example.reachme.databinding.ActivityRequestsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RequestsActivity extends AppCompatActivity {
    ActivityRequestsBinding binding;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRequestsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();

        ArrayList<FollowerModel>list = new ArrayList<>();
        FriendRequestsAdapter friendRequestsAdapter = new FriendRequestsAdapter(this,list);
        binding.friendrequestsRv.setAdapter(friendRequestsAdapter);
        binding.friendrequestsRv.setLayoutManager(new LinearLayoutManager(this));
        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .child("Friend Requests")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            FollowerModel follower = dataSnapshot.getValue(FollowerModel.class);
                            follower.setId(dataSnapshot.getKey());
                            list.add(follower);
                        }
                        friendRequestsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}