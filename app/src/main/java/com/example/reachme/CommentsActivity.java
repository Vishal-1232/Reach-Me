package com.example.reachme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.reachme.Adapters.CommentsAdapter;
import com.example.reachme.Models.CommentsModel;
import com.example.reachme.Models.NotificationModel;
import com.example.reachme.Models.PostModel;
import com.example.reachme.Models.Users;
import com.example.reachme.databinding.ActivityCommentsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class CommentsActivity extends AppCompatActivity {

    ActivityCommentsBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;

    String postId, postedBy;
    ArrayList<CommentsModel> list;
    PostModel Postmodel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        postedBy = intent.getStringExtra("postedBy");
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        list = new ArrayList<>();

        // getting Post data
        database.getReference().child("Posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PostModel postModel = snapshot.getValue(PostModel.class);
                Postmodel=postModel;
                Picasso.get().load(postModel.getPostImg())
                        .placeholder(R.drawable.image).into(binding.postImg);

                binding.desc.setText(postModel.getPostDescription());
                binding.like.setText(postModel.getLikesCounter() + "");
                binding.comment.setText(postModel.getCommentsCounter() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // getting user data who had posted this post
        database.getReference().child("Users").child(postedBy).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                Picasso.get().load(user.getProfilePic()).placeholder(R.drawable.avatar)
                        .into(binding.profileImage);
                binding.user.setText(user.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // uploading comment
        binding.postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentBody = binding.commentetxt.getText().toString();
                if (commentBody.isEmpty()) {
                    binding.commentetxt.setError("Required");
                    return;
                }
                binding.commentetxt.setText("");
                CommentsModel commentsModel = new CommentsModel(auth.getUid(), commentBody, new Date().getTime());
                database.getReference().child("Posts").child(postId)
                        .child("Comments")
                        .push()
                        .setValue(commentsModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference().child("Posts")
                                        .child(postId)
                                        .child("commentsCounter").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                int commentCount = 0;
                                                if (snapshot.exists()) {
                                                    commentCount = snapshot.getValue(Integer.class);
                                                }
                                                database.getReference().child("Posts").child(postId)
                                                        .child("commentsCounter")
                                                        .setValue(commentCount + 1)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Toast.makeText(CommentsActivity.this, "Commented", Toast.LENGTH_SHORT).show();

                                                                // Notification Work
                                                                NotificationModel notification = new NotificationModel();
                                                                notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                notification.setNotificationType("Comment");
                                                                notification.setTime(new Date().getTime());
                                                                notification.setPostId(postId);
                                                                notification.setPostedBy(postedBy);

                                                                FirebaseDatabase.getInstance().getReference()
                                                                        .child("Notifications")
                                                                        .child(postedBy)
                                                                        .push()
                                                                        .setValue(notification);
                                                            }
                                                        });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        });
            }
        });

        // setting comment adapter and fetching comments from database
        CommentsAdapter commentsAdapter = new CommentsAdapter(CommentsActivity.this, list);
        binding.commentsRv.setNestedScrollingEnabled(false);
        binding.commentsRv.setAdapter(commentsAdapter);
        binding.commentsRv.setLayoutManager(new LinearLayoutManager(CommentsActivity.this));

        database.getReference().child("Posts").child(postId)
                .child("Comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            CommentsModel commentsModel = dataSnapshot.getValue(CommentsModel.class);
                            list.add(commentsModel);
                        }
                        commentsAdapter.notifyDataSetChanged();
                        binding.commentsRv.scrollToPosition(binding.commentsRv.getAdapter().getItemCount()-1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // likes Management
        FirebaseDatabase.getInstance().getReference().child("Posts")
                .child(postId)
                .child("Likes")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_favorite_24, 0, 0, 0);
                        } else {
                            binding.like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    FirebaseDatabase.getInstance().getReference().child("Posts")
                                            .child(postId)
                                            .child("Likes")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("Posts")
                                                            .child(postId)
                                                            .child("likesCounter")
                                                            .setValue(Postmodel.getLikesCounter() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_favorite_24, 0, 0, 0);
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}