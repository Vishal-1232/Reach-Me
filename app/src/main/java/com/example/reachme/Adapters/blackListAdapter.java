package com.example.reachme.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reachme.Models.FollowerModel;
import com.example.reachme.Models.Users;
import com.example.reachme.R;
import com.example.reachme.databinding.SampleShowBlockUserBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class blackListAdapter extends RecyclerView.Adapter<blackListAdapter.viewHolder>{

    Context context;
    ArrayList<FollowerModel>list = new ArrayList<>();

    public blackListAdapter(Context context, ArrayList<FollowerModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_block_user,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        FollowerModel model = list.get(position);
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(model.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users user = snapshot.getValue(Users.class);
                        holder.binding.Name.setText(user.getUserName());
                        holder.binding.profession.setText(user.getAbout());
                        Picasso.get().load(user.getProfilePic()).placeholder(R.drawable.man).into(holder.binding.profileimg);
                        // UnBlock
                        holder.binding.removeFriend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
                                FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .child("BlockList")
                                        .child(model.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(context, "User Unblocked", Toast.LENGTH_SHORT).show();
                                                // adding this unblocked user into friend list again
                                                FirebaseDatabase.getInstance().getReference().child("Users")
                                                        .child(FirebaseAuth.getInstance().getUid())
                                                        .child("Friends")
                                                        .child(model.getId()).setValue(model);

                                                FollowerModel currUser = new FollowerModel(FirebaseAuth.getInstance().getUid(), new Date().getTime());
                                                FirebaseDatabase.getInstance().getReference().child("Users")
                                                        .child(model.getId())
                                                        .child("Friends")
                                                        .child(currUser.getId())
                                                        .setValue(currUser);
                                            }
                                        });
                            }
                        });
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

    public class viewHolder extends RecyclerView.ViewHolder{
        SampleShowBlockUserBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SampleShowBlockUserBinding.bind(itemView);
        }
    }

}
