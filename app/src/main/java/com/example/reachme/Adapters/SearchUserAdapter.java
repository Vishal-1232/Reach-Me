package com.example.reachme.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.graphics.drawable.DrawableContainerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reachme.FcmNotificationsSender;
import com.example.reachme.Models.FollowerModel;
import com.example.reachme.Models.NotificationModel;
import com.example.reachme.Models.Users;
import com.example.reachme.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.viewHolder> {
    Context context;
    ArrayList<Users> list;
    public FirebaseDatabase database = FirebaseDatabase.getInstance();

    public SearchUserAdapter(Context context, ArrayList<Users> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_find_user, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Users model = list.get(position);
        holder.userName.setText(model.getUserName());
        holder.about.setText(model.getAbout());
        Picasso.get().load(model.getProfilePic()).placeholder(R.drawable.avatar).into(holder.profilePic);

        FirebaseDatabase.getInstance().getReference().child("Users").child(model.getUserID()).child("Friends").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.follow.setBackgroundResource(R.drawable.following_btn);
                    holder.follow.setText("Added");
                    holder.follow.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                    holder.follow.setEnabled(false);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Users").child(model.getUserID()).child("Friend Requests").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                holder.follow.setBackgroundResource(R.drawable.following_btn);
                                holder.follow.setText("Request Sent");
                                holder.follow.setTextColor(context.getResources().getColor(R.color.arrow));
                                holder.follow.setEnabled(false);
                            } else {
                                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("BlockList")
                                        .child(model.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    holder.follow.setBackgroundResource(R.drawable.following_btn);
                                                    holder.follow.setText("Blocked");
                                                    holder.follow.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                                                    holder.follow.setEnabled(false);
                                                } else {
                                                    FirebaseDatabase.getInstance().getReference().child("Users")
                                                            .child(FirebaseAuth.getInstance().getUid()).child("BlockedBy")
                                                            .child(model.getUserID())
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if (snapshot.exists()) {
                                                                        holder.follow.setBackgroundResource(R.drawable.following_btn);
                                                                        holder.follow.setText("Not Available");
                                                                        holder.follow.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                                                                        holder.follow.setEnabled(false);
                                                                    } else {
                                                                        holder.follow.setOnClickListener(new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View view) {
                                                                                FollowerModel follower = new FollowerModel(FirebaseAuth.getInstance().getUid(), new Date().getTime());
                                                                                database.getReference().child("Users").child(model.getUserID()).child("Friend Requests").child(FirebaseAuth.getInstance().getUid()).setValue(follower).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {
                                                                                        database.getReference().child("Users").child(model.getUserID()).child("followerCount").setValue(model.getFollowerCount() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void unused) {
                                                                                                Toast.makeText(context, "Started following : " + model.getUserName(), Toast.LENGTH_SHORT).show();
                                                                                                holder.follow.setBackgroundResource(R.drawable.following_btn);
                                                                                                holder.follow.setText("Request Sent");
                                                                                                holder.follow.setTextColor(context.getResources().getColor(R.color.arrow));
                                                                                                holder.follow.setEnabled(false);
                                                                                                Toast.makeText(context, "User Follower" + model.getFollowerCount(), Toast.LENGTH_SHORT).show();

                                                                                                // notifiation work
                                                                                                NotificationModel notification = new NotificationModel();
                                                                                                notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                                                notification.setNotificationType("Requested");
                                                                                                notification.setTime(new Date().getTime());
                                                                                                FirebaseDatabase.getInstance().getReference().child("Notifications").child(model.getUserID()).push().setValue(notification);
                                                                                                sendNotification(FirebaseAuth.getInstance().getUid(), model.getFcmTokken());
                                                                                            }
                                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
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

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView profilePic;
        TextView userName, about;
        Button follow;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profileimg);
            userName = itemView.findViewById(R.id.Name);
            about = itemView.findViewById(R.id.profession);
            follow = itemView.findViewById(R.id.follow);
        }
    }

    void sendNotification(String userId, String followerFcm) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                FcmNotificationsSender notificationsSender = new FcmNotificationsSender(followerFcm, "Request Recived", user.getUserName() + " Send you a friend request", context, (Activity) context);
                notificationsSender.SendNotifications();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
