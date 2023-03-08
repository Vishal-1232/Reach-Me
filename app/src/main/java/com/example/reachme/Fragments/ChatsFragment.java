package com.example.reachme.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.reachme.Adapters.UsersAdapter;
import com.example.reachme.MainActivity;
import com.example.reachme.Models.FollowerModel;
import com.example.reachme.Models.Users;
import com.example.reachme.R;
import com.example.reachme.databinding.FragmentChatsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

public class ChatsFragment extends Fragment {


    public ChatsFragment() {
        // Required empty public constructor
    }

    FragmentChatsBinding binding;
    MainActivity mainActivity;

    ArrayList<Users> list = new ArrayList<>();

    FirebaseDatabase database;
    FirebaseAuth auth;

    UsersAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        mainActivity = (MainActivity) getActivity();

        adapter = new UsersAdapter(list, getContext());
        binding.chatsRecyclerView.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.chatsRecyclerView.setLayoutManager(linearLayoutManager);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        HashSet<String> friendList = new HashSet<>();
        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .child("Friends").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            FollowerModel follower = dataSnapshot.getValue(FollowerModel.class);
                            follower.setId(dataSnapshot.getKey());
                            friendList.add(follower.getId());
                            Toast.makeText(mainActivity, "Follower added " + friendList.size() + " ", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    users.setUserID(dataSnapshot.getKey());

                    if (friendList.contains(users.getUserID())) {
                        list.add(users);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        final int[] state = new int[1];
        binding.chatsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                state[0] = newState;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0 && (state[0] == 0 || state[0] == 2)) {
                    hideToolbar();
                } else if (dy < -10) {
                    showTooolbar();
                }
            }
        });

        return binding.getRoot();
    }

    private void hideToolbar() {
        mainActivity.findViewById(R.id.toolbar2).setVisibility(View.GONE);
    }

    private void showTooolbar() {
        mainActivity.findViewById(R.id.toolbar2).setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        adapter.notifyDataSetChanged();
        mainActivity.getSupportActionBar().show();
        super.onResume();
    }

}