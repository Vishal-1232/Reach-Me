package com.example.reachme.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.reachme.Adapters.StatusAdapter;
import com.example.reachme.Models.Status;
import com.example.reachme.Models.UserStatus;
import com.example.reachme.Models.Users;
import com.example.reachme.R;
import com.example.reachme.databinding.FragmentStatusBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class StatusFragment extends Fragment {


    public StatusFragment() {
        // Required empty public constructor
    }

    FragmentStatusBinding binding;
    ArrayList<UserStatus> userStatuses = new ArrayList<>();
    StatusAdapter adapter;

    ProgressDialog dialog;
    Users user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStatusBinding.inflate(inflater, container, false);

        // Adapter set
        adapter = new StatusAdapter(getContext(), userStatuses);
        binding.statusRecyclerView.setAdapter(adapter);
        binding.statusRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // -------------------------
        // Taking info of current user

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user = snapshot.getValue(Users.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // --------------------
        // Uploading status
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Uploading Status..");
        dialog.setCancelable(false);
        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 75);
            }
        });
        // ----------------------------------
        // Reteriving status from database
        FirebaseDatabase.getInstance().getReference().child("Users Status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    userStatuses.clear();
                    for (DataSnapshot statusSnapshot : snapshot.getChildren()){
                        UserStatus userStatus = new UserStatus();
                        userStatus.setName(statusSnapshot.child("name").getValue(String.class));
                        userStatus.setProfileImage(statusSnapshot.child("profileImage").getValue(String.class));
                        userStatus.setLastUpdated(statusSnapshot.child("lastUpdated").getValue(Long.class));

                        ArrayList<Status>statusCount = new ArrayList<>();
                        for (DataSnapshot StatusesSnapshot : statusSnapshot.child("statuses").getChildren()){
                            Status sampleStatus = StatusesSnapshot.getValue(Status.class);
                            statusCount.add(sampleStatus);
                        }
                        userStatus.setStatuses(statusCount);
                        userStatuses.add(userStatus);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // -----------------------
        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (data.getData() != null) {

                dialog.show();

                FirebaseStorage storage = FirebaseStorage.getInstance();
                Date date = new Date();
                StorageReference reference = storage.getReference().child("Status").child(date.getTime() + "");

                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    UserStatus userStatus = new UserStatus();
                                    userStatus.setName(user.getUserName());
                                    userStatus.setProfileImage(user.getProfilePic());
                                    userStatus.setLastUpdated(date.getTime());

                                    HashMap<String, Object> obj = new HashMap<>();
                                    obj.put("name", userStatus.getName());
                                    obj.put("profileImage", userStatus.getProfileImage());
                                    obj.put("lastUpdated", userStatus.getLastUpdated());

                                    String imageUri = uri.toString();
                                    Status status = new Status(imageUri, userStatus.getLastUpdated());

                                    FirebaseDatabase.getInstance().getReference().child("Users Status")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .updateChildren(obj);

                                    FirebaseDatabase.getInstance().getReference().child("Users Status")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .child("statuses")
                                            .push()
                                            .setValue(status);

                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                });
            }
        }
    }
}