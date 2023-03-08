package com.example.reachme.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reachme.Models.Story;
import com.example.reachme.Models.UserStoriesModel;
import com.example.reachme.Models.Users;
import com.example.reachme.R;
import com.example.reachme.databinding.SampleShowStoriesBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.viewHolder> {
    ArrayList<Story> list;
    Context context;

    public StoryAdapter(ArrayList<Story> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_stories, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Story model = list.get(position);
        if (model.getStoriesList().size() > 0) {
            UserStoriesModel lastStory = model.getStoriesList().get(model.getStoriesList().size() - 1);
            Picasso.get().load(lastStory.getImage()).placeholder(R.drawable.image).into(holder.binding.story);
            holder.binding.circularStatusView.setPortionsCount(model.getStoriesList().size());

            FirebaseDatabase.getInstance().getReference().child("Users").child(model.getStoryBy())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Users user = snapshot.getValue(Users.class);
                            Picasso.get().load(user.getProfilePic()).placeholder(R.drawable.avatar)
                                    .into(holder.binding.profileImage);
                            holder.binding.name.setText(user.getUserName());
                            holder.binding.story.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ArrayList<MyStory> myStories = new ArrayList<>();

                                    for (UserStoriesModel storiesModel : model.getStoriesList()) {
                                        myStories.add(new MyStory(
                                                storiesModel.getImage()
                                        ));
                                    }
                                    new StoryView.Builder(((AppCompatActivity) context).getSupportFragmentManager())
                                            .setStoriesList(myStories) // Required
                                            .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                                            .setTitleText(user.getUserName()) // Default is Hidden
                                            .setSubtitleText(user.getAbout()) // Default is Hidden
                                            .setTitleLogoUrl(user.getProfilePic()) // Default is Hidden
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
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        SampleShowStoriesBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SampleShowStoriesBinding.bind(itemView);
        }
    }
}
