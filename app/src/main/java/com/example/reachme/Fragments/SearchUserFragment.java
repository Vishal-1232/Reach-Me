package com.example.reachme.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.reachme.Adapters.SearchUserAdapter;
import com.example.reachme.MainActivity;
import com.example.reachme.Models.Users;
import com.example.reachme.R;
import com.example.reachme.RequestsActivity;
import com.example.reachme.databinding.FragmentSearchUserBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchUserFragment extends Fragment {


    public SearchUserFragment() {
        // Required empty public constructor
    }

    FragmentSearchUserBinding binding;
    FirebaseDatabase database;
    ArrayList<Users> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchUserBinding.inflate(inflater, container, false);
        MainActivity mainActivity = (MainActivity)getActivity();
        mainActivity.getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        list = new ArrayList<>();
        SearchUserAdapter searchUserAdapter = new SearchUserAdapter(getContext(), list);
        binding.searchUser.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.searchUser.setAdapter(searchUserAdapter);
        database.getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users user = dataSnapshot.getValue(Users.class);
                    user.setUserID(dataSnapshot.getKey());
                    if (!user.getUserID().equals(FirebaseAuth.getInstance().getUid())) {
                        list.add(user);
                    }
                }
                searchUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), RequestsActivity.class));
            }
        });

        // Request Counter
        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .child("Friend Requests")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int counter = 0;
                        for (DataSnapshot snapshot1 : snapshot.getChildren()){
                            counter++;
                        }
                        binding.requests.setText("Pending Requests : "+counter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        return binding.getRoot();
    }
}