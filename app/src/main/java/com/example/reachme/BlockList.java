package com.example.reachme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.reachme.Adapters.blackListAdapter;
import com.example.reachme.Models.FollowerModel;
import com.example.reachme.databinding.ActivityBlockListBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BlockList extends AppCompatActivity {
    ActivityBlockListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBlockListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayList<FollowerModel> blockList = new ArrayList<>();
        blackListAdapter blackListAdapter = new blackListAdapter(this, blockList);
        binding.blockUserRv.setLayoutManager(new LinearLayoutManager(this));
        binding.blockUserRv.setAdapter(blackListAdapter);

        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .child("BlockList")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        blockList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            FollowerModel blockUser = dataSnapshot.getValue(FollowerModel.class);
                            blockUser.setId(dataSnapshot.getKey());
                            blockList.add(blockUser);
                        }
                        blackListAdapter.notifyDataSetChanged();
                        if (blockList.isEmpty()){
                            Toast.makeText(BlockList.this, "No Blocked User", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}