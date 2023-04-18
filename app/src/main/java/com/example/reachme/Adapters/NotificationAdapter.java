package com.example.reachme.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reachme.CommentsActivity;
import com.example.reachme.Models.NotificationModel;
import com.example.reachme.Models.Users;
import com.example.reachme.R;
import com.example.reachme.databinding.SampleShowNotificationsBinding;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.viewHolder> {
    Context context;
    ArrayList<NotificationModel> list;

    public NotificationAdapter(Context context, ArrayList<NotificationModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_notifications, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        NotificationModel model = list.get(position);
        holder.binding.time.setText(TimeAgo.using(model.getTime()));
        String type = model.getNotificationType();
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(model.getNotificationBy()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Users user = snapshot.getValue(Users.class);
                            Picasso.get().load(user.getProfilePic())
                                    .placeholder(R.drawable.avatar)
                                    .into(holder.binding.profileimg);
                            if (type.equals("Like")) {
                                holder.binding.detail.setText(Html.fromHtml("<b>" + user.getUserName() + "</b>" + " Liked your Post."));
                            } else if (type.equals("Comment")) {
                                holder.binding.detail.setText(Html.fromHtml("<b>" + user.getUserName() + "</b>" + " Commented on your Post."));
                            } else if (type.equals("Requested")) {
                                holder.binding.detail.setText(Html.fromHtml("<b>" + user.getUserName() + "</b>" + " Sent you a Friend Request"));
                            }else if(type.equals("Accepted")){
                                holder.binding.detail.setText(Html.fromHtml("<b>" + user.getUserName() + "</b>" + " Accepted your Friend Request"));
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.binding.openNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!type.equals("Follow")) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Notifications")
                            .child(model.getPostedBy())
                            .child(model.getNotificationId())
                            .child("checkOpen")
                            .setValue(true);

                    holder.binding.openNotification.setBackgroundColor(Color.parseColor("#000000"));
                    Intent intent = new Intent(context, CommentsActivity.class);
                    intent.putExtra("postId", model.getPostId());
                    intent.putExtra("postedBy", model.getPostedBy());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });
        Boolean checkOpen = model.isCheckOpen();
        if (checkOpen){
            holder.binding.openNotification.setBackgroundColor(Color.parseColor("#000000"));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        SampleShowNotificationsBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SampleShowNotificationsBinding.bind(itemView);
        }
    }
}
