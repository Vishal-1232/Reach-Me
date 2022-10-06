package com.example.reachme.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devlomi.circularstatusview.CircularStatusView;
import com.example.reachme.Fragments.StatusFragment;
import com.example.reachme.MainActivity;
import com.example.reachme.Models.Status;
import com.example.reachme.Models.UserStatus;
import com.example.reachme.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder> {

    Context context;
    ArrayList<UserStatus> userStatuses;

    public StatusAdapter(Context context, ArrayList<UserStatus> userStatuses) {
        this.context = context;
        this.userStatuses = userStatuses;
    }

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_status,parent,false);
        return new StatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, int position) {
        UserStatus userStatus = userStatuses.get(position);

        holder.userName.setText(userStatus.getName());
        Status lastStatus =   userStatus.getStatuses().get(userStatus.getStatuses().size() -1);
        Picasso.get().load(lastStatus.getImageURL()).placeholder(R.drawable.avatar).into((ImageView) holder.image);
        holder.circleStatusView.setPortionsCount(userStatus.getStatuses().size());

//        if (userStatus.areAllSeen()) {
//            //set all portions color
//            circularStatusView.setPortionsColor(seenColor);
//        } else {
//            for (int i = 0; i < statusList.size(); i++) {
//                Status status = statusList.get(i);
//                int color = status.isSeen() ? seenColor : notSeenColor;
//                //set specific color for every portion
//                circularStatusView.setPortionColorForIndex(i, color);
//            }
//
//        }

        holder.circleStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<MyStory> myStories = new ArrayList<>();
                for (Status status : userStatus.getStatuses()){
                    myStories.add(new MyStory(status.getImageURL()));
                }

                new StoryView.Builder(((MainActivity)context).getSupportFragmentManager())
                        .setStoriesList(myStories) // Required
                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                        .setTitleText(userStatus.getName()) // Default is Hidden
                        .setSubtitleText("") // Default is Hidden
                        .setTitleLogoUrl(userStatus.getProfileImage()) // Default is Hidden
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                //your action
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userStatuses.size();
    }

    public class StatusViewHolder extends RecyclerView.ViewHolder {
        CircularStatusView circleStatusView;
        CircleImageView image;
        TextView userName;
        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);
            circleStatusView = itemView.findViewById(R.id.circular_status_view);
            userName = itemView.findViewById(R.id.NameUser);
            image = itemView.findViewById(R.id.profilePic);
        }
    }
}
