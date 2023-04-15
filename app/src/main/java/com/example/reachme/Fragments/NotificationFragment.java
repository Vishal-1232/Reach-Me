package com.example.reachme.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.reachme.Adapters.NotificationAdapter;
import com.example.reachme.Models.NotificationModel;
import com.example.reachme.databinding.FragmentNotificationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {
    ArrayList<NotificationModel>list;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentNotificationBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(inflater,container,false);

        list = new ArrayList<>();
        NotificationAdapter notificationAdapter = new NotificationAdapter(getContext(),list);
        binding.notificationsRv.setAdapter(notificationAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.notificationsRv.setLayoutManager(linearLayoutManager);

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                NotificationModel notification = dataSnapshot.getValue(NotificationModel.class);
                                notification.setNotificationId(dataSnapshot.getKey());
                                list.add(notification);
                        }
                        notificationAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return binding.getRoot();
    }
}