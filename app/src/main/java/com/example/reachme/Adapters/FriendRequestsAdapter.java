package com.example.reachme.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reachme.FcmNotificationsSender;
import com.example.reachme.Models.FollowerModel;
import com.example.reachme.Models.Users;
import com.example.reachme.R;
import com.example.reachme.databinding.SampleShowFriendRequestsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.viewHolder> {
    Context context;
    ArrayList<FollowerModel> list;

    String followerFcm;

    public FriendRequestsAdapter(Context context, ArrayList<FollowerModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_friend_requests, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        FollowerModel model = list.get(position);
        FirebaseDatabase.getInstance().getReference().child("Users").child(model.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users user = snapshot.getValue(Users.class);
                        followerFcm = user.getFcmTokken();
                        Picasso.get().load(user.getProfilePic()).placeholder(R.drawable.avatar).into(holder.binding.profileimg);
                        holder.binding.Name.setText(user.getUserName());
                        holder.binding.profession.setText(user.getAbout());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        holder.binding.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FollowerModel follower = new FollowerModel(FirebaseAuth.getInstance().getUid(), new Date().getTime());
                FirebaseDatabase.getInstance().getReference().child("Users").child(model.getId())
                        .child("Friends").child(FirebaseAuth.getInstance().getUid()).setValue(follower).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                FollowerModel followerModel = new FollowerModel(model.getId(), new Date().getTime());
                                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                        .child("Friends").child(model.getId()).setValue(followerModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(context, "Request Accepted", Toast.LENGTH_SHORT).show();
                                                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        Users users = snapshot.getValue(Users.class);
                                                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(followerFcm,"Request Accepted",users.getUserName()+" accepted your friend request",context, (Activity) context);
                                                        notificationsSender.SendNotifications();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                                        .child("Friend Requests").child(model.getId()).setValue(null);
                                            }
                                        });
                            }
                        });
            }
        });
        holder.binding.cancelrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                        .child("Friend Requests").child(model.getId()).setValue(null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        SampleShowFriendRequestsBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SampleShowFriendRequestsBinding.bind(itemView);
        }
    }
}
