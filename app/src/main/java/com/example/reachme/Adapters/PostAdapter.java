package com.example.reachme.Adapters;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reachme.CommentsActivity;
import com.example.reachme.Models.NotificationModel;
import com.example.reachme.Models.PostModel;
import com.example.reachme.Models.Users;
import com.example.reachme.R;
import com.example.reachme.databinding.SampleShowPostsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder> {

    ArrayList<PostModel> list;
    Context context;

    public PostAdapter(ArrayList<PostModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_posts, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        PostModel model = list.get(position);
        String postDescrp = model.getPostDescription();
        if (postDescrp.isEmpty()) {
            holder.binding.descrp.setVisibility(View.GONE);
        } else {
            holder.binding.descrp.setText(postDescrp);
        }
        Picasso.get().load(model.getPostImg()).placeholder(R.drawable.placeholder_photo)
                .into(holder.binding.post);
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(model.getPostedBy()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Users user = snapshot.getValue(Users.class);
                            Picasso.get().load(user.getProfilePic())
                                    .placeholder(R.drawable.avatar)
                                    .into(holder.binding.profileImage);
                            holder.binding.uname.setText(user.getUserName());
                            holder.binding.proff.setText(user.getAbout());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // post likes
        holder.binding.like.setText(model.getLikesCounter() + "");
        FirebaseDatabase.getInstance().getReference().child("Posts")
                .child(model.getPostId())
                .child("Likes")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_favorite_24, 0, 0, 0);
                        } else {
                            holder.binding.like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    FirebaseDatabase.getInstance().getReference().child("Posts")
                                            .child(model.getPostId())
                                            .child("Likes")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("Posts")
                                                            .child(model.getPostId())
                                                            .child("likesCounter")
                                                            .setValue(model.getLikesCounter() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_favorite_24, 0, 0, 0);

                                                                    // Notification work
                                                                    NotificationModel notification = new NotificationModel();
                                                                    notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                    notification.setNotificationType("Like");
                                                                    notification.setTime(new Date().getTime());
                                                                    notification.setPostId(model.getPostId());
                                                                    notification.setPostedBy(model.getPostedBy());

                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("Notifications")
                                                                            .child(model.getPostedBy())
                                                                            .push()
                                                                            .setValue(notification);
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

        // Comments
        holder.binding.comment.setText(model.getCommentsCounter()+"");
        holder.binding.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentsActivity.class);
                intent.putExtra("postId",model.getPostId());
                intent.putExtra("postedBy",model.getPostedBy());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        SampleShowPostsBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SampleShowPostsBinding.bind(itemView);
        }
    }
}
