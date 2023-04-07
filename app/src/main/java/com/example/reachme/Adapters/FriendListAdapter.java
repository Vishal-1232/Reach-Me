package com.example.reachme.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reachme.Models.Users;
import com.example.reachme.R;
import com.example.reachme.databinding.SampleFriendListBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.viewHolder>{

    Context context;
    ArrayList<Users>list;

    public FriendListAdapter(Context context, ArrayList<Users> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_friend_list,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Users user = list.get(position);
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(user.getUserID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users currUser = snapshot.getValue(Users.class);
                        Picasso.get().load(currUser.getProfilePic())
                                .placeholder(R.drawable.man)
                                .into(holder.binding.profileimg);
                        holder.binding.Name.setText(currUser.getUserName());
                        holder.binding.profession.setText(currUser.getAbout());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        holder.binding.removeFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(FirebaseAuth.getInstance().getUid())
                        .child("Friends").child(user.getUserID())
                        .removeValue();
                FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(user.getUserID())
                        .child("Friends").child(FirebaseAuth.getInstance().getUid())
                        .removeValue();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        SampleFriendListBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SampleFriendListBinding.bind(itemView);
        }
    }
}
