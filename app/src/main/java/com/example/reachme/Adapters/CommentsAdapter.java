package com.example.reachme.Adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reachme.Models.CommentsModel;
import com.example.reachme.Models.Users;
import com.example.reachme.R;
import com.example.reachme.databinding.SampleShowCommentsBinding;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.viewHolder> {

    Context context;
    ArrayList<CommentsModel>list;

    public CommentsAdapter(Context context, ArrayList<CommentsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_comments, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        CommentsModel model = list.get(position);

        String commentedAt = TimeAgo.using(model.getCommentTime());
        FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(model.getCommentedBy())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Users user = snapshot.getValue(Users.class);
                                        Picasso.get().load(user.getProfilePic())
                                                .placeholder(R.drawable.avatar)
                                                .into(holder.binding.profileImage);
                                        holder.binding.nameTime.setText(Html.fromHtml("<b>" + user.getUserName() + "</b")+"  "+ commentedAt);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

        holder.binding.comment.setText(model.getCommentBody());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        SampleShowCommentsBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SampleShowCommentsBinding.bind(itemView);
        }
    }
}
