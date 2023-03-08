package com.example.reachme;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.reachme.Models.PostModel;
import com.example.reachme.Models.Users;
import com.example.reachme.databinding.ActivityAddPostBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class AddPostActivity extends AppCompatActivity {

    ActivityAddPostBinding binding;
    ActivityResultLauncher<String> galleryLauncher;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        database.getReference().child("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Users user = snapshot.getValue(Users.class);
                            Picasso.get().load(user.getProfilePic())
                                    .placeholder(R.drawable.avatar)
                                    .into(binding.profileimg);
                            binding.profession.setText(user.getAbout());
                            binding.Name.setText(user.getUserName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.postDescrp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String description = binding.postDescrp.getText().toString();
                if (!description.isEmpty()) {
                    binding.uploadPost.setBackgroundDrawable(ContextCompat.getDrawable(AddPostActivity.this, R.drawable.post_active_btn));
                    binding.uploadPost.setTextColor(getResources().getColor(R.color.white));
                    binding.uploadPost.setEnabled(true);
                } else {
                    binding.uploadPost.setBackgroundDrawable(ContextCompat.getDrawable(AddPostActivity.this, R.drawable.following_btn));
                    binding.uploadPost.setEnabled(false);
                    binding.uploadPost.setTextColor(getResources().getColor(android.R.color.darker_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        Uri empty = null;
                        try {
                            if (!uri.equals(empty)) {
                                img = uri;
                                binding.postImg.setImageURI(uri);
                                binding.postImg.setVisibility(View.VISIBLE);
                                binding.uploadPost.setBackgroundDrawable(ContextCompat.getDrawable(AddPostActivity.this, R.drawable.post_active_btn));
                                binding.uploadPost.setTextColor(getResources().getColor(R.color.white));
                                binding.uploadPost.setEnabled(true);
                            } else {
                                Toast.makeText(AddPostActivity.this, "Image not selected", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(AddPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        binding.gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryLauncher.launch("image/*");
            }
        });

        binding.uploadPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(AddPostActivity.this);
                dialog.setContentView(R.layout.dialog_loading);
                if(dialog.getWindow()!=null){
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
                final StorageReference storageReference = storage.getReference()
                        .child("Posts").child(FirebaseAuth.getInstance().getUid())
                        .child(new Date().getTime() + "");
                storageReference.putFile(img).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                PostModel post = new PostModel();
                                post.setPostImg(uri.toString());
                                post.setPostDescription(binding.postDescrp.getText().toString());
                                post.setPostedBy(FirebaseAuth.getInstance().getUid());
                                post.setPostedAt(new Date().getTime());

                                database.getReference().child("Posts")
                                        .push().setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                dialog.dismiss();
                                                Toast.makeText(AddPostActivity.this, "Posted Successfully", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });
                            }
                        });
                    }
                });
            }
        });
    }
}